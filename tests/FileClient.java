import javax.xml.crypto.Data;
import java.io.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class FileClient {
    private String serverAddress;
    private int serverPort;
    private final int CHUNK_SIZE = 4096;
    public int code = 0;


    public void setCode(int code) {
        this.code = code;
    }

    public FileClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /*
    public void sendFiles(String[] filePaths) {
        for (String filePath : filePaths) {
            new Thread(() -> sendFile(filePath)).start();
        }
    }*/

    // ref : https://www.baeldung.com/java-countdown-latch
    public void sendFiles(String[] filePaths,String fname, int fileParts) {

        CountDownLatch latch = new CountDownLatch(filePaths.length);

        for (String filePath : filePaths) {
            new Thread(() -> {
                try {
                    sendFile(filePath);
                } finally {
                    latch.countDown();
                }
            }).start();
        }


        new Thread(() -> {
            try {
                latch.await();
                try {
                    DataOutputStream a = new DataOutputStream(new Socket(serverAddress, serverPort).getOutputStream());
                    a.writeInt(-9999);
                    a.writeUTF(fname);
                    a.writeInt(fileParts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }




    private void sendFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return;
        }

        try (Socket socket = new Socket(serverAddress, serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             FileInputStream fis = new FileInputStream(file)) {

            System.out.println("Connected to the server for file: " + filePath);

            dos.writeInt(code);
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;

            System.out.println("Sending file: " + file.getName());
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            System.out.println("File " + file.getName() + " sent successfully.");

            String serverResponse = dis.readUTF();
            System.out.println("Server response: " + serverResponse);

        } catch (IOException e) {
            System.err.println("Error sending file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 5000;


        String[] filePaths = {
                "big.zip.json",
                "big.zip.part0",
                "big.zip.part1",
                "big.zip.part2",
                "big.zip.part3",
                "big.zip.part4",
                //"big.zip.part5"

        };

        FileClient client = new FileClient(serverAddress, serverPort);
        client.setCode(new Scanner(System.in).nextInt());
        client.sendFiles(filePaths,"big.zip",6);
    }
}
