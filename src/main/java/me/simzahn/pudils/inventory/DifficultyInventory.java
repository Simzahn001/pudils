package me.simzahn.pudils.inventory;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.util.Difficulty;
import me.simzahn.pudils.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class DifficultyInventory implements InventoryGUI {

    private final Inventory inventory;

    private static final NamespacedKey difficultyKey = new NamespacedKey(Main.getPlugin(), "difficulty");

    public DifficultyInventory(Main plugin) {
        this.inventory = plugin.getServer().createInventory(this, 4*9);

        //set the background to gray stained glass
        for(int i = 0; i < 4*9; i++) {
            this.inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayname("§4§lDifficulties")
                .setLore("")
                .save());
        }

        //set the items representing the difficulties
        this.inventory.setItem(11, new ItemBuilder(Material.TOTEM_OF_UNDYING)
                .setDisplayname("§2§lImmortable")
                .setLore("", "§4---COMING SOON---", "", "§8Makes you immortal, you take no damage!", "")
                .save());
        this.inventory.setItem(12, new ItemBuilder(Material.APPLE)
                .setDisplayname("§a§lNormal")
                .setLore("", "§8Just normal Minecraft... OwO", "")
                .save());
        this.inventory.setItem(13, new ItemBuilder(Material.GOLDEN_APPLE)
                .setDisplayname("§e§lUHC")
                .setLore("", "§8Ultra Hardcore", "", "§8No natural regeneration",
                        "§8Only regeneration through golden apples,",
                        "§8potions, and spider eye soup ;^)", "")
                .save());
        this.inventory.setItem(14, new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE)
                .setDisplayname("§6§lUUHC")
                .setLore("", "§8Ultra Ultra Hardcore", "", "§8No regeneration",
                        "§8Really no regeneration", "")
                .save());
        this.inventory.setItem(15, new ItemBuilder(Material.WITHER_SKELETON_SKULL)
                .setDisplayname("§4§lHalf Heart")
                .setLore("", "§8One half heart", "§8One hit", "§8Death", "")
                .save());

        refreshDifficultyIndicators();

    }

    //refreshes the items and the Indicators
    private void refreshDifficultyIndicators(){

        //set the selection indicators
        int i = 20;
        ItemStack currentItem;
        Difficulty activeDifficulty = Main.getDifficulty();
        for (Difficulty difficulty : Difficulty.values()) {

            if (difficulty == activeDifficulty) {
                currentItem = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayname("§a§lSelected")
                        .setLore("", "§8This difficulty is now selected",
                                "§8Click on the red field of another difficulty to select it")
                        .save();
            } else {
                currentItem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayname("§4Not Selected")
                        .setLore("", "§8Click to select")
                        .save();
            }

            ItemMeta meta = currentItem.getItemMeta();
            meta.getPersistentDataContainer().set(difficultyKey, PersistentDataType.STRING, difficulty.toString());
            currentItem.setItemMeta(meta);
            this.inventory.setItem(i, currentItem);

            i++;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

        event.setCancelled(true);

        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) {
            return;
        }
        if (currentItem.getItemMeta() == null || currentItem.getItemMeta().getPersistentDataContainer().isEmpty()) {
            return;
        }

        PersistentDataContainer pdc = currentItem.getItemMeta().getPersistentDataContainer();

        if(pdc.has(difficultyKey, PersistentDataType.STRING)){

            String pdcValue = pdc.get(difficultyKey, PersistentDataType.STRING);

            if (pdcValue == null) {
                return;
            }

            try {
                Difficulty difficulty = Difficulty.valueOf(pdcValue);
                Main.setDifficulty(difficulty);
                refreshDifficultyIndicators();
            } catch (IllegalArgumentException e) {
                return;
            }

        }

    }
}
