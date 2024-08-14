package ru.nern.fconfiglib.example;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import ru.nern.fconfiglib.v1.config.ConfigFixer;
import ru.nern.fconfiglib.v1.config.ConfigManager;
import ru.nern.fconfiglib.v1.config.Validator;
import ru.nern.fconfiglib.v1.config.annotations.InRangeInt;
import ru.nern.fconfiglib.v1.config.annotations.InRangeLong;
import ru.nern.fconfiglib.v1.config.annotations.MaxLength;
import ru.nern.fconfiglib.v1.config.annotations.Validate;
import ru.nern.fconfiglib.v1.config.json.JsonConfigManager;

import java.util.LinkedHashSet;

import static ru.nern.fconfiglib.v1.config.json.JsonConfigUtils.move;

public class ExampleMod implements ModInitializer {
    public static int CONFIG_VERSION = 7;

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
    public static LinkedHashSet<ConfigFixer<ExampleConfig, JsonObject>> getFixers() {
        LinkedHashSet<ConfigFixer<ExampleConfig, JsonObject>> fixers = new LinkedHashSet<>();

        fixers.add(new ConfigFixer<ExampleConfig, JsonObject>(2) {
            @Override
            public void apply(ExampleConfig config, JsonObject raw) {
                move(raw, "Nested.bcd", "Nested.intu");
                move(raw, "Nested.a", "Nested.longu");
                move(raw, "Nested.i", "Nested.charu");
            }
        });

        fixers.add(new ConfigFixer<ExampleConfig, JsonObject>(3) {
            @Override
            public void apply(ExampleConfig config, JsonObject raw) {
                move(raw, "Nested.intu", "Nested.a");
                move(raw, "Nested.longu", "Nested.b");
                move(raw, "Nested.charu", "Nested.c");
            }
        });

        fixers.add(new ConfigFixer<ExampleConfig, JsonObject>(5) {
            @Override
            public void apply(ExampleConfig config, JsonObject raw) {
                move(raw, "went", "Nested.wented");
            }
        });

        return fixers;
    }

    public static ConfigManager<ExampleConfig, JsonObject> manager = JsonConfigManager
            .builderOf(ExampleConfig.class)
            .modId("examplemod")
            .fixers(getFixers())
            .version(CONFIG_VERSION)
            .create();


    public static ExampleConfig config() {
        return manager.config();
    }

    @Override
    public void onInitialize() {
        manager.init();
    }

    public static class ExampleConfig {
        public boolean hello = false;
        @MaxLength(length = 2)
        public String a = "125";
        public Nested Nested = new Nested();

        public static class Nested {
            @InRangeInt(min = 0, max = 125)
            public int a = 121;
            public boolean wented = false;

            @Validate(validator = ExampleMod.ExampleValidator.class)
            public int b8 = 95;

            @InRangeLong(max = 1998L)
            public long b = 2000L;
            public char c = 'a';
        }
    }

    public static class ExampleValidator implements Validator<ExampleConfig> {
        @Override
        public boolean validate(ExampleConfig config) {
            if(config.hello && config.Nested.b8 == 5) {
                System.out.println("Validation passed successfully");
                return false;
            }
            System.out.println("Validation failed");
            return false;
        }
    }

}
