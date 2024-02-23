package mc233.fun.snowmotd.Bungee;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {
    private Logger logger = Logger.getLogger("Snow-BCMotd");
    private Plugin plugin;
    private String motd;

    public PingListener(Plugin plugin, String motd) {
        this.plugin = plugin;
        this.motd = motd;
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) throws IOException {
        File configFile = new File(this.plugin.getDataFolder(), "bconfig.yml");
        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        String motd = config.getString("motd");
        String motdWithGradients = Color.applyGradients(motd);
        String motdWithColorCodes = Color.translateHexColorCodes(motdWithGradients);
        String motds = ChatColor.translateAlternateColorCodes('&', motdWithColorCodes);
        event.getResponse().setDescription(motds);
        int maxPlayers = config.getInt("max-players");
        event.getResponse().getPlayers().setMax(maxPlayers);
        boolean versionEnabled = config.getBoolean("version.enable");
        if (versionEnabled) {
            String versionText = ChatColor.translateAlternateColorCodes('&', config.getString("version.text"));
            event.getResponse().setVersion(new ServerPing.Protocol(versionText, event.getResponse().getVersion().getProtocol()));
        }

        boolean onlineEnabled = config.getBoolean("online.enable");
        if (onlineEnabled) {
            String mode = config.getString("online.mode");
            int num1 = config.getInt("online.num-1");
            int num2 = config.getInt("online.num-2");
            int onlinePlayers;
            switch (mode) {
                case "num":
                    onlinePlayers = num1;
                    break;
                case "plus":
                    onlinePlayers = (int)((double)event.getResponse().getPlayers().getOnline() * (1.0 + (double)num2 / 100.0));
                    break;
                case "random":
                    if (num1 - num2 + 1 <= 0) {
                        this.logger.warning("无效的在线玩家数量设置：num-2 必须小于 num-1。");
                        return;
                    }

                    onlinePlayers = (new Random()).nextInt(num1 - num2 + 1) + num2;
                    break;
                default:
                    onlinePlayers = event.getResponse().getPlayers().getOnline();
            }

            event.getResponse().getPlayers().setOnline(onlinePlayers);
        }

    }
}
