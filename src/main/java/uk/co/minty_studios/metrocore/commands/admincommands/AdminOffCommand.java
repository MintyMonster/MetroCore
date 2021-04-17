package uk.co.minty_studios.metrocore.commands.admincommands;

import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.methods.AdminMode;
import org.bukkit.entity.Player;

public class AdminOffCommand extends ChildCommand {

    private final AdminMode adminMode;

    public AdminOffCommand(String command, AdminMode adminMode) {
        super(command);
        this.adminMode = adminMode;
    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Turn admin mode off!";
    }

    @Override
    public String getSyntax() {
        return "&e/admin off";
    }

    @Override
    public void perform(Player player, String[] args) {
        adminMode.disableAdminMode(player);
    }
}
