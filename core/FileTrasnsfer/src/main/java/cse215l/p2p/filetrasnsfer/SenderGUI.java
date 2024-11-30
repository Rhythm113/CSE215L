package cse215l.p2p.filetrasnsfer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class SenderGUI {

    private static JTextArea console;

    public SenderGUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("File Transfer - Sender");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- IP Address ---
        JLabel ipLabel = new JLabel("IP Address:");
        JTextField ipField = new JTextField(20);
        ipField.setToolTipText("Enter the recipient's IP address");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(ipLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(ipField, gbc);

        // --- File Selection ---
        JLabel fileLabel = new JLabel("File:");
        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false);
        filePathField.setToolTipText("Selected file path");
        JButton fileButton = new JButton("Select File");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(fileLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(filePathField, gbc);
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(fileButton, gbc);

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                log("File selected: " + selectedFile.getAbsolutePath());
            }
        });

        // --- Threads Selection ---
        JLabel threadLabel = new JLabel("Threads:");
        JSlider threadSlider = new JSlider(1, 10, 1);
        threadSlider.setMajorTickSpacing(1);
        threadSlider.setPaintTicks(true);
        threadSlider.setPaintLabels(true);
        threadSlider.setToolTipText("Select the number of threads for transfer");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(threadLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(threadSlider, gbc);
        gbc.gridwidth = 1;

        // --- Pair Code ---
        JLabel pairCodeLabel = new JLabel("Pair Code:");
        JTextField pairCodeField = new JTextField(10);
        pairCodeField.setToolTipText("Enter the pair code for authentication");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(pairCodeLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(pairCodeField, gbc);

        // --- Send Button ---
        JButton sendButton = new JButton("Send");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(sendButton, gbc);

        sendButton.addActionListener((ActionEvent e) -> {
            String ip = ipField.getText();
            String filePath = filePathField.getText();
            int threads = threadSlider.getValue();
            String pairCode = pairCodeField.getText();

            validateAndSend(ip, filePath, threads, pairCode);
        });

        // --- Console Area ---
        console = new JTextArea(8, 40);
        console.setEditable(false);
        JScrollPane consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScrollPane.setBorder(BorderFactory.createTitledBorder("Console Output"));

        // --- Frame Layout ---
        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(consoleScrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void validateAndSend(String ip, String filePath, int threads, String pairCode) {
        if (ip.trim().isEmpty() || filePath.trim().isEmpty() || pairCode.trim().isEmpty()) {
            log("Error: All fields are required!");
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Utils.isValidIP(ip)) {
            log("Error: Invalid IP address format!");
            JOptionPane.showMessageDialog(null, "Invalid IP address format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            log("Error: File does not exist!");
            JOptionPane.showMessageDialog(null, "File does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log("Starting file transfer...");

        SwingUtilities.invokeLater(() -> {
            try {
                FileClient client = new FileClient(ip, 5000);
                client.setCode(Integer.parseInt(pairCode));
                try {
                    FileServ a = new FileServ(filePath,threads);
                    a.split();
                }finally {
                    String[] paths = client.getPaths(filePath, threads);
                    client.sendFiles(paths, file.getName(), threads);
                    log("File transfer initiated successfully.");
                }

            } catch (Exception ex) {
                log("Error initiating file transfer: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Failed to start transfer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void log(String message) {
        console.append(message + "\n");
        console.setCaretPosition(console.getDocument().getLength());
        System.out.println(message);
    }
     /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SenderGUI::new);
    }*/
}
