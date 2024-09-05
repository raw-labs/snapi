/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.lineage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SnapiLineageJson {
  private final ObjectMapper mapper = new ObjectMapper();
  private final ObjectNode rootNode = mapper.createObjectNode();

  @Override
  public String toString() {
    return rootNode.toString();
  }

  public void add(String key, String value) {
    rootNode.put(key, value);
  }
}
