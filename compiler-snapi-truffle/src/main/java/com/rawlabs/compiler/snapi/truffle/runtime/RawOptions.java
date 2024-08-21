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

package com.rawlabs.compiler.snapi.truffle.runtime;

import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionDescriptors;
import org.graalvm.options.OptionKey;

public class RawOptions {

  public static final String STAGED_COMPILER = optionName("staged-compiler");
  public static final OptionKey<String> STAGED_COMPILER_KEY = new OptionKey<>("");
  public static final OptionDescriptor STAGED_COMPILER_DESCRIPTOR =
      OptionDescriptor.newBuilder(STAGED_COMPILER_KEY, STAGED_COMPILER).build();

  public static final String RAW_SETTINGS = optionName("settings");
  public static final OptionKey<String> RAW_SETTINGS_KEY = new OptionKey<>("");
  public static final OptionDescriptor RAW_SETTINGS_DESCRIPTOR =
      OptionDescriptor.newBuilder(RAW_SETTINGS_KEY, RAW_SETTINGS).build();

  // List of all available options
  public static final OptionDescriptors OPTION_DESCRIPTORS =
      OptionDescriptors.create(
          java.util.Arrays.asList(STAGED_COMPILER_DESCRIPTOR, RAW_SETTINGS_DESCRIPTOR));

  private static String optionName(String name) {
    return RawLanguage.ID + "." + name;
  }
}
