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

module raw.client {
  requires org.graalvm.polyglot;
  requires scala.library;

  requires raw.utils;

  exports raw.client.api;
  opens raw.client.api;
  exports raw.client.writers;
}
