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

package raw.runtime.truffle;

import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionDescriptors;
import org.graalvm.options.OptionKey;

public class RawOptions {

  public static final String OUTPUT_FORMAT = optionName("output-format");

  public static final OptionKey<String> OUTPUT_FORMAT_KEY = new OptionKey<>("");
  public static final OptionDescriptor OUTPUT_FORMAT_DESCRIPTOR =
      OptionDescriptor.newBuilder(OUTPUT_FORMAT_KEY, OUTPUT_FORMAT).build();
  public static final OptionDescriptors OPTION_DESCRIPTORS =
      OptionDescriptors.create(java.util.Arrays.asList(OUTPUT_FORMAT_DESCRIPTOR));

  private static String optionName(String name) {
    return RawLanguage.ID + "." + name;
  }
}
