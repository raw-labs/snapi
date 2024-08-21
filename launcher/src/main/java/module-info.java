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

module raw.cli {
    requires org.graalvm.polyglot;
    requires raw.client;
    requires raw.utils.core;
    requires typesafe.config;
    requires scala.library;
    requires org.slf4j;
    requires org.jline.terminal;
    requires org.jline.reader;
//    requires raw.creds.api;
//    requires raw.utils.core;
//    requires scala.library;
//    requires com.typesafe.scalalogging_2.12;
    exports raw.cli;
}