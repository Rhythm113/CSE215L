import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class SenderGUI {
    public static void main(String[] args) {
       //body
        JFrame frame = new JFrame("File Transfer - Sender");
        frame.setSize(600, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //gui
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //ip
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

        //path
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

    //JFileChooser
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });


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
        pairCodeField.setToolTipText("Enter the pair code for authentication"); // Add tooltip
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(pairCodeLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(pairCodeField, gbc);

        // send
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

            // Input validation
            if (ip.trim().isEmpty() || filePath.trim().isEmpty() || pairCode.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!isValidIP(ip)) {
                JOptionPane.showMessageDialog(frame, "Invalid IP address format!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!new File(filePath).exists()) {
                JOptionPane.showMessageDialog(frame, "File does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {

                JOptionPane.showMessageDialog(frame,
                        "Starting file transfer...\nIP: " + ip +
                                "\nFile: " + filePath +
                                "\nThreads: " + threads +
                                "\nPair Code: " + pairCode,
                        "Transfer Started", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        frame.add(formPanel, BorderLayout.CENTER);


        frame.setVisible(true);
    }

    // ref : github
    private static boolean isValidIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}