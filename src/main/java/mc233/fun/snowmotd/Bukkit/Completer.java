package mc233.fun.snowmotd;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Completer implements TabCompleter {
    public Completer() {
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("snowmotd")) {
            if (args.length == 1) {
                return Arrays.asList("reload", "help", "update");
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("update")) {
                return Arrays.asList("config", "join", "paper-config");
            }
        }

        return null;
    }
}
