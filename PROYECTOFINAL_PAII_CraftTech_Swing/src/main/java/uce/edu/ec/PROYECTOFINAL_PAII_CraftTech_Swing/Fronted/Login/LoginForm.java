/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (FRONTED)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Login;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Window.AdminWindow;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Window.ClientWindow;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.Form.RegisterForm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginForm() {
        initializeUI();
    }

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        addComponents();
        addEventHandlers();
    }

    private void addComponents() {
        JLabel logoLabel = createLogoLabel();
        JLabel sloganLabel = createSloganLabel();

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        styleTextFields(usernameField, passwordField);

        loginButton = createStyledButton("Login", new Color(33, 150, 243));
        registerButton = createStyledButton("Register", new Color(76, 175, 80));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.add(sloganLabel, BorderLayout.SOUTH);

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        setLayout(new BorderLayout());
        add(logoPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel();
        try (InputStream logoStream = getClass().getResourceAsStream("/logo2.png")) {
            if (logoStream != null) {
                BufferedImage logoImage = ImageIO.read(logoStream);
                Image scaledLogoImage = logoImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledLogoImage));
            } else {
                System.err.println("Logo image not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return logoLabel;
    }

    private JLabel createSloganLabel() {
        JLabel sloganLabel = new JLabel("<html>Creating your ideas with wood:<br>Unique furniture for a unique home</html>");
        sloganLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sloganLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        sloganLabel.setForeground(Color.DARK_GRAY);
        return sloganLabel;
    }

    private void styleTextFields(JTextField usernameField, JPasswordField passwordField) {
        usernameField.setPreferredSize(new Dimension(250, 30));
        passwordField.setPreferredSize(new Dimension(250, 30));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(passwordField, gbc);

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        return buttonPanel;
    }

    private void addEventHandlers() {
        loginButton.addActionListener(this::onLoginButtonClick);
        registerButton.addActionListener(e -> new RegisterForm().setVisible(true));
    }

    private void onLoginButtonClick(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        authenticate(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(userId -> handleAuthenticationResult(userId, username, password),
                        error -> SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(LoginForm.this, "Error: " + error.getMessage())));
    }

    private void handleAuthenticationResult(long userId, String username, String password) {
        SwingUtilities.invokeLater(() -> {
            if (userId >= 0) {
                if (username.equals("@admin") && password.equals("@admin")) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Login successful as Admin!");
                    new AdminWindow().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Login successful as Client!");
                    new ClientWindow(userId).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginForm.this, "Invalid username or password!");
            }
            usernameField.setText("");
            passwordField.setText("");
        });
    }

    private Observable<Long> authenticate(String username, String password) {
        return Observable.create(emitter -> {
            try {
                URL url = new URL("http://localhost:8080/api/auth/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        long userId = getUserId(username);
                        emitter.onNext(userId);
                    }
                } else {
                    emitter.onNext(-1L);
                }
                emitter.onComplete();
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        });
    }

    private long getUserId(String username) throws IOException {
        URL url = new URL("http://localhost:8080/api/users/" + username + "/id");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int code = connection.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return Long.parseLong(response.toString());
            }
        }
        return -1L;
    }
}
