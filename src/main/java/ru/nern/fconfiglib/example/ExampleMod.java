package ru.nern.fconfiglib.example;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import ru.nern.fconfiglib.v1.api.ConfigFixer;
import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.OptionValidatorCallback;
import ru.nern.fconfiglib.v1.api.annotations.*;
import ru.nern.fconfiglib.v1.api.annotations.restriction.InRangeInt;
import ru.nern.fconfiglib.v1.api.annotations.restriction.InRangeLong;
import ru.nern.fconfiglib.v1.api.annotations.restriction.MaxLength;
import ru.nern.fconfiglib.v1.api.annotations.restriction.OptionValidator;
import ru.nern.fconfiglib.v1.api.annotations.mixin.MixinOption;
import ru.nern.fconfiglib.v1.json.JsonConfigManager;
import ru.nern.fconfiglib.v1.validators.RestrictionConfigValidator;
import ru.nern.fconfiglib.v1.validators.MixinConfigValidator;
import ru.nern.fconfiglib.v1.validators.VersionConfigValidator;

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

    @ConfigValidator({
            MixinConfigValidator.class,
            VersionConfigValidator.class,
            RestrictionConfigValidator.class // Убедитесь, что это наследник AbstractConfigValidator
    })
    public static class ExampleConfig {
        public boolean hello = false;
        @MaxLength(2)
        public String a = "125";
        public Nested Nested = new Nested();

        public static class Nested {
            @InRangeInt(min = 0, max = 125)
            public int a = 121;
            public boolean wented = false;

            @OptionValidator(validator = ExampleOptionValidatorCallback.class)
            public int b8 = 95;

            @InRangeLong(max = 1998L)
            public long b = 2000L;

            @MixinOption(path = "ru.nern.fconfiglib.example.mixin.AbcMixin")
            public char c = 'a';
        }
    }

    public static class ExampleOptionValidatorCallback implements OptionValidatorCallback<ExampleConfig> {
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
