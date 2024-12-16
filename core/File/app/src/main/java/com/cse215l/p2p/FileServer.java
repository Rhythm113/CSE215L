package com.cse215l.p2p;

import android.content.Context;

import java.io.*;
import java.net.*;

public class FileServer {
    private final int port;
    private final int CHUNK_SIZE = 4096;
    private final int code;
    private volatile boolean stat = true;
    private final String pathToSave;
    private ServerSocket serverSocket;
    private Context context;

    public FileServer(int port, int code, String pathToSave,Context context) {
        this.port = port;
        this.code = code;
        this.pathToSave = pathToSave;
        this.context = context;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            RcvActivity.log("Server running on port " + port);


            File dlDirectory = new File(pathToSave, "dl");
            if (!dlDirectory.exists() && !dlDirectory.mkdir()) {
                RcvActivity.log("Failed to create directory: " + dlDirectory.getAbsolutePath());
                return;
            }
            RcvActivity.log("Save directory set to: " + dlDirectory.getAbsolutePath());
            RcvActivity.log("Pair code set to: " + code);

            while (stat) {
                RcvActivity.log("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                RcvActivity.log("Connected to " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket, dlDirectory)).start();
            }
        } catch (IOException e) {
            RcvActivity.log("Server stopped or encountered an error: " + e.getMessage());
        } //finally {
        // stop();
        //}
    }

    private void handleClient(Socket clientSocket, File dlDirectory) {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            int receivedCode = dis.readInt();
            RcvActivity.log("Received pairing code: " + receivedCode);


            if (receivedCode == -9999) {
                handleRebuild(dis, dlDirectory);
                stop();
                return;
            }

            if (receivedCode != code) {
                RcvActivity.log("Invalid pairing code received.");
                dos.writeUTF("Invalid pairing code");
                stop();
                return;
            }

            String clientFileName = dis.readUTF();
            long fileSize = dis.readLong();
            RcvActivity.log("Receiving file: " + clientFileName + " (" + fileSize + " bytes)");

            File file = new File(dlDirectory, clientFileName);
            receiveFile(dis, dos, file, fileSize);
        } catch (IOException e) {
            RcvActivity.log("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                RcvActivity.log("Error closing client socket: " + e.getMessage());
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

            RcvActivity.log("File received successfully: " + file.getName());
            dos.writeUTF("File " + file.getName() + " received successfully.");
        } catch (IOException e) {
            RcvActivity.log("Error receiving file: " + file.getName() + " - " + e.getMessage());
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
            RcvActivity.log("Rebuilding file: " + fileName + " from " + parts + " parts.");

            File targetDirectory = new File(context.getExternalFilesDir(null), dlDirectory.getName());
            if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                RcvActivity.log("Failed to create directory: " + targetDirectory.getAbsolutePath());
                return;
            }

            File targetFile = new File(targetDirectory, fileName);
            rebuild(fileName, FileUtil.getExternalStorageDir().concat("/Download/P2P/dl/"), parts);
            stat = false;
            stop();
        } catch (IOException e) {
            RcvActivity.log("Error rebuilding file: " + e.getMessage());
        }
    }


    private void rebuild(String fileName, String path, int parts) {
        FileServ fileServ = new FileServ(null, 0);
        try {
            fileServ.rebuild(context,fileName, path);
            fileServ.deleteS(path + fileName, parts);
            RcvActivity.log("File rebuilt successfully: " + fileName);
        } catch (Exception e) {
            RcvActivity.log("Error during rebuild: " + e.getMessage());
        }
    }

    public void stop() {
        stat = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            RcvActivity.log("Server stopped.");
        } catch (IOException e) {
            RcvActivity.log("Error stopping server: " + e.getMessage());
        }
    }



}
