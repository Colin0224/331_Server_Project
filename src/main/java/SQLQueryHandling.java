// Purpose: This class is responsible for handling SQL queries
// Contribution: Colin [100%]

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLQueryHandling {
  private static final String INSERT_QUERY = "INSERT INTO sql_queries (problem_id, query_text) VALUES (?, ?)";
  private static final String SELECT_BY_PROBLEM = "SELECT * FROM sql_queries WHERE problem_id = ?";
  private static final String SELECT_ALL = "SELECT sq.*, p.description as problem_description " +
      "FROM sql_queries sq " +
      "JOIN problems p ON sq.problem_id = p.id " +
      "ORDER BY sq.created_at DESC";

  public void createQuery(int problemId, String queryText) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
      stmt.setInt(1, problemId);
      stmt.setString(2, queryText);
      int affected = stmt.executeUpdate();
      if (affected > 0) {
        System.out.println("[OK] SQL Query saved successfully!");
      }
    } catch (SQLException e) {
      System.err.println("Error saving SQL query: " + e.getMessage());
    }
  }

  public List<SQLQuery> getQueriesForProblem(int problemId) {
    List<SQLQuery> queries = new ArrayList<>();
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PROBLEM)) {
      stmt.setInt(1, problemId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        queries.add(createQueryFromResultSet(rs));
      }
    } catch (SQLException e) {
      System.err.println("Error retrieving queries: " + e.getMessage());
    }
    return queries;
  }

  private SQLQuery createQueryFromResultSet(ResultSet rs) throws SQLException {
    return new SQLQuery(
        rs.getInt("id"),
        rs.getInt("problem_id"),
        rs.getString("query_text"),
        rs.getTimestamp("created_at"),
        rs.getString("problem_description") // Make sure column name matches
    );
  }

  public List<SQLQuery> getAllQueries() {
    List<SQLQuery> queries = new ArrayList<>();
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
      while (rs.next()) {
        queries.add(createQueryFromResultSet(rs));
      }
    } catch (SQLException e) {
      System.err.println("Error retrieving all queries: " + e.getMessage());
    }
    return queries;
  }

}