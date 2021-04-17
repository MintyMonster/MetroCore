package uk.co.minty_studios.metrocore.database;

import uk.co.minty_studios.metrocore.MetroCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;

public class NotesDatabase {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public NotesDatabase(MetroCore plugin){
        this.plugin = plugin;
    }

    private Connection getConnection(){
        File dir = plugin.getDataFolder();
        File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
        File file = new File(Folder, "AidanNotes.db");
        String path = file.getPath();

        try{
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    public void addNote(String message){
        int count = getNotesCount();
        Connection con = getConnection();
        try{
            statement = con.createStatement();
            statement.executeUpdate("INSERT INTO AIDANNOTES (ID,NOTE,STATUS) VALUES (" + count + ", '" + message + "', 'In progress'");
            statement.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void removeNote(String id){
        Connection con = getConnection();
        try{
            statement = con.createStatement();
            statement.executeUpdate("UPDATE AIDANNOTES SET STATUS = 'removed' WHERE ID = " + id);
            statement.close();
            con.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getNotesCount(){
        Connection con = getConnection();
        int result = 0;
        try{
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM AIDANNOTES");
            rs.next();
            result = rs.getInt(1);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    public void listNotes(Player player){
        Connection con = getConnection();
        try{
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ID, NOTE, STATUS FROM AIDANNOTES WHERE STATUS = 'In progress'");
            while(rs.next()){
                int id = rs.getInt("ID");
                String note = rs.getString("NOTE");
                player.sendMessage(ChatColor.GRAY + "#" + id + ": " + ChatColor.GREEN + note);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
