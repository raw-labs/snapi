module raw.launcher {
//    requires java.base;
//    requires org.graalvm.polyglot;
//    requires org.slf4j;
    requires org.graalvm.polyglot;
//    requires org.graalvm.truffle;

//    requires raw.creds.api;
//    requires raw.utils;
    requires scala.library;

//    requires com.typesafe.scalalogging_2.12;
//    requires raw.language ;
//    requires raw.language.extensions;

    exports raw.tests;

    requires java.base;
    requires java.logging;
    requires jdk.unsupported;
//    requires org.graalvm.truffle;
//    requires com.fasterxml.jackson.core;
//    requires com.fasterxml.jackson.databind;
//    requires com.fasterxml.jackson.dataformat.csv;
//    requires com.esotericsoftware.kryo;
//    requires com.esotericsoftware.minlog;
//    requires com.esotericsoftware.reflectasm;
//    requires java.xml;
//    requires java.sql;
//    requires scala.library;
//    //    requires scala.reflect;
//    requires org.apache.commons.io;
//    //    requires org.apache.commons.text;
//    requires com.ctc.wstx;
//    requires com.ibm.icu;
//    requires typesafe.config;
//    requires typesafe.scalalogging;
//    requires kiama;
//    requires org.apache.commons.lang3;
//    requires org.slf4j;
//    requires ch.qos.logback.classic;
//    requires com.google.common;
////    // requires jul.to.slf4j;
}