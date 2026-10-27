import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StockTradingPlatform extends JFrame {
    private static final double STARTING_CASH = 100000.00;
    private double cash = STARTING_CASH;

    private static class Stock {
        String symbol, name;
        double price;
        Stock(String symbol, String name, double price) {
            this.symbol = symbol; this.name = name; this.price = price;
        }
    }

    private static class Transaction {
        String type, symbol;
        int quantity;
        double price, total;
        Transaction(String type, String symbol, int quantity, double price) {
            this.type = type; this.symbol = symbol; this.quantity = quantity;
            this.price = price; this.total = quantity * price;
        }
    }

    private final ArrayList<Stock> market = new ArrayList<>();
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private final Map<String, Integer> holdings = new LinkedHashMap<>();
    private final DefaultTableModel marketModel = model(new String[]{"Symbol", "Company", "Price (₹)"});
    private final DefaultTableModel portfolioModel = model(new String[]{"Symbol", "Shares", "Market Price (₹)", "Value (₹)"});
    private final DefaultTableModel historyModel = model(new String[]{"Type", "Symbol", "Qty", "Price (₹)", "Total (₹)"});
    private final JTable marketTable = new JTable(marketModel);
    private final JTable portfolioTable = new JTable(portfolioModel);
    private final JTable historyTable = new JTable(historyModel);
    private final JTextField quantityField = new JTextField("1", 7);
    private final JLabel cashLabel = new JLabel();
    private final JLabel portfolioValueLabel = new JLabel();
    private final JLabel profitLabel = new JLabel();
    private final JLabel sharesLabel = new JLabel();

    public StockTradingPlatform() {
        super("Stock Trading Platform — Simulation");
        market.add(new Stock("TCS", "Tata Consultancy Services", 3850.00));
        market.add(new Stock("INFY", "Infosys", 1780.00));
        market.add(new Stock("RELIANCE", "Reliance Industries", 2920.00));
        market.add(new Stock("HDFCBANK", "HDFC Bank", 1680.00));
        market.add(new Stock("ITC", "ITC Limited", 445.00));
        market.add(new Stock("WIPRO", "Wipro", 520.00));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(242, 246, 252));
        setLayout(new BorderLayout(12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        refreshAll();
    }

    private static DefaultTableModel model(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 55, 94));
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JLabel title = new JLabel("Stock Trading Platform");
        title.setForeground(Color.WHITE); title.setFont(new Font("SansSerif", Font.BOLD, 26));
        JLabel subtitle = new JLabel("Practice buying and selling stocks with virtual money");
        subtitle.setForeground(new Color(220, 231, 246)); subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 5));
        texts.setOpaque(false); texts.add(title); texts.add(subtitle);
        header.add(texts, BorderLayout.WEST);
        return header;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JPanel marketPanel = panel("Market Watch");
        marketTable.setRowHeight(25); marketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        marketTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        marketPanel.add(new JScrollPane(marketTable), BorderLayout.CENTER);

        JPanel tradeControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tradeControls.setBackground(Color.WHITE);
        tradeControls.add(new JLabel("Quantity:"));
        tradeControls.add(quantityField);
        JButton buyButton = new JButton("Buy Selected");
        buyButton.setBackground(new Color(25, 145, 95)); buyButton.setForeground(Color.WHITE); buyButton.setFocusPainted(false);
        JButton sellButton = new JButton("Sell Selected");
        sellButton.setBackground(new Color(205, 70, 70)); sellButton.setForeground(Color.WHITE); sellButton.setFocusPainted(false);
        tradeControls.add(buyButton); tradeControls.add(sellButton);
        JLabel hint = new JLabel("Select a stock row first"); hint.setForeground(new Color(90, 100, 115));
        tradeControls.add(hint);
        marketPanel.add(tradeControls, BorderLayout.SOUTH);
        buyButton.addActionListener(e -> trade(true));
        sellButton.addActionListener(e -> trade(false));

        JPanel portfolioPanel = panel("My Portfolio");
        portfolioTable.setRowHeight(25); portfolioTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        portfolioPanel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);

        JPanel historyPanel = panel("Transaction History");
        historyTable.setRowHeight(23); historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, marketPanel, portfolioPanel);
        topSplit.setResizeWeight(0.55); topSplit.setBorder(null); topSplit.setDividerSize(8);
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, historyPanel);
        verticalSplit.setResizeWeight(0.56); verticalSplit.setBorder(null); verticalSplit.setDividerSize(8);
        main.add(verticalSplit, BorderLayout.CENTER);
        return main;
    }

    private JPanel panel(String title) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 222, 238)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 17)); label.setForeground(new Color(25, 55, 94));
        p.add(label, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new GridLayout(1, 4, 10, 0));
        footer.setOpaque(false); footer.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        footer.add(statCard("Available Cash", cashLabel, new Color(35, 105, 190)));
        footer.add(statCard("Portfolio Value", portfolioValueLabel, new Color(20, 145, 95)));
        footer.add(statCard("Unrealized P/L", profitLabel, new Color(125, 80, 190)));
        footer.add(statCard("Shares Held", sharesLabel, new Color(190, 120, 25)));
        return footer;
    }

    private JPanel statCard(String title, JLabel value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210, 222, 238)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel heading = new JLabel(title); heading.setForeground(new Color(85, 98, 116));
        value.setFont(new Font("SansSerif", Font.BOLD, 19)); value.setForeground(color);
        card.add(heading); card.add(value); return card;
    }

    private void trade(boolean buying) {
        int row = marketTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a stock from Market Watch.", "Select Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a whole-number quantity greater than zero.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Stock stock = market.get(row);
        double total = stock.price * quantity;
        int owned = holdings.getOrDefault(stock.symbol, 0);
        if (buying) {
            if (total > cash) {
                JOptionPane.showMessageDialog(this, "Insufficient cash. Available: " + money(cash), "Trade Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cash -= total;
            holdings.put(stock.symbol, owned + quantity);
            transactions.add(new Transaction("BUY", stock.symbol, quantity, stock.price));
        } else {
            if (quantity > owned) {
                JOptionPane.showMessageDialog(this, "You only own " + owned + " share(s) of " + stock.symbol + ".", "Trade Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cash += total;
            int remaining = owned - quantity;
            if (remaining == 0) holdings.remove(stock.symbol); else holdings.put(stock.symbol, remaining);
            transactions.add(new Transaction("SELL", stock.symbol, quantity, stock.price));
        }
        refreshAll();
        JOptionPane.showMessageDialog(this, (buying ? "Bought " : "Sold ") + quantity + " share(s) of " + stock.symbol + " for " + money(total) + ".", "Trade Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAll() {
        marketModel.setRowCount(0);
        for (Stock s : market) marketModel.addRow(new Object[]{s.symbol, s.name, money(s.price)});

        portfolioModel.setRowCount(0);
        double value = 0;
        int totalShares = 0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = findStock(entry.getKey());
            if (stock != null) {
                int qty = entry.getValue(); double holdingValue = qty * stock.price;
                value += holdingValue; totalShares += qty;
                portfolioModel.addRow(new Object[]{stock.symbol, qty, money(stock.price), money(holdingValue)});
            }
        }

        historyModel.setRowCount(0);
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            historyModel.addRow(new Object[]{t.type, t.symbol, t.quantity, money(t.price), money(t.total)});
        }
        double profitLoss = value + cash - STARTING_CASH;
        cashLabel.setText(money(cash));
        portfolioValueLabel.setText(money(value));
        profitLabel.setText((profitLoss > 0 ? "+" : "") + money(profitLoss));
        sharesLabel.setText(String.valueOf(totalShares));
    }

    private Stock findStock(String symbol) {
        for (Stock s : market) if (s.symbol.equals(symbol)) return s;
        return null;
    }

    private String money(double amount) { return String.format("₹%,.2f", amount); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockTradingPlatform().setVisible(true));
    }
}
