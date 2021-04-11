package com.minty.metrocore.filehandling;

import com.minty.metrocore.MetroCore;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {

    private static Connection connection;
    private static Statement statement;
    private final MetroCore plugin;
    private final CreateDirectory createDirectory;


    public CreateDatabase(MetroCore plugin, CreateDirectory createDirectory) {
        this.plugin = plugin;
        this.createDirectory = createDirectory;
    }

    public void createDb(String directory, String filename, String sql){
        File dir = createDirectory.getDirectory(directory);
        File file = new File(dir, filename);
        String path = file.getPath();
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
