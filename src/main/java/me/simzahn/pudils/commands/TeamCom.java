package me.simzahn.pudils.commands;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamCom implements CommandExecutor, TabCompleter {

    private final String SELECTall = "SELECT name FROM player WHERE playing=?";
    private final String SELECT = "SELECT * FROM player WHERE uuid=?";
    private final String UPDATE = "UPDATE player SET playing=? WHERE uuid=?";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            if(args.length==1) {
                if(args[0].equals("list") || args[0].equals("l")) {

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try (Connection connection = Main.getPlugin().getHikari().getConnection();
                                 PreparedStatement selectAll = connection.prepareStatement(SELECTall)) {

                                //get all players, which are playing
                                selectAll.setBoolean(1, true);
                                ResultSet allResult = selectAll.executeQuery();
                                sender.sendMessage(
                                        Component.text("------------------------------")
                                                .color(TextColor.color(0, 36, 254))
                                );
                                sender.sendMessage(
                                        Component.text("Diese Spieler sind im Team:")
                                                .color(TextColor.color(255, 177, 68))
                                                .decorate(TextDecoration.BOLD)
                                );
                                while (allResult.next()) {
                                    sender.sendMessage(
                                            Component.text("-")
                                                    .color(TextColor.color(255,255,255))
                                                .append(Component.text(allResult.getString("name"))
                                                    .color(TextColor.color(255, 177, 68)))
                                    );
                                }
                                sender.sendMessage(
                                        Component.text("------------------------------")
                                                .color(TextColor.color(0, 36, 254))
                                );

                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }
                        }
                    }.runTaskAsynchronously(Main.getPlugin());
                }


            }else if(args.length==2) {


                //check if Player exists
                Player victim = Bukkit.getPlayer(args[1]);
                if (victim == null) {
                    sender.sendMessage(
                            Component.text("Den Spieler " + args[1] + "gibt es nicht!")
                                    .color(TextColor.color(255, 0, 0))
                    );
                    return false;
                }


                //check if the Player wants to add himself xD
                if (victim.equals(sender) && !sender.isOp()) {
                    sender.sendMessage(
                            Component.text("Du kannst deinen Status nicht selber verändern!")
                                    .color(TextColor.color(255, 0, 0))
                    );
                    return false;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try (Connection connection = Main.getPlugin().getHikari().getConnection();
                             PreparedStatement senderSelect = connection.prepareStatement(SELECT);
                             PreparedStatement playerSelect = connection.prepareStatement(SELECT);
                             PreparedStatement update = connection.prepareStatement(UPDATE)) {


                            //get the current state of the sender
                            senderSelect.setString(1, sender.getUniqueId().toString());
                            ResultSet senderResult = senderSelect.executeQuery();

                            if (senderResult.next()) {

                                //check if the sender is playing himself
                                if (!senderResult.getBoolean("playing") && !sender.isOp()) {
                                    sender.sendMessage(
                                            Component.text("Du kannst niemanden einladen, wenn du selbst nicht dabei bist!")
                                                    .color(TextColor.color(255, 0, 0))
                                    );
                                    return;
                                }

                                //get the victims Data
                                Player victim = Bukkit.getPlayer(args[1]);
                                if (victim == null) {
                                    sender.sendMessage(
                                            Component.text("Der Spieler " + args[1] + " ist nicht online!")
                                                    .color(TextColor.color(255, 0, 0))
                                    );
                                    return;
                                }

                                playerSelect.setString(1, victim.getUniqueId().toString());
                                ResultSet victimResult = playerSelect.executeQuery();

                                //check if the victim exists in the database
                                if(!victimResult.next()) {
                                    sender.sendMessage(
                                            Component.text("Der Spieler " + victim.getName() +
                                                    " konnte leider nicht in unserer Datenbank gefunden werden!")
                                                    .color(TextColor.color(255, 0, 0))
                                    );
                                    return;
                                }



                                //now we get to the actual command ;^)
                                //check through the arguments
                                switch (args[0]) {

                                    //add the victim to the Team
                                    case "add":
                                    case "a":
                                        //check if the victim is already playing
                                        if (victimResult.getBoolean("playing")) {
                                            sender.sendMessage(
                                                    Component.text("Der Spieler ist bereits im Team!")
                                                            .color(TextColor.color(255, 0, 0))
                                            );
                                            return;
                                        }
                                        //add him to the team
                                        update.setBoolean(1, true);
                                        update.setString(2, victim.getUniqueId().toString());
                                        update.execute();
                                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(
                                                Component.text("Der Spieler ")
                                                        .color(TextColor.color(33, 255, 0))
                                                    .append(Component.text(victim.getName())
                                                        .color(TextColor.color(0, 36, 254))
                                                        .decorate(TextDecoration.BOLD))
                                                    .append(Component.text(" wurde zu Team hinzugefügt!")
                                                        .color(TextColor.color(33, 255, 0)))
                                        ));
                                        victim.showTitle(
                                            Title.title(
                                                Component.text("Du bist im Team!")
                                                        .color(TextColor.color(33, 255, 0)),
                                                Component.text(sender.getName())
                                                        .color(TextColor.color(0, 36, 254))
                                                        .decorate(TextDecoration.BOLD)
                                                    .append(Component.text(" hat dich hinzugefügt!")
                                                        .color(TextColor.color(255, 255, 255)))
                                            )
                                        );
                                        //have to run #setGameMode sync <- cant be run async
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                victim.teleport(sender.getLocation());
                                                victim.setGameMode(GameMode.SURVIVAL);
                                            }
                                        }.runTaskLater(Main.getPlugin(), 1);


                                        break;



                                    //remove the victim from the team
                                    case "remove":
                                    case "r":

                                        //check if the victim isn't playing at all
                                        if (!victimResult.getBoolean("playing")) {
                                            sender.sendMessage(
                                                    Component.text("Der Spieler ist bereits nicht im Team!")
                                                            .color(TextColor.color(255, 0, 0))
                                            );
                                            return;
                                        }

                                        //remove him from the team
                                        update.setBoolean(1, false);
                                        update.setString(2, victim.getUniqueId().toString());
                                        update.execute();
                                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(
                                                Component.text("Der Spieler ")
                                                        .color(TextColor.color(33, 255, 0))
                                                    .append(Component.text(victim.getName())
                                                        .color(TextColor.color(0, 36, 254))
                                                        .decorate(TextDecoration.BOLD))
                                                    .append(Component.text(" wurde vom Team entfernt!")
                                                        .color(TextColor.color(33, 255, 0)))
                                        ));
                                        victim.showTitle(
                                                Title.title(
                                                        Component.text("Du wurdest rausgeschmissen!")
                                                                .color(TextColor.color(255, 0, 31)),
                                                        Component.text(sender.getName())
                                                                .color(TextColor.color(0, 36, 254))
                                                                .decorate(TextDecoration.BOLD)
                                                            .append(Component.text(" hat dich gekickt!")
                                                                .color(TextColor.color(255, 255, 255)))
                                                )
                                        );
                                        //have to run #setGameMode sync <- cant be run async
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                victim.setGameMode(GameMode.SPECTATOR);
                                            }
                                        }.runTaskLater(Main.getPlugin(), 1);


                                        break;
                                    default:
                                        break;

                                }

                            }else {
                                sender.sendMessage(
                                        Component.text("Der Spieler " + args[1] +
                                                "konnte nicht gefunden werden!")
                                                .color(TextColor.color(0, 36, 254))
                                );
                            }

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(Main.getPlugin());

            }else {
                sender.sendMessage(
                        Component.text("Bitte benutze ")
                                .color(TextColor.color(255, 0, 0))
                                .decorate(TextDecoration.BOLD)
                            .append(Component.text("/team <action> <player>")
                                .color(TextColor.color(0, 36, 254)))
                            .append(Component.text(" !")
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
        if (args.length==1) {
            list.add("add");
            list.add("remove");
            list.add("list");
            return list;
        }else if(args.length==2) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
            return list;
        }
        return null;
    }
}
