
// Purpose: This class is responsible for managing the database connection and providing a connection object to the caller.
// Contribution: Joshua [100%]
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseConnection {
  private static HikariDataSource dataSource;

  public static void initialize(boolean isRemote) {
    HikariConfig config = new HikariConfig();

    if (isRemote) {
      // EC2 configuration
      config.setJdbcUrl(
          "jdbc:mysql://ec2-54-157-250-147.compute-1.amazonaws.com:3306/chad?allowPublicKeyRetrieval=true&useSSL=false");

      // If SSL is enabled on your EC2 MySQL instance
      config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
      config.addDataSourceProperty("useSSL", "false");
      config.addDataSourceProperty("requireSSL", "false");
      config.addDataSourceProperty("verifyServerCertificate", "false"); // Set to true if using certificates

      config.addDataSourceProperty("connectTimeout", "5000");
      config.addDataSourceProperty("socketTimeout", "30000");
      config.setConnectionTestQuery("SELECT 1");

      // EC2 MySQL credentials
      config.setUsername("root");
      config.setPassword("root2");

      System.out.println("Attempting connection with URL: " + config.getJdbcUrl());
      System.out.println("Username: " + config.getUsername());
    } else {
      // Local configuration
      config.setJdbcUrl("jdbc:mysql://localhost:3306/chad");
      config.setUsername("root");
      config.setPassword("root");
    }

    config.setMaximumPoolSize(10);
    dataSource = new HikariDataSource(config);
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public static void closePool() {
    if (dataSource != null) {
      dataSource.close();
    }
  }

  public static void testConnection() { // Changed to public static
    System.out.println("\nTesting database connection...");
    try {
      // Initialize connection with remote flag
      DatabaseConnection.initialize(true); // true for EC2

      Connection connec = null;
      try {
        connec = DatabaseConnection.getConnection();
        if (connec != null && !connec.isClosed()) {
          System.out.println("[OK] Connection to EC2 MySQL successful!");
          System.out.println("  URL: " + connec.getMetaData().getURL());
          System.out.println("  Database: " + connec.getCatalog());
        }
      } catch (SQLException e) {
        System.err.println("[X] Failed to connect: " + e.getMessage());
        e.printStackTrace();
      } finally {
        if (connec != null) {
          connec.close();
        }
      }
    } catch (Exception e) {
      System.err.println("[X] Critical error: " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println("Connection test complete.\n");
  }
}
