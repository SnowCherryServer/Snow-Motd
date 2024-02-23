package mc233.fun.snowmotd.Bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigManager {
    private File configFile;
    private Configuration config;

    public ConfigManager(File dataFolder) {
        this.configFile = new File(dataFolder, "bconfig.yml");
    }

    public String getMotd() {
        return this.config.getString("motd");
    }

    public void load() throws IOException {
        if (!this.configFile.exists()) {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("bconfig.yml");
            Files.copy(in, this.configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        }

        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);
    }

    public void save() throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.configFile);
    }

    public Configuration getConfig() {
        return this.config;
    }
}