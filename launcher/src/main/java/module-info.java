module raw.cli {
    requires org.graalvm.polyglot;
    requires raw.client;
    requires raw.utils;
    requires typesafe.config;
    requires scala.library;
    requires org.slf4j;
    requires org.jline.terminal;
    requires org.jline.reader;
//    requires raw.creds.api;
//    requires raw.utils;
//    requires scala.library;
//    requires com.typesafe.scalalogging_2.12;
    exports raw.cli;
}