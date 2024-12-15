
// Purpose: This class is responsible for creation of a the queries
// Contribution: Colin [100%]
import java.sql.Timestamp;
import java.util.Scanner;

public class SQLQuery {
  private int id;
  private int problemId;
  private String queryText;
  private Timestamp createdAt;
  private String problemDescription;

  public SQLQuery(int id, int problemId, String queryText, Timestamp createdAt, String problemDescription) {
    this.id = id;
    this.problemId = problemId;
    this.queryText = queryText;
    this.createdAt = createdAt;
    this.problemDescription = problemDescription;
  }

  public String getProblemDescription() {
    return problemDescription;
  }

  public int getId() {
    return id;
  }

  public int getProblemId() {
    return problemId;
  }

  public String getQueryText() {
    return queryText;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public String replaceParameters() {
    Scanner scanner = new Scanner(System.in);
    String modifiedQuery = this.queryText;

    // Look for parameters like :param
    while (modifiedQuery.contains(":")) {
      int start = modifiedQuery.indexOf(":");
      int end = modifiedQuery.indexOf(" ", start);
      if (end == -1)
        end = modifiedQuery.length();

      // Get parameter name without colon
      String param = modifiedQuery.substring(start, end);
      String paramName = param.substring(1); // Remove colon

      // Get user input
      System.out.print("Enter value for " + paramName + ": ");
      String value = scanner.nextLine();

      // Replace in query
      modifiedQuery = modifiedQuery.replace(param, value);
    }
    return modifiedQuery;

  }

  @Override
  public String toString() {
    return String.format("Query ID: [%d]\nProblem: %s\nSQL: %s\nCreated: %s\n----------------------------------------",
        id, problemDescription, queryText, createdAt.toString());
  }
}