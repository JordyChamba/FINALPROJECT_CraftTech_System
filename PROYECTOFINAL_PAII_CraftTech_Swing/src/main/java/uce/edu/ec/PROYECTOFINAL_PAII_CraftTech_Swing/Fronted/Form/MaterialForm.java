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

public class MaterialForm extends JDialog {

    private JTextField nameField;
    private JButton saveButton;
    private JButton cancelButton;

    public MaterialForm(Frame parent) {
        super(parent, "Create Material", true);
        setLayout(new GridLayout(3, 2));

        nameField = new JTextField();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        add(new JLabel("Material Name:"));
        add(nameField);
        add(saveButton);
        add(cancelButton);

        saveButton.addActionListener(e -> saveMaterial());
        cancelButton.addActionListener(e -> dispose());

        setSize(300, 150);
        setLocationRelativeTo(parent);
    }

    private void saveMaterial() {
        String name = nameField.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the material name.");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/materials/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + name + "\"}"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Material created successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating material.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating material: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
