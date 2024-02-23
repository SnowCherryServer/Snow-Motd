
package mc233.fun.snowmotd.Motds;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ColorManager {
    private final File file;
    private YamlConfiguration config;

    public ColorManager(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "colors.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (!this.file.exists()) {
            this.saveDefaultColors();
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public String translateNamedColorCodes(String message) {
        ConfigurationSection colorSection = this.config.getConfigurationSection("colors");
        for (String key : colorSection.getKeys(false)) {
            String value = colorSection.getString(key);
            message = message.replaceAll("\\{#" + key + "(>|<)?\\}", "{#" + value + "$1}");
        }
        return message;
    }

    private void saveDefaultColors() {
        Map<String, String> defaultColors = getDefaultColors();
        for (Map.Entry<String, String> entry : defaultColors.entrySet()) {
            this.config.set("colors." + entry.getKey(), entry.getValue());
        }
        try {
            this.config.save(this.file);
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    private Map<String, String> getDefaultColors() {
        Map<String, String> defaultColors = new HashMap<>();
        defaultColors.put("red", "FF0000");
        defaultColors.put("yellow", "FFFF00");
        defaultColors.put("blue", "0000FF");
        defaultColors.put("pink", "FFC0CB");
        defaultColors.put("green", "008000");
        defaultColors.put("orange", "FFA500");
        defaultColors.put("purple", "800080");
        defaultColors.put("brown", "A52A2A");
        defaultColors.put("black", "000000");
        defaultColors.put("white", "FFFFFF");
        defaultColors.put("gray", "808080");
        defaultColors.put("lightblue", "ADD8E6");
        defaultColors.put("lightgreen", "90EE90");
        defaultColors.put("lightpink", "FFB6C1");
        defaultColors.put("lightsalmon", "FFA07A");
        defaultColors.put("lightseagreen", "20B2AA");
        defaultColors.put("lightskyblue", "87CEFA");
        defaultColors.put("lightslategray", "778899");
        defaultColors.put("红色", "FF0000");
        defaultColors.put("黄色", "FFFF00");
        defaultColors.put("蓝色", "0000FF");
        defaultColors.put("粉色", "FFC0CB");
        defaultColors.put("淡绿色", "90EE90");
        defaultColors.put("绿色", "008000");
        defaultColors.put("橙色", "FFA500");
        return defaultColors;
    }
}
