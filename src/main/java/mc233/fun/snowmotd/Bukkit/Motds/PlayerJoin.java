package mc233.fun.snowmotd.Motds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import mc233.fun.snowmotd.ConfigManager;
import mc233.fun.snowmotd.Snow_Motd;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoin implements Listener {
    private Plugin plugin;
    private Snow_Motd plugins;
    private ConfigManager configManager;

    public PlayerJoin(Plugin plugin, Snow_Motd plugins, ConfigManager configManager) {
        this.plugin = plugin;
        this.plugins = plugins;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration joinConfig = this.configManager.getJoinFile();
        Player player = event.getPlayer();
        String playerName = player.getName();
        boolean join = joinConfig.getBoolean("enable.join");
        if (join) {
            event.setJoinMessage("");
            String currentTime = getCurrentTime();
            boolean order = joinConfig.getBoolean("order");
            if (!player.hasPlayedBefore()) {
                processMessages(joinConfig.getStringList("FirstJoin"), joinConfig.getString("Message.First"), player, playerName, currentTime, order);
            } else {
                processMessages(joinConfig.getStringList("Join"), joinConfig.getString("Message.Join"), player, playerName, currentTime, order);
            }
        }
    }


    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date());
    }

    private void processMessages(List<String> messages, String message, Player player, String playerName, String currentTime, boolean order) {
        String formattedMessage = null;
        if (message != null) {
            formattedMessage = formatMessage(message, player, playerName, currentTime);
        }
        if (order) {
            if (formattedMessage != null) {
                Bukkit.broadcastMessage(formattedMessage);
            }
            for (String msg : messages) {
                player.sendMessage(formatMessage(msg, player, playerName, currentTime));
            }
        } else {
            for (String msg : messages) {
                player.sendMessage(formatMessage(msg, player, playerName, currentTime));
            }
            if (formattedMessage != null) {
                Bukkit.broadcastMessage(formattedMessage);
            }
        }
    }

    private String formatMessage(String message, Player player, String playerName, String currentTime) {
        message = message.replace("{player}", playerName);
        message = message.replace("{time}", currentTime);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = plugins.colorManager.translateNamedColorCodes(message);
        message = Color.applyGradients(message);
        return Color.translateHexColorCodes(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        FileConfiguration joinConfig = this.configManager.getJoinFile();
        boolean quit = joinConfig.getBoolean("enable.quit");
        if (quit) {
            event.setQuitMessage("");
            String leaveMessage = joinConfig.getString("Message.Leave");
            if (leaveMessage != null) {
                leaveMessage = formatMessage(leaveMessage, player, playerName, "");
                Bukkit.broadcastMessage(leaveMessage);
            }
        }
    }

}
