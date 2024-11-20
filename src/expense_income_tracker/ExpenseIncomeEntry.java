package expense_income_tracker;

public class ExpenseIncomeEntry {
    private String date;
    private String description;
    private double amount;
    private String type;
    private String category;
    private String username;

    public ExpenseIncomeEntry(String username, String date,String description, double amount,  String type, String category) {
        this.username=username;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }


    public String getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getCategory() { return category; }

    public String getUsername() {
        return username;
    }
}
