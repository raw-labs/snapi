package raw.client.sql.antlr4

trait BaseSqlNode extends Product

final case class BaseSqlProgram() extends BaseSqlNode

final case class BaseSqlStatement() extends BaseSqlNode

final case class BaseSqlProgramParam() extends BaseSqlNode
