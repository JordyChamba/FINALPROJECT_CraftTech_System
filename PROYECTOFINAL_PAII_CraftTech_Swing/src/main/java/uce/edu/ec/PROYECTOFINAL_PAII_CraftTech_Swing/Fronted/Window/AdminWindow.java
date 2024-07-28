/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (FRONTED)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Window;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Form.MaterialForm;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Form.ProductForm;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Login.LoginForm;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.model.MusicPlayer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminWindow extends JFrame {

    private static final Color GREEN_COLOR = new Color(76, 175, 80);
    private static final Color BLUE_COLOR = new Color(33, 150, 243);
    private static final Color YELLOW_COLOR = new Color(255, 193, 7);
    private static final Color ORANGE_COLOR = new Color(255, 87, 34);

    private JTable ordersTable;
    private JTable completedOrdersTable;
    private JScrollPane scrollPane;
    private JScrollPane completedScrollPane;
    private JButton changeAccountButton;
    private JButton createMaterialButton;
    private JButton createProductButton;
    private JButton refreshButton;
    private JButton playMusicButton;
    private MusicPlayer musicPlayer;
    private ExecutorService executorService;

    public AdminWindow() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(2); // Configurar un pool de hilos
        musicPlayer = new MusicPlayer();

        initUI();
        fetchOrdersFromServer();
        addTableListeners();
    }

    private void initUI() {
        setTitle("Admin Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1));

        ordersTable = new JTable();
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrollPane = new JScrollPane(ordersTable);

        completedOrdersTable = new JTable();
        completedOrdersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        completedOrdersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        completedScrollPane = new JScrollPane(completedOrdersTable);

        JPanel tablePanel = new JPanel(new GridLayout(1, 2));
        tablePanel.add(scrollPane);
        tablePanel.add(completedScrollPane);

        add(tablePanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        changeAccountButton = createButton("Change Account / Logout", GREEN_COLOR);
        changeAccountButton.addActionListener(e -> changeAccount());
        buttonPanel.add(changeAccountButton);

        createMaterialButton = createButton("Create Material", BLUE_COLOR);
        createMaterialButton.addActionListener(e -> showCreateMaterialPage());
        buttonPanel.add(createMaterialButton);

        createProductButton = createButton("Create Product", YELLOW_COLOR);
        createProductButton.addActionListener(e -> showCreateProductPage());
        buttonPanel.add(createProductButton);

        refreshButton = createButton("Refresh", ORANGE_COLOR);
        refreshButton.addActionListener(e -> fetchOrdersFromServer());
        buttonPanel.add(refreshButton);

        playMusicButton = createButton("Play Music", BLUE_COLOR); // Ajusta el color si es necesario
        playMusicButton.addActionListener(e -> playMusic());
        buttonPanel.add(playMusicButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        return button;
    }

    private void changeAccount() {
        this.dispose();
        LoginForm loginPage = new LoginForm();
        loginPage.setVisible(true);
    }

    private void fetchOrdersFromServer() {
        executorService.submit(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/orders")).GET().build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<List<Object>> inProcessData = new ArrayList<>();
                    List<List<Object>> completedData = new ArrayList<>();

                    JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    for (JsonElement element : jsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();
                        List<Object> row = new ArrayList<>();

                        row.add(jsonObject.has("id") && !jsonObject.get("id").isJsonNull() ? jsonObject.get("id").getAsLong() : null);
                        row.add(jsonObject.has("productName") && !jsonObject.get("productName").isJsonNull() ? jsonObject.get("productName").getAsString() : "N/A");
                        row.add(jsonObject.has("userId") && !jsonObject.get("userId").isJsonNull() ? jsonObject.get("userId").getAsLong() : "N/A");

                        String dateString = jsonObject.has("orderDate") && !jsonObject.get("orderDate").isJsonNull() ? jsonObject.get("orderDate").getAsString() : "";
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(dateString);
                            row.add(sdf.format(date));
                        } catch (Exception e) {
                            row.add("Invalid Date");
                        }

                        row.add(jsonObject.has("status") && !jsonObject.get("status").isJsonNull() ? jsonObject.get("status").getAsString() : "Unknown");

                        if ("COMPLETED".equals(jsonObject.get("status").getAsString())) {
                            completedData.add(row);
                        } else {
                            inProcessData.add(row);
                        }
                    }

                    SwingUtilities.invokeLater(() -> {
                        updateTable(ordersTable, inProcessData);
                        updateTable(completedOrdersTable, completedData);
                    });

                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error fetching orders from the server."));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error fetching orders: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
    }

    private void updateTable(JTable table, List<List<Object>> data) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Product");
        model.addColumn("User");
        model.addColumn("Date");
        model.addColumn("Status");

        for (List<Object> row : data) {
            model.addRow(row.toArray());
        }

        table.setModel(model);
    }

    private void addTableListeners() {
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Long id = (Long) ordersTable.getValueAt(selectedRow, 0);
                    String status = (String) ordersTable.getValueAt(selectedRow, 4);

                    if ("IN_PROGRESS".equals(status)) {
                        fetchOrderDetails(id);
                    }
                }
            }
        });

        completedOrdersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = completedOrdersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    JOptionPane.showMessageDialog(this, "Completed orders cannot be edited.");
                }
            }
        });
    }

    private void fetchOrderDetails(Long id) {
        executorService.submit(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/orders/" + id)).GET().build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                    String product = jsonObject.has("productName") ? jsonObject.get("productName").getAsString() : "Unknown";
                    JsonArray stepsArray = jsonObject.has("steps") ? jsonObject.get("steps").getAsJsonArray() : new JsonArray();
                    List<String> steps = new ArrayList<>();
                    List<Integer> times = new ArrayList<>();

                    for (JsonElement stepElement : stepsArray) {
                        JsonObject stepObject = stepElement.getAsJsonObject();
                        steps.add(stepObject.get("stepName").getAsString());
                        times.add(stepObject.get("duration").getAsInt());
                    }

                    SwingUtilities.invokeLater(() -> new ProductionWindow(id, product, steps, times));
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error fetching order details."));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error fetching order details: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
    }

    private void showCreateMaterialPage() {
        SwingUtilities.invokeLater(() -> new MaterialForm(this).setVisible(true));
    }

    private void showCreateProductPage() {
        SwingUtilities.invokeLater(() -> new ProductForm(this).setVisible(true));
    }

    private void playMusic() {
        executorService.submit(() -> musicPlayer.play());
    }
}