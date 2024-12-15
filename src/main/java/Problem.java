
// Purpose: This class is responsible for creation of a Problem object
// Contribution: Gavin [100%]
import java.sql.Timestamp;

public class Problem {
  private int id;
  private String description;
  private Timestamp createdAt;

  public Problem(int id, String description, Timestamp createdAt) {
    this.id = id;
    this.description = description;
    this.createdAt = createdAt;
  }

  // Getters
  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return String.format("[%d] %s (Created: %s)",
        id, description, createdAt.toString());
  }
}