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

package com.rawlabs.snapi.frontend.inferrer.api

import com.google.common.base.Stopwatch
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.rawlabs.utils.core.{RawException, RawService, RawSettings, RawUtils}

import java.util.concurrent.{ExecutionException, Executors, TimeUnit, TimeoutException}

object InferrerService {
  private val INFERRER_TIMEOUT = "raw.snapi.frontend.inferrer.timeout"
  private val INFERRER_EXPIRY = "raw.snapi.frontend.inferrer.expiry"

  private val INFERRER_CACHE_SIZE = "raw.snapi.frontend.inferrer.cache-size"
}

abstract class InferrerService(implicit settings: RawSettings) extends RawService {

  import InferrerService._

  // Print stack trace when the inferrer starts
  logger.warn(s"INFER: $this: InferrerService started.", new Throwable())

  private val inferrerTimeoutMillis = settings.getDuration(INFERRER_TIMEOUT).toMillis
  private val inferrerExpirySeconds = settings.getDuration(INFERRER_EXPIRY).toSeconds

  private val inferrerCacheSize = settings.getInt(INFERRER_CACHE_SIZE)

  private val inferrerThreadPool = Executors.newCachedThreadPool(RawUtils.newThreadFactory("inferrer-thread"))

  private val inferCache: LoadingCache[InferrerInput, Either[String, InferrerOutput]] = CacheBuilder
    .newBuilder()
    .maximumSize(inferrerCacheSize)
    .expireAfterAccess(inferrerExpirySeconds, TimeUnit.SECONDS)
    .build(new CacheLoader[InferrerInput, Either[String, InferrerOutput]] {
      def load(inferrerInput: InferrerInput): Either[String, InferrerOutput] = {
        val inferrerFuture = inferrerThreadPool.submit(() => {
          try {
            Right(infer(inferrerInput))
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

  def inferWithCache(inferrerInput: InferrerInput): Either[String, InferrerOutput] = {
    inferCache.get(inferrerInput)
  }

  /**
   * Infers the schema of a data source.
   * It throws an exception if the inference fails.
   * (We prefer in this case to use exceptions instead of Option or Try because we often want to exit early.)
   *
   * @param properties The properties of the data source.
   * @throws RawException if the inference fails.
   * @return The inferred schema.
   */
  @throws[RawException]
  def infer(properties: InferrerInput): InferrerOutput

  final override def doStop(): Unit = {
    RawUtils.withSuppressNonFatalException {
      logger.warn(s"INFER: $this: InferrerService stopped.", new Throwable())
      inferrerThreadPool.shutdownNow()
      inferrerThreadPool.awaitTermination(5, TimeUnit.SECONDS)
    }
  }

}
