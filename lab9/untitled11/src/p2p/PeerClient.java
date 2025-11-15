package p2p;

import java.io.*;
import java.net.*;
import java.util.*;

public class PeerClient {

    private String downloadFolder;  // куди зберігати скачані архіви

    public PeerClient(String downloadFolder) {
        this.downloadFolder = downloadFolder;
        new File(downloadFolder).mkdirs();
    }


    public List<ArchiveInfo> getArchiveList(String host, int port) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // відправляємо запит
            out.writeObject(MessageType.LIST_ARCHIVES);
            out.flush();

            // отримуємо відповідь
            List<ArchiveInfo> archives = (List<ArchiveInfo>) in.readObject();
            System.out.println("Отримано список архівів від " + host + ":" + port);
            return archives;

        } catch (Exception e) {
            System.err.println("Помилка підключення до " + host + ":" + port);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public boolean downloadArchive(String host, int port, String archiveName) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // відправляємо запит на конкретний архів
            out.writeObject(MessageType.REQUEST_ARCHIVE);
            out.writeObject(archiveName);
            out.flush();

            // отримуємо відповідь
            String status = (String) in.readObject();

            if (status.equals("OK")) {
                byte[] fileData = (byte[]) in.readObject();

                // зберігаємо файл
                File outputFile = new File(downloadFolder, archiveName);
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(fileData);
                }

                System.out.println("Скачано архів: " + archiveName + " (" + fileData.length + " bytes)");
                return true;
            } else {
                System.err.println("Помилка: " + status);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Помилка скачування: " + e.getMessage());
            return false;
        }
    }
}