package uk.co.minty_studios.metrocore.commands.discordcommands;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.DiscordCodeDatabase;
import uk.co.minty_studios.metrocore.discord.DiscordLinking;
import org.bukkit.entity.Player;

import java.util.Random;

public class LinkDiscordCommand extends ChildCommand {

    private final DiscordLinking discordLinking;
    private final MetroCore plugin;
    private final DiscordCodeDatabase discordCodeDatabase;

    public LinkDiscordCommand(String command, DiscordLinking discordLinking, MetroCore plugin, DiscordCodeDatabase discordCodeDatabase) {
        super(command);
        this.discordLinking = discordLinking;
        this.plugin = plugin;
        this.discordCodeDatabase = discordCodeDatabase;
    }

    @Override
    public String getPermission() {
        return "metrocore.admin";
    }

    @Override
    public String getDescription() {
        return "Link your Discord!";
    }

    @Override
    public String getSyntax() {
        return "&e/discord link";
    }

    @Override
    public void perform(Player player, String[] args) {

        if(discordCodeDatabase.linkExistsUuid(player)){
            plugin.sendWithPrefix(player, plugin.getConfig().getString("discord.link.already_linked"));
            return;
        }

        if(discordLinking.mapExists(player.getUniqueId())){
            int code = discordLinking.getCode(player.getUniqueId());
            plugin.sendWithPrefix(player, plugin.getConfig().getString("discord.link.currently_linking")
                    .replaceAll("%code%", String.valueOf(code)));
            return;
        }

        int code = 100000 + new Random().nextInt(999999);
        discordLinking.linkPlayer(player, code);

        plugin.sendWithPrefix(player, plugin.getConfig().getString("discord.link.link_message")
                .replace("%code%", String.valueOf(code)));
    }
}
