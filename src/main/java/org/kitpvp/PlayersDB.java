package org.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayersDB {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:Players.db");

    }

    // --------Создание таблицы--------
    public static void CreateDB() throws ClassNotFoundException, SQLException {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'players' ('id' TEXT PRIMARY KEY);");
        statmt.execute("CREATE TABLE if not exists 'kits' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nameKit TEXT, " +
                        "owner TEXT REFERENCES players (id) ON DELETE CASCADE);");

    }

    // --------Методы обращения к таблице--------
    public static void setKitPlayerDB(String player) throws SQLException{
        try{
            statmt.executeUpdate("INSERT INTO players (id) VALUES ('"+ player +"')");
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Игрок " + player + " добавлен в базу!");
        } catch (org.sqlite.SQLiteException e){return;}
    }

    public static void setJoinPlayerDB(String player) throws SQLException{
        try{
            statmt.executeUpdate("INSERT INTO players (id) VALUES ('"+ player +"')");
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Игрок " + player + " добавлен в базу!");
        } catch (org.sqlite.SQLiteException e){return;}
    }

    public static List<String> getKitsPlayer(String player) throws ClassNotFoundException, SQLException {
        resSet = statmt.executeQuery("SELECT nameKit" + "  FROM kits" + "  WHERE owner = '"+ player + "';");
        List<String> list = new ArrayList<>();

        while (resSet.next()) {
            String kit = resSet.getString(1);
            list.add(kit);
        }
        return list;
    }

        public static void CloseDB() throws ClassNotFoundException, SQLException {
            try {
                conn.close();
                statmt.close();
                resSet.close();
            }catch (Exception e){
                e.printStackTrace();
            }
    }
}