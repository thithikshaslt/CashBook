package expense_income_tracker;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class ExpensesIncomesTracker extends JFrame {

    private final ExpenseIncomeTableModel tableModel;
    private final JTable table;
    private final JTextField dateField;
    private final JTextField descriptionField;
    private final JTextField amountField;
    private final JComboBox<String> typeCombobox;
    private final JComboBox<String> categoryComboBox;
    private final JComboBox<String> filterCategoryComboBox;
    private final JTextField newCategoryField;
    private final JButton addCategoryButton;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton removeButton;
    private final JLabel balanceLabel;
    private double balance;
    private String currentUsername;
    private final List<String> categories;
    private final JButton showChartsButton;



    public ExpensesIncomesTracker() {
        categories = new ArrayList<>();
        categories.add("Food");
        categories.add("Transportation");
        categories.add("Entertainment");
        categories.add("Salary");
        categories.add("Other");

        tableModel = new ExpenseIncomeTableModel();
        table = new JTable(tableModel);
        dateField = new JTextField(10);
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);
        typeCombobox = new JComboBox<>(new String[]{"Expense", "Income"});
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        removeButton = new JButton("Remove");
        balanceLabel = new JLabel("Balance: Rs " + balance);

        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.addActionListener(e -> exportToPDF());


        categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));

        filterCategoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        filterCategoryComboBox.addItem("All"); // Option to show all entries
        filterCategoryComboBox.addActionListener(e -> filterByCategory());

        newCategoryField = new JTextField(10);
        addCategoryButton = new JButton("Add Category");

        showChartsButton = new JButton("Show Charts");
        showChartsButton.addActionListener(e -> showCharts());

        addButton.addActionListener(e -> addEntry());
        editButton.addActionListener(e -> editEntry());
        removeButton.addActionListener(e -> removeEntry());
        addCategoryButton.addActionListener(e -> addCategory());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1;
        inputPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Amount"), gbc);
        gbc.gridx = 1;
        inputPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Type"), gbc);
        gbc.gridx = 1;
        inputPanel.add(typeCombobox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Category"), gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel("Filter by Category"), gbc);
        gbc.gridx = 1;
        inputPanel.add(filterCategoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(new JLabel("New Category"), gbc);
        gbc.gridx = 1;
        inputPanel.add(newCategoryField, gbc);
        gbc.gridx = 2;
        inputPanel.add(addCategoryButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        inputPanel.add(addButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(editButton, gbc);
        gbc.gridx = 2;
        inputPanel.add(removeButton, gbc);

        gbc.gridx = 3;
        inputPanel.add(showChartsButton, gbc);

        gbc.gridx = 4;
        inputPanel.add(exportPdfButton, gbc);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(balanceLabel);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setTitle("CashBook");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addEntry() {
        String date = dateField.getText();
        String description = descriptionField.getText();
        String amountStr = amountField.getText();
        String type = (String) typeCombobox.getSelectedItem();
        double amount = Double.parseDouble(amountField.getText());



        balance += amount;
        balanceLabel.setText("Balance: Rs." + balance);

        clearInputFields();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the Amount", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (type.equals("Expense")) {
            amount *= -1; // Make expense negative
        }

        if (!isValidDate(date)) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String category = (String) categoryComboBox.getSelectedItem();
        ExpenseIncomeEntry entry = new ExpenseIncomeEntry(currentUsername, date, description,amount,type, category);
        tableModel.addEntry(entry); // Update the table model
        saveRecordToCSV(entry); // Save the record to CSV

        balance += amount;
        balanceLabel.setText("Balance: Rs." + balance);

        clearInputFields();
    }

    private boolean isValidDate(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }

        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) {
            return false;
        }

        int daysInMonth;
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                daysInMonth = 31;
                break;
            case 4: case 6: case 9: case 11:
                daysInMonth = 30;
                break;
            case 2:
                // Leap year check
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    daysInMonth = 29; // Leap year
                } else {
                    daysInMonth = 28; // Non-leap year
                }
                break;
            default:
                return false;
        }

        // Check if the day is valid
        return day >= 1 && day <= daysInMonth;
    }

    private void addCategory() {
        String newCategory = newCategoryField.getText().trim();
        if (!newCategory.isEmpty() && !categories.contains(newCategory)) {
            categories.add(newCategory);
            categoryComboBox.addItem(newCategory);
            filterCategoryComboBox.addItem(newCategory);
            newCategoryField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid or duplicate category!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByCategory() {
        String selectedCategory = (String) filterCategoryComboBox.getSelectedItem();
        if (selectedCategory.equals("All")) {
            tableModel.removeFilter();
        } else {
            tableModel.filterByCategory(selectedCategory);
        }
    }

    public void loadUserRecords(String username) {
        currentUsername = username;
        List<ExpenseIncomeEntry> userEntries = FileUtils.loadRecordsFromCSV(username);
        double totalIncome = 0;
        double totalExpense = 0;
        balance = 0;

        for (ExpenseIncomeEntry entry : userEntries) {
            tableModel.addEntry(entry);
            if (entry.getAmount() > 0) {
                totalIncome += entry.getAmount();
            } else {
                totalExpense += Math.abs(entry.getAmount());
            }
            balance += entry.getAmount();
        }

        balanceLabel.setText("Balance: Rs." + balance);
    }

    public void saveRecordToCSV(ExpenseIncomeEntry entry) {
        FileUtils.saveRecordsToCSV(entry);
    }

    private void editEntry() {
        int selectedRowIndex = table.getSelectedRow();
        if (selectedRowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String updatedDate = dateField.getText();
        String updatedDescription = descriptionField.getText();
        String updatedAmountStr = amountField.getText();
        String updatedType = (String) typeCombobox.getSelectedItem();

        if (updatedDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (updatedAmountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the Updated Amount", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double updatedAmount = Double.parseDouble(updatedAmountStr);
            if (updatedAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Updated amount must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (updatedType.equals("Expense")) {
                updatedAmount *= -1; // Make expense negative
            }

            if (!isValidDate(updatedDate)) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updatedCategory = (String) categoryComboBox.getSelectedItem();
            ExpenseIncomeEntry updatedEntry = new ExpenseIncomeEntry(currentUsername,updatedDate, updatedDescription,updatedAmount,  updatedType, updatedCategory);
            tableModel.editEntry(selectedRowIndex, updatedEntry);

            balance += updatedAmount;
            balanceLabel.setText("Balance: Rs." + balance);

            clearInputFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Updated Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeEntry() {
        int selectedRowIndex = table.getSelectedRow();
        if (selectedRowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double removedAmount = (double) table.getValueAt(selectedRowIndex, 2);
        tableModel.removeEntry(selectedRowIndex);

        balance -= removedAmount;
        balanceLabel.setText("Balance: Rs." + balance);
    }

    private void clearInputFields() {
        dateField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        typeCombobox.setSelectedIndex(0);
        newCategoryField.setText(""); // Clear new category field
    }

    private void showCharts() {
        JFrame chartFrame = new JFrame("Charts");
        chartFrame.setLayout(new GridLayout(2, 1));

        JFreeChart expenseIncomeChart = ChartUtils.createExpenseIncomeChart(balance, tableModel.getTotalExpense());
        JFreeChart categoryBreakdownChart = ChartUtils.createCategoryBreakdownChart(tableModel.getAllEntries());

        ChartPanel expenseIncomeChartPanel = new ChartPanel(expenseIncomeChart);
        ChartPanel categoryBreakdownChartPanel = new ChartPanel(categoryBreakdownChart);

        chartFrame.add(expenseIncomeChartPanel);
        chartFrame.add(categoryBreakdownChartPanel);

        chartFrame.setSize(800, 600);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setVisible(true);
    }

    private void exportToPDF() {
        if (currentUsername == null) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String filePath = currentUsername + "_expense_records.pdf"; // Define the file name
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Expense Records for " + currentUsername).setFontSize(20).setBold());

            Table table = new Table(new float[]{1, 3, 1, 1, 2}); // Adjust column widths as necessary
            table.addHeaderCell("Date");
            table.addHeaderCell("Description");
            table.addHeaderCell("Amount");
            table.addHeaderCell("Type");
            table.addHeaderCell("Category");

            List<ExpenseIncomeEntry> entries = tableModel.getAllEntries();
            for (ExpenseIncomeEntry entry : entries) {
                table.addCell(entry.getDate());
                table.addCell(entry.getDescription());
                table.addCell(String.valueOf(Math.abs(entry.getAmount()))); // Change to this
                table.addCell(entry.getType());
                table.addCell(entry.getCategory());
            }

            document.add(table);

            document.add(new Paragraph("Total Balance: Rs " + balance).setFontSize(14).setBold());

            document.close();
            JOptionPane.showMessageDialog(this, "PDF exported successfully to " + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting to PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpensesIncomesTracker());
    }
}
