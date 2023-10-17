module raw.snapi.client {
    requires scala.library;
    requires org.slf4j;
    requires org.graalvm.polyglot;
//    requires org.graalvm.truffle;

    requires raw.utils;
    requires raw.client;
    requires raw.snapi.frontend;
    requires raw.snapi.truffle;

    provides raw.client.api.CompilerServiceBuilder with
            raw.client.rql2.truffle.Rql2TruffleCompilerServiceBuilder;

}