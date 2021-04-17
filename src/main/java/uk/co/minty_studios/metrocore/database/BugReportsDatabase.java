package uk.co.minty_studios.metrocore.database;

import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;

public class BugReportsDatabase {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public BugReportsDatabase(MetroCore plugin){
        this.plugin = plugin;
    }

    private Connection getConnection(){
        File dir = plugin.getDataFolder();
        File folder = new File(dir + File.separator + "BugReports" + File.separator);
        File file = new File(folder, "BugReports.db");
        String path = file.getPath();
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    public void setFixed(String id){
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            statement.executeUpdate("UPDATE BUGREPORTS SET STATUS = 'fixed' WHERE ID = " + id);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void listBugs(Player player){
        Connection con = getConnection();
        try {
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ID, NAME, REPORT, STATUS FROM BUGREPORTS");

            while(rs.next()){
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                String report = rs.getString("REPORT");
                String result = rs.getString("STATUS");
                if (result.equalsIgnoreCase("fixed")) {
                    String message = plugin.getConfig().getString("bugreport.list_formatting")
                            .replaceAll("%id%", String.valueOf(id))
                            .replaceAll("%name%", name)
                            .replaceAll("%report%", report);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + ChatColor.GREEN + " [Fixed]");
                } else {
                    String message = plugin.getConfig().getString("bugreport.list_formatting")
                            .replaceAll("%id%", String.valueOf(id))
                            .replaceAll("%name%", name)
                            .replaceAll("%report%", report);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + ChatColor.RED + " [" + result + "]");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            plugin.sendWithPrefix(player, "&cSomething went wrong. Check logs for error.");
        }
    }


    public void submitBug(String name, String report){
        Connection con = getConnection();
        try {
            int id = getTotalCount() + 1;

            statement.executeUpdate("INSERT INTO BUGREPORTS (ID,NAME,REPORT,STATUS) VALUES (" + String.valueOf(id) + ", '" + name + "', '" + report + "', 'In progress' );");
            statement.close();
            con.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public int getTotalCount(){
        int count = 0;
        Connection con = getConnection();
        try{
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM BUGREPORTS");
            rs.next();
            count = rs.getInt(1);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return count;
    }

}
