package uk.co.minty_studios.metrocore.commands.bugreport;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.BugReportsDatabase;
import org.bukkit.entity.Player;

public class ListCommand extends ChildCommand {

    private final MetroCore plugin;
    private final BugReportsDatabase bugReportsDatabase;

    public ListCommand(String command, MetroCore plugin, BugReportsDatabase bugReportsDatabase) {
        super(command);
        this.bugReportsDatabase = bugReportsDatabase;
        this.plugin = plugin;
    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "List all bug reports";
    }

    @Override
    public String getSyntax() {
        return "&e/bugreport list";
    }

    @Override
    public void perform(Player player, String[] args) {

        bugReportsDatabase.listBugs(player);
    }
}
