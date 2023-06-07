package me.simzahn.pudils.inventory;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.challenge.Challenge;
import me.simzahn.pudils.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.floor;

public class ChallengeInventory implements InventoryHolder {

    private final Inventory inventory;

    //the slots where the challenges are displayed
    private final int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    //the challenges
    private final Challenge[] challenges = Main.getChallengeManager().getAllRegisteredChallenges().toArray(Challenge[]::new);
    //current row
    private int row = 0;
    //a key used for navigation
    private static final NamespacedKey navigationKey = new NamespacedKey(Main.getPlugin(), "navigation");
    //a key used for challenges
    private static final NamespacedKey challengeKey = new NamespacedKey(Main.getPlugin(), "challenge");



    public ChallengeInventory(Main plugin) {
        this.inventory = plugin.getServer().createInventory(this, 6*9);

        //set the background to gray stained glass
        for(int i = 0; i < 6*9; i++) {
            this.inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayname("Challenges").save());
        }

        //set the challenge items
        refreshChallenges();

        //set the navigation items
        ItemStack scrollUpItem = new ItemBuilder(Material.OAK_BUTTON).setDisplayname("Scroll up").save();
        ItemMeta scrollUpItemMeta = scrollUpItem.getItemMeta();
        scrollUpItemMeta.getPersistentDataContainer().set(navigationKey, PersistentDataType.STRING, "scrollUp");
        scrollUpItem.setItemMeta(scrollUpItemMeta);
        this.inventory.setItem(26, scrollUpItem);

        ItemStack scrollDownItem = new ItemBuilder(Material.OAK_BUTTON).setDisplayname("Scroll down").save();
        ItemMeta scrollDownItemMeta = scrollDownItem.getItemMeta();
        scrollDownItemMeta.getPersistentDataContainer().set(navigationKey, PersistentDataType.STRING, "scrollDown");
        scrollDownItem.setItemMeta(scrollDownItemMeta);
        this.inventory.setItem(35, scrollDownItem);
    }


    //Increase/Decrease the row before calling this method to create a "scrolling" effect
    //refreshes the challenges
    private void refreshChallenges() {
        ItemStack item;
        Challenge currentChallenge;
        for (int i = 0; i < slots.length; i++) {


            //fill the other slots with empty items
            if (challenges.length > i + row*7) {

                currentChallenge = challenges[i + row*7];
                item = currentChallenge.getItem();

                //set the PDC of the item to the challenge name
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(challengeKey, PersistentDataType.STRING, currentChallenge.getName());
                item.setItemMeta(meta);

                //if the challenge is enabled, apply the glowing effect to the item
                if (Main.getChallengeManager().isChallengeEnabled(challenges[i + row * 7]).orElse(false)) {
                    addEnchantmentGlow(item);
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



    //handle the click event
    public void handleClick(InventoryClickEvent event) {

        //cancel the event
        event.setCancelled(true);

        ItemStack currentItem = event.getCurrentItem();

        //check if the clicked item has a PDC
        if (currentItem == null) {
            return;
        }
        if (currentItem.getItemMeta().getPersistentDataContainer().isEmpty()) {
            return;
        }

        PersistentDataContainer pdc = currentItem.getItemMeta().getPersistentDataContainer();

        //check if the clicked item is a navigation item
        if (pdc.has(navigationKey, PersistentDataType.STRING)) {

            String value = pdc.get(navigationKey, PersistentDataType.STRING);

            if (value == null) {
                return;
            }

            if (value.equals("scrollUp")) {
                if (row > 0) {
                    row--;
                    refreshChallenges();
                }
            } else if (value.equals("scrollDown")) {
                //should stop scrolling if the list ends
                if (row < floor((double) challenges.length / 7)) {
                    row++;
                    refreshChallenges();
                }
            }

        //check if the clicked item is a challenge item
        } else if (pdc.has(challengeKey, PersistentDataType.STRING)) {

            if (event.getClick() == ClickType.LEFT) {

                //retrieve the challenge
                String value = pdc.get(challengeKey, PersistentDataType.STRING);
                if (value == null) {
                    return;
                }
                Challenge challenge = Main.getChallengeManager().getChallenge(value);
                if (challenge == null) {
                    return;
                }

                //toggle the challenge
                boolean isEnabled =  Main.getChallengeManager().toggleChallenge(challenge).orElse(false);
                if (isEnabled) {
                    addEnchantmentGlow(currentItem);
                } else {
                    removeEnchantmentGlow(currentItem);
                }


            }

        }


    }



    private void addEnchantmentGlow(@NotNull ItemStack item) {

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(itemMeta);

    }

    private void removeEnchantmentGlow(@NotNull ItemStack item) {

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.removeEnchant(Enchantment.WATER_WORKER);
        itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(itemMeta);

    }
}
