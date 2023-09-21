module raw.sources.local {
    requires raw.utils;
    requires raw.creds;
    requires raw.sources;
    exports raw.sources.filesystem.local;
}