package expense_income_tracker;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseIncomeTableModel extends AbstractTableModel {
    private final List<ExpenseIncomeEntry> entries;
    private List<ExpenseIncomeEntry> filteredEntries;

    public ExpenseIncomeTableModel() {
        entries = new ArrayList<>();
        filteredEntries = new ArrayList<>(entries);
    }

    @Override
    public int getRowCount() {
        return filteredEntries.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ExpenseIncomeEntry entry = filteredEntries.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> entry.getDate();
            case 1 -> entry.getDescription();
            case 2 -> Math.abs(entry.getAmount()) ;
            case 3 -> entry.getType();
            case 4 -> entry.getCategory();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> "Date";
            case 1 -> "Description";
            case 2 -> "Amount";
            case 3 -> "Type";
            case 4 -> "Category";
            default -> super.getColumnName(columnIndex);
        };
    }

    public void addEntry(ExpenseIncomeEntry entry) {
        entries.add(entry);
        filteredEntries.add(entry);
        fireTableDataChanged();
    }

    public void editEntry(int rowIndex, ExpenseIncomeEntry updatedEntry) {
        ExpenseIncomeEntry oldEntry = filteredEntries.get(rowIndex);
        int originalIndex = entries.indexOf(oldEntry);
        entries.set(originalIndex, updatedEntry);
        filteredEntries.set(rowIndex, updatedEntry);
        fireTableDataChanged();
    }

    public void removeEntry(int rowIndex) {
        ExpenseIncomeEntry entryToRemove = filteredEntries.get(rowIndex);
        entries.remove(entryToRemove);
        filteredEntries.remove(rowIndex);
        fireTableDataChanged();
    }

    public void filterByCategory(String category) {
        if (category.equals("All")) {
            filteredEntries = new ArrayList<>(entries);
        } else {
            filteredEntries = entries.stream()
                    .filter(entry -> entry.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public void removeFilter() {
        filteredEntries = new ArrayList<>(entries);
        fireTableDataChanged();
    }

    public List<ExpenseIncomeEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }
    public ExpenseIncomeEntry getEntryAt(int index) {
        return entries.get(index);
    }


    public double getTotalExpense() {
        return entries.stream()
                .filter(entry -> entry.getAmount() < 0)
                .mapToDouble(ExpenseIncomeEntry::getAmount)
                .sum();
    }

}
