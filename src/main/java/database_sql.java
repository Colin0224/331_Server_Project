
// Purpose: This class is running the main application and handling user input for the Community Health Assessment Data Collaborative Environment.
// Contribution: Joshua [33%], Colin [33%], Gavin [33%]
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;

public class database_sql {

  private static final int SPECIFY_PROBLEM = 1;
  private static final int CONTRIBUTE_SQL = 2;
  private static final int VIEW_AND_RUN = 3;
  private static final int EXIT = 4;

  public static void main(String[] args) {
    DatabaseConnection.testConnection();
    database_sql app = new database_sql();
    app.testImplementation();
    app.run();
  }

  public void run() {
    boolean running = true;
    while (running) {
      displayMenu();
      int choice = getUserChoice();
      running = handleMenuChoice(choice);
    }
  }

  private void displayMenu() {
    System.out.println("\n=== Community Health Assessment Data Collaborative Environment ===");
    System.out.println("1. Specify a problem (Enter query description)");
    System.out.println("2. Contribute an SQL query");
    System.out.println("3. View and run queries");
    System.out.println("4. Exit");
    System.out.print("Enter your choice: ");
  }

  private int getUserChoice() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      try {
        String input = scanner.nextLine().trim();
        int choice = Integer.parseInt(input);
        if (choice >= SPECIFY_PROBLEM && choice <= EXIT) {
          return choice;
        } else {
          System.out.println("Please enter a number between " + SPECIFY_PROBLEM + " and " + EXIT);
        }
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number");
      }
      System.out.print("Enter your choice: ");
    }
  }

  private boolean handleMenuChoice(int choice) {
    switch (choice) {
      case SPECIFY_PROBLEM:
        specifyProblem();
        return true;
      case CONTRIBUTE_SQL:
        contributeSql();
        return true;
      case VIEW_AND_RUN:
        viewAndRunQueries();
        return true;
      case EXIT:
        System.out.println("Goodbye!");
        return false;
      default:
        System.out.println("Invalid choice. Try again.");
        return true;
    }
  }

  private void specifyProblem() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\nEnter problem description:");
    String description = scanner.nextLine();

    ProblemHandling problemDao = new ProblemHandling();
    problemDao.createProblem(description);

  }

  private void contributeSql() {
    Scanner scanner = new Scanner(System.in);
    ProblemHandling problemDao = new ProblemHandling();

    // Show problems without SQL
    System.out.println("\nProblems needing SQL solutions:");
    List<Problem> problems = problemDao.getAllProblems();
    problems.forEach(p -> {
      if (!problemDao.hasExistingSql(p.getId())) {
        System.out.println(p);
      }
    });

    // Show data dictionary with loop
    System.out.println("\n=== CHAD Data Dictionary ===");
    System.out.println("Available columns: " + DataDictionary.getColumnsList());

    while (true) {
      System.out.print("\nEnter column name for details (or press Enter to proceed with query): ");
      String columnName = scanner.nextLine().trim().toUpperCase();

      if (columnName.isEmpty()) {
        break;
      }

      System.out.println(DataDictionary.getColumnExample(columnName));
      System.out.println("\nExample Query:");
      System.out.println("SELECT * FROM chad_encoded_data WHERE " + columnName + " & 1 > 0");
    }

    // Get problem and query
    System.out.print("\nEnter Problem ID: ");
    int problemId = scanner.nextInt();
    scanner.nextLine();

    System.out.println("\nEnter SQL Query:");
    String queryText = scanner.nextLine();

    SQLQueryHandling queryHandler = new SQLQueryHandling();
    queryHandler.createQuery(problemId, queryText);
  }

  private void viewAndRunQueries() {
    Scanner scanner = new Scanner(System.in);
    SQLQueryHandling queryHandler = new SQLQueryHandling();
    List<SQLQuery> queries = queryHandler.getAllQueries();

    System.out.println("\n=== Available Queries ===");
    if (queries.isEmpty()) {
      System.out.println("No queries found. Please contribute a query first.");
      return;
    }
    queries.forEach(System.out::println);

    System.out.print("\nEnter Query ID to execute (0 to cancel): ");
    int queryId = scanner.nextInt();
    scanner.nextLine();

    if (queryId == 0) {
      return;
    }

    SQLQuery selectedQuery = queries.stream()
        .filter(q -> q.getId() == queryId)
        .findFirst()
        .orElse(null);

    if (selectedQuery == null) {
      System.out.println("Invalid Query ID!");
      return;
    }

    String executableQuery = selectedQuery.replaceParameters();
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(executableQuery)) {

      // Get metadata for column names
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      System.out.printf("%n=== Query Results ===%n");
      // Headers
      for (int i = 1; i <= columnCount; i++) {
        System.out.printf("%-20s", metaData.getColumnName(i));
      }
      System.out.println("\n" + String.join("", Collections.nCopies(20 * columnCount, "=")));

      // Data with row counter
      int rowCount = 0;
      while (rs.next()) {
        rowCount++;
        for (int i = 1; i <= columnCount; i++) {
          System.out.printf("%-20s", rs.getString(i));
        }
        System.out.println();
      }
      System.out.printf("%nTotal rows: %d%n", rowCount);

    } catch (SQLException e) {
      System.err.println("Error executing query: " + e.getMessage());
    }
  }

  private void testImplementation() {
    ProblemHandling ph = new ProblemHandling();
    ph.clearAllData();
  }

}
