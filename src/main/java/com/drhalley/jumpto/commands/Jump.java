package com.drhalley.jumpto.commands;

import com.drhalley.jumpto.JumpTo;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Jump implements CommandExecutor {
    private JumpTo jumpTo;
    public Jump(JumpTo jumpTo){
        this.jumpTo = jumpTo;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(player.hasPermission(jumpTo.getConfig().getString("permission"))){
                if(strings.length != 1){
                    player.sendMessage("invalid args");
                    return false;
                }
                String controlsql = "SELECT * FROM LOCATION" +
                        " WHERE name = '"+strings[0]+"';";

                try {
                    if(!jumpTo.getConnection().prepareStatement(controlsql).executeQuery().next()){
                        player.sendMessage("There is no location like that");
                        return false;
                    }
                    PreparedStatement statement = jumpTo.getConnection().prepareStatement(controlsql);
                    ResultSet data = statement.executeQuery();
                    data.next();
                    Location location = new Location(player.getServer().getWorld(data.getString("world")), data.getDouble("x"), data.getDouble("y"), data.getDouble("z"), data.getFloat("yaw"), data.getFloat("pitch"));

                    player.teleport(location);
                    player.sendMessage("You succesfully teleported");

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                player.sendMessage("You dont have enough permissions for this command");
                return false;
            }
        }else{
            commandSender.sendMessage("Only players can use this command");
            return false;
        }
        return false;
    }
}
