package com.drhalley.jumpto.commands;

import com.drhalley.jumpto.JumpTo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddLocation implements CommandExecutor {

    private JumpTo jumpTo;

    public AddLocation(JumpTo jumpTo){
        this.jumpTo = jumpTo;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(!player.hasPermission("op")){
                player.sendMessage("You dont have enough permissions for this command");
                return false;
            }
            if(strings.length != 1){
                player.sendMessage("invalid args");
                return false;
            }
            String controlsql = "SELECT * FROM LOCATION" +
                    " WHERE name = '"+strings[0]+"';";

            try {
                if(jumpTo.getConnection().prepareStatement(controlsql).executeQuery().next()){
                    player.sendMessage("Location already exists!");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO LOCATION (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?);";
            try {
                PreparedStatement statement =  jumpTo.getConnection().prepareStatement(sql);
                statement.setString(1, strings[0]);
                statement.setString(2, player.getWorld().getName());
                statement.setDouble(3, player.getLocation().getX());
                statement.setDouble(4, player.getLocation().getY());
                statement.setDouble(5, player.getLocation().getZ());
                statement.setFloat(6, player.getLocation().getYaw());
                statement.setFloat(7, player.getLocation().getPitch());

                statement.executeUpdate();
                player.sendMessage("Location added!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return true;
        }else{
            commandSender.sendMessage("Only players can use this command");
            return false;
        }

    }
}
