package uk.co.minty_studios.metrocore.commands.discordcommands;

import org.bukkit.entity.Player;
import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.DiscordCodeDatabase;
import uk.co.minty_studios.metrocore.discord.DiscordLinking;
import uk.co.minty_studios.metrocore.methods.GenericUseMethods;

import java.util.UUID;

public class UnlinkCommand extends ChildCommand {

    private final MetroCore plugin;
    private final DiscordLinking discordLinking;
    private final DiscordCodeDatabase discordCodeDatabase;
    private final GenericUseMethods genericUseMethods;

    public UnlinkCommand(String command, MetroCore plugin,
                            DiscordLinking discordLinking,
                            DiscordCodeDatabase discordCodeDatabase,
                            GenericUseMethods genericUseMethods) {
        super(command);
        this.plugin = plugin;
        this.discordLinking = discordLinking;
        this.discordCodeDatabase = discordCodeDatabase;
        this.genericUseMethods = genericUseMethods;
    }

    @Override
    public String getPermission() {
        return "metrocore.admin";
    }

    @Override
    public String getDescription() {
        return "Unlink a player's Discord.";
    }

    @Override
    public String getSyntax() {
        return "&e/discord unlink <player>";
    }

    @Override
    public void perform(Player player, String[] args) {

        UUID uuid = genericUseMethods.getOnlineUuid(args[1]);

        if(uuid == null){
            plugin.sendWithPrefix(player, "&cError: Player must be online.");
            return;
        }

        discordLinking.removePlayer(uuid);
        discordCodeDatabase.removePreviousLink(uuid);

        plugin.sendWithPrefix(player, plugin.getConfig().getString("discord.link.unlink_message").replace("%player%", args[1]));
    }
}
