//package com.simondoestuff.fishon.customitems;
//
//import com.codingforcookies.armorequip.ArmorEquipEvent;
//import com.codingforcookies.armorequip.ArmorListener;
//import com.codingforcookies.armorequip.DispenserArmorListener;
//import com.simondoestuff.fishon.Plugin;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.inventory.InventoryInteractEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.PlayerInventory;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.concurrent.ThreadLocalRandom;
//
//public class Tank implements Listener {
//    private double threshold = Math.pow(10, Math.log(.5) / (3*30));
//    private HashSet<Player> users = new HashSet<>() {
//        @Override
//        public boolean add(Player player) {
//            player.sendMessage(Plugin.prefix + " you have equipped your Tank.");
//            return super.add(player);
//        }
//    };
//
//    private BukkitRunnable tankMaintenance = new BukkitRunnable() {
//        @Override
//        public void run() {
//            for (Player user : users) {
//                double num = ThreadLocalRandom.current().nextDouble(1);
//                if (num < threshold) shatterTank(user);
//                else {
//                    user.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 20, 1, true, true), true);
//                }
//            }
//        }
//    };
//
////    public Tank() {
////        Plugin.registerEvents(new ArmorListener(new ArrayList<>()));
////        Plugin.registerEvents(new DispenserArmorListener());
////    }
//
//    public void shatterTank(Player p) {
//        p.sendMessage(Plugin.prefix + " Your Tank has been shattered!");
//
//        ItemStack shatteredHelm = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, ThreadLocalRandom.current().nextInt(1, 3));
//        ItemMeta meta = shatteredHelm.getItemMeta();
//        meta.setDisplayName("§3§oShattered Tank Remains");
//        shatteredHelm.setItemMeta(meta);
//
//        p.getInventory().setHelmet(shatteredHelm);
//    }
//
//    public boolean isTank(ItemStack stack) {
//        Bukkit.broadcastMessage(stack.getItemMeta().getDisplayName().replaceAll("§", "&"));
//        if (stack.getType() != Material.GLASS) {
//            if (stack.getItemMeta() == null || !stack.getItemMeta().getDisplayName().equals("yeet")) return false;
//        }
//
//        return true;
//    }
//
//    @EventHandler
//    public void onEquip(PlayerInteractEvent e) {
//        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
//        if (e.getItem() == null) return;
//
//        if (isTank(e.getItem())) {
//            if (users.isEmpty()) tankMaintenance.runTaskTimer(Plugin.getInstance(), 0, 400);
//
//            users.add( e.getPlayer());
//        } else {
//            users.add(e.getPlayer());
//
//            if (users.isEmpty()) tankMaintenance.cancel();
//        }
//    }
//}
