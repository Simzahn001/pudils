package me.simzahn.pudils.timer;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TimerCom implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length==1) {

                switch (args[0]) {
                    case "start" -> Main.getTimer().start(true);
                    case "stop", "s" -> Main.getTimer().stop(true);
                    case "resume", "r" -> Main.getTimer().resume(true);
                    case "reset" -> Main.getTimer().reset(true);
                    case "help", "?" -> {
                        player.sendMessage("§1-------------§6<§6§fTimer§6>§1-------------");
                        player.sendMessage("§6Mit diesem Command kannst du den Timer kontrollieren!");
                        player.sendMessage("§1/timer <action>");
                        player.sendMessage("§6§fFolgendes ist für den Parameter §1<action> §6§fverfügbar:");
                        player.sendMessage("§6- §1start §6Resetet den Timer und startet in von 00:00:00");
                        player.sendMessage("§6- §1stop §6Hält den Timer an");
                        player.sendMessage("§6- §1resume §6Lässt dem Timer weiterlaufen");
                        player.sendMessage("§6- §1reset §6Setzt den Timer auf 00:00:00 zurück, startet ihn jedoch nicht");
                        player.sendMessage("§1-------------------------------------------");
                        player.sendMessage(
                                Component.text("-------------")
                                        .color(TextColor.color(0, 36, 254))
                                        .append(Component.text("<")
                                                .color(TextColor.color(255, 177, 68)))
                                        .append(Component.text("Timer")
                                                .color(TextColor.color(255, 177, 68))
                                                .decorate(TextDecoration.BOLD))
                                        .append(Component.text(">")
                                                .color(TextColor.color(255, 177, 68)))
                                        .append(Component.text("-------------")
                                                .color(TextColor.color(0, 36, 254)))
                        );
                        player.sendMessage(
                                Component.text("Mit diesem Command kannst du den Timer kontrollieren!")
                                        .color(TextColor.color(255, 177, 68))
                        );
                        player.sendMessage(
                                Component.text("/timer <action>")
                                        .color(TextColor.color(0, 36, 254))
                        );
                        player.sendMessage(
                                Component.text("Folgendes ist für den Parameter ")
                                        .color(TextColor.color(255, 177, 68))
                                        .decorate(TextDecoration.BOLD)
                        );
                    }
                    default -> {
                    }
                }

            }else {
                player.sendMessage(
                        Component.text("Bitte benutze ")
                                .color(TextColor.color(255, 0, 0))
                                .decorate(TextDecoration.BOLD)
                            .append(Component.text("/timer <action>")
                                .color(TextColor.color(0, 36, 254)))
                            .append(Component.text("!")
                                .color(TextColor.color(255, 0, 0))
                                .decorate(TextDecoration.BOLD))
                );
                player.sendMessage(
                        Component.text("Nutze ")
                                .color(TextColor.color(255, 0, 0))
                                .decorate(TextDecoration.BOLD)
                            .append(Component.text("/timer help")
                                .color(TextColor.color(0, 36, 254))
                                .decorate(TextDecoration.BOLD))
                            .append(Component.text(" für weitere Infos!")
                                .color(TextColor.color(255, 0, 0))
                                .decorate(TextDecoration.BOLD))
                );
            }


        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> list = new ArrayList<>();
        list.add("start");
        list.add("stop");
        list.add("resume");
        list.add("reset");



        return list;
    }
}
