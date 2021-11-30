package model;

import java.sql.*;

public class SQL {

    public static class Connect {
        /**
         * Connect to a sample database
         */
        public static void connect() {
            Connection conn = null;
            try {
                // db parameters
                String url = "jdbc:sqlite:C:/sqlite/JTP.db";
                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite has been established.");

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } // end connect()
    } // end Class Connect

    public static class Create {

        public static void createNewDatabase(String fileName) {

            String url = "jdbc:sqlite:C:/sqlite/" + fileName;

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
    } // end Class Create

    public static class CreateTable {

        public static void createNewTable() {
            // SQLite connection string
            String url = "jdbc:sqlite:C://sqlite/SSSIT.db";

            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS employees (\n"
                    + " id integer PRIMARY KEY,\n"
                    + " name text NOT NULL,\n"
                    + " capacity real\n"
                    + ");";

            try {
                Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } // end createNewTable()
    } // end Class CreateTable

    public static class InsertRecords {

        private Connection connect() {
            // SQLite connection string
            String url = "jdbc:sqlite:C://sqlite/SSSIT.db";
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return conn;
        } // end connect()


        public void insert(String name, double capacity) {
            String sql = "INSERT INTO employees(name, capacity) VALUES(?,?)";

            try {
                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setDouble(2, capacity);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } // end insert(String, double)
    } // end Class InsertRecords

    public static class SelectRecords {

        private Connection connect() {
            // SQLite connection string
            String url = "jdbc:sqlite:C://sqlite/SSSIT.db";
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return conn;
        } // end connect()


        public void selectAll() {
            String sql = "SELECT * FROM employees";

            try {
                Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                // loop through the result set
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + "\t" +
                            rs.getString("name") + "\t" +
                            rs.getDouble("capacity"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } // end selectAll()
    } // end Class SelectRecords
} // end Class SQL