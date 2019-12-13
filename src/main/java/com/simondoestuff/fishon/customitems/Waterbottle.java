package com.simondoestuff.fishon.customitems;

import com.simondoestuff.fishon.Plugin;
import com.simondoestuff.fishon.PrimaryBreathingOperations;
import com.simondoestuff.fishon.event.ChangeManager;
import com.simondoestuff.fishon.event.SwimChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.net.http.WebSocket;

public class Waterbottle implements Listener {

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() != Material.POTION) return;

        PotionMeta meta = ((PotionMeta) e.getItem().getItemMeta());
        if (meta != null && meta.getBasePotionData().getType() != PotionType.WATER) return;

        if (ChangeManager.getState(e.getPlayer().getUniqueId()) != ChangeManager.State.SWIMMING) {
            Bukkit.getServer().getPluginManager().callEvent(new SwimChangeEvent(e.getPlayer(), false));
        }
    }
}
