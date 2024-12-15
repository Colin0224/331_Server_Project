
// Purpose: This class is responsible for handling problems
// Contribution: Gavin [100%]
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemHandling {
  public void createProblem(String description) {
    String sql = "INSERT INTO problems (description) VALUES (?)";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, description);
      int affected = stmt.executeUpdate();

      if (affected > 0) {
        System.out.println("[OK] Problem saved successfully!");
      }

    } catch (SQLException e) {
      System.err.println("Error saving problem: " + e.getMessage());
    }
  }

  public List<Problem> getAllProblems() {
    List<Problem> problems = new ArrayList<>();
    String sql = "SELECT * FROM problems ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Problem problem = new Problem(
            rs.getInt("id"),
            rs.getString("description"),
            rs.getTimestamp("created_at"));
        problems.add(problem);
      }

    } catch (SQLException e) {
      System.err.println("Error retrieving problems: " + e.getMessage());
    }

    return problems;
  }

  public void clearAllData() {
    try (Connection conn = DatabaseConnection.getConnection()) {
      // Disable foreign key checks temporarily
      try (Statement stmt = conn.createStatement()) {
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        stmt.execute("TRUNCATE TABLE sql_queries");
        stmt.execute("TRUNCATE TABLE problems");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        System.out.println("[OK] All data cleared successfully!");
      }
    } catch (SQLException e) {
      System.err.println("Error clearing data: " + e.getMessage());
    }
  }

  public boolean hasExistingSql(int problemId) {
    String sql = "SELECT COUNT(*) FROM sql_queries WHERE problem_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, problemId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
    } catch (SQLException e) {
      System.err.println("Error checking SQL existence: " + e.getMessage());
    }
    return false;
  }

}
