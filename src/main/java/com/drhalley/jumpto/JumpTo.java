package com.drhalley.jumpto;

import com.drhalley.jumpto.commands.AddLocation;
import com.drhalley.jumpto.commands.Jump;
import com.drhalley.jumpto.commands.RemoveLocation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public final class JumpTo extends JavaPlugin {


    private Connection connection;



    public Connection getConnection(){
        return this.connection;
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        String dbAddress = "jdbc:mysql://"+getConfig().getString("database.adress")+":"+getConfig().getInt("database.port")+"/";

        String dbName = getConfig().getString("database.dbName");
        String userName = getConfig().getString("database.userName");
        String password = getConfig().getString("database.password");
        String userPass = "?user="+userName+"&password="+password;


        PreparedStatement preStatement;
        Statement statement;
        ResultSet result;
        Connection con = null;

        try {
            con = DriverManager.getConnection(dbAddress + dbName, userName, password);
            Bukkit.getConsoleSender().sendMessage("Plugin succesfully enabled and database connected");
        }catch (SQLException e) {
            try {
                con = DriverManager.getConnection(dbAddress + userPass);
                Statement s = con.createStatement();
                int myResult = s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            }
            catch (SQLException b) {
                b.printStackTrace();
            }
            String myTableName = "CREATE TABLE LOCATION ("
                    + "name TEXT NOT NULL,"
                    + "world TEXT NOT NULL,"
                    + "x DOUBLE NOT NULL,"
                    + "y DOUBLE NOT NULL,"
                    + "z DOUBLE NOT NULL,"
                    + "yaw FLOAT NOT NULL,"
                    + "pitch FLOAT NOT NULL)";
            try {
                con = DriverManager.getConnection(dbAddress + dbName, userName, password);
                statement = con.createStatement();
                //The next line has the issue
                statement.executeUpdate(myTableName);
                System.out.println("Table Created");
            }
            catch (SQLException c ) {
                System.out.println("An error has occurred on Table Creation");
            }
            
        }
        connection = con;
        getCommand("add-location").setExecutor(new AddLocation(this));
        getCommand("jumpto").setExecutor(new Jump(this));
        getCommand("remove-location").setExecutor(new RemoveLocation(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
