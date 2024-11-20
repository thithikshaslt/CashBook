package expense_income_tracker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartUtils {

    public double calculateTotalIncome(List<ExpenseIncomeEntry> entries) {
        double totalIncome = 0.0;
        for (ExpenseIncomeEntry entry : entries) {
            if (entry.getAmount() > 0) { // Assuming positive amounts are income
                totalIncome += entry.getAmount();
            }
        }
        return totalIncome;
    }

    public double calculateTotalExpense(List<ExpenseIncomeEntry> entries) {
        double totalExpense = 0.0;
        for (ExpenseIncomeEntry entry : entries) {
            if (entry.getAmount() < 0) { // Assuming negative amounts are expenses
                totalExpense += Math.abs(entry.getAmount()); // Use absolute value
            }
        }
        return totalExpense;
    }

    public static JFreeChart createExpenseIncomeChart(double totalIncome, double totalExpense) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(totalIncome, "Income", "Total");
        dataset.addValue(totalExpense, "Expense", "Total"); // This should directly show positive expense

        JFreeChart barChart = ChartFactory.createBarChart(
                "Expense vs Income",
                "Category",
                "Amount",
                dataset
        );

        return barChart;
    }


    public static JFreeChart createCategoryBreakdownChart(List<ExpenseIncomeEntry> entries) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Double> categoryTotals = calculateCategoryTotals(entries);

        categoryTotals.forEach((category, total) -> {
            if (total > 0) {
                dataset.setValue(category, total);
            }
        });

        return ChartFactory.createPieChart(
                "Category Breakdown",
                dataset,
                true, true, false
        );
    }

    public static Map<String, Double> calculateCategoryTotals(List<ExpenseIncomeEntry> entries) {
        Map<String, Double> categoryTotals = new HashMap<>();

        for (ExpenseIncomeEntry entry : entries) {
            String category = entry.getCategory();
            double amount = entry.getAmount();

            if (amount < 0) {
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + Math.abs(amount));
            }
        }

        return categoryTotals;
    }
}
