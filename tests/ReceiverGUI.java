import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class ReceiverGUI {

    private JTextField pairCodeField;
    private JTextField saveFolderField;
    private static JTextArea console = new JTextArea(8, 40);
    static int randomCode = Utils.randomCode();
    private static String path_selected = "";
    private FileServer fileServer;
    private Thread serverThread;
    private JButton startButton = new JButton("Start Server");

    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReceiverGUI::new);
    }*/

    public ReceiverGUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        // Frame
        JFrame frame = new JFrame("File Transfer - Receiver");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(10, 10));

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;

        // Local IP
        JLabel ipLabel = new JLabel("IP Address:");
        JTextField ipField = new JTextField(20);
        ipField.setEditable(false);
        JButton copyButton = new JButton("Copy");
        try {
            InetAddress localIP = InetAddress.getLocalHost();
            ipField.setText(localIP.getHostAddress());
        } catch (UnknownHostException e) {
            ipField.setText("Error: Unable to retrieve IP");
        }

        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(ipField.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            SetMsg("IP Copied..");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(ipLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(ipField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0; // Reset weight
        mainPanel.add(copyButton, gbc);

        // Code
        JLabel pairCodeLabel = new JLabel("Pair Code:");
        pairCodeField = new JTextField(10);
        pairCodeField.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0; // Reset weight
        mainPanel.add(pairCodeLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(pairCodeField, gbc);

        pairCodeField.setText(String.valueOf(randomCode));

        // Save
        JLabel saveFolderLabel = new JLabel("Save to Folder:");
        saveFolderField = new JTextField(20);
        saveFolderField.setEditable(false);
        JButton folderButton = new JButton("Select Folder");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(saveFolderLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(saveFolderField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        mainPanel.add(folderButton, gbc);

        folderButton.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = folderChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                path_selected = folderChooser.getSelectedFile().getAbsolutePath();
                saveFolderField.setText(path_selected);
            }
        });

        // start-server
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(startButton, gbc);

        startButton.addActionListener(e -> {
                //startButton.setText("Started See console");
                startButton.setVisible(false);
                startServer();
        });

        // Console
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        JScrollPane consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setBorder(BorderFactory.createTitledBorder("Console"));

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(consoleScrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void startServer() {
        if (path_selected.isEmpty()) {
            SetMsg("Please select a folder to save files.");
            startButton.setVisible(true);
            return;
        }

        fileServer = new FileServer(5000, randomCode, path_selected);
        serverThread = new Thread(fileServer::start);
        serverThread.start();
        SetMsg("Server started...");
    }

    public static void SetMsg(String msg) {
        console.append("\n" + msg);
    }
}
