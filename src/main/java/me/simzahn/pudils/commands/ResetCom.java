package me.simzahn.pudils.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
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

        isResetting = true;

        //send all players to the lobby
        for (Player player : Bukkit.getOnlinePlayers()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("Lobby");
            player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
            System.out.println("Sent " + player.getName() + " to lobby");
        }

        //reset the timer
        Main.getTimer().reset();

        //wait 5 seconds for the players to be sent to the lobby
        new BukkitRunnable() {
            @Override
            public void run() {
                //reset the server
                //the script can be found in the resources folder
                //copy the script to the server folder to make it work
                //the script must not be contained in the jar file!

                System.out.println("Resetting the server...");

                //run the bash script
                //the bash script deletes all words, sets a new seeds in the config and then restarts the server
                //check if file exists
                if (!Files.exists(Path.of("worldReset.sh"))) {
                    commandSender.sendMessage(
                            Component.text("Cannot find the bash script!")
                                    .color(TextColor.color(255, 0, 0))
                                    .decorate(TextDecoration.BOLD)
                    );
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
                }


                //stop server
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(Main.getPlugin(), 100);



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
