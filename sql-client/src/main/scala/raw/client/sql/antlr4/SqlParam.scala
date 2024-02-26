package raw.client.sql.antlr4

/**
 * Represents a parameter in a SQL program
 * @param name the name of the parameter
 * @param description the description of the parameter
 * @param tipe the type of the parameter
 * @param default the default value of the parameter
 * @param nodes tree nodes where the parameter is defined, used for deduplication
 * @param occurrences tree nodes where the parameter occurs
 */
case class SqlParam(
    name: String,
    description: Option[String],
    tipe: Option[String],
    default: Option[String],
    nodes: Vector[BaseSqlNode],
    occurrences: Vector[SqlParamUse]
)
