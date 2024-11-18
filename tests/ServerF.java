import java.io.*;
import java.net.*;

public class ServerF {
    private int port;
    private final int CHUNK_SIZE = 4096;
    private int code = new Utils().randomCode();

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

            System.out.println("pair code : "+ code);

            while (true) {
                System.out.println("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket, DIR, code)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, File dlDirectory, int c) {
        String clientFileName = null;
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            int receivedCode = dis.readInt();
            System.out.println("Received pairing code: " + receivedCode);

            if(receivedCode == -9999){
                String fn = dis.readUTF();
                int p = dis.readInt();
                rebuild(fn,"dl\\",p);
                return;
            }

            if (c != receivedCode) {
                System.out.println("Invalid pairing code");
                dos.writeUTF("Invalid pairing code");
                return;
            }

            clientFileName = dis.readUTF();
            long fileSize = dis.readLong();
            System.out.println("Receiving file: " + clientFileName + " (" + fileSize + " bytes)");

            File file = new File(dlDirectory, clientFileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[CHUNK_SIZE];
                long bytesReceived = 0;
                int bytesRead;

                while (bytesReceived < fileSize) {
                    bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesReceived));
                    if (bytesRead == -1) {
                        throw new IOException("Unexpected end of stream");
                    }
                    fos.write(buffer, 0, bytesRead);
                    bytesReceived += bytesRead;
                }
                System.out.println("File " + clientFileName + " received successfully.");
                dos.writeUTF("File " + clientFileName + " received successfully.");
            } catch (IOException e) {
                System.err.println("Error saving file " + clientFileName + ": " + e.getMessage());
                dos.writeUTF("Error saving file: " + clientFileName);
            }
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

    private void rebuild(String name,String path,int parts){
        FileServ a = new FileServ(null,0);
        try {
            a.rebuild(name, path);
            a.deleteS("dl\\"+name,parts);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        int port = 5000;
        ServerF server = new ServerF(port);
        server.start();
    }
}
