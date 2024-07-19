package mc233.fun.snowmotd.Bukkit.Motds;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import mc233.fun.snowmotd.Bukkit.ConfigManager;
import mc233.fun.snowmotd.Bukkit.Snow_Motd;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

public class Motd implements Listener {
    private Snow_Motd plugin;
    private ConfigManager configManager;
    private List<CachedServerIcon> icons = new ArrayList();
    private File iconsFolder;

    public Motd(Snow_Motd plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.iconsFolder = new File(plugin.getDataFolder(), "icons");
        this.iconsFolder.mkdirs();
        this.loadIcons();
    }

    private void loadIcons() {
        if (this.iconsFolder.exists() && this.iconsFolder.isDirectory()) {
            File[] files = this.iconsFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));

            for (File file : files) {
                try (InputStream is = new FileInputStream(file)) {
                    BufferedImage image = ImageIO.read(is);
                    CachedServerIcon icon = Bukkit.loadServerIcon(image);
                    this.icons.add(icon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        FileConfiguration config = this.configManager.getConfigFile();
        String motd = getMotd(config);
        motd = formatMotd(motd);
        setCenteredMotd(config, motd, event);
        event.setMaxPlayers(config.getInt("max-players"));
        setFavicon(config, event);
    }
    private String getMotd(FileConfiguration config) {
        if (config.getBoolean("random")) {
            List<String> motds = config.getStringList("motds");
            int randomIndex = new Random().nextInt(motds.size());
            return motds.get(randomIndex);
        } else {
            return config.getString("motd");
        }
    }

    private String formatMotd(String motd) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            motd = PlaceholderAPI.setPlaceholders(null, motd);
        }
        motd = this.plugin.colorManager.translateNamedColorCodes(motd);
        motd = Color.applyGradients(motd);
        motd = ChatColor.translateAlternateColorCodes('&', motd);
        return Color.translateHexColorCodes(motd);
    }

    private void setCenteredMotd(FileConfiguration config, String motd, ServerListPingEvent event) {
        if (config.getBoolean("center")) {
            int totalChars = 80;
            int paddingSize = (totalChars - motd.length()) / 2;
            String padding = String.format("%" + paddingSize + "s", "");
            motd = padding + motd + padding;
        }
        event.setMotd(motd);
    }


    private void setFavicon(FileConfiguration config, ServerListPingEvent event) {
        String favicon = config.getString("favicon");
        if (favicon != null) {
            if (favicon.equalsIgnoreCase("random")) {
                if (!this.icons.isEmpty()) {
                    int index = new Random().nextInt(this.icons.size());
                    CachedServerIcon icon = this.icons.get(index);
                    event.setServerIcon(icon);
                }
            } else {
                File file = new File(this.iconsFolder, favicon);
                if (file.exists() && file.isFile()) {
                    try {
                        BufferedImage image = ImageIO.read(file);
                        CachedServerIcon icon = Bukkit.loadServerIcon(image);
                        event.setServerIcon(icon);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
