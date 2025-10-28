package me.simzahn.pudils.challenge;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.inventory.ChallengeInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChallengeCom implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        switch (strings.length) {
            case 0:
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    ChallengeInventory challengeInv = new ChallengeInventory(Main.getPlugin());
                    player.openInventory(challengeInv.getInventory());
                }

            break;

            //(de-) activate challenges
            case 2:


                new BukkitRunnable() {
                    @Override
                    public void run() {

                        //retrieve challenge
                        Challenge challenge = Main.getChallengeManager().getChallenge(strings[0]);
                        if (challenge==null) {
                            commandSender.sendMessage(
                                    Component.text("Die Challenge mit dem Namen ")
                                            .color(TextColor.color(255, 0, 0))
                                            .append(Component.text(strings[0])
                                                    .color(TextColor.color(255,0,0))
                                                    .decorate(TextDecoration.BOLD))
                                            .append(Component.text(" konnte nicht gefunden werden!")
                                                    .color(TextColor.color(255,0,0)))
                            );
                        }

                        //retrieve boolean
                        boolean enabled;
                        switch (strings[1]) {
                            case "enable", "active", "true", "a", "t" -> enabled = true;
                            case "disable", "inactive", "false", "i", "f" -> enabled = false;
                            //inform the player, that the boolean was invalid
                            default -> {
                                commandSender.sendMessage(
                                        Component.text("Bitte benutze \"")
                                                .color(TextColor.color(255, 0, 0))
                                                .append(Component.text("active")
                                                        .color(TextColor.color(255, 177, 68))
                                                        .decorate(TextDecoration.BOLD))
                                                .append(Component.text("\" oder \"")
                                                        .color(TextColor.color(255, 0, 0)))
                                                .append(Component.text("inactive")
                                                        .color(TextColor.color(255, 177, 68))
                                                        .decorate(TextDecoration.BOLD))
                                                .append(Component.text("\"!")
                                                        .color(TextColor.color(255, 0, 0)))
                                );
                                return;
                            }
                        }

                        if (enabled) {
                            Main.getChallengeManager().enableChallenge(challenge);
                        } else {
                            Main.getChallengeManager().disableChallenge(challenge);
                        }

                    }
                }.runTaskAsynchronously(Main.getPlugin());

            break;

            //send sender information about this command
            default:
                //@TODO explain the command
            break;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        ArrayList<String> list = new ArrayList<String>();

        //as the first argument, a challenge should be returned
        if (strings.length == 1) {

            //get the trimmed first argument
            String arg1 = strings[0];
            arg1 = arg1.trim();

            //add all challenges to the list, which start with the argument in a for loop (ignoring the case)
            for (Challenge challenge : Main.getChallengeManager().getAllRegisteredChallenges()) {
                if (challenge.getName().toLowerCase().startsWith(arg1.toLowerCase())) {
                    list.add(challenge.getName());
                }
            }

        } else if (strings.length == 2) {

            list.add("enable");
            list.add("disable");

        }


        return list;
    }
}

