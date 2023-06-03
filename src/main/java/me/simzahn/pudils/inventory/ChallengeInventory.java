package me.simzahn.pudils.inventory;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.challenges.Challenge;
import me.simzahn.pudils.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ChallengeInventory implements InventoryHolder {

    private Inventory inventory;

    //the slots where the challenges are displayed
    private final int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    //the challenges
    private final Challenge[] challenges = Main.getChallengeManager().getAllRegisteredChallenges().toArray(Challenge[]::new);
    //current row
    private int row = 0;


    public ChallengeInventory(Main plugin) {
        this.inventory = plugin.getServer().createInventory(this, 6*9);

        //set the background to gray stained glass
        for(int i = 0; i < 6*9; i++) {
            this.inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayname("Challenges").save());
        }

        //set the challenge items
        refreshChallenges();

        //set the navigation items
        this.inventory.setItem(26, new ItemBuilder(Material.OAK_BUTTON).setDisplayname("Scroll up").save());
        this.inventory.setItem(35, new ItemBuilder(Material.OAK_BUTTON).setDisplayname("Scroll down").save());
    }


    //Increase/Decrease the row before calling this method to create a "scrolling" effect
    //refreshes the challenges
    private void refreshChallenges() {
        ItemStack item;
        for (int i = 0; i < slots.length; i++) {


            //fill the other slots with empty items
            if (challenges.length > i + row*7) {
                item = challenges[i + row*7].getItem();

                //if the challenge is enabled, apply the glowing effect to the item
                if (Boolean.TRUE.equals(Main.getChallengeManager().isChallengeEnabled(challenges[i + row * 7]))) {
                    ItemMeta meta = item.getItemMeta();
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    item.setItemMeta(meta);
                }

            } else {
                item = new ItemBuilder(Material.AIR).save();
            }


            inventory.setItem(slots[i], item);
        }
    }



    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
