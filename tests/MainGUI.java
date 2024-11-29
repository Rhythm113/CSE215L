
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainGUI {

    public MainGUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("File Transfer APP - CSE215L");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);


        JButton sendButton = new JButton("Send a File");
        JButton receiveButton = new JButton("Receive a File");


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sendButton, gbc);


        gbc.gridy = 1;
        mainPanel.add(receiveButton, gbc);


        sendButton.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(SenderGUI::new);
        });

        receiveButton.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(ReceiverGUI::new);
        });


        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
