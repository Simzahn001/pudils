package me.simzahn.pudils.challenge.challenges;

import me.simzahn.pudils.challenge.ListenerChallenge;
import me.simzahn.pudils.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class BlocksToRandomMobsChallenge implements ListenerChallenge {

    private final Random random = new Random();

    private final HashMap<Material, EntityType> blockToMobMap = new HashMap<>();
    private static final EntityType[] MOBS = {
            EntityType.ALLAY,
            EntityType.ARMOR_STAND,
            EntityType.ARROW,
            EntityType.AXOLOTL,
            EntityType.BAT,
            EntityType.BEE,
            EntityType.BLAZE,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CAVE_SPIDER,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.CREEPER,
            EntityType.DOLPHIN,
            EntityType.DONKEY,
            EntityType.DROWNED,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.FOX,
            EntityType.FROG,
            EntityType.GHAST,
            EntityType.GIANT,
            EntityType.GLOW_SQUID,
            EntityType.GOAT,
            EntityType.GUARDIAN,
            EntityType.HOGLIN,
            EntityType.HORSE,
            EntityType.HUSK,
            EntityType.ILLUSIONER,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MAGMA_CUBE,
            EntityType.MULE,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PHANTOM,
            EntityType.PIG,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.PILLAGER,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.RAVAGER,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SKELETON_HORSE,
            EntityType.SLIME,
            EntityType.SNOWMAN,
            EntityType.SPIDER,
            EntityType.SQUID,
            EntityType.STRAY,
            EntityType.STRIDER,
            EntityType.TADPOLE,
            EntityType.PRIMED_TNT,
            EntityType.TRADER_LLAMA,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VEX,
            EntityType.VILLAGER,
            EntityType.VINDICATOR,
            EntityType.WANDERING_TRADER,
            EntityType.WARDEN,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.WOLF,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN
    };

    @Override
    public Listener getChallengeListener() {
        return new Listener() {
            @EventHandler
            public void onEvent(BlockBreakEvent event) {

                EntityType entity;

                Material material = event.getBlock().getType();
                if (blockToMobMap.containsKey(material)) {
                    entity =  blockToMobMap.get(material);
                } else {
                    entity = getRandomEntity();
                    blockToMobMap.put(material, entity);
                }

                event.getBlock().getWorld().spawnEntity(
                        event.getBlock().getLocation(),
                        entity
                );

            }
        };
    }

    @Override
    public String getName() {
        return "blocksToRandomMobs";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.ZOMBIE_HEAD)
                .setDisplayname("$aRandom Mobs on Block Break Challenge")
                .save();
    }

    @Override
    public void onStart() {
        this.blockToMobMap.clear();
    }

    private EntityType getRandomEntity() {
        return MOBS[this.random.nextInt(MOBS.length)];
    }
}
