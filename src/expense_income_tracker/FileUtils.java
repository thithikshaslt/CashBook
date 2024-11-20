package expense_income_tracker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static void saveRecordsToCSV(ExpenseIncomeEntry entry) {
        String fileName = "records.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            // Write the entry to the CSV in the format: username,date,description,amount,type,category
            String record = String.join(",", entry.getUsername(), entry.getDate(),
                    entry.getDescription(),
                    String.valueOf(entry.getAmount()),
                    entry.getType(),
                    entry.getCategory());
            bw.write(record);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ExpenseIncomeEntry> loadRecordsFromCSV(String username) {
        List<ExpenseIncomeEntry> entries = new ArrayList<>();
        String fileName = "records.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Assuming CSV values are comma-separated

                if (values.length < 6) {
                    System.out.println("Insufficient data in line: " + line);
                    continue; // Skip this line if there are not enough values
                }

                String csvUsername = values[0]; // Username
                if (!csvUsername.equals(username)) {
                    continue;
                }

                String date = values[1]; // Date
                String description = values[2]; // Description
                double amount = Double.parseDouble(values[3]); // Amount (4th index)
                String type = values[4]; // Type (5th index)
                String category = values[5]; // Category (6th index)

                ExpenseIncomeEntry entry = new ExpenseIncomeEntry(csvUsername,date, description,amount,  type, category);
                entries.add(entry); // Add to the list of entries
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
        }
        return entries;
    }
}
