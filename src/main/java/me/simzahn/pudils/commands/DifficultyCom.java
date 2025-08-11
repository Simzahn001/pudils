package me.simzahn.pudils.commands;

import me.simzahn.pudils.Main;
import me.simzahn.pudils.inventory.DifficultyInventory;
import me.simzahn.pudils.util.Difficulty;
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

public class DifficultyCom implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length==1) {
                Main.setDifficulty(Difficulty.valueOf(args[0]));
            }else if (args.length==0) {
                player.openInventory(new DifficultyInventory(Main.getPlugin()).getInventory());
            }else {
                //@TODO remove this line vvv
                player.sendMessage("§4§fBitte benutze §1/difficulty <difficulty>§4§f!");
                player.sendMessage(
                        Component.text("Bitte benutze ")
                                .color(TextColor.color(1,1,1))
                                .decorate(TextDecoration.BOLD)
                        .append(Component.text("/difficulty <difficulty>")
                                .color(TextColor.color(1,1,1)))
                        .append(Component.text("!")
                                .color(TextColor.color(1,1,1))
                                .decorate(TextDecoration.BOLD))
                        //@TODO fix rgb colors
                );
            }
        }
        return false;
    }




    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for (Difficulty currentDifficulty : Difficulty.values()) {
                list.add(currentDifficulty.name());
            }
            return list;
        }else
            return null;
    }
}
