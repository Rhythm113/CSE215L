package cse215l.p2p.filetrasnsfer;

import java.io.*;
import java.net.*;

public class FileServer {
    private final int port;
    private final int CHUNK_SIZE = 4096;
    private final int code;
    private volatile boolean stat = true;
    private final String pathToSave;
    private ServerSocket serverSocket;

    public FileServer(int port, int code, String pathToSave) {
        this.port = port;
        this.code = code;
        this.pathToSave = pathToSave;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            logMessage("Server running on port " + port);


            File dlDirectory = new File(pathToSave, "dl");
            if (!dlDirectory.exists() && !dlDirectory.mkdir()) {
                logMessage("Failed to create directory: " + dlDirectory.getAbsolutePath());
                return;
            }
            logMessage("Save directory set to: " + dlDirectory.getAbsolutePath());
            logMessage("Pair code set to: " + code);

            while (stat) {
                logMessage("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                logMessage("Connected to " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket, dlDirectory)).start();
            }
        } catch (IOException e) {
            logMessage("Server stopped or encountered an error: " + e.getMessage());
        } //finally {
        // stop();
        //}
    }

    private void handleClient(Socket clientSocket, File dlDirectory) {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            int receivedCode = dis.readInt();
            logMessage("Received pairing code: " + receivedCode);


            if (receivedCode == -9999) {
                handleRebuild(dis, dlDirectory);
                stop();
                return;
            }

            if (receivedCode != code) {
                logMessage("Invalid pairing code received.");
                dos.writeUTF("Invalid pairing code");
                stop();
                return;
            }

            String clientFileName = dis.readUTF();
            long fileSize = dis.readLong();
            logMessage("Receiving file: " + clientFileName + " (" + fileSize + " bytes)");

            File file = new File(dlDirectory, clientFileName);
            receiveFile(dis, dos, file, fileSize);
        } catch (IOException e) {
            logMessage("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logMessage("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void receiveFile(DataInputStream dis, DataOutputStream dos, File file, long fileSize) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            long bytesReceived = 0;
            int bytesRead;

            while (bytesReceived < fileSize) {
                bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesReceived));
                if (bytesRead == -1) {
                    throw new IOException("Unexpected end of stream while receiving file.");
                }
                fos.write(buffer, 0, bytesRead);
                bytesReceived += bytesRead;
            }

            logMessage("File received successfully: " + file.getName());
            dos.writeUTF("File " + file.getName() + " received successfully.");
        } catch (IOException e) {
            logMessage("Error receiving file: " + file.getName() + " - " + e.getMessage());
            try {
                dos.writeUTF("Error receiving file: " + file.getName());
            } catch (IOException ignored) {
            }
        }
    }

    private void handleRebuild(DataInputStream dis, File dlDirectory) {
        try {
            String fileName = dis.readUTF();
            int parts = dis.readInt();
            logMessage("Rebuilding file: " + fileName + " from " + parts + " parts.");

            rebuild(fileName, dlDirectory.getAbsolutePath() + File.separator, parts);
            stat = false;
            stop();
        } catch (IOException e) {
            logMessage("Error rebuilding file: " + e.getMessage());
        }
    }

    private void rebuild(String fileName, String path, int parts) {
        FileServ fileServ = new FileServ(null, 0);
        try {
            fileServ.rebuild(fileName, path);
            fileServ.deleteS(path + fileName, parts);
            logMessage("File rebuilt successfully: " + fileName);
        } catch (Exception e) {
            logMessage("Error during rebuild: " + e.getMessage());
        }
    }

    public void stop() {
        stat = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            logMessage("Server stopped.");
        } catch (IOException e) {
            logMessage("Error stopping server: " + e.getMessage());
        }
    }

    private void logMessage(String msg) {
        System.out.println(msg);
        ReceiverGUI.SetMsg(msg);
    }
}
