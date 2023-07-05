import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ATM_GUI extends JFrame {
    private Map<String, String> users;
    private Map<String, Double> balances;
    private Map<String, String> transactionHistory;

    private JTextField userIdField;
    private JPasswordField pinField;
    private JTextArea transactionHistoryArea;

    public ATM_GUI() {
        users = new HashMap<>();
        balances = new HashMap<>();
        transactionHistory = new HashMap<>();

        setTitle("ATM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // User ID label and text field
        JLabel userIdLabel = new JLabel("User ID: ");
        userIdField = new JTextField(15);
        add(userIdLabel);
        add(userIdField);

        // PIN label and password field
        JLabel pinLabel = new JLabel("PIN: ");
        pinField = new JPasswordField(15);
        add(pinLabel);
        add(pinField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        add(loginButton);

        // Transaction history area
        transactionHistoryArea = new JTextArea(10, 25);
        transactionHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionHistoryArea);
        add(scrollPane);

        pack();
        setVisible(true);
    }

    public void addUser(String userId, String pin, double initialBalance) {
        users.put(userId, pin);
        balances.put(userId, initialBalance);
        transactionHistory.put(userId, "");
    }

    public boolean login(String userId, String pin) {
        String storedPin = users.get(userId);
        return storedPin != null && storedPin.equals(pin);
    }

    public void showTransactionHistory(String userId) {
        String history = transactionHistory.get(userId);
        transactionHistoryArea.setText(history.isEmpty() ? "No transactions." : history);
    }

    public void withdraw(String userId, double amount) {
        double balance = balances.get(userId);
        if (amount <= balance) {
            balance -= amount;
            balances.put(userId, balance);
            String history = transactionHistory.get(userId);
            history += "Withdraw: $" + amount + "\n";
            transactionHistory.put(userId, history);
            JOptionPane.showMessageDialog(this, "Withdraw successful.");
        } else {
            JOptionPane.showMessageDialog(this, "Insufficient balance.");
        }
    }

    public void deposit(String userId, double amount) {
        double balance = balances.get(userId);
        balance += amount;
        balances.put(userId, balance);
        String history = transactionHistory.get(userId);
        history += "Deposit: $" + amount + "\n";
        transactionHistory.put(userId, history);
        JOptionPane.showMessageDialog(this, "Deposit successful.");
    }

    public void transfer(String senderId, String receiverId, double amount) {
        double senderBalance = balances.get(senderId);
        if (amount <= senderBalance) {
            double receiverBalance = balances.get(receiverId);

            senderBalance -= amount;
            receiverBalance += amount;

            balances.put(senderId, senderBalance);
            balances.put(receiverId, receiverBalance);

            String senderHistory = transactionHistory.get(senderId);
            senderHistory += "Transfer to " + receiverId + ": $" + amount + "\n";
            transactionHistory.put(senderId, senderHistory);

            String receiverHistory = transactionHistory.get(receiverId);
            receiverHistory += "Transfer from " + senderId + ": $" + amount + "\n";
            transactionHistory.put(receiverId, receiverHistory);

            JOptionPane.showMessageDialog(this, "Transfer successful.");
        } else {
            JOptionPane.showMessageDialog(this, "Insufficient balance.");
        }
    }

    public void login() {
        String userId = userIdField.getText();
        String pin = new String(pinField.getPassword());

        if (login(userId, pin)) {
            JOptionPane.showMessageDialog(this, "Login successful.");

            // Create and show the ATM functionality dialog
            ATMFunctionalityDialog functionalityDialog = new ATMFunctionalityDialog(userId);
            functionalityDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid User ID or PIN.");
        }
    }

    private class ATMFunctionalityDialog extends JDialog {
        private JTextField amountField;
        private JTextField receiverIdField;

        public ATMFunctionalityDialog(String userId) {
            setTitle("ATM Functionality");
            setLayout(new FlowLayout());

            // Transaction history button
            JButton transactionHistoryButton = new JButton("Transaction History");
            transactionHistoryButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showTransactionHistory(userId);
                }
            });
            add(transactionHistoryButton);

            // Withdraw button
            JButton withdrawButton = new JButton("Withdraw");
            withdrawButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String amountString = JOptionPane.showInputDialog(ATMFunctionalityDialog.this,
                            "Enter amount to withdraw:");
                    double amount = Double.parseDouble(amountString);
                    withdraw(userId, amount);
                }
            });
            add(withdrawButton);

            // Deposit button
            JButton depositButton = new JButton("Deposit");
            depositButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String amountString = JOptionPane.showInputDialog(ATMFunctionalityDialog.this,
                            "Enter amount to deposit:");
                    double amount = Double.parseDouble(amountString);
                    deposit(userId, amount);
                }
            });
            add(depositButton);

            // Transfer button
            JButton transferButton = new JButton("Transfer");
            transferButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    receiverIdField = new JTextField(15);
                    amountField = new JTextField(15);

                    JPanel transferPanel = new JPanel();
                    transferPanel.setLayout(new GridLayout(3, 2));
                    transferPanel.add(new JLabel("Receiver's User ID:"));
                    transferPanel.add(receiverIdField);
                    transferPanel.add(new JLabel("Amount to transfer:"));
                    transferPanel.add(amountField);

                    int result = JOptionPane.showConfirmDialog(ATMFunctionalityDialog.this,
                            transferPanel, "Transfer", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        String receiverId = receiverIdField.getText();
                        double amount = Double.parseDouble(amountField.getText());
                        transfer(userId, receiverId, amount);
                    }
                }
            });
            add(transferButton);

            pack();
            setLocationRelativeTo(ATM_GUI.this);
        }
    }

    public static void main(String[] args) {
        ATM_GUI atm = new ATM_GUI();
        atm.addUser("user1", "1234", 1000.0);
        atm.addUser("user2", "5678", 2000.0);
    }
}
