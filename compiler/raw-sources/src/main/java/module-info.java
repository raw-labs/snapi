module raw.sources {
    requires raw.utils;
    requires raw.creds;
    requires org.apache.commons.lang3;

    exports raw.sources;
    exports raw.sources.bytestream;
    exports raw.sources.filesystem;
    exports raw.sources.jdbc;
}