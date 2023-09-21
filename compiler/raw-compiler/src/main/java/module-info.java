module raw.compiler {
    requires raw.utils;
    requires raw.inferrer;
    requires raw.auth;
    requires raw.runtime;

    exports raw.compiler;
    exports raw.compiler.base;
    exports raw.compiler.base.errors;
    exports raw.compiler.base.source;
    exports raw.compiler.jvm;
    exports raw.compiler.scala2;
}