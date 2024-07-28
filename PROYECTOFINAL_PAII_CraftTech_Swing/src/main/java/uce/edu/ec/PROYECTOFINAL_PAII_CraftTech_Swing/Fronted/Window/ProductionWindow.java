/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (FRONTED)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductionWindow extends JFrame {
    private Long orderId;
    private ExecutorService executorService;
    private AtomicInteger activeProcesses;
    private JButton sendButton;

    public ProductionWindow(Long orderId, String product, List<String> steps, List<Integer> times) {
        this.orderId = orderId;
        executorService = Executors.newFixedThreadPool(4);
        activeProcesses = new AtomicInteger(0);
        setTitle("Production of " + product);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel labelProduct = new JLabel("Product: " + product);
        panel.add(labelProduct);

        switch (product.toLowerCase()) {
            case "table":
                addStepButton(panel, "Cut wood for legs", 15);
                addStepButton(panel, "Sand wood for legs", 20);
                addStepButton(panel, "Assemble legs", 15);
                addStepButton(panel, "Finish wood for legs", 20);
                addStepButton(panel, "Inspect and adjust", 15);
                break;
            case "chair":
                addStepButton(panel, "Cut wood for seat", 15);
                addStepButton(panel, "Sand wood for seat", 20);
                addStepButton(panel, "Assemble seat and back", 20);
                addStepButton(panel, "Finish wood for seat", 15);
                addStepButton(panel, "Inspect and adjust", 15);
                break;
            case "bed":
                addStepButton(panel, "Cut wood for frame", 20);
                addStepButton(panel, "Sand wood for frame", 20);
                addStepButton(panel, "Assemble frame", 25);
                addStepButton(panel, "Finish wood for frame", 20);
                addStepButton(panel, "Inspect and adjust", 15);
                break;
            case "utensils":
                addStepButton(panel, "Cut material to size", 10);
                addStepButton(panel, "Shape the utensil", 15);
                addStepButton(panel, "Sand or polish edges", 10);
                addStepButton(panel, "Apply finish", 15);
                addStepButton(panel, "Inspect and pack", 10);
                break;
            case "ladder":
                addStepButton(panel, "Cut wood for steps", 15);
                addStepButton(panel, "Sand wood for steps", 20);
                addStepButton(panel, "Assemble steps", 20);
                addStepButton(panel, "Finish wood for steps", 20);
                addStepButton(panel, "Inspect stability", 15);
                break;
            case "bench":
                addStepButton(panel, "Cut wood for seat", 15);
                addStepButton(panel, "Sand wood for seat", 20);
                addStepButton(panel, "Assemble seat and backrest", 20);
                addStepButton(panel, "Finish wood for seat", 15);
                addStepButton(panel, "Inspect and adjust", 15);
                break;
            case "coat rack":
                addStepButton(panel, "Cut wood for support", 15);
                addStepButton(panel, "Shape wood for hooks", 15);
                addStepButton(panel, "Sand wood pieces", 20);
                addStepButton(panel, "Finish wood", 20);
                addStepButton(panel, "Inspect and assemble", 15);
                break;
            case "rack":
                addStepButton(panel, "Cut wood for main post", 15);
                addStepButton(panel, "Cut wood for legs", 15);
                addStepButton(panel, "Sand wood pieces", 20);
                addStepButton(panel, "Finish wood", 20);
                addStepButton(panel, "Inspect and assemble", 15);
                break;
            case "cutting board":
                addStepButton(panel, "Cut wood to size", 10);
                addStepButton(panel, "Sand wood surface", 15);
                addStepButton(panel, "Round edges", 10);
                addStepButton(panel, "Apply finish", 10);
                addStepButton(panel, "Inspect and package", 10);
                break;
            case "wardrobe":
                addStepButton(panel, "Cut wood for walls", 20);
                addStepButton(panel, "Sand wood for walls", 20);
                addStepButton(panel, "Assemble wardrobe walls", 25);
                addStepButton(panel, "Finish wood for walls", 20);
                addStepButton(panel, "Inspect and adjust", 15);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown product type.");
                return;
        }

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markOrderAsCompleted(orderId);
                JOptionPane.showMessageDialog(ProductionWindow.this, "Product sent.");
                saveNotification(product + " is ready for pickup.");
            }
        });

        panel.add(sendButton);
        add(panel);
        setVisible(true);
    }

    private void saveNotification(String message) {
        executorService.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", true))) {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void markOrderAsCompleted(long orderId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/orders/" + orderId + "/updateStatus"))
                .PUT(HttpRequest.BodyPublishers.ofString("COMPLETED"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP status code: " + response.statusCode());
            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Order marked as completed.");
                fetchOrdersFromServer();
            } else {
                JOptionPane.showMessageDialog(this, "Error marking the order as completed.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error marking the order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fetchOrdersFromServer() {
        executorService.submit(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/orders"))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("HTTP status code: " + response.statusCode());
                if (response.statusCode() == 200) {
                } else {
                    JOptionPane.showMessageDialog(this, "Error fetching orders.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error fetching orders: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void addStepButton(JPanel panel, String step, int time) {
        JPanel stepPanel = new JPanel(new BorderLayout());

        JLabel stepLabel = new JLabel(step);
        JProgressBar progressBar = new JProgressBar(0, time);
        progressBar.setStringPainted(true);

        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(10, 10));
        statusPanel.setBackground(Color.RED);
        statusPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        statusPanel.setLayout(new BorderLayout());

        stepPanel.add(statusPanel, BorderLayout.WEST);
        stepPanel.add(stepLabel, BorderLayout.CENTER);
        stepPanel.add(progressBar, BorderLayout.EAST);

        JButton button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeProcesses.incrementAndGet();
                button.setEnabled(false);
                statusPanel.setBackground(Color.ORANGE);

                executorService.submit(() -> {
                    try {
                        for (int i = 0; i < time; i++) {
                            Thread.sleep(1000);
                            final int progress = i + 1;
                            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                        }
                        SwingUtilities.invokeLater(() -> {
                            statusPanel.setBackground(Color.GREEN);
                            button.setEnabled(true);
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } finally {
                        if (activeProcesses.decrementAndGet() == 0) {
                            SwingUtilities.invokeLater(() -> sendButton.setEnabled(true));
                        }
                    }
                });
            }
        });

        stepPanel.add(button, BorderLayout.SOUTH);
        panel.add(stepPanel);
    }
}
