package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext

import java.nio.file.Files
import scala.io.Source

trait RD9445Test extends CompilerTestContext {

  test("Timestamp.Build(1975, 6, 23, 9, 0)") { it =>
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
      val s = Source.fromFile(tmpFile.toFile)
      val lines: List[String] = s.getLines().toList
      assert(lines == List("1975-23-06T9:00.000"))
      s.close()
    } finally {
      Files.delete(tmpFile)
    }
  }
}
