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

package com.rawlabs.snapi.compiler

import com.rawlabs.compiler.CompilerServiceTestContext
import com.rawlabs.utils.core.{RawTestSuite, RawUtils, SettingsTestContext}
import org.graalvm.polyglot.Engine

trait SnapiCompilerServiceTestContext extends CompilerServiceTestContext {
  this: RawTestSuite with SettingsTestContext =>

  var snapiTruffleCompilerService: SnapiCompilerService = _

  var engine: Engine = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    property("raw.compiler.impl", "snapi")

    // Create an isolated Truffle Engine
    val options = new java.util.HashMap[String, String]()

    // Diagnostics-related settings
//    options.put("engine.CompilationStatistics", "true")
//    //    options.put("engine.CompilationStatisticDetails", "true")
//    //    options.put("compiler.InstrumentBoundaries", "true")
//    options.put("engine.CompilationFailureAction", "Diagnose")
//    options.put("engine.TracePerformanceWarnings", "all")
//    options.put("engine.TraceCompilation", "true")
//    options.put("engine.TraceSplitting", "true")
//    options.put("engine.TraceDeoptimizeFrame", "true")
//    options.put("engine.TraceTransferToInterpreter", "true")
//    options.put("engine.TraceCompilationPolymorphism", "true")
//    options.put("engine.TraceSplittingSummary", "true")
//    //    options.put("engine.TraceCompilationDetails", "true")
//    //    options.put("engine.CompileImmediately", "true")
//    options.put("engine.BackgroundCompilation", "false")
//    options.put("engine.SpecializationStatistics", "false")
//    options.put("engine.OSR", "false")

    //  Optimizations-related settings
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

    snapiTruffleCompilerService = new SnapiCompilerService((engine, false))
    setCompilerService(snapiTruffleCompilerService)
  }

  override def afterAll(): Unit = {
    if (snapiTruffleCompilerService != null) {
      RawUtils.withSuppressNonFatalException(snapiTruffleCompilerService.stop())
      snapiTruffleCompilerService = null
    }
    if (engine != null) {
      RawUtils.withSuppressNonFatalException(engine.close())
      engine = null
    }
    super.afterAll()
  }

}
