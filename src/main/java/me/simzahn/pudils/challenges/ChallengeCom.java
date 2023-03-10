package me.simzahn.pudils.challenges;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
                //@TODO open inventory
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
                            case "enabled", "active", "true", "a", "t" -> enabled = true;
                            case "disabled", "inactive", "false", "i", "f" -> enabled = false;
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

        ArrayList<String> list = new ArrayList<>();

        //as the first argument, a challenge should be returned
        if (strings.length == 1) {

            //get all challenges
            Main.getChallengeManager().getAllRegisteredChallenges().forEach(challenge -> {
                list.add(challenge.getDisplayName());
            });

            String arg1 = strings[0];
            arg1.trim();

            //if the player starts typing, only suggestions with the starting letters they used, should be shown
            list.forEach(challenge -> {
                if (!challenge.startsWith(arg1)) {
                    list.remove(challenge);
                }
            });

        } else if (strings.length == 2) {

            list.add("enable");
            list.add("disable");

        }


        return list;
    }
}

