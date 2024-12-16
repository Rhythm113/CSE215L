package com.cse215l.p2p;

import android.content.Context;
import android.net.Uri;

import com.google.gson.*;  //https://github.com/google/gson
import java.io.*;
import java.security.*;
import java.util.*;

// Author : Rhythm113

public class FileServ {

    private final String Path;  //if rebuild then path == filename
    private final int segmentCount;
    private final int CHUNK_SIZE = 8192; // not working for splitting

    public FileServ(String Path, int segmentCount) {
        this.Path = Path;
        this.segmentCount = segmentCount;
    }

    public void deleteS(String tPathofFile,int parts) throws Exception{
        for (int i = 0; i < parts; i++){
            File tod = new File(tPathofFile+".part"+i);
            //System.out.println(tPathofFile);
            if(!tod.delete()){
                throw new Exception("Failed");
            }
            System.out.println("deleted " + tPathofFile+".part"+i);
        }
        File metadata = new File(tPathofFile+".json");
        if(!metadata.delete()){
            throw new Exception("Metadata Failed");
        }

    }


    public void split() throws Exception {
        File file = new File(Path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + Path);
        }

        long fileLength = file.length();
        if (segmentCount <= 0 || segmentCount > fileLength) {
            throw new IllegalArgumentException("Invalid segment count.");
        }

        long segmentSize = fileLength / segmentCount;
        long remainingBytes = fileLength % segmentCount;

        String fileName = file.getName();
        String fPath = file.getAbsolutePath();
        FileInputStream fis = new FileInputStream(file);
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");

        byte[] buffer = new byte[(int)segmentSize];

        //md5 calc REF : GITHUB
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            md5Digest.update(buffer, 0, bytesRead);
        }
        String fileHash = bytesToHex(md5Digest.digest());
        fis.close();
        //

        FileInputStream fis2 = new FileInputStream(file);
        List<Long> segmentSizes = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++) {
            String segmentName = fPath + ".part" + i;
            FileOutputStream fos = new FileOutputStream(segmentName );

            long bytesToWrite = segmentSize;
            if (i == segmentCount - 1) {
                bytesToWrite += remainingBytes;
            }

            long written = 0;

            while (written < bytesToWrite) {
                bytesRead = fis2.read(buffer);
                if (bytesRead == -1) break; //EOF

                long bytesToCopy = Math.min(bytesToWrite - written, bytesRead);
                //System.out.println((int)bytesToCopy);
                fos.write(buffer, 0, (int) bytesToCopy );
                written += bytesToCopy;
                //System.out.println(written);
            }


            fos.close();
            segmentSizes.add(bytesToWrite);
        }
        fis2.close();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name",fileName);
        metadata.put("hash", fileHash);
        metadata.put("segments", segmentSizes);

        Gson gson = new Gson();
        String jsonMetadata = gson.toJson(metadata);

        try (FileWriter jsonFile = new FileWriter(fPath + ".json")) {
            jsonFile.write(jsonMetadata);
        }

        //System.out.println("saved in " + fileName + ".json");
    }



    public void rebuild(Context context, String fileName, String filePath) throws Exception {
        Gson gson = new Gson();
        Map<String, Object> metadata;

        String metadataPath = filePath + File.separator + fileName + ".json";


        try (FileReader jsonFile = new FileReader(metadataPath)) {
            metadata = gson.fromJson(jsonFile, Map.class);
        }

        String originalHash = (String) metadata.get("hash");
        List<Double> segmentSizes = (List<Double>) metadata.get("segments");


        String outputFilePath = filePath + File.separator + "downloaded_" + fileName;
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");

            for (int i = 0; i < segmentSizes.size(); i++) {

                String segmentPath = filePath + File.separator + fileName + ".part" + i;

                try (FileInputStream fis = new FileInputStream(segmentPath)) {
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        md5Digest.update(buffer, 0, bytesRead);
                    }
                }
            }

            String rebuiltHash = bytesToHex(md5Digest.digest());
            if (!rebuiltHash.equals(originalHash)) {
                throw new Exception("Hash mismatch.");
            }

            RcvActivity.log( "File successfully rebuilt and verified.");
        }
    }

    //reference gitHub
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

/*
    public static void main(String[] args) throws Exception {
            FileServ fileServ = new FileServ("big.zip", 6);

            //fileServ.split();

            //fileServ.rebuild("big.zip", "D:\\CSE215L\\tests\\");
            fileServ.deleteS("big.zip",6);

    }*/


}

