package com.simondoestuff.fishon.Commands;

import com.simondoestuff.fishon.Plugin;
import com.simondoestuff.fishon.event.ChangeManager;
import com.simondoestuff.fishon.event.SwimTrigger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCheckNow implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Plugin.prefix + " §4Did not specify the correct number of arguments.");
            return false;
        }

        if (args[0].equals("*")) {
            int caught = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                ChangeManager.State before = ChangeManager.getState(player.getUniqueId());

                ChangeManager.CheckState current = (SwimTrigger.isSafeBlock(player.getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND;
                ChangeManager.inform(player.getUniqueId(), current);

                ChangeManager.State after = ChangeManager.getState(player.getUniqueId());

                if (before != after) caught++;
            }

            if (caught == 0) sender.sendMessage(Plugin.prefix + " All players are as expected.");
            else
                sender.sendMessage(Plugin.prefix + " §6§l" + caught + ((caught == 1) ? " §eplayer was" : " §eplayers were") + " caught.");
        } else {
            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(Plugin.prefix + " §4Player not found.");
                return true;
            }

            ChangeManager.State before = ChangeManager.getState(player.getUniqueId());

            ChangeManager.CheckState current = (SwimTrigger.isSafeBlock(player.getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND;
            ChangeManager.inform(player.getUniqueId(), current);

            ChangeManager.State after = ChangeManager.getState(player.getUniqueId());

            if (before != after) {
                sender.sendMessage(Plugin.prefix + " Currently " + ((after == ChangeManager.State.SWIMMING) ? "in: §3Water§e" : "on: §2Land§e") + " to my §csurprise.");
                return true;
            } else
                sender.sendMessage(Plugin.prefix + " Currently " + ((after == ChangeManager.State.SWIMMING) ? "in: §3Water§e" : "on: §2Land§e") + " as §aexpected.");
        }

        return true;
    }
}
