package com.manesht;



import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Manesht extends JFrame {
    private final JTextField originalRepoField;
    private final JTextField forkRepoField;

    public Manesht() {
        setTitle("Manesht - Fork Check");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 200));
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel originalRepoLabel = new JLabel("Original Repository URL:");
        JLabel forkRepoLabel = new JLabel("Fork Repository URL:");

        originalRepoField = new JTextField();
        forkRepoField = new JTextField();

        JButton checkButton = new JButton("Check Fork Status");
        checkButton.addActionListener(e -> {
            String originalRepoUrl = originalRepoField.getText();
            String forkRepoUrl = forkRepoField.getText();

            boolean isBehind = checkIfForkIsBehind(originalRepoUrl, forkRepoUrl);
            if (isBehind) {
                JOptionPane.showMessageDialog(Manesht.this, "Your fork is behind the original repository. Please update your fork before committing.", "Fork Status", JOptionPane.OK_OPTION);
            } else {
                JOptionPane.showMessageDialog(Manesht.this, "Your fork is up to date. You can proceed with committing.", "Fork Status", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(originalRepoLabel, constraints);

        constraints.gridy = 1;
        panel.add(forkRepoLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(originalRepoField, constraints);

        constraints.gridy = 1;
        panel.add(forkRepoField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        panel.add(checkButton, constraints);

        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private boolean checkIfForkIsBehind(String originalRepoUrl, String forkRepoUrl) {
        try {
            // Extract the owner and repository name from the original repository URL
            String[] originalRepoUrlParts = originalRepoUrl.split("/");
            String originalRepoOwner = originalRepoUrlParts[3];
            String originalRepoName = originalRepoUrlParts[4].split("\\.")[0];


            String[] forkRepoUrlParts = forkRepoUrl.split("/");
            String forkRepoOwner = forkRepoUrlParts[3];

            String apiUrl = "https://api.github.com/repos/" + originalRepoOwner + "/" + originalRepoName + "/compare/" +
                    originalRepoOwner + ":master..." + forkRepoOwner + ":master";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String status = jsonResponse.getString("status");

            return status.equals("behind");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Manesht::new);
    }
}
