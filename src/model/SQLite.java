package model;

import java.io.File;
import java.sql.*;
import java.util.List;

public class SQLite {

    final static String databasePath = "";                  // path of SQLite database
    final static String databaseName = "recent_files.db";   // name of SQLite database

    public static boolean databaseExists() {
        File file = new File (databaseName);

        return file.exists();
    }
    public static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + databasePath + databaseName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    } // end connect()

    public static void createNewDatabase() {

        String url = "jdbc:sqlite:" + databasePath + databaseName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // end createNewDatabase(String)


    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + databasePath + databaseName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS recentfiles (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " fileImg text NOT NULL,\n"
                + " filePath text NOT NULL UNIQUE\n"

                + ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // end createNewTable()


    public static void insert(String name, String fileImg, String filePath) {
        String sql = "INSERT INTO recentfiles(name, fileImg, filePath) VALUES(?,?,?)" +
                " ON CONFLICT(filePath) DO UPDATE SET name=excluded.name, fileImg=excluded.fileImg";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, fileImg);
            pstmt.setString(3, filePath);
            //pstmt +=
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // end insert(String, double)

    public static void fileList(List<FileData> fileList) {
        String sql = "SELECT * FROM recentfiles";
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                FileData curfile = new FileData();
                curfile.name = rs.getString("name");
                curfile.fileImg = rs.getString("fileImg");
                curfile.filePath = rs.getString("filePath");
                fileList.add(curfile);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // end fileList
} // end Class SQLite