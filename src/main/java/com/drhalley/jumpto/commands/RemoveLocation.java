package com.drhalley.jumpto.commands;

import com.drhalley.jumpto.JumpTo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveLocation implements CommandExecutor {


    private JumpTo jumpTo;

    public RemoveLocation(JumpTo jumpTo){
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
                PreparedStatement statement = jumpTo.getConnection().prepareStatement(controlsql);
                ResultSet rs = statement.executeQuery();
                if(rs.next()){
                    PreparedStatement preparedStatement = jumpTo.getConnection().prepareStatement("DELETE FROM LOCATION WHERE name='" + strings[0]+"'");
                    preparedStatement.executeUpdate();
                    player.sendMessage("Location removed!");
                }else{
                    player.sendMessage("Cant find location");
                }

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
