module raw.inferrer.local {
    requires raw.inferrer;
    provides raw.inferrer.InferrerServiceBuilder with raw.inferrer.local.LocalInferrerServiceBuilder;
    exports raw.inferrer.local;
}