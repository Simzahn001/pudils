package me.simzahn.pudils.commands;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResetCom implements CommandExecutor, Listener {

    private boolean isResetting = false;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length < 1) {
            return sendHelpMessage(commandSender);
        }
        if (!strings[0].equals("confirm")) {
            return sendHelpMessage(commandSender);
        }



        //kick all Players
        //@TODO send to lobby via velocity
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(
                Component.text("Die Welt wird zurückgesetzt!")
        ));

        isResetting = true;

        //reset the timer
        Main.getTimer().reset();


        //run the bash script
        //the bash script deletes all words, sets a new seeds in the config and then restarts the server
        //check if file exists
        if (!Files.exists(Path.of("worldReset.sh"))) {
            commandSender.sendMessage(
                    Component.text("Cannot find the bash script!")
                            .color(TextColor.color(255, 0, 0))
                            .decorate(TextDecoration.BOLD)
            );
            return false;
        }

        //run the script
        try {
            ProcessBuilder pBuilder = new ProcessBuilder();
            pBuilder.command("nohup", "bash", "worldReset.sh");
            pBuilder.start();
        } catch (IOException e) {
            commandSender.sendMessage(
                    Component.text("Failed to execute Bash script!")
                            .color(TextColor.color(255, 0, 0))
                            .decorate(TextDecoration.BOLD)
            );
            e.printStackTrace();
            return false;
        }


        //stop server
        Bukkit.getServer().shutdown();



       return true;
    }

    private boolean sendHelpMessage(@NotNull CommandSender commandSender) {
        commandSender.sendMessage(
                Component.text("Bitte benutzte ")
                        .color(TextColor.color(255, 0, 0))
                        .decorate(TextDecoration.BOLD)
                        .append(Component.text("/reset confirm")
                                .color(TextColor.color(0, 36, 254)))
                        .append(Component.text("!")
                                .color(TextColor.color(255, 0, 0))
                                .decorate(TextDecoration.BOLD))
        );
        return false;
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.isResetting) {
            event.getPlayer().kick(
                    Component.text("Der Server setzt gerade die Welt zurück!")
                            .color(TextColor.color(255, 0, 0))
                            .decorate(TextDecoration.BOLD)
            );
        }
    }
}
