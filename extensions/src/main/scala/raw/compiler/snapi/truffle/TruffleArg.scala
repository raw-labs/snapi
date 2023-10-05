package raw.compiler.snapi.truffle

import raw.compiler.base.source.Type
import raw.runtime.truffle.ExpressionNode

class TruffleArg(private val exprNode: ExpressionNode, private val `type`: Type, private val identifier: String) {
  def getExprNode: ExpressionNode = exprNode
  def getType: Type = `type`
  def getIdentifier: String = identifier
}
