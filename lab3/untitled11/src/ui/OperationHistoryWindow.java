package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import facade.ArchiverFacade;
import model.*;
import java.util.List;

public class OperationHistoryWindow extends JDialog {
    private ArchiverFacade facade;
    private long archiveId;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JTextArea detailsArea;

    public OperationHistoryWindow(JFrame parent, ArchiverFacade facade, long archiveId, String archiveName) {
        super(parent, "Історія операцій: " + archiveName, true);


        this.facade = facade;
        this.archiveId = archiveId;

        init();
        loadHistory();
    }

    private void init() {
        setLayout(new BorderLayout());
        setSize(800, 400);


        String[] columns = {"ID", "Тип операції", "Статус", "Час", "Повідомлення"};
        historyModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(historyModel);


        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Історія операцій"));
        historyScroll.setPreferredSize(new Dimension(0, 250));


        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Деталі операції"));

        JButton closeBtn = new JButton("Закрити");
        closeBtn.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);

        add(historyScroll, BorderLayout.PAGE_START);
        add(detailsScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);

        setLocationRelativeTo(getParent());
    }

    private void loadHistory() {
        historyModel.setRowCount(0);

        try {
            List<OperationLog> logs = facade.getOperationLogsForCurrentArchive(50);

            for (OperationLog log : logs) {
                Object[] row = {
                        log.getId(),
                        log.getOperationType().name(),
                        log.getStatus().name(),
                        log.getTimestamp(),
                        log.getMessage()
                };
                historyModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Помилка: " + e.getMessage());
        }
    }

    private void showDetails() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            long operationId = (Long) historyModel.getValueAt(selectedRow, 0);
            try {
                List<OperationDetail> details = facade.getDetailsForOperation(operationId);

                StringBuilder sb = new StringBuilder();
                sb.append("Деталі операції ID: ").append(operationId).append("\n");
                sb.append("=====================================\n");

                for (OperationDetail detail : details) {
                    sb.append("Файл: ").append(detail.getItemPath()).append("\n");
                    sb.append("Дія: ").append(detail.getType()).append("\n");
                    sb.append("Статус: ").append(detail.getStatus()).append("\n");
                    sb.append("Повідомлення: ").append(detail.getMessage()).append("\n");
                    sb.append("-------------------------------------\n");
                }

                detailsArea.setText(sb.toString());

            } catch (Exception e) {
                detailsArea.setText("Помилка завантаження деталей: " + e.getMessage());
            }
        }
    }
}