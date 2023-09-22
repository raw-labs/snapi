import raw.auth.AuthServiceBuilder;

module raw.compiler {
    requires java.base;
    requires java.logging;
    requires jdk.unsupported;
    requires org.graalvm.truffle;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.csv;
    requires com.esotericsoftware.kryo;
    requires com.esotericsoftware.minlog;
    requires com.esotericsoftware.reflectasm;
    requires java.xml;
    requires java.sql;
    requires scala.library;
//    requires scala.reflect;
    requires org.apache.commons.io;
//    requires org.apache.commons.text;
    requires com.ctc.wstx;
    requires com.ibm.icu;
    requires typesafe.config;
    requires typesafe.scalalogging;
    requires kiama;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    //requires jul.to.slf4j;

    uses raw.auth.AuthServiceBuilder;
    uses raw.compiler.base.CompilerBuilder;
    uses raw.compiler.common.CommonCompilerBuilder;

    uses raw.creds.CredentialsServiceBuilder;
    provides raw.creds.CredentialsServiceBuilder with raw.creds.local.LocalCredentialsServiceBuilder;
//    provides raw.creds.CredentialsServiceBuilder with raw.creds.mock.MockCredentialsServiceBuilder;

    uses raw.inferrer.InferrerServiceBuilder;
    provides raw.inferrer.InferrerServiceBuilder with raw.inferrer.local.LocalInferrerServiceBuilder;

    uses raw.sources.LocationBuilder;

    uses raw.sources.bytestream.ByteStreamLocationBuilder;
    uses raw.sources.filesystem.FileSystemLocationBuilder;
    uses raw.sources.jdbc.JdbcLocationBuilder;
    uses raw.sources.jdbc.JdbcSchemaLocationBuilder;
    uses raw.sources.jdbc.JdbcTableLocationBuilder;

    uses raw.compiler.rql2.PackageExtension;

    provides com.oracle.truffle.api.provider.TruffleLanguageProvider with raw.runtime.truffle.RawLanguageProvider;
}
