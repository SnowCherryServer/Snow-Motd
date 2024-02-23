package mc233.fun.snowmotd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private JavaPlugin plugin;
    private FileConfiguration configFile;
    private FileConfiguration joinFile;
    private FileConfiguration paperConfigFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.setup();
    }

    private void setup() {
        File config = new File(this.plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            this.plugin.saveResource("config.yml", false);
        }

        this.configFile = YamlConfiguration.loadConfiguration(config);
        this.updateConfig(config, "config");
        File join = new File(this.plugin.getDataFolder(), "join.yml");
        if (!join.exists()) {
            this.plugin.saveResource("join.yml", false);
        }

        this.joinFile = YamlConfiguration.loadConfiguration(join);
        this.updateConfig(join, "join");
        if (CheckPaper.isPaper()) {
            File paperConfig = new File(this.plugin.getDataFolder(), "paper-config.yml");
            if (!paperConfig.exists()) {
                this.plugin.saveResource("paper-config.yml", false);
            }

            this.paperConfigFile = YamlConfiguration.loadConfiguration(paperConfig);
            this.updateConfig(paperConfig, "paper-config");
        }

    }

    private void updateConfig(File file, String name) {
        String version = this.configFile.getString("version");
        if (version == null || !version.equals(this.plugin.getDescription().getVersion())) {
            try {
                Files.copy(file.toPath(), (new File(this.plugin.getDataFolder(), name + "_old.yml")).toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.plugin.saveResource(name + ".yml", true);
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

    }

    public FileConfiguration getConfigFile() {
        return this.configFile;
    }

    public FileConfiguration getJoinFile() {
        return this.joinFile;
    }

    public FileConfiguration getPaperConfigFile() {
        return this.paperConfigFile;
    }

    public void saveConfig() {
        try {
            this.configFile.save(new File(this.plugin.getDataFolder(), "config.yml"));
            this.joinFile.save(new File(this.plugin.getDataFolder(), "join.yml"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void reloadConfig() {
        this.configFile = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "config.yml"));
        this.joinFile = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "join.yml"));
        this.paperConfigFile = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "paper-config.yml"));
    }

    public void updateConfigFromJar(String name) {
        File oldConfig = new File(this.plugin.getDataFolder(), name + ".yml");
        File oldConfigRenamed = new File(this.plugin.getDataFolder(), name + "_old.yml");
        if (oldConfigRenamed.exists()) {
            oldConfigRenamed.delete();
        }

        oldConfig.renameTo(oldConfigRenamed);
        this.plugin.saveResource(name + ".yml", false);
    }
}
