package mc233.fun.snowmotd.Bungee;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {
    private Snow_BCMotd plugin;

    public ReloadCommand(Snow_BCMotd plugin) {
        super("snowmotd");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            this.plugin.refresh();
            sender.sendMessage("Snow-Motd 配置已刷新！");
        } else {
            sender.sendMessage("未知命令。请使用 /snowmotd reload 来刷新配置文件。");
        }

    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList();
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
            completions.add("reload");
        }

        return completions;
    }
}
