package Color_yr.Minecraft_QQ.Side.SideBC;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import Color_yr.Minecraft_QQ.Command.*;

public class CommandBC extends Command implements TabExecutor {

    public CommandBC() {
        super("qq");
    }

    public void execute(CommandSender sender, String[] args) {
        CommandEX.Ex(sender, sender.getName(), args, sender.hasPermission("Minecraft_QQ.admin"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return CommandTab.getList(sender.hasPermission("Minecraft_QQ.admin"), args);
    }
}
