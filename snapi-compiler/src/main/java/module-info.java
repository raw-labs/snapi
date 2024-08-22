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

import com.rawlabs.compiler.CompilerServiceBuilder;
import com.rawlabs.snapi.compiler.Rql2CompilerServiceBuilder;

module raw.snapi.client {
  requires scala.library;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires raw.utils.core;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.core;
  requires raw.utils.sources;
  requires raw.protocol.raw;
  requires raw.protocol.compiler;
  requires raw.compiler;
  requires raw.snapi.frontend;

  uses CompilerServiceBuilder;

  provides CompilerServiceBuilder with
      Rql2CompilerServiceBuilder;
}
