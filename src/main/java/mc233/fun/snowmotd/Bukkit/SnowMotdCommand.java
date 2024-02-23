package mc233.fun.snowmotd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SnowMotdCommand implements CommandExecutor {
    private Snow_Motd plugin;
    private ConfigManager configManager;

    public SnowMotdCommand(Snow_Motd plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (!sender.hasPermission("snowmotd.reload")) {
                        sender.sendMessage(ChatColor.RED + "您没有权限执行此命令。");
                        return true;
                    }

                    long startTime = System.currentTimeMillis();
                    this.configManager.reloadConfig();
                    this.plugin.colorManager.reload();
                    sender.sendMessage(ChatColor.GREEN + "Snow-Motd 配置文件已重新加载。");
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    sender.sendMessage(ChatColor.GREEN + "总计用时 " + totalTime + "ms");
                    return true;
                case "help":
                    if (!sender.hasPermission("snowmotd.help")) {
                        sender.sendMessage(ChatColor.RED + "您没有权限执行此命令。");
                        return true;
                    }

                    sender.sendMessage(ChatColor.GREEN + "Snow-Motd - 指令帮助");
                    sender.sendMessage(ChatColor.GREEN + "/snowmotd help - 打开帮助 - snowmotd.help");
                    sender.sendMessage(ChatColor.GREEN + "/snowmotd reload - 刷新插件 - snowmotd.reload");
                    sender.sendMessage(ChatColor.GREEN + "/snowmotd update - 更新插件配置文件 - snowmotd.update");
                    return true;
                case "update":
                    if (!sender.hasPermission("snowmotd.update")) {
                        sender.sendMessage(ChatColor.RED + "对不起，您没有权限执行此命令。");
                    } else if (args.length > 1) {
                        String configName = args[1];

                        try {
                            this.configManager.updateConfigFromJar(configName);
                            sender.sendMessage(ChatColor.GREEN + "Snow-Motd " + configName + " 配置文件已更新。");
                        } catch (Exception var15) {
                            sender.sendMessage(ChatColor.RED + "更新配置文件时出错。请检查您的插件设置。");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "请输入要更新的配置文件名。例如：/snowmotd update config/join/paper-config");
                    }

                    return true;
                default:
                    sender.sendMessage(ChatColor.RED + "未知命令。请使用 /snowmotd help 查看帮助。");
                    return true;
            }
        } else {
            return false;
        }
    }
}
