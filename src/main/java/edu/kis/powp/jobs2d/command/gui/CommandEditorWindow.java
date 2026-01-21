package edu.kis.powp.jobs2d.command.gui;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.visitor.CommandVisitor;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandEditorWindow extends JFrame {
    private final CommandManager commandManager;
    private final CommandPreviewWindow previewWindow;
    private final JTable table;
    private final CommandTableModel tableModel;
    private DriverCommand originalCommand;

    public CommandEditorWindow(CommandManager commandManager, CommandPreviewWindow previewWindow) {
        this.commandManager = commandManager;
        this.previewWindow = previewWindow;
        this.originalCommand = commandManager.getCurrentCommand();
        this.setTitle("Command Editor");
        this.setSize(500, 400);
        this.setLayout(new BorderLayout());

        tableModel = new CommandTableModel();
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new ChangeHighlightRenderer());

        loadCommands();

        tableModel.addTableModelListener(e -> previewChanges());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton previewButton = new JButton("Preview");
        JButton upButton = new JButton("Move Up");
        JButton downButton = new JButton("Move Down");

        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> cancelChanges());
        previewButton.addActionListener(e -> {
            if (this.previewWindow != null && !this.previewWindow.isVisible()) {
                this.previewWindow.setVisible(true);
            }
        });
        upButton.addActionListener(e -> moveUp());
        downButton.addActionListener(e -> moveDown());

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(previewButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Ensure strictly that closing the window via X also cancels properly if needed
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cancelChanges();
            }
        });
    }

    private void moveUp() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow > 0) {
            tableModel.moveRow(selectedRow, selectedRow - 1);
            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }

    private void moveDown() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount() - 1) {
            tableModel.moveRow(selectedRow, selectedRow + 1);
            table.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
    }

    private void loadCommands() {
        DriverCommand currentCommand = commandManager.getCurrentCommand();
        if (currentCommand != null) {
            FlatCommandExtractor extractor = new FlatCommandExtractor();
            currentCommand.accept(extractor);
            tableModel.setData(extractor.getExtractedCommands());
        }
    }

    private void previewChanges() {
        List<DriverCommand> newCommands = new ArrayList<>();
        for (EditableCommand editableCommand : tableModel.getData()) {
            newCommands.add(editableCommand.getUpdatedCommand());
        }
        commandManager.setCurrentCommand(newCommands, "Preview");
    }

    private void cancelChanges() {
        if (originalCommand != null) {
            commandManager.setCurrentCommand(originalCommand);
        }
        dispose();
    }

    private void saveChanges() {
        List<DriverCommand> newCommands = new ArrayList<>();
        for (EditableCommand editableCommand : tableModel.getData()) {
            newCommands.add(editableCommand.getUpdatedCommand());
        }
        commandManager.setCurrentCommand(newCommands, "Edited Command");
        dispose();
    }

    private static class FlatCommandExtractor implements CommandVisitor {
        private final List<EditableCommand> extractedCommands = new ArrayList<>();

        public List<EditableCommand> getExtractedCommands() {
            return extractedCommands;
        }

        @Override
        public void visit(SetPositionCommand command) {
            extractedCommands.add(new EditableCommand(command));
        }

        @Override
        public void visit(OperateToCommand command) {
            extractedCommands.add(new EditableCommand(command));
        }

        @Override
        public void visit(ICompoundCommand command) {
            Iterator<DriverCommand> iterator = command.iterator();
            while (iterator.hasNext()) {
                iterator.next().accept(this);
            }
        }
    }

    private static class EditableCommand {
        private final DriverCommand command;
        private final int originalX;
        private final int originalY;
        private final String type;

        private int currentX;
        private int currentY;

        public EditableCommand(SetPositionCommand command) {
            this.command = command;
            this.originalX = command.getPosX();
            this.originalY = command.getPosY();
            this.type = "SetPosition";
            this.currentX = originalX;
            this.currentY = originalY;
        }

        public EditableCommand(OperateToCommand command) {
            this.command = command;
            this.originalX = command.getPosX();
            this.originalY = command.getPosY();
            this.type = "OperateTo";
            this.currentX = originalX;
            this.currentY = originalY;
        }

        public boolean isModified() {
            return currentX != originalX || currentY != originalY;
        }

        public DriverCommand getUpdatedCommand() {
             if (command instanceof SetPositionCommand) {
                 return new SetPositionCommand(currentX, currentY);
             } else if (command instanceof OperateToCommand) {
                 return new OperateToCommand(currentX, currentY);
             }
             return command.copy(); // Fallback
        }

        // apply() removed as we use getUpdatedCommand() to avoid side effects
    }

    private class CommandTableModel extends AbstractTableModel {
        private List<EditableCommand> data = new ArrayList<>();
        private final String[] columnNames = {"Type", "X", "Y"};

        public void setData(List<EditableCommand> data) {
            this.data = data;
            fireTableDataChanged();
        }

        public List<EditableCommand> getData() {
            return data;
        }

        public void moveRow(int fromIndex, int toIndex) {
            EditableCommand item = data.remove(fromIndex);
            data.add(toIndex, item);
            fireTableDataChanged();
        }

        public boolean isCellModified(int rowIndex, int columnIndex) {
            EditableCommand cmd = data.get(rowIndex);
            if (columnIndex == 1) { // X
                return cmd.currentX != cmd.originalX;
            } else if (columnIndex == 2) { // Y
                return cmd.currentY != cmd.originalY;
            }
            return false;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0; // X and Y are editable
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            EditableCommand cmd = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return cmd.type;
                case 1: return cmd.currentX;
                case 2: return cmd.currentY;
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                int val = Integer.parseInt(aValue.toString());
                EditableCommand cmd = data.get(rowIndex);
                if (columnIndex == 1) {
                    cmd.currentX = val;
                } else if (columnIndex == 2) {
                    cmd.currentY = val;
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (NumberFormatException e) {
                // simple ignore
            }
        }
    }

    private class ChangeHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (tableModel.isCellModified(row, column)) {
                c.setBackground(Color.GREEN);
            } else {
                c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            }
            return c;
        }
    }
}
