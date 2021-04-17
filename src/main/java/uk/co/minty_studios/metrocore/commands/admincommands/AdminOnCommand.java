package uk.co.minty_studios.metrocore.commands.admincommands;

import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.methods.AdminMode;
import org.bukkit.entity.Player;

public class AdminOnCommand extends ChildCommand {

    private final AdminMode adminMode;

    public AdminOnCommand(String command, AdminMode adminMode) {
        super(command);
        this.adminMode = adminMode;
    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Turn admin mode on!";
    }

    @Override
    public String getSyntax() {
        return "&e/admin on";
    }

    @Override
    public void perform(Player player, String[] args) {

        adminMode.enableAdminMode(player);
    }
}
