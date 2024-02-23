package mc233.fun.snowmotd.Motds;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import mc233.fun.snowmotd.ConfigManager;
import mc233.fun.snowmotd.Snow_Motd;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;

public class MotdPaper implements Listener {
    private Snow_Motd plugin;
    private List<CachedServerIcon> icons = new ArrayList();
    private ConfigManager configManager;
    private Logger logger = Logger.getLogger("Snow-Motd");
    private File iconsFolder;

    public MotdPaper(Snow_Motd plugin, ConfigManager configManager) {
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
    public void onServerListPing(PaperServerListPingEvent event) {
        FileConfiguration paperconfig = this.configManager.getPaperConfigFile();
        String motd = getMotd();
        motd = applyMotdModifications(motd);
        setEventDetails(event, motd, paperconfig);
        setFavicon(event);
    }

    private String getMotd() {
        FileConfiguration paperconfig = this.configManager.getConfigFile();
        if (paperconfig.getBoolean("random")) {
            List<String> motds = paperconfig.getStringList("motds");
            if (!motds.isEmpty()) {
                int index = new Random().nextInt(motds.size());
                return motds.get(index);
            }
        }
        return paperconfig.getString("motd");
    }

    private String applyMotdModifications(String motd) {
        FileConfiguration paperconfig = this.configManager.getConfigFile();
        motd = formatMotd(motd);
        if (paperconfig.getBoolean("center")) {
            motd = centerMotd(motd);
        }
        return motd;
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

    private String centerMotd(String motd) {
        int totalChars = 80;
        int paddingSize = (totalChars - motd.length()) / 2;
        String padding = String.format("%" + paddingSize + "s", "");
        motd = padding + motd + padding;
        return motd;
    }


    private void setEventDetails(PaperServerListPingEvent event, String motd, FileConfiguration config) {
        event.setMotd(motd);
        setVersion(event, config);
        FileConfiguration configs = this.configManager.getConfigFile();
        event.setMaxPlayers(configs.getInt("max-players"));
        event.setHidePlayers(config.getBoolean("hide-players"));
        setNumPlayers(event, config);
    }


    private void setVersion(PaperServerListPingEvent event, FileConfiguration paperconfig) {
        if (paperconfig.getBoolean("version.enable")) {
            event.setVersion(paperconfig.getString("version.srt"));
        }
    }

    private void setNumPlayers(PaperServerListPingEvent event, FileConfiguration paperconfig) {
        if (paperconfig.getBoolean("online-players.enable")) {
            String mode = paperconfig.getString("online-players.mode");
            int num = paperconfig.getInt("online-players.num");
            int num2 = paperconfig.getInt("online-players.num-2");
            switch (mode) {
                case "set":
                    event.setNumPlayers(num);
                    break;
                case "plus":
                    int realNumPlayers = Bukkit.getOnlinePlayers().size();
                    int displayedNumPlayers = (int)Math.round((double)realNumPlayers * (1.0 + (double)num2 / 100.0));
                    event.setNumPlayers(displayedNumPlayers);
                    break;
                case "random":
                    if (num2 > num) {
                        int randomNumPlayers = (new Random()).nextInt(num2 - num + 1) + num;
                        event.setNumPlayers(randomNumPlayers);
                    } else {
                        this.logger.info("你设置的num-2与num值不正确！");
                    }
            }
        } else {
            event.setNumPlayers(Bukkit.getOnlinePlayers().size());
        }
    }

    private void setFavicon(PaperServerListPingEvent event) {
        FileConfiguration paperconfig1 = this.configManager.getConfigFile();
        String favicon = paperconfig1.getString("favicon");
        if (favicon != null) {
            if (favicon.equalsIgnoreCase("random")) {
                setRandomFavicon(event);
            } else {
                setSpecificFavicon(event, favicon);
            }
        }
    }

    private void setRandomFavicon(PaperServerListPingEvent event) {
        if (!this.icons.isEmpty()) {
            int index = (new Random()).nextInt(this.icons.size());
            CachedServerIcon icon = (CachedServerIcon)this.icons.get(index);
            event.setServerIcon(icon);
        }
    }

    private void setSpecificFavicon(PaperServerListPingEvent event, String favicon) {
        File file = new File(this.iconsFolder, favicon);
        if (file.exists() && file.isFile()) {
            try {
                BufferedImage image = ImageIO.read(file);
                CachedServerIcon icon = Bukkit.loadServerIcon(image);
                event.setServerIcon(icon);
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }
    }

}
