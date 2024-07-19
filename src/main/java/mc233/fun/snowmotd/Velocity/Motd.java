package mc233.fun.snowmotd.Velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Motd {

    private final Snow_VCMotd plugin;

    public Motd(Snow_VCMotd plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        ServerPing originalPing = event.getPing();
        ServerPing.Builder pingBuilder = originalPing.asBuilder();

        // 获取配置文件中的 MOTD
        String motd = plugin.getConfig().getProperty("motd");

        // 使用 MiniMessage 解析 MOTD
        Component motdComponent = MiniMessage.miniMessage().deserialize(motd);

        // 设置新的 MOTD
        pingBuilder.description(motdComponent);

        // 更新 ping
        event.setPing(pingBuilder.build());
    }
}
