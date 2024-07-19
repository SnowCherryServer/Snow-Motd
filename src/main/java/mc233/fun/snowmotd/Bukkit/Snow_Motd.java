package mc233.fun.snowmotd.Bukkit;

import mc233.fun.snowmotd.Bukkit.Motds.ColorManager;
import mc233.fun.snowmotd.Bukkit.Motds.Motd;
import mc233.fun.snowmotd.Bukkit.Motds.MotdPaper;
import mc233.fun.snowmotd.Bukkit.Motds.PlayerJoin;
import mc233.fun.snowmotd.Bukkit.util.PluginUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public final class Snow_Motd extends JavaPlugin {
    public ColorManager colorManager;
    private Logger logger = Logger.getLogger("Snow-Motd");
    private boolean hasWarned = false;
    private ConfigManager configManager;
    private volatile boolean isRunning = true;
    private List<BukkitTask> tasks = new ArrayList();
    private static String latestVersion = null;
    private String currentVersion = this.getDescription().getVersion();
    private static Logger logger1 = Logger.getLogger("SnowMotd-CheckUpdate");
    private AutoReloadConfig autoReloadConfig;

    public Snow_Motd() {
    }

    public void onDisable() {
        cancelTasksAndUnregister();
        this.getCommand("snowmotd").setExecutor((CommandExecutor)null);
        logInfo("Snow-Motd 已禁用!");
        if (this.autoReloadConfig != null) {
            this.autoReloadConfig.stopWatching();
        }
    }

    public void onEnable() {
        long startTime = System.currentTimeMillis();
        logInfo("----------------------------");
        logInfo("Snow-Motd 开始加载...");
        PluginUtil.check();
        checkPlaceholderAPI();
        initializeConfigManager();
        checkServerVersionAndRegisterEvents();
        // 我是傻子，上个版本忘记注册了
        PlayerJoin playerJoinListener = new PlayerJoin(this, this, this.configManager);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, this);
        logInfo("当前版本：" + this.getDescription().getVersion());
        logInfo("");
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logInfo("加载完成，所用时间：" + totalTime + "ms");
        logInfo("");
        logInfo("Snow-Motd 完成加载");
        logInfo("");
        logInfo("----------------------------");
    }


    private void checkServerVersionAndRegisterEvents() {
        String serverVersion = Bukkit.getVersion();
        logInfo("本服务器使用核心 " + serverVersion);
        if (CheckPaper.isPaper()) {
            this.getServer().getPluginManager().registerEvents(new MotdPaper(this, this.configManager), this);
            logInfo("");
            logInfo("本服务端支持 Paper API 您可以配置Paper核心特有功能");
        } else {
            this.getServer().getPluginManager().registerEvents(new Motd(this, this.configManager), this);
            if (!this.hasWarned) {
                logInfo("");
                this.logger.warning("----------");
                this.logger.warning("请注意，你的服务端不支持 Paper API 您可能正在使用Spigot或Bukkit");
                this.logger.warning("我们推荐您使用 Paper 或 Purpur 以获得更好的功能");
                this.logger.warning("若您无法更换核心，请不必更换，这不是必须的，您可以忽视这条提示");
                this.logger.warning("----------");
                this.hasWarned = true;
            }
        }
    }

    private void cancelTasksAndUnregister() {
        Bukkit.getScheduler().cancelTasks(this);
        Iterator var1 = this.tasks.iterator();

        while(var1.hasNext()) {
            BukkitTask task = (BukkitTask)var1.next();
            task.cancel();
        }

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void logInfo(String message) {
        this.logger.info(message);
    }

    private void checkPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logInfo("");
            this.logger.warning("未检测到 PlaceholderAPI 插件。请安装 PlaceholderAPI 以获得最佳体验。");
        }
    }

    private void initializeConfigManager() {
        this.configManager = new ConfigManager(this);
        FileConfiguration config = this.configManager.getConfigFile();
        FileConfiguration join = this.configManager.getJoinFile();
        this.colorManager = new ColorManager(this);
        this.autoReloadConfig = new AutoReloadConfig(this, this.configManager);
        this.autoReloadConfig.startWatching();
        this.getCommand("snowmotd").setTabCompleter(new Completer());
        this.getCommand("snowmotd").setExecutor(new SnowMotdCommand(this, this.configManager));
        int pluginId = 18373;
        new Metrics(this, pluginId);
        this.checkForUpdates();
    }

    public void checkForUpdates() {
        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            logger1.info("开始检查是否有新版更新");
            try {
                latestVersion = getLatestVersion();
                if (!latestVersion.equals(this.currentVersion)) {
                    logger1.info("发现新版本：" + latestVersion + "，当前版本：" + this.currentVersion);
                    String announcement = getAnnouncement();
                    logger1.info(announcement);
                } else {
                    logger1.info("当前版本：" + this.currentVersion + "已是最新版本");
                }
            } catch (IOException var8) {
                logger1.warning("检查更新时出现错误：" + var8.getMessage());
            }
        });
        this.tasks.add(task);
    }

    private String getAnnouncement() throws IOException {
        URL url = new URL("https://gh.api.99988866.xyz/https://raw.githubusercontent.com/HaoServer/Snow-Motd-Update/master/announcement.txt");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder announcement = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            announcement.append(line).append("\n");
        }
        in.close();
        connection.disconnect();
        return announcement.toString();
    }


    private String getLatestVersion() throws IOException {
        URL url = new URL("https://gh.api.99988866.xyz/https://raw.githubusercontent.com/HaoServer/Snow-Motd-Update/master/version.txt");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String latestVersion = in.readLine();
        in.close();
        connection.disconnect();
        return latestVersion;
    }

    private void scheduleUpdateChecks() {
        boolean enableUpdateCheck = this.getConfig().getBoolean("update.enable");
        if (enableUpdateCheck) {
            long delay = 100L;
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (this.isRunning) {
                    this.checkForUpdates();
                    boolean enableAutoUpdateCheck = this.getConfig().getBoolean("update.autocheck");
                    if (enableAutoUpdateCheck) {
                        String autoUpdateCheckTime = this.getConfig().getString("update.autochecktime");
                        long interval = parseInterval(autoUpdateCheckTime);
                        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
                            if (this.isRunning) {
                                this.checkForUpdates();
                            }
                        }, interval, interval);
                        this.tasks.add(task);
                    }
                }
            }, delay);
        }
    }

    private long parseInterval(String autoUpdateCheckTime) {
        long interval = 0L;
        if (autoUpdateCheckTime.endsWith("h")) {
            interval = Long.parseLong(autoUpdateCheckTime.substring(0, autoUpdateCheckTime.length() - 1)) * 72000L;
        } else if (autoUpdateCheckTime.endsWith("m")) {
            interval = Long.parseLong(autoUpdateCheckTime.substring(0, autoUpdateCheckTime.length() - 1)) * 1200L;
        } else if (autoUpdateCheckTime.endsWith("s")) {
            interval = Long.parseLong(autoUpdateCheckTime.substring(0, autoUpdateCheckTime.length() - 1)) * 20L;
        }
        return interval;
    }

}
