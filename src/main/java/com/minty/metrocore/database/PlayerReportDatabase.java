package com.minty.metrocore.database;

import com.minty.metrocore.MetroCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;

public class PlayerReportDatabase {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public PlayerReportDatabase(MetroCore plugin){
        this.plugin = plugin;
    }

    private Connection getConnection(){
        File dir = plugin.getDataFolder();
        File folder = new File(dir + File.separator + "PlayerReports" + File.separator);
        File file = new File(folder, "PlayerReports.db");
        String path = file.getPath();

        try{
            connection = DriverManager.getConnection("jdbc:sqlite" + path);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    public void submitPlayerReport(String name, String reported, String reason){
        int totalCount = getTotalCount();

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        String currentDate = formatter.format(new Date(System.currentTimeMillis()));

        try{
            Connection con = getConnection();
            statement = con.createStatement();
            statement.executeUpdate("INSERT INTO PLAYERREPORTS (ID,PLAYER,REPORTED,REASON,DATE) VALUES ("
                    + totalCount + ", '" + name + "', '" + reported + "', '" + reason + "', '" + currentDate + "' );");
            statement.close();
            con.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getTotalCount(){
        int reportCount = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS");
            rs.next();
            reportCount = rs.getInt("ROWCOUNT");
            con.close();
            statement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return reportCount;
    }

    public int getTotalReportsFromPlayer(String name){
        int totalReports = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
            rs.next();
            totalReports = rs.getInt("ROWCOUNT");
            con.close();
            statement.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return totalReports;
    }

    public int getTotalReportsAboutPlayer(String name){
        int totalReports = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS WHERE REPORTED = '" + name + "'");
            rs.next();
            totalReports = rs.getInt("ROWCOUNT");
            con.close();
            statement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return totalReports;
    }

    public void getReportHistory(Player sender, String name){
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");

            while(rs.next()){
                int id = rs.getInt("ID");
                String player = rs.getString("PLAYER");
                String reported = rs.getString("REPORTED");
                String reason = rs.getString("REASON");
                String date = rs.getString("DATE");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("playerreport.report_submitted_history_format")
                                .replaceAll("%date%", date)
                                .replaceAll("%player%", reported)
                                .replaceAll("%reason%", reason)));
            }
            rs.close();
            con.close();
            statement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getReportsAbout(Player sender, String name){
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM PLAYERREPORTS WHERE REPORTED = " + name + "'");

            while(rs.next()){
                int id = rs.getInt("ID");
                String player = rs.getString("PLAYER");
                String report = rs.getString("REPORTED");
                String reason = rs.getString("REASON");
                String date = rs.getString("DATE");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("playerreport.report_received_history_format")
                                .replaceAll("%date%", date)
                                .replaceAll("%reporter%", player)
                                .replaceAll("%reason%", reason)));
            }
            rs.close();
            con.close();
            statement.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
