package me.simzahn.pudils.util;

import me.simzahn.pudils.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Inventories {

    //@TODO redo with inventory holder and PCD
    /**
     *   DIFFICUTLY INVENTORY HAS TO BE REDONE WITH PCD AND INVENTORY HOLDER
     *   MIGRATE TO NEW INVENTORY CLICK LISTENER CLASS
     */

    public static void openDifficultyInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 4*9, "§4§lDifficulties");
        for(int i =  0; i <= inventory.getSize()-1; i++) {
            inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayname("§8Difficulties").setLore("").save());
        }

        inventory.setItem(11, new ItemBuilder(Material.TOTEM_OF_UNDYING).setDisplayname("§2§lImmortable").setLore("", "§4---COMEING SOON---", "", "§8Macht dich unsterblich, du bekommst keinen Schaden mehr!", "").save());
        inventory.setItem(12, new ItemBuilder(Material.APPLE).setDisplayname("§a§lNormal").setLore("", "§8Nur normales Minecraft... OwO", "").save());
        inventory.setItem(13,  new ItemBuilder(Material.GOLDEN_APPLE).setDisplayname("§e§lUHC").setLore("", "§8Ultra Hardcore", "", "§8Keine Natürliche Regeneration", "§8Nur Regeneration durch Goldäpfel,","§8Potions und Spiegeleiersuppen ;^)", "").save());
        inventory.setItem(14, new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).setDisplayname("§6§lUUHC").setLore("", "§8Ultra Ultra Hardcore", "", "§8Keine Regenaration", "§8Wirklich nicht", "").save());
        inventory.setItem(15, new ItemBuilder(Material.WITHER_SKELETON_SKULL).setDisplayname("§4§lHalf Heart").setLore("", "§8Ein halbes Herz", "§8Ein Schlag", "§8Tod","").save());

        for(int i = 20; i <= 24; i++) {
            inventory.setItem(i, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayname("§4Not Selected").setLore("","§8Click to select", "").save());
        }

        switch (Main.getDifficulty()) {
            case IMMORTABLE:
                inventory.setItem(20, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
                break;
            case HALF_HEART:
                inventory.setItem(24, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
                break;
            case NORMAl:
                inventory.setItem(21, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
                break;
            case UUHC:
                inventory.setItem(23, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
                break;
            case UHC:
                inventory.setItem(22, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayname("§a§lSelected").setLore("", "§8Diese Schwierigkeit ist jetzt ausgewählt", "§8Clicke auf das rote Feld einer anderen Schwierigkein um diese auszuwählen").save());
                break;
            default:
                break;
        }

        player.openInventory(inventory);
    }

}
