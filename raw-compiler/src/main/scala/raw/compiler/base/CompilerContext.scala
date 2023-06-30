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

package raw.compiler.base

import com.google.common.base.Stopwatch
import com.typesafe.scalalogging.StrictLogging
import raw.api.AuthenticatedUser
import raw.config.RawSettings
import raw.sources.SourceContext
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import raw.inferrer.{InferrerException, InferrerProperties, InferrerService, InputFormatDescriptor}
import raw.runtime.{ExecutionLogger, NullExecutionLogger}
import raw.utils.RawUtils

import java.util.concurrent.{Executors, TimeUnit, TimeoutException}
import scala.concurrent.ExecutionException

/**
 * Contains state that is shared between different programs.
 */
abstract class CompilerContext(
    val language: String,
    val user: AuthenticatedUser,
    val inferrer: InferrerService,
    val sourceContext: SourceContext
)(
    implicit val settings: RawSettings
) extends StrictLogging {

  private val inferrerThreadPoolSize = settings.getInt("raw.compiler.inferrer.thread-pool-size")
  private val inferrerThreadPool =
    Executors.newFixedThreadPool(inferrerThreadPoolSize, RawUtils.newThreadFactory("compiler-context-inferrer"))
  private val inferrerTimeoutMillis = settings.getDuration("raw.compiler.inferrer.timeout").toMillis
  private val inferrerCacheSize = settings.getInt("raw.compiler.inferrer.cache-size")
  private val inferrerExpirySeconds = settings.getDuration("raw.compiler.inferrer.expiry").toSeconds

  def infer(
      properties: InferrerProperties
  ): Either[String, InputFormatDescriptor] = {
    inferCache.get(properties)
  }

  // RQL compatible call (inferer results aren't cached)
  def inferNoCache(
      properties: InferrerProperties
  )(implicit executionLogger: ExecutionLogger): Either[String, InputFormatDescriptor] = {
    try {
      Right(inferrer.infer(properties))
    } catch {
      case ex: InferrerException => Left(ex.getMessage)
    }
  }

  private val inferCache: LoadingCache[InferrerProperties, Either[String, InputFormatDescriptor]] = CacheBuilder
    .newBuilder()
    .maximumSize(inferrerCacheSize)
    .expireAfterAccess(inferrerExpirySeconds, TimeUnit.SECONDS)
    .build(new CacheLoader[InferrerProperties, Either[String, InputFormatDescriptor]] {
      def load(properties: InferrerProperties): Either[String, InputFormatDescriptor] = {
        val inferrerFuture = inferrerThreadPool.submit(() => {
          try {
            Right(inferrer.infer(properties)(NullExecutionLogger))
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

}
