/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (FRONTED)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.*;

import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Login.LoginForm;

public class ClientWindow extends JFrame {
    private Long currentUserId;
    private static final Color GREEN_COLOR = new Color(76, 175, 80);
    private static final Color BLUE_COLOR = new Color(33, 150, 243);
    private static final Color YELLOW_COLOR = new Color(255, 193, 7);
    private static final Color ORANGE_COLOR = new Color(255, 87, 34);

    private JComboBox<String> materialComboBox;
    private JComboBox<String> productComboBox;
    private JTextField clientField;
    private JButton sendOrderButton;
    private JButton changeAccountButton;
    private JButton viewNotificationsButton;
    private JButton cartButton;

    private Map<String, Long> materialIdMap = new HashMap<>();
    private List<String> cart = new ArrayList<>();

    public ClientWindow(Long userId) {
        this.currentUserId = userId;
        setTitle("Client Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        initComponents();
        setVisible(true);
        loadMaterials();
    }

    private void initComponents() {
        materialComboBox = new JComboBox<>();
        productComboBox = new JComboBox<>();
        clientField = new JTextField(20);

        sendOrderButton = createButton("Send Order", "/send.png", GREEN_COLOR, this::sendOrderToServer);
        changeAccountButton = createButton("Change Account", "/change.png", BLUE_COLOR, this::openLogin);
        viewNotificationsButton = createButton("View Notifications", "/notify.png", YELLOW_COLOR, this::showNotifications);
        cartButton = createButton("View Cart", "/cart.png", ORANGE_COLOR, this::showCart);

        materialComboBox.addActionListener(e -> loadProducts());

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);
        panel.add(new JLabel("Product:"));
        panel.add(productComboBox);
        panel.add(new JLabel("Client:"));
        panel.add(clientField);
        panel.add(new JLabel());
        panel.add(sendOrderButton);
        panel.add(new JLabel());
        panel.add(changeAccountButton);
        panel.add(new JLabel());
        panel.add(viewNotificationsButton);
        panel.add(new JLabel());
        panel.add(cartButton);

        add(panel);
    }

    private JButton createButton(String text, String iconPath, Color backgroundColor, Runnable action) {
        JButton button = new JButton(text);
        ImageIcon icon = resizeIcon(iconPath, 20, 20);
        button.setIcon(icon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void loadMaterials() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/materials")).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                materialComboBox.removeAllItems();
                materialIdMap.clear();

                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    String materialName = jsonObject.get("name").getAsString();
                    Long materialId = jsonObject.get("id").getAsLong();
                    materialComboBox.addItem(materialName);
                    materialIdMap.put(materialName, materialId);
                }
            } else {
                showError("Error loading materials: " + response.body());
            }
        } catch (Exception e) {
            showError("Error loading materials: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        String selectedMaterial = (String) materialComboBox.getSelectedItem();
        if (selectedMaterial == null) return;

        Long materialId = materialIdMap.get(selectedMaterial);
        if (materialId == null) {
            showError("Error: Selected material does not have a valid ID.");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/products/material/" + materialId)).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            productComboBox.removeAllItems();

            if (response.statusCode() == 200) {
                JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    String productName = jsonObject.get("name").getAsString();
                    productComboBox.addItem(productName);
                }
            } else {
                showError("Error loading products: " + response.body());
            }
        } catch (Exception e) {
            showError("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendOrderToServer() {
        String product = (String) productComboBox.getSelectedItem();
        String clientName = clientField.getText();

        if (product == null || clientName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        Long userId = getUserIdByName(clientName);

        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Error: Selected user does not have a valid ID.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String currentDate = LocalDateTime.now(ZoneOffset.UTC).format(formatter);

        String jsonOrder = new Gson().toJson(Map.of("userId", userId, "productName", product, "orderDate", currentDate, "status", "IN_PROGRESS"));

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/orders/create")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonOrder)).build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                JOptionPane.showMessageDialog(this, "Order successfully sent to the server.");
                cart.add("Product: " + product + ", Material: " + materialComboBox.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(this, "Error sending the order to the server.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error sending the order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Long getUserIdByName(String clientName) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/users/name/" + clientName)).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                return jsonObject.get("id").getAsLong();
            } else {
                showError("Error obtaining user ID: " + response.body());
                return null;
            }
        } catch (Exception e) {
            showError("Error obtaining user ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void openLogin() {
        new LoginForm().setVisible(true);
        dispose();
    }

    private void showNotifications() {
        if (currentUserId == null) {
            showError("Error: No user ID found.");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/orders/notifications/" + currentUserId)).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            if (response.statusCode() == 200) {
                JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                StringBuilder sb = new StringBuilder("<html><body style='font-family: Arial, sans-serif;'>");

                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    String productName = jsonObject.get("productName").getAsString();
                    String status = jsonObject.get("status").getAsString();

                    String statusColor = status.equals("COMPLETED") ? "green" : "orange";

                    sb.append("<div style='border: 1px solid #ddd; padding: 10px; margin-bottom: 5px;'>").append("<b>Product:</b> ").append(productName).append("<br>").append("<b>Status:</b> <span style='color: ").append(statusColor).append(";'>").append(status).append("</span>").append("</div>");
                }
                sb.append("</body></html>");

                JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                textPane.setText(sb.toString());
                textPane.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(textPane);
                JOptionPane.showMessageDialog(this, scrollPane, "Notifications", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Error loading notifications: " + response.body());
            }
        } catch (Exception e) {
            showError("Error loading notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        StringBuilder sb = new StringBuilder("<html><body style='font-family: Arial, sans-serif;'>");
        sb.append("<h2>Shopping Cart</h2>");
        sb.append("<table border='1' cellpadding='5' cellspacing='0' width='100%' style='border-collapse: collapse;'>");
        sb.append("<tr><th>Item</th></tr>");

        for (String item : cart) {
            sb.append("<tr><td>").append(item).append("</td></tr>");
        }

        sb.append("</table>");
        sb.append("</body></html>");

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(sb.toString());
        textPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textPane);
        JOptionPane.showMessageDialog(this, scrollPane, "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        try {
            Image image = ImageIO.read(getClass().getResource(path));
            Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
