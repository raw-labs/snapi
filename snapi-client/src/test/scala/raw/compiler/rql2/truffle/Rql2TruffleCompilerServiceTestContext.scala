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

package raw.compiler.rql2.truffle

import org.graalvm.polyglot.Engine
import raw.client.rql2.truffle.Rql2TruffleCompilerService
import raw.compiler.rql2.api.Rql2CompilerServiceTestContext
import raw.utils.{RawTestSuite, RawUtils, SettingsTestContext}

trait Rql2TruffleCompilerServiceTestContext extends Rql2CompilerServiceTestContext {
  this: RawTestSuite with SettingsTestContext =>

  var rql2TruffleCompilerService: Rql2TruffleCompilerService = _

  var engine: Engine = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    // Create an isolated Truffle Engine
    val options = new java.util.HashMap[String, String]()
    options.put("rql.settings", settings.renderAsString)
//    //diagnostics
//    options.put("engine.CompilationStatistics", "true")
//    options.put("engine.CompilationStatisticDetails", "true")
//    options.put("compiler.InstrumentBoundaries", "true")
//    options.put("engine.CompilationFailureAction", "Diagnose")
//    options.put("engine.TracePerformanceWarnings", "all")
//    options.put("engine.TraceCompilation", "true")
//    options.put("engine.TraceDeoptimizeFrame", "true")
//    options.put("engine.TraceTransferToInterpreter", "true")
//    options.put("engine.CompileImmediately", "true")

//    //    optimizations
//    options.put("compiler.InlineAcrossTruffleBoundary", "true")
//    options.put("engine.CompilerThreads", "24")
//    options.put("engine.FirstTierCompilationThreshold", "100")
//    options.put("engine.FirstTierMinInvokeThreshold", "5")
//    options.put("engine.MinInvokeThreshold", "10")
//    options.put("engine.Mode", "throughput")
//    options.put("engine.MultiTier", "false")
//    options.put("engine.OSR", "false")
//    options.put("engine.PartialBlockCompilation", "false")
//    options.put("engine.PartialBlockCompilationSize", "5000")
//    options.put("engine.PartialBlockMaximumSize", "15000")
//    options.put("engine.SingleTierCompilationThreshold", "500")
//    options.put("engine.Splitting", "false")
//    options.put("compiler.FirstTierUseEconomy", "false")
//    options.put("compiler.InliningExpansionBudget", "20000")
//    options.put("compiler.InliningInliningBudget", "20000")
//    options.put("compiler.InliningRecursionDepth", "10")
//    options.put("engine.IsolateMemoryProtection", "false")

    engine = Engine
      .newBuilder()
      .allowExperimentalOptions(true)
      .options(options)
      .build()

    property("raw.compiler.impl", "rql2-truffle")

    rql2TruffleCompilerService = new Rql2TruffleCompilerService((engine, false), None)
    setCompilerService(rql2TruffleCompilerService)
  }

  override def afterAll(): Unit = {
    if (rql2TruffleCompilerService != null) {
      RawUtils.withSuppressNonFatalException(rql2TruffleCompilerService.stop())
      rql2TruffleCompilerService = null
    }
    if (engine != null) {
      RawUtils.withSuppressNonFatalException(engine.close())
      engine = null
    }
    super.afterAll()
  }

}
