package mc233.fun.snowmotd;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoReloadConfig {
    private JavaPlugin plugin;
    private ConfigManager configManager;
    private WatchService watchService;
    private ExecutorService executorService;
    private Future<?> watchFuture;

    public AutoReloadConfig(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;

        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            Paths.get(plugin.getDataFolder().getAbsolutePath()).register(this.watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void startWatching() {
        this.watchFuture = this.executorService.submit(() -> {
            label39:
            while(true) {
                WatchKey key;
                try {
                    key = this.watchService.take();
                } catch (InterruptedException var10) {
                    return null;
                }

                Iterator var2 = key.pollEvents().iterator();

                while(true) {
                    String fileName;
                    do {
                        WatchEvent event;
                        do {
                            if (!var2.hasNext()) {
                                boolean valid = key.reset();
                                if (valid) {
                                    continue label39;
                                }

                                return null;
                            }

                            event = (WatchEvent)var2.next();
                        } while(event.kind() != StandardWatchEventKinds.ENTRY_MODIFY);

                        fileName = event.context().toString();
                    } while(!fileName.equals("config.yml") && !fileName.equals("join.yml") && !fileName.equals("paper-config.yml"));

                    long startTime = System.currentTimeMillis();
                    this.configManager.reloadConfig();
                    this.plugin.getLogger().info("Snow-Motd 配置文件已重新加载。");
                    this.plugin.getLogger().info("- 自动重载 -");
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    this.plugin.getLogger().info("总计用时 " + totalTime + "ms");
                    this.configManager.reloadConfig();
                }
            }
        });
    }

    public void stopWatching() {
        if (this.watchFuture != null) {
            this.watchFuture.cancel(true);
        }

        this.executorService.shutdownNow();
    }
}
