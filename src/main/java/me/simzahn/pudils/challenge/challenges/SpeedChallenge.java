package me.simzahn.pudils.challenge.challenges;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.challenge.Challenge;
import me.simzahn.pudils.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SpeedChallenge implements Challenge {

    private List<Player> playerWithEffect = new ArrayList<>();


    @Override
    public String getName() {
        return "speed";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.FEATHER)
                .setDisplayname("§aSpeed-Challenge")
                .save();
    }

    @Override
    public void onStart() {
        System.out.println("this is executed");
        System.out.println(Main.getTimer().getActivePlayers());
        for(Player player : Main.getTimer().getActivePlayers()) {
            System.out.println(player.getName());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 30, false, false, false));
            playerWithEffect.add(player);
        }
    }

    @Override
    public void onStop() {
        System.out.println("the stop as well");
        for (Player player : playerWithEffect) {
            player.removePotionEffect(PotionEffectType.SPEED);
        }
        playerWithEffect = new ArrayList<>();
    }
}
