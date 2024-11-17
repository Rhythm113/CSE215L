import java.io.*;
import java.net.*;

public class ServerF {
    private int port;
    private final int CHUNK_SIZE = 4096;

    public ServerF(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server running on port " + port);

            File DIR = new File("dl");
            if (!DIR.exists()) {
                if (DIR.mkdir()) {
                    System.out.println("dir created");
                } else {
                    System.err.println("dir failed.");
                }
            }

            while (true) {
                System.out.println("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket, DIR)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, File dlDirectory) {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            System.out.println(" rcv file: " + fileName + " (" + fileSize + " bytes)");

            File file = new File(dlDirectory, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[CHUNK_SIZE];
                long bytesReceived = 0;
                int bytesRead;

                while (bytesReceived < fileSize) {
                    bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesReceived));
                    fos.write(buffer, 0, bytesRead);
                    bytesReceived += bytesRead;
                }
            }

            System.out.println("File " + fileName + " received successfully.");
            dos.writeUTF("File " + fileName + " received successfully.");
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        ServerF server = new ServerF(port);
        server.start();
    }
}
