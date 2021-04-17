package uk.co.minty_studios.metrocore.commands.modcommands;

import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.methods.ModMode;
import org.bukkit.entity.Player;

public class ModOnCommand extends ChildCommand {

    private final ModMode modMode;

    public ModOnCommand(String command, ModMode modMode) {
        super(command);
        this.modMode = modMode;
    }

    @Override
    public String getPermission(){ return "metrocore.mod"; }

    @Override
    public String getDescription() {
        return "Turn mod mode on!";
    }

    @Override
    public String getSyntax() {
        return "&e/mod on";
    }

    @Override
    public void perform(Player player, String[] args) {
        modMode.enableModMode(player);
    }
}
