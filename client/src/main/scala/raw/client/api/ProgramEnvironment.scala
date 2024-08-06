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

package raw.client.api

import raw.utils.RawUid

trait ProgramEnvironment {

  /**
   * The user id of the program.
   * @return the user id
   */
  def uid: RawUid

  /**
   * The arguments of the program.
   * @return the arguments
   */
  def maybeArguments: Option[Array[(String, RawValue)]]

  /**
   * The scopes of the program.
   * @return the scopes
   */
  def scopes: Set[String]

  /**
   * The options of the program.
   * @return the options
   */
  def options: Map[String, String]

  /**
   * The traceId to include in logging messages.
   * @return the traceId
   */
  def maybeTraceId: Option[String]

}
