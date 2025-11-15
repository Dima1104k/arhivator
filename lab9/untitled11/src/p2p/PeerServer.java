package p2p;

import java.io.*;
import java.net.*;
import java.util.*;

public class PeerServer implements Runnable {

    private int port;
    private String sharedFolder; // папка з архівами які ділимо
    private boolean running = true;

    public PeerServer(int port, String sharedFolder) {
        this.port = port;
        this.sharedFolder = sharedFolder;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("P2P Server запущено на порту " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Підключився: " + clientSocket.getInetAddress());

                // обробка кожного клієнта в окремому потоці
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Помилка сервера: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            // тип запиту
            MessageType messageType = (MessageType) in.readObject();

            switch (messageType) {
                case LIST_ARCHIVES:
                    // відправляємо список наших архівів
                    sendArchiveList(out);
                    break;

                case REQUEST_ARCHIVE:
                    // відправляємо конкретний архів
                    String archiveName = (String) in.readObject();
                    sendArchive(out, archiveName);
                    break;

                default:
                    System.out.println("Невідомий тип повідомлення");
            }

        } catch (Exception e) {
            System.err.println("Помилка обробки клієнта: " + e.getMessage());
        }
    }

    private void sendArchiveList(ObjectOutputStream out) throws IOException {
        List<ArchiveInfo> archives = new ArrayList<>();

        File folder = new File(sharedFolder);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".zip") || file.getName().endsWith(".tar.gz")) {
                    String format = file.getName().endsWith(".zip") ? "ZIP" : "TAR.GZ";
                    archives.add(new ArchiveInfo(file.getName(), file.length(), format));
                }
            }
        }

        out.writeObject(archives);
        out.flush();
        System.out.println("Відправлено список з " + archives.size() + " архівів");
    }

    private void sendArchive(ObjectOutputStream out, String archiveName) throws IOException {
        File archiveFile = new File(sharedFolder, archiveName);

        if (!archiveFile.exists()) {
            out.writeObject("ERROR: Архів не знайдено");
            return;
        }

        // читаємо файл в байти
        byte[] fileData = new byte[(int) archiveFile.length()];
        try (FileInputStream fis = new FileInputStream(archiveFile)) {
            fis.read(fileData);
        }

        out.writeObject("OK");
        out.writeObject(fileData);
        out.flush();
        System.out.println("Відправлено архів: " + archiveName);
    }

    public void stop() {
        running = false;
    }
}