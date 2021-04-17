package uk.co.minty_studios.metrocore.commands.modcommands;

import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.methods.ModMode;
import org.bukkit.entity.Player;

public class ModOffCommand extends ChildCommand {

    private final ModMode modMode;

    public ModOffCommand(String command, ModMode modMode) {
        super(command);
        this.modMode = modMode;
    }

    @Override
    public String getPermission(){ return "metrocore.mod"; }

    @Override
    public String getDescription() {
        return "Turn mod mode off!";
    }

    @Override
    public String getSyntax() {
        return "&e/mod off";
    }

    @Override
    public void perform(Player player, String[] args) {
        modMode.disableModMode(player);
    }
}
