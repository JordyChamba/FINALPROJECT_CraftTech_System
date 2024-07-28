/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (FRONTED)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Form;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ProductForm extends JDialog {

    private JTextField nameField;
    private JComboBox<String> materialComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    public ProductForm(Frame parent) {
        super(parent, "Create Product", true);
        setLayout(new GridLayout(4, 2));

        nameField = new JTextField();
        materialComboBox = new JComboBox<>();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        add(new JLabel("Product Name:"));
        add(nameField);
        add(new JLabel("Material:"));
        add(materialComboBox);
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());

        loadMaterials();
        setSize(300, 200);
        setLocationRelativeTo(parent);
    }

    private void loadMaterials() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/materials"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    String materialName = element.getAsJsonObject().get("name").getAsString();
                    String materialId = element.getAsJsonObject().get("id").getAsString();
                    materialComboBox.addItem(materialName + "|" + materialId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error loading materials.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading materials: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void saveProduct() {
        String name = nameField.getText();
        String selectedMaterial = (String) materialComboBox.getSelectedItem();

        if (name.isEmpty() || selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, "Please enter the product name and select a material.");
            return;
        }

        String[] parts = selectedMaterial.split("\\|");
        String materialId = parts[1];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/products/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + name + "\",\"materialId\":\"" + materialId + "\"}"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Product created successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating product: " + response.body());
                System.out.println("Status code: " + response.statusCode());
                System.out.println("Server response: " + response.body());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating product: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
