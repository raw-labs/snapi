/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.frontend.base

import com.google.common.base.Stopwatch
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.rawlabs.utils.core.{RawSettings, RawUid}
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.snapi.frontend.inferrer.api.{InferrerException, InferrerInput, InferrerOutput, InferrerService}
import com.rawlabs.snapi.frontend.snapi.extensions.Arg
import com.rawlabs.utils.core._

import java.util.concurrent.{ExecutionException, Executors, TimeUnit, TimeoutException}

object CompilerContext {
  private val INFERRER_TIMEOUT = "raw.snapi.frontend.inferrer.timeout"
  private val INFERRER_EXPIRY = "raw.snapi.frontend.inferrer.expiry"

  private val INFERRER_CACHE_SIZE = "raw.snapi.frontend.inferrer.cache-size"
}

/**
 * Contains state that is shared between different programs.
 */
class CompilerContext(
    val user: RawUid,
    val inferrer: InferrerService
)(
    implicit val settings: RawSettings
) extends RawService
    with StrictLogging {

  import CompilerContext._

  private val inferrerTimeoutMillis = settings.getDuration(INFERRER_TIMEOUT).toMillis
  private val inferrerExpirySeconds = settings.getDuration(INFERRER_EXPIRY).toSeconds

  private val inferrerCacheSize = settings.getInt(INFERRER_CACHE_SIZE)

  private val inferrerThreadPool = Executors.newCachedThreadPool(RawUtils.newThreadFactory("inferrer-thread"))

  private val inferCache: LoadingCache[CompilerContextInferrerKey, Either[String, InferrerOutput]] = CacheBuilder
    .newBuilder()
    .maximumSize(inferrerCacheSize)
    .expireAfterAccess(inferrerExpirySeconds, TimeUnit.SECONDS)
    .build(new CacheLoader[CompilerContextInferrerKey, Either[String, InferrerOutput]] {
      def load(key: CompilerContextInferrerKey): Either[String, InferrerOutput] = {
        val inferrerFuture = inferrerThreadPool.submit(() => {
          try {
            Right(inferrer.infer(key.inferrerInput))
          } catch {
            case ex: InferrerException => Left(ex.getMessage)
          }
        })

        try {
          val start = Stopwatch.createStarted()
          val r = inferrerFuture.get(inferrerTimeoutMillis, TimeUnit.MILLISECONDS)
          logger.debug(s"Inferrer done in ${start.elapsed(TimeUnit.MILLISECONDS)} millis.")
          r
        } catch {
          // Note that in InterruptException or TimeoutException, we do not cancel the request.
          // That's because other independent programs can themselves be waiting on the 'get' and those can succeed
          // in under 'inferrerTimeoutMillis' because they started a bit later to do .get blocking call.
          // In any case, this shouldn't be too much of a problem because the LoadingCache ensure that a single request
          // is running for a single key.
          case _: TimeoutException => Left("inferrer took too long")
          case ex: ExecutionException => throw ex.getCause
        }
      }
    })

  def infer(
      inferrerInput: InferrerInput,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  ): Either[String, InferrerOutput] = {
    inferCache.get(CompilerContextInferrerKey(mandatoryArgs, optionalArgs, varArgs, inferrerInput))
  }

  final override def doStop(): Unit = {
    RawUtils.withSuppressNonFatalException {
      inferrerThreadPool.shutdownNow()
      inferrerThreadPool.awaitTermination(5, TimeUnit.SECONDS)
    }
  }

}

/**
 * Key for the inferrer cache.
 * The key is based on the arguments and properties passed to the inferrer.
 * The properties are not considered in the equals and hashCode methods but only used to actually infer the output.
 *
 * @param mandatoryArgs the mandatory arguments
 * @param optionalArgs the optional arguments
 * @param varArgs the varargs
 * @param inferrerInput the inferrer input
 */
private case class CompilerContextInferrerKey(
    mandatoryArgs: Seq[Arg],
    optionalArgs: Seq[(String, Arg)],
    varArgs: Seq[Arg],
    inferrerInput: InferrerInput
) {
  // Override equals and hashCode to consider only the arguments, not the properties
  override def equals(other: Any): Boolean = other match {
    case that: CompilerContextInferrerKey => this.mandatoryArgs == that.mandatoryArgs &&
        this.optionalArgs == that.optionalArgs &&
        this.varArgs == that.varArgs
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(mandatoryArgs, optionalArgs, varArgs)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
