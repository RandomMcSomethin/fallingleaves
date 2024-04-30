package randommcsomethin.fallingleaves.config.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonConfigHelper {
    private final Path configPath;
    private final Gson gson;

    public GsonConfigHelper(String configName) {
        this(configName, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()); // note: not using IdentifierTypeAdapter
    }

    public GsonConfigHelper(String configName, Gson gson) {
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(configName + ".json");
        this.gson = gson;
    }

    public boolean exists() {
        return Files.exists(configPath);
    }

    public <T> T load(Class<T> configType) throws IOException, JsonParseException {
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            T config = gson.fromJson(reader, configType);
            if (config == null)
                throw new JsonParseException("config file is empty");
            return config;
        }
    }

    public void save(Object config) throws IOException, JsonIOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            gson.toJson(config, writer);
        }
    }
}
