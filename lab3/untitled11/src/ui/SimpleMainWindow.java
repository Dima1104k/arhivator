package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import facade.ArchiverFacade;
import model.*;
import repository.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SimpleMainWindow extends JFrame {
    private ArchiverFacade facade;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea logArea;

    public SimpleMainWindow() {

        IArchiveInfoRepository archiveRepo = new PostgresArchiveInfoRepository();
        IOperationLogRepository logRepo = new PostgresOperationLogRepository();
        IOperationDetailRepository detailRepo = new PostgresOperationDetailRepository();
        IArchiveBookmarkRepository bookmarkRepo = new PostgresArchiveBookmarkRepository();
        this.facade = new ArchiverFacade(archiveRepo, logRepo, bookmarkRepo, detailRepo);

        init();
        loadData();
    }

    private void init() {
        setTitle("Система Архіватор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton createBtn = new JButton("Створити архів");
        JButton refreshBtn = new JButton("Оновити");
        JButton openBtn = new JButton("Відкрити архів");
        JButton historyBtn = new JButton("Історія");
        createBtn.addActionListener(e -> createArchive());
        refreshBtn.addActionListener(e -> loadData());
        openBtn.addActionListener(e -> openArchive());
        historyBtn.addActionListener(e -> showHistory());

        topPanel.add(createBtn);
        topPanel.add(openBtn);
        topPanel.add(historyBtn);
        topPanel.add(refreshBtn);

        String[] columns = {"ID", "Шлях", "Формат", "Розмір", "Створено"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Архіви в БД"));


        logArea = new JTextArea(5, 50);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Журнал подій"));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);

        setSize(800, 500);
        setLocationRelativeTo(null);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<ArchiveInfo> archives = facade.listRecentArchives(20);

            for (ArchiveInfo info : archives) {
                Object[] row = {
                        info.getId(),
                        info.getFilePath(),
                        info.getFormat(),
                        info.getTotalSize(),
                        info.getCreatedAt()
                };
                tableModel.addRow(row);
            }
            logArea.append("Завантажено " + archives.size() + " архівів\n");
        } catch (Exception e) {
            logArea.append("Помилка: " + e.getMessage() + "\n");
        }
    }

    private void createArchive() {

        String archiveName = JOptionPane.showInputDialog(null, "Введіть назву архіву:", "test.zip");

        if (archiveName != null && !archiveName.isEmpty()) {
            try {
                List<String> files = createTestFiles();
                Archive archive = facade.create(archiveName, ArchiveFormat.ZIP, files);

                logArea.append("Створено архів: " + archive.getFilePath() + "\n");
                loadData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Помилка: " + e.getMessage());
                logArea.append("Помилка: " + e.getMessage() + "\n");
            }
        }
    }
    private List<String> createTestFiles() {
        List<String> filePaths = new ArrayList<>();
        try {
            File testDir = new File("test_files");
            if (!testDir.exists()) {
                testDir.mkdirs();
            }
            File file1 = new File(testDir, "test_file1.txt");
            try (FileWriter writer = new FileWriter(file1)) {
                writer.write("Це тестовий файл номер 1\n");
                writer.write("Створено для перевірки архіватора\n");
                writer.write("Дата: " + LocalDateTime.now() + "\n");
            }
            filePaths.add(file1.getAbsolutePath());
            File file2 = new File(testDir, "test_file2.txt");
            try (FileWriter writer = new FileWriter(file2)) {
                writer.write("Другий тестовий файл\n");
                writer.write("ТУТА БІЛЬШЕ ТЕКСТУ\n");
                writer.write("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n");
                writer.write("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB\n");
            }
            filePaths.add(file2.getAbsolutePath());

            File file3 = new File(testDir, "test_file3.txt");
            try (FileWriter writer = new FileWriter(file3)) {
                writer.write("Третій файл\n");
                for (int i = 0; i < 100; i++) {
                    writer.write("Рядок номер " + i + " з довгим текстом для збільшення розміру файлу\n");
                }
            }
            filePaths.add(file3.getAbsolutePath());

            logArea.append("Створено " + filePaths.size() + " тестових файлів\n");

        } catch (IOException e) {
            logArea.append("Помилка створення тестових файлів: " + e.getMessage() + "\n");
        }
        return filePaths;
    }
    private void showHistory() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            Long archiveId = (Long) tableModel.getValueAt(selectedRow, 0);
            String archivePath = (String) tableModel.getValueAt(selectedRow, 1);

            try {
                facade.open(archivePath);
                OperationHistoryWindow historyWindow = new OperationHistoryWindow(this, facade, archiveId, archivePath);
                historyWindow.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Помилка: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Виберіть архів для перегляду історії");
        }
    }
    private void openArchive() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String path = (String) tableModel.getValueAt(selectedRow, 1);
            try {
                Archive archive = facade.open(path);
                logArea.append("Відкрито архів: " + archive.getFilePath() + "\n");

                StringBuilder content = new StringBuilder("Вміст архіву:\n");
                for (ArchiveItem item : archive.getRootFolder().getChildren()) {
                    content.append("- ").append(item.getName()).append("\n");
                }
                JOptionPane.showMessageDialog(null, content.toString());

            } catch (Exception e) {
                logArea.append("Помилка відкриття: " + e.getMessage() + "\n");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Виберіть архів для відкриття");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleMainWindow window = new SimpleMainWindow();
            window.setVisible(true);
        });
    }
}