package uk.co.minty_studios.metrocore.database;

import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DiscordCodeDatabase {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public DiscordCodeDatabase(MetroCore plugin){
        this.plugin = plugin;
    }

    private Connection getConnection(){
        File dir = plugin.getDataFolder();
        File folder = new File(dir + File.separator + "DiscordLink" + File.separator);
        File file = new File(folder, "DiscordLink.db");
        String path = file.getPath();
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    public void linkPlayerToDiscord(UUID uuid, long id, int code){
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            statement.executeUpdate("INSERT INTO DISCORDLINK (UUID,ID,CODE) VALUES ('" + uuid + "', " + id + ", " + code + ");");
            statement.close();
            con.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public int getTotalCount(){
        int count = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM DISCORDLINK");
            rs.next();
            count = rs.getInt("ROWCOUNT");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return count;
    }

    public Boolean linkExistsUuid(Player player){
        UUID uuid = player.getUniqueId();
        int count = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM DISCORDLINK WHERE UUID = '" + uuid + "'");
            while(rs.next()){
                count += rs.getInt("ROWCOUNT");
            }

            if(count > 0)
                return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public boolean linkExistsDiscord(long id){
        int count = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM DISCORDLINK WHERE ID = " + id);
            while(rs.next()){
                count += rs.getInt("ROWCOUNT");
            }

            if(count > 0)
                return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public long getDiscordId(UUID uuid){
        long id = 0;
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM DISCORDLINK WHERE UUID = '" + uuid + "'");
            rs.next();
            id = rs.getLong("ID");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return id;
    }

    public void removePreviousLink(UUID uuid){
        try{
            Connection con = getConnection();
            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM DISCORDLINK WHERE UUID = '" + uuid + "'");
            statement.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
