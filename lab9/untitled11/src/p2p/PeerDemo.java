package p2p;

import java.util.*;

public class PeerDemo {

    public static void main(String[] args) throws Exception {

        System.out.println("P2P АРХІВАТОР\n");

        // створюємо два вузли (як два комп'ютери)
        // вузол 1 має архіви в папці shared1, скачує в downloads1
        PeerNode node1 = new PeerNode("Node-1", 8081, "shared1", "downloads1");

        // вузол 2 має архіви в папці shared2, скачує в downloads2
        PeerNode node2 = new PeerNode("Node-2", 8082, "shared2", "downloads2");

        // запускаємо обидва вузли
        node1.start();
        node2.start();

        // трохи затримки щоб сервери запустились
        Thread.sleep(1000);

        System.out.println("\n Node 2 запитує список архівів у Node 1");
        List<ArchiveInfo> node1Archives = node2.listRemoteArchives("localhost", 8081);

        System.out.println("Архіви на Node 1:");
        for (ArchiveInfo archive : node1Archives) {
            System.out.println(" - " + archive);
        }

        System.out.println("\n Node 1 запитує список архівів у Node 2");
        List<ArchiveInfo> node2Archives = node1.listRemoteArchives("localhost", 8082);

        System.out.println("Архіви на Node 2:");
        for (ArchiveInfo archive : node2Archives) {
            System.out.println(" - " + archive);
        }

        // скачуємо архів якщо є
        if (!node1Archives.isEmpty()) {
            String archiveName = node1Archives.get(0).getName();
            System.out.println("\n Node 2 скачує '" + archiveName + "' з Node 1");
            boolean success = node2.downloadFromPeer("localhost", 8081, archiveName);
            if (success) {
                System.out.println("Результат: " + "УСПІХ");
            } else {
                System.out.println("Результат: " + "ПОМИЛКА");
            }
        }
        if (!node2Archives.isEmpty()) {
            String archiveName = node2Archives.get(0).getName();
            System.out.println("\n Node 1 скачує '" + archiveName + "' з Node 2");
            boolean success = node1.downloadFromPeer("localhost", 8082, archiveName);
            if (success) {
                System.out.println("Результат: " + "УСПІХ");
            } else {
                System.out.println("Результат: " + "ПОМИЛКА");
            }
        }

        // зупиняємо вузли
        Thread.sleep(2000);
        node1.stop();
        node2.stop();

        System.out.println("\n КІНЕЦЬ");
    }
}