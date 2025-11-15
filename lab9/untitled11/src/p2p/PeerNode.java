package p2p;

import java.util.*;


public class PeerNode {

    private String nodeId;
    private int port;
    private String sharedFolder;
    private String downloadFolder;

    private PeerServer server;
    private PeerClient client;
    private Thread serverThread;

    public PeerNode(String nodeId, int port, String sharedFolder, String downloadFolder) {
        this.nodeId = nodeId;
        this.port = port;
        this.sharedFolder = sharedFolder;
        this.downloadFolder = downloadFolder;

        this.server = new PeerServer(port, sharedFolder);
        this.client = new PeerClient(downloadFolder);
    }


    public void start() {
        serverThread = new Thread(server);
        serverThread.start();
        System.out.println("Вузол " + nodeId + " запущено на порту " + port);
    }


    public void stop() {
        server.stop();
        System.out.println("Вузол " + nodeId + " зупинено");
    }


    public List<ArchiveInfo> listRemoteArchives(String host, int port) {
        System.out.println("Запитую список архівів у " + host + ":" + port);
        return client.getArchiveList(host, port);
    }


    public boolean downloadFromPeer(String host, int port, String archiveName) {
        System.out.println("Скачую " + archiveName + " з " + host + ":" + port);
        return client.downloadArchive(host, port, archiveName);
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getPort() {
        return port;
    }
}