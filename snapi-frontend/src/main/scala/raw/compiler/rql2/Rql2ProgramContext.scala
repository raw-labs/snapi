package raw.compiler.rql2

import raw.compiler.base.CompilerContext
import raw.runtime.RuntimeContext

class Rql2ProgramContext(
    override val runtimeContext: RuntimeContext,
    override val compilerContext: CompilerContext
) extends ProgramContext
