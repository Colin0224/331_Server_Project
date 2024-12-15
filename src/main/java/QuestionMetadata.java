
// QuestionMetadata.java
import java.util.Map;

public class QuestionMetadata {
 private String id;
 private String description;
 private Map<Integer, CodeValue> values;

 public static class CodeValue {
  private int code;
  private String description;

  public CodeValue(int code, String description) {
   this.code = code;
   this.description = description;
  }

  public int getCode() {
   return code;
  }

  public String getDescription() {
   return description;
  }
 }

 public QuestionMetadata(String id, String description, Map<Integer, CodeValue> values) {
  this.id = id;
  this.description = description;
  this.values = values;
 }

 public String getId() {
  return id;
 }

 public String getDescription() {
  return description;
 }

 public Map<Integer, CodeValue> getValues() {
  return values;
 }
}