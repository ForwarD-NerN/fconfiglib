package ru.nern.fconfiglib.example;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import ru.nern.fconfiglib.v1.api.ConfigFixer;
import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.validation.OptionValidator;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.InRangeInt;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.InRangeLong;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.MaxLength;
import ru.nern.fconfiglib.v1.api.annotations.restrictions.ValidateOption;
import ru.nern.fconfiglib.v1.api.annotations.validation.ConfigValidators;
import ru.nern.fconfiglib.v1.json.JsonConfigManager;
import ru.nern.fconfiglib.v1.validation.OptionConfigValidator;
import ru.nern.fconfiglib.v1.validation.RestrictionsConfigValidator;
import ru.nern.fconfiglib.v1.validation.VersionConfigValidator;

import java.util.Map;

import static ru.nern.fconfiglib.v1.json.JsonConfigUtils.move;

public class ExampleMod implements ModInitializer {
    public static int CONFIG_VERSION = 7;

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
            OptionConfigValidator.class
    })
    public static class ExampleConfig {
        public boolean hello = false;

        @MaxLength(value = 2)
        public String a = "125";
        public Nested Nested = new Nested();

        public static class Nested {
            @InRangeInt(min = 0, max = 125)
            public int a = 121;
            public boolean wented = false;

            @ValidateOption(ExampleOptionValidator.class)
            public int b8 = 95;

            @InRangeLong(max = 1998L)
            public long b = 2000L;

            public char c = 'a';
        }
    }

    public static class ExampleOptionValidator extends OptionValidator<ExampleConfig> {
        @Override
        public void validate(ExampleConfig config) {
            if(config.hello && config.Nested.b8 == 5) {
                System.out.println("Validation passed successfully");
            }else{
                System.out.println("Validation failed");
            }

        }
    }

}
