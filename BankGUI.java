
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
class Account {
    private int accNo;
    private String name;
    protected double balance;
    private ArrayList<String> history = new ArrayList<>();

    protected static final double MIN_BALANCE = 0;

    public Account(int accNo, String name, double initialDeposit) {
        this.accNo = accNo;
        this.name = name;
        this.balance = initialDeposit;
        history.add("Account opened with " + initialDeposit);
    }

    public int getAccNo() { return accNo; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public ArrayList<String> getHistory() { return history; }
    public String getType() { return "Current"; }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        history.add("Deposited: " + amount);
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (balance - amount < MIN_BALANCE) return false;
        balance -= amount;
        history.add("Withdrawn: " + amount);
        return true;
    }

    protected void addHistory(String text) {
        history.add(text);
    }
}


class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(int accNo, String name, double initialDeposit, double interestRate) {
        super(accNo, name, initialDeposit);
        this.interestRate = interestRate;
    }

    public double addInterest() {
        double interest = balance * interestRate / 100;
        balance += interest;
        addHistory("Interest added: " + interest);
        return interest;
    }

    @Override
    public String getType() { return "Savings (" + interestRate + "%)"; }
}

public class BankGUI extends JFrame {
    private ArrayList<Account> accounts = new ArrayList<>();
    private int nextAccNo = 1001;

    private static final double INTEREST_RATE = 5;

    private JTextField nameField = new JTextField();
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{"Current", "Savings"});
    private JTextField accNoField = new JTextField();
    private JTextField amountField = new JTextField();
    private JTextArea output = new JTextArea();

    public BankGUI() {
        setTitle("Bank Account System");
        setSize(460, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Account Type:"));
        form.add(typeBox);
        form.add(new JLabel("Account No:"));
        form.add(accNoField);
        form.add(new JLabel("Amount:"));
        form.add(amountField);

        JButton createBtn = new JButton("Create Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton balanceBtn = new JButton("Check Balance");
        JButton historyBtn = new JButton("History");
        JButton interestBtn = new JButton("Add Interest");

        JPanel buttons = new JPanel(new GridLayout(3, 2, 8, 8));
        buttons.add(createBtn);
        buttons.add(depositBtn);
        buttons.add(withdrawBtn);
        buttons.add(balanceBtn);
        buttons.add(historyBtn);
        buttons.add(interestBtn);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createTitledBorder("Output"));
        add(scroll, BorderLayout.CENTER);

        createBtn.addActionListener(e -> createAccount());
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        balanceBtn.addActionListener(e -> checkBalance());
        historyBtn.addActionListener(e -> showHistory());
        interestBtn.addActionListener(e -> addInterest());
    }

    
    private void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private Account findAccount() {
        try {
            int no = Integer.parseInt(accNoField.getText().trim());
            for (Account a : accounts) {
                if (a.getAccNo() == no) return a;
            }
            message("Account not found!");
        } catch (NumberFormatException e) {
            message("Enter a valid account number!");
        }
        return null;
    }

    private double readAmount() {
        try {
            return Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException e) {
            message("Enter a valid amount!");
            return -1;
        }
    }

    
    private void createAccount() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            message("Please enter a name!");
            return;
        }
        double amount = readAmount();
        if (amount < 0) return;

        Account acc;
        if (typeBox.getSelectedItem().equals("Savings")) {
            acc = new SavingsAccount(nextAccNo, name, amount, INTEREST_RATE);
        } else {
            acc = new Account(nextAccNo, name, amount);
        }
        accounts.add(acc);
        output.setText("Account created!\nAccount No: " + nextAccNo
                + "\nName: " + name + "\nType: " + acc.getType()
                + "\nBalance: " + acc.getBalance());
        message("Account created! Your account number is " + nextAccNo);
        nextAccNo++;
    }

    private void deposit() {
        Account a = findAccount();
        if (a == null) return;
        double amount = readAmount();
        if (amount < 0) return;

        if (a.deposit(amount)) {
            output.setText("Deposited: " + amount + "\nNew balance: " + a.getBalance());
        } else {
            message("Invalid amount!");
        }
    }

    private void withdraw() {
        Account a = findAccount();
        if (a == null) return;
        double amount = readAmount();
        if (amount < 0) return;

        if (a.withdraw(amount)) {
            output.setText("Withdrawn: " + amount + "\nNew balance: " + a.getBalance());
        } else {
            message("Insufficient balance or invalid amount!");
        }
    }

    private void checkBalance() {
        Account a = findAccount();
        if (a == null) return;
        output.setText("Acc No : " + a.getAccNo()
                + "\nName   : " + a.getName()
                + "\nType   : " + a.getType()
                + "\nBalance: " + a.getBalance());
    }

    private void showHistory() {
        Account a = findAccount();
        if (a == null) return;
        StringBuilder sb = new StringBuilder("--- History (" + a.getName() + ") ---\n");
        for (String h : a.getHistory()) {
            sb.append(h).append("\n");
        }
        output.setText(sb.toString());
    }

    private void addInterest() {
        Account a = findAccount();
        if (a == null) return;
        if (a instanceof SavingsAccount) {
            double interest = ((SavingsAccount) a).addInterest();
            output.setText("Interest added: " + interest + "\nNew balance: " + a.getBalance());
        } else {
            message("This is not a savings account!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankGUI().setVisible(true));
    }
}
