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

import java.util.concurrent.{ExecutionException, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit, TimeoutException}

object InferrerService {
  private val INFERRER_TIMEOUT = "raw.snapi.frontend.inferrer.timeout"
  private val INFERRER_EXPIRY = "raw.snapi.frontend.inferrer.expiry"

  private val INFERRER_CACHE_SIZE = "raw.snapi.frontend.inferrer.cache-size"

  private val INFERRER_THREAD_POOL_SIZE = "raw.snapi.frontend.inferrer.thread-pool-size"
}

abstract class InferrerService(implicit settings: RawSettings) extends RawService {

  import InferrerService._

  private val inferrerTimeoutMillis = settings.getDuration(INFERRER_TIMEOUT).toMillis
  private val inferrerExpirySeconds = settings.getDuration(INFERRER_EXPIRY).toSeconds

  private val inferrerCacheSize = settings.getInt(INFERRER_CACHE_SIZE)

  private val inferrerThreadPoolSize = settings.getInt(INFERRER_THREAD_POOL_SIZE)

  // That thread pool is the one that holds threads running inference. It remains alive for the lifetime of the service.
  // After 1 minute of inactivity, threads are shutdown.
  private val inferrerThreadPool = new ThreadPoolExecutor(
    inferrerThreadPoolSize,
    inferrerThreadPoolSize,
    1L,
    TimeUnit.MINUTES,
    new LinkedBlockingQueue[Runnable], // Queue is unbounded to not reject tasks
    RawUtils.newThreadFactory("inferrer-service")
  )
  // Applies the idle timeout to core threads too.
  inferrerThreadPool.allowCoreThreadTimeOut(true)
  // The main entrypoint for the inferrer.
  // Using an exception for inference is reasonable because we often want inference to exit early.
  @throws[RawException]
  def infer(properties: InferrerInput): InferrerOutput

  // Inferrer that uses internal cache and expiry.
  // Instead of an exception, it returns an Either (since the timeout error is returned as a Left).
  // @param timeout How long the inference can take before aborting with an exception.
  // @param expiry How long an old result of the inference is still accepted as valid.
  final def inferWithExpiry(properties: InferrerInput): Either[String, InferrerOutput] = {
    inferCache.get(properties)
  }

  private val inferCache: LoadingCache[InferrerInput, Either[String, InferrerOutput]] = CacheBuilder
    .newBuilder()
    .maximumSize(inferrerCacheSize)
    .expireAfterAccess(inferrerExpirySeconds, TimeUnit.SECONDS)
    .build(new CacheLoader[InferrerInput, Either[String, InferrerOutput]] {
      def load(properties: InferrerInput): Either[String, InferrerOutput] = {
        val inferrerFuture = inferrerThreadPool.submit(() => {
          try {
            Right(infer(properties))
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

  final override def doStop(): Unit = {
    RawUtils.withSuppressNonFatalException {
      inferrerThreadPool.shutdownNow()
      inferrerThreadPool.awaitTermination(5, TimeUnit.SECONDS)
    }
  }

}
