package RoniShlomo_And_NikolYosef;

import java.sql.*;

public class Main_db {
    public static void main(String[] args) {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            // Connection details
            String url = "jdbc:postgresql://localhost:5432/superMarket_db";
            String user = "postgres";
            String password = "Ny12345678";

            // Connect to the database
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");

            // Create a statement
            Statement stmt = conn.createStatement();

            // Example query: get all users
            ResultSet results = stmt.executeQuery("SELECT * FROM Users");

            while (results.next()) {
                String name = results.getString("name");
                String pass = results.getString("password");
                System.out.println("User: " + name + ", Password: " + pass);
            }

            results.close();
            stmt.close();
            conn.close();

            System.out.println("Done.");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

    }
}
