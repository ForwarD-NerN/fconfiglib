package ru.nern.config.json;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;
import ru.nern.config.ConfigManager;

import java.io.*;

public class JsonConfigManager<T> extends ConfigManager<T, JsonObject> {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonConfigManager(ConfigManager.Builder<T, JsonObject> builder) {
        super(builder);
    }

    @Override
    public void init() {
        if(getConfigFile().exists()) {
            this.load(null);
        }else {
            this.instance = this.createEmptyConfig();
            this.save(getConfigFile());
        }
    }

    @Override
    public void load(@Nullable JsonObject from) {
        if(from != null) {
            this.instance = gson.fromJson(from, this.type);
            return;
        }

        File file = this.getConfigFile();
        if(!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            this.instance = gson.fromJson(root, this.type);

            JsonElement lastLoadedVersion = root.get("lastLoadedVersion");

            if(lastLoadedVersion == null || !lastLoadedVersion.isJsonPrimitive()) {
                lastLoadedVersion = new JsonPrimitive(1);
            }

            this.validate(lastLoadedVersion.getAsInt(), root);


        } catch (Exception e) {
            System.out.printf("Exception occurred during loading of the %s config. %s%n", this.getModId(), e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void save(File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(getSerializedData()));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JsonObject getSerializedData() {
        JsonObject object = gson.toJsonTree(config()).getAsJsonObject();
        object.addProperty("lastLoadedVersion", getConfigVersion());
        return object;
    }

    public T createEmptyConfig() {
        try {
             return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <A> JsonConfigManager.Builder<A, JsonObject> builderOf(Class<A> clazz) {
        return new Builder<>() {
            @Override
            public ConfigManager<A, JsonObject> create() {
                return new JsonConfigManager<>(this.of(clazz));
            }
        };
    }
}
