package ru.nern.example;

import com.google.gson.JsonObject;
import ru.nern.config.ConfigFixer;
import ru.nern.config.ConfigManager;
import ru.nern.config.annotations.InRangeInt;
import ru.nern.config.json.JsonConfigManager;
import ru.nern.config.annotations.InRangeLong;
import ru.nern.config.annotations.MaxLength;

import java.util.LinkedHashSet;

import static ru.nern.config.json.JsonConfigUtils.move;

public class ExampleMod {
    public static int CONFIG_VERSION = 5;

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

        fixers.add(new ConfigFixer<>(2) {
            @Override
            public void apply(ExampleConfig config, JsonObject raw) {
                System.out.println("FIXER APPLIED");
                move(raw, "Nested.bcd", "Nested.intu");
                move(raw, "Nested.a", "Nested.longu");
                move(raw, "Nested.i", "Nested.charu");
            }
        });

        fixers.add(new ConfigFixer<>(3) {
            @Override
            public void apply(ExampleConfig config, JsonObject raw) {
                System.out.println("FIXER APPLIED1");

                move(raw, "Nested.intu", "Nested.a");
                move(raw, "Nested.longu", "Nested.b");
                move(raw, "Nested.charu", "Nested.c");
            }
        });

        fixers.add(new ConfigFixer<>(5) {
            @Override
            public void apply(ExampleConfig config, JsonObject raw) {
                System.out.println("FIXER APPLIED2");
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


    public static class ExampleConfig {
        public boolean hello = false;
        @MaxLength(length = 2)
        public String a = "125";
        public Nested Nested = new Nested();

        public static class Nested {
            @InRangeInt(min = 0, max = 125)
            public int a = 121;
            public boolean wented = false;

            @InRangeLong(max = 1998L)
            public long b = 2000L;
            public char c = 'a';
        }
    }
}
