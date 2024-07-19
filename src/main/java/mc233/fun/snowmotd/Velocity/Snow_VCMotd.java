package mc233.fun.snowmotd.Velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

@Plugin(
        id = "snow-vcmotd",
        name = "Snow-VCMotd",
        version = "1.0-Dev",
        authors = {"mincHR549"}
)

public class Snow_VCMotd {

    private final ProxyServer server;
    private final Logger logger;
    private Properties config = new Properties();

    @Inject
    public Snow_VCMotd(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        long startTime = System.currentTimeMillis();
        logger.info("----------------------------");
        logger.info("Snow-Motd 开始加载...");

        // 配置文件的路径
        Path configDir = Paths.get("plugins/snow-vcmotd");
        Path configPath = configDir.resolve("vconfig.yml");

        // 如果配置文件不存在，则从 JAR 包中释放
        if (!Files.exists(configPath)) {
            if (!Files.exists(configDir)) {
                try {
                    Files.createDirectories(configDir);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            try (InputStream input = getClass().getClassLoader().getResourceAsStream("vconfig.yml")) {
                if (input != null) {
                    Files.copy(input, configPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    logger.error("无法加载配置文件 vconfig.yml");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // 加载配置文件
        try (InputStream input = new FileInputStream(configPath.toFile())) {
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        server.getEventManager().register(this, new Motd(this));
        logger.info("当前版本：" + server.getVersion().getVersion());
        logger.info("");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        logger.info("加载完成，所用时间：" + totalTime + "ms");
        logger.info("");
        logger.info("Snow-Motd 完成加载");
        logger.info("");
        logger.info("----------------------------");
    }

    public Properties getConfig() {
        return this.config;
    }

}
