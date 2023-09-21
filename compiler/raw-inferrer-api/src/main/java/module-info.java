module raw.inferrer {
    requires raw.utils;
    requires raw.creds;
    requires raw.sources;
    uses raw.inferrer.InferrerServiceBuilder;
    exports raw.inferrer;
}