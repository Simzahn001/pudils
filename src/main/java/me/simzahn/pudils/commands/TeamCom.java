package me.simzahn.pudils.commands;

import me.simzahn.pudils.Main;
import net.kyori.adventure.text.Component;
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

    private String SELECTall = "SELECT name FROM player WHERE playing=?";
    private String SELECT = "SELECT * FROM player WHERE uuid=?";
    private String UPDATE = "UPDATE player SET playing=? WHERE uuid=?";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            Player victim;

            //check if the Player exists
            if (Bukkit.getPlayer(args[1])==null) {
                sender.sendMessage(Component.text("§4Den Spieler " + args[1] + " gibt es nicht!"));
                return false;
            }
            victim = Bukkit.getPlayer(args[1]);

            //check if the Player wants to add himself xD
            if (victim.equals(sender) && !sender.isOp()) {
                sender.sendMessage(Component.text("§4Du kannst deinen Status nicht selber verändern!"));
                return false;
            }


            if(args.length==2) {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try (Connection connection = Main.getPlugin().getHikari().getConnection();
                             PreparedStatement senderSelect = connection.prepareStatement(SELECT);
                             PreparedStatement playerSelect = connection.prepareStatement(SELECT);
                             PreparedStatement update = connection.prepareStatement(UPDATE);
                             PreparedStatement selectAll = connection.prepareStatement(SELECTall)) {


                            //get the current state of the sender
                            senderSelect.setString(1, sender.getUniqueId().toString());
                            ResultSet senderResult = senderSelect.executeQuery();

                            if (senderResult.next()) {

                                //check if the sender is playing himself
                                if (!senderResult.getBoolean("playing") && !sender.isOp()) {
                                    sender.sendMessage(Component.text("§4Du kannst niemanden einladen, wenn du selbst nich dabei bist!"));
                                    return;
                                }

                                //get the victims Data
                                playerSelect.setString(1, sender.getUniqueId().toString());
                                ResultSet victimResult = playerSelect.executeQuery();

                                //check if the victim exists in the database
                                if(!victimResult.next()) {
                                    sender.sendMessage(Component.text("§4Der Spieler " + victim.getName() + " konnte leider nicht in der Datenbank gefunden werden!"));
                                    return;
                                }



                                //now we get to the actual command ;^)
                                //check trough the arguments
                                switch (args[0]) {

                                    //add the victim to the Team
                                    case "add":
                                    case "a":
                                        //check if the victim is already playing
                                        if (victimResult.getBoolean("playing")) {
                                            sender.sendMessage("§4Der Spieler ist bereits im Team!");
                                            return;
                                        }
                                        //add him to the team
                                        update.setBoolean(1, true);
                                        update.setString(2, victim.getUniqueId().toString());
                                        update.execute();
                                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(Component.text("§aDer Spieler §1§f" + victim.getName() + " §awurde zum Team hinzugefügt!")));
                                        victim.sendTitle("§aDu bist im Team!", "§1§f" + sender.getName() + "§a hat dich hinzugefüt!", 10, 60, 10);
                                        victim.teleport(sender.getLocation());
                                        victim.setGameMode(GameMode.SURVIVAL);

                                        break;



                                    //remove the victim from the team
                                    case "remove":
                                    case "r":

                                        //check if the the victim isnt playing at all
                                        if (!victimResult.getBoolean("playing")) {
                                            sender.sendMessage("§4Der Spieler ist bereits nicht im Team!");
                                            return;
                                        }

                                        //remove him from the team
                                        update.setBoolean(1, false);
                                        update.setString(2, victim.getUniqueId().toString());
                                        update.execute();
                                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(Component.text("§aDer Spieler §1§f" + victim.getName() + " §a wurde vom Team entfernt!")));
                                        victim.sendTitle("§fDu wurdest rausgeschmissen!", "§1§f" + sender.getName() + "§a hat dich gekickt!", 10, 60, 10);
                                        victim.setGameMode(GameMode.SPECTATOR);

                                        break;

                                    //to list all the Players in the team
                                    case "list":
                                    case "l":

                                        //get all players, which are playing
                                        selectAll.setBoolean(1, true);
                                        ResultSet allResult = selectAll.executeQuery();
                                        sender.sendMessage(Component.text("§1------------------------------"));
                                        sender.sendMessage(Component.text("§6§fDiese Spieler sind im Team:"));
                                        while (allResult.next()) {
                                            sender.sendMessage(Component.text("-§6" + allResult.getString("name")));
                                        }
                                        sender.sendMessage(Component.text("§1------------------------------"));

                                        break;
                                    default:
                                        break;

                                }

                            }else {
                                sender.sendMessage(Component.text("§4Der Spieler" + args[1] + "konnte nicht in unseren Datenbanken gefunden werden!"));
                            }

                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(Main.getPlugin());

            }else {
                sender.sendMessage("§4§fBitte benutze §1/team <action> <sender>§4§f!");
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
