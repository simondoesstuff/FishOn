package com.simondoestuff.fishon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class AntiWaterbucketSurf implements Listener {

    @EventHandler
    public void onBucketPull(PlayerBucketFillEvent e) {
        if (e.getItemStack().getType() != Material.WATER_BUCKET) return;

        final Location loc = e.getBlockClicked().getLocation();
        int found = 0;

        BlockFace[] faces = {
                BlockFace.UP,
                BlockFace.DOWN,
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST,
        };

        for (BlockFace face : faces) {
            Location nearby = loc.clone().add(face.getDirection());
            BlockData data = nearby.getBlock().getBlockData();
            Levelled level = null;

            if (nearby.getBlock().getType() == Material.WATER && data instanceof Levelled) level = (Levelled) data;

            if (nearby.getBlock().getType() == Material.WATER) {
                if (level != null && level.getLevel() == 0) found++;
            } else if (data instanceof Waterlogged && ((Waterlogged) data).isWaterlogged()) found++;

            if (found > 1) return;
        }

        e.getItemStack().setType(Material.BUCKET);
        e.getPlayer().sendMessage(Plugin.prefix + " Your bucket can't be filled by that lonely water source.");
    }
}
