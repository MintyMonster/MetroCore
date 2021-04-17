package uk.co.minty_studios.metrocore.filehandling;

import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;

public class PlayerJoinDatabase {

    private final MetroCore plugin;

    private static Connection connection;
    private static Statement statement;

    public PlayerJoinDatabase(MetroCore plugin){
        this.plugin = plugin;
    }

    public void addToDatabase(Player player){
        String name = player.getName();
        String UUID = player.getUniqueId().toString();
        String IP = player.getAddress().getAddress().getHostAddress();
        String date = LocalDateTime.now().toString();
        File dir = plugin.getDataFolder();
        File playerDataFolder = new File(dir + File.separator + "PlayerData" + File.separator);
        File playerData = new File(playerDataFolder, "PlayerData.db");
        String path = playerData.getPath();
        int rowCount = 0;

        try{
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM PLAYERDATA");
            rs.next();
            rowCount = rs.getInt(1) + 1;
            rs.close();

            ResultSet rsn = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERDATA WHERE NAME = '" + name + "'");
            rsn.next();
            int playerNum = rsn.getInt("ROWCOUNT");
            rsn.close();

            if(playerNum != 1){
                statement.executeUpdate("INSERT INTO PLAYERDATA (ID,NAME,UUID,IP,DATE) VALUES (" + String.valueOf(rowCount) + ", '" + name + "', '" + UUID + "', '" + IP + "', '" + date + "');");
                statement.close();
                connection.close();
                plugin.getLogger().info("[MetroCore] Added " + name + " to PlayerData");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
