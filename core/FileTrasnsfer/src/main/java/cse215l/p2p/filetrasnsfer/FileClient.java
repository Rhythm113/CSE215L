package cse215l.p2p.filetrasnsfer;

import java.io.*;
import java.io.DataOutputStream;
import java.net.Socket;
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
                    new FileServ(null,0).deleteS(new File(filePaths[0]).getParent() + "\\" + fname,fileParts);
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
            SenderGUI.log("File not found: " + filePath);
            return;
        }

        try (Socket socket = new Socket(serverAddress, serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             FileInputStream fis = new FileInputStream(file)) {

            SenderGUI.log("Connected to the server for file: " + filePath);

            dos.writeInt(code);
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;

            SenderGUI.log("Sending file: " + file.getName());
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            SenderGUI.log("File " + file.getName() + " sent successfully.");

            String serverResponse = dis.readUTF();
            SenderGUI.log("Server response: " + serverResponse);

        } catch (IOException e) {
            SenderGUI.log("Error sending file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String[] getPaths(String PathT, int segments){
        String[] paths = new String[segments+1];
        paths[0] =  PathT + ".json";
        for(int i = 0; i < segments; i++){
            paths[i+1] = PathT  + ".part" + i;
        }

        return paths;
    }



/*
    public static void main(String[] args) {
        String serverAddress = "192.168.0.120";
        int serverPort = 5000;

        // split using parts

        FileServ a = new FileServ("E:\\test\\dl\\big.zip",6);
        try {
            a.split();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
                FileClient client = new FileClient(serverAddress, serverPort);
                client.setCode(new Scanner(System.in).nextInt());
                client.sendFiles(client.getPaths("E:\\test\\dl\\big.zip", 6), "big.zip", 6);
            }




    }
    */

}
