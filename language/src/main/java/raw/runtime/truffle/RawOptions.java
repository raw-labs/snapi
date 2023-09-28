package raw.runtime.truffle;

import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionDescriptors;
import org.graalvm.options.OptionKey;

public class RawOptions {

    public static final String OUTPUT_FORMAT = optionName("output-format");

    public static final OptionKey<String> OUTPUT_FORMAT_KEY = new OptionKey<>("");
    public static final OptionDescriptor OUTPUT_FORMAT_DESCRIPTOR = OptionDescriptor.newBuilder(OUTPUT_FORMAT_KEY, OUTPUT_FORMAT).build();
    public static final OptionDescriptors OPTION_DESCRIPTORS = OptionDescriptors.create(
            java.util.Arrays.asList(
                    OUTPUT_FORMAT_DESCRIPTOR
            )
    );

    private static String optionName(String name) {
        return RawLanguage.ID + "." + name;
    }

}
