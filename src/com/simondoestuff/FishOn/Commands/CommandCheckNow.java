package com.simondoestuff.FishOn.Commands;

import com.simondoestuff.FishOn.Event.ChangeManager;
import com.simondoestuff.FishOn.Event.SwimTrigger;
import com.simondoestuff.FishOn.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandCheckNow implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Main.prefix + " §4Did not specify the correct number of arguments.");
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

            if (caught == 0) sender.sendMessage(Main.prefix + " All players are as expected.");
            else
                sender.sendMessage(Main.prefix + " §6§l" + caught + ((caught == 1) ? " §eplayer was" : " §eplayers were") + " caught.");
        } else {
            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(Main.prefix + " §4Player not found.");
                return true;
            }

            ChangeManager.State before = ChangeManager.getState(player.getUniqueId());

            ChangeManager.CheckState current = (SwimTrigger.isSafeBlock(player.getLocation().getBlock())) ? ChangeManager.CheckState.WATER : ChangeManager.CheckState.LAND;
            ChangeManager.inform(player.getUniqueId(), current);

            ChangeManager.State after = ChangeManager.getState(player.getUniqueId());

            if (before != after) {
                sender.sendMessage(Main.prefix + " Currently " + ((after == ChangeManager.State.SWIMMING) ? "in: §3Water§e" : "on: §2Land§e") + " to my §csurprise.");
                return true;
            } else
                sender.sendMessage(Main.prefix + " Currently " + ((after == ChangeManager.State.SWIMMING) ? "in: §3Water§e" : "on: §2Land§e") + " as §aexpected.");
        }

        return true;
    }
}
