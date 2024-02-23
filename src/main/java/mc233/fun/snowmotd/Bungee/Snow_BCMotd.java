package mc233.fun.snowmotd.Bungee;

import java.io.IOException;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public final class Snow_BCMotd extends Plugin {
    private Logger logger = Logger.getLogger("Snow-BCMotd");
    private ConfigManager configManager;

    public Snow_BCMotd() {
        this.configManager = new ConfigManager(this.getDataFolder());
    }

    public void onEnable() {
        long startTime = System.currentTimeMillis();
        this.logger.info("----------------------------");
        this.logger.info("Snow-Motd 开始加载...");
        this.logger.info("");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        int pluginId = 19979;
        new Metrics(this, pluginId);

        try {
            this.configManager.load();
        } catch (IOException var13) {
            var13.printStackTrace();
            return;
        }

        Configuration config = this.configManager.getConfig();
        String motd = config.getString("motd");
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener(this, motd));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReloadCommand(this));
        String version = this.getDescription().getVersion();
        this.logger.info("当前版本：" + version);
        this.logger.info("");
        String serverVersion = ProxyServer.getInstance().getVersion();
        this.logger.info("服务端核心 " + serverVersion);
        this.logger.info("");
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        this.logger.info("加载完成，所用时间：" + totalTime + "ms");
        this.logger.info("");
        this.logger.info("Snow-Motd 加载完成");
        this.logger.info("");
        this.logger.info("----------------------------");
    }

    public void onDisable() {
        this.logger.info("Snow-Motd 已禁用!");
    }

    public void refresh() {
        try {
            this.configManager.load();
            String motd = this.configManager.getMotd();
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener(this, motd));
        } catch (IOException var2) {
            this.logger.warning("无法刷新配置文件，请确保 bconfig.yml 存在并正确配置。");
        }

    }
}