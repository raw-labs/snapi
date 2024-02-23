package raw.client.sql.antlr4

import org.bitbucket.inkytonik.kiama.util.Position

/**
 * Represents a parameter in a SQL statement.
 * @param name the name of the parameter
 * @param tipe the type of the parameter
 * @param default the default value of the parameter
 * @param occurs start and end positions of the parameter occurrences in the source code
 */
case class SqlParam(
    name: String,
    tipe: Option[String],
    default: Option[String],
    occurs: Vector[(Position, Position)]
)
