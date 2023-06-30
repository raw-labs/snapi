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

package raw.compiler.base.source

trait BaseNode extends Product

trait Type extends BaseNode

sealed trait BaseType extends Type

/**
 * This type is used as a type constraint for the type checker (i.e. any type is accepted).
 * It will never be user-visible.
 */
final case class AnythingType() extends BaseType

/**
 * This type is used when the expression does not return a value. (i.e. it is not an expression).
 * Typical case is when calling a program that does not return.
 */
final case class NotValueType() extends BaseType

/**
 * Identifier reference
 */
trait BaseIdnNode extends BaseNode {
  def idn: String
}

trait BaseProgram extends BaseNode

trait RawBridge[T]

// A true Bridge which prevents nested languages from seeing each other.
// (Kiama's default Bridge is a case class, so collect/query/everywhere still go through it...)
final class RawBridgeImpl[T](val language: String, val cross: T) extends RawBridge[T] {
  override def toString: String = s"$language: ${cross.toString}"
}

object RawBridgeImpl {
  def apply[T](language: String, cross: T): RawBridgeImpl[T] = new RawBridgeImpl(language, cross)

  def unapply[T](arg: RawBridgeImpl[T]): Option[(String, T)] = Some((arg.language, arg.cross))
}

// This node is only used during code templated phase (refer to Compiler.template).
// It is never part of compiler phases and only used during code execution, as a way to refer
// to a program (which can be found by 'id' on the ProgramContext) of a given type 't'.
final class RawBridgeRef[T](val t: Type, val id: String) extends RawBridge[T]

object RawBridgeRef {
  def apply[T](t: Type, id: String) = new RawBridgeRef[T](t, id)
  def unapply[T](arg: RawBridgeRef[T]): Option[(Type, String)] = Some((arg.t, arg.id))
}
