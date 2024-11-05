package ru.nern.fconfiglib.example;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.ConfigFixer;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.InRangeInt;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.InRangeLong;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.MaxLength;
import ru.nern.fconfiglib.v1.api.annotations.validation.ConfigValidators;
import ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField;
import ru.nern.fconfiglib.v1.json.JsonConfigManager;
import ru.nern.fconfiglib.v1.utils.ValueReference;
import ru.nern.fconfiglib.v1.validation.FieldValidator;
import ru.nern.fconfiglib.v1.validation.FieldsConfigValidator;
import ru.nern.fconfiglib.v1.validation.RestrictionsConfigValidator;
import ru.nern.fconfiglib.v1.validation.VersionConfigValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.nern.fconfiglib.v1.json.JsonConfigUtils.move;

public class ExampleMod implements ModInitializer {
    public static int CONFIG_VERSION = 8;

    public static ConfigManager<ExampleConfig, JsonObject> manager = JsonConfigManager
            .builderOf(ExampleConfig.class)
            .modId("examplemod")
            .version(CONFIG_VERSION)
            .fixers(ExampleMod::registerFixers)
            .create();

    public static void registerFixers(Map<Integer, ConfigFixer<ExampleConfig, JsonObject>> fixers) {
        fixers.put(2, (config, raw) -> {
            move(raw, "Nested.bcd", "Nested.intu");
            move(raw, "Nested.a", "Nested.longu");
            move(raw, "Nested.i", "Nested.charu");
            System.out.println("Fixer 1 applied");
        });

        fixers.put(5, (config, raw) -> {
            move(raw, "went", "Nested.wented");
            System.out.println("Fixer 3 applied");
        });

        fixers.put(3, (config, raw) -> {
            move(raw, "Nested.intu", "Nested.a");
            move(raw, "Nested.longu", "Nested.b");
            move(raw, "Nested.charu", "Nested.c");
            System.out.println("Fixer 2 applied");
        });
    }


    /*
    {
  "hello": false,
  "a": "125",
  "Nested": {
    "bcd": 128,
    "a": 2001,
    "i": "a"
  },
  "lastLoadedVersion": 1
}
     */

    public static ExampleConfig config() {
        return manager.config();
    }

    @Override
    public void onInitialize() {
        manager.init();
    }

    @ConfigValidators({
            VersionConfigValidator.class,
            RestrictionsConfigValidator.class,
            FieldsConfigValidator.class
    })
    public static class ExampleConfig {
        public boolean hello = false;

        @MaxLength(value = 2)
        public String a = "125";

        public SubConfig SubConfig = new SubConfig();

        public static class SubConfig {
            @InRangeInt(min = 0, max = 125)
            public int a = 121;
            public boolean wented = false;

            @ValidateField(ExampleFieldValidator.class)
            public int b8 = 128;

            @ValidateField(ExampleListValidator.class)
            public List<String> list = new ArrayList<>();

            @InRangeLong(max = 1998L)
            public long b = 2000L;

            public char c = 'a';
        }
    }

    public static class ExampleFieldValidator implements FieldValidator<Integer, ExampleConfig> {
        @Override
        public void validate(ValueReference<Integer> reference, ExampleConfig config) {
            int value = reference.get();
            if(value > 128) reference.set(0);
            System.out.println("Validating an integer");
        }
    }

    static class ExampleListValidator implements FieldValidator<List<String>, ExampleConfig> {
        @Override
        public void validate(ValueReference<List<String>> reference, ExampleConfig instance) {
            System.out.println("Validating a string list");
        }
    }

}
