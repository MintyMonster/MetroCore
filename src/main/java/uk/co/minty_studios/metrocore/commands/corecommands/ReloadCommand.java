package uk.co.minty_studios.metrocore.commands.corecommands;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import org.bukkit.entity.Player;

public class ReloadCommand extends ChildCommand {

    private final MetroCore plugin;

    public ReloadCommand(String command, MetroCore plugin) {
        super(command);
        this.plugin = plugin;
    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Reload command for MetroCore";
    }

    @Override
    public String getSyntax() {
        return "&e/mcore reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        try{
            plugin.reloadConfig();
            plugin.sendWithPrefix(player, "&aReload complete.");
        }catch(Exception e){
            e.printStackTrace();
            plugin.sendWithPrefix(player, "&cSomething went wrong with the reload. Check logs for error.");
        }
    }
}
