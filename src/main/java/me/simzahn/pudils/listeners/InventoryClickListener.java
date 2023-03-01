package me.simzahn.pudils.listeners;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.util.Difficulty;
import me.simzahn.pudils.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if(event.getView().getTitle().equals("§4§lDifficulties")) {
            event.setCancelled(true);

            if(event.getSlot() == 20) {
                Main.setDifficulty(Difficulty.IMMORTABLE);
                for(int i = 20; i <= 24; i++) {
                    event.getInventory().setItem(i, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayname("§4Not Selected").setLore("","§8Click to select", "").save());
                }
                event.getInventory().setItem(20, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
            }else if(event.getSlot() == 21) {
                Main.setDifficulty(Difficulty.NORMAl);
                for(int i = 20; i <= 24; i++) {
                    event.getInventory().setItem(i, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayname("§4Not Selected").setLore("","§8Click to select", "").save());
                }
                event.getInventory().setItem(21, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
            }else if(event.getSlot() == 22) {
                Main.setDifficulty(Difficulty.UHC);
                for(int i = 20; i <= 24; i++) {
                    event.getInventory().setItem(i, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayname("§4Not Selected").setLore("","§8Click to select", "").save());
                }
                event.getInventory().setItem(22, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
            }else if(event.getSlot() == 23) {
                Main.setDifficulty(Difficulty.UUHC);
                for(int i = 20; i <= 24; i++) {
                    event.getInventory().setItem(i, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayname("§4Not Selected").setLore("","§8Click to select", "").save());
                }
                event.getInventory().setItem(23, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
            }else if(event.getSlot() == 24) {
                Main.setDifficulty(Difficulty.HALF_HEART);
                for(int i = 20; i <= 24; i++) {
                    event.getInventory().setItem(i, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayname("§4Not Selected").setLore("","§8Click to select", "").save());
                }
                event.getInventory().setItem(24, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
            }

        }

    }

}
