
// Purpose: This class is responsible for loading the mappings from the mappings.json file and providing the column example for a given column name.
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;
import java.lang.reflect.Type;

public class DataDictionary {
 private static final Map<String, QuestionMetadata> QUESTIONS = new HashMap<>();
 private static final Gson gson = new Gson();

 static {
  loadMappingsFromFile();
 }

 private static void loadMappingsFromFile() {
  String workingDir = System.getProperty("user.dir");
  File jsonFile = new File(workingDir, "mappings.json");

  System.out.println("Looking for mappings.json at: " + jsonFile.getAbsolutePath());

  if (!jsonFile.exists()) {
   System.err.println("mappings.json not found!");
   return;
  }

  try (Reader reader = new FileReader(jsonFile)) {
   Type type = new TypeToken<Map<String, QuestionMetadata>>() {
   }.getType();
   Map<String, QuestionMetadata> loaded = gson.fromJson(reader, type);

   if (loaded == null) {
    System.err.println("Failed to parse JSON - null result");
    return;
   }

   QUESTIONS.putAll(loaded);
   System.out.println("Successfully loaded " + QUESTIONS.size() + " questions");

  } catch (IOException e) {
   System.err.println("Error reading mappings.json: " + e.getMessage());
  } catch (JsonSyntaxException e) {
   System.err.println("Error parsing JSON: " + e.getMessage());
  }
 }

 public static String getColumnExample(String columnName) {
  QuestionMetadata question = QUESTIONS.get(columnName);
  if (question == null) {
   return "Column information not available";
  }

  StringBuilder example = new StringBuilder();
  example.append(String.format("\n=== %s ===\n", question.getId()));
  example.append(question.getDescription()).append("\n\n");
  example.append("Values:\n");

  question.getValues().forEach((k, v) -> example.append(String.format("%s = %s\n", k, v.getDescription())));

  return example.toString();
 }

 public static List<String> getAvailableColumns() {
  return new ArrayList<>(QUESTIONS.keySet());
 }

 public static String getColumnsList() {
  return String.join(", ", getAvailableColumns());
 }

}