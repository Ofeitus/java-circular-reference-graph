package com.ofeitus.jcrg.ui.dialog;

import javax.swing.*;
import java.awt.*;

public class ModalProgressMonitor {
    private final JDialog dialog;
    private final JProgressBar progressBar;
    private final JLabel noteLabel;

    public ModalProgressMonitor(Window owner, String title, String message) {
        dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(350, 30));

        noteLabel = new JLabel(message);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(noteLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
    }

    public void setProgress(int value, String note) {
        progressBar.setValue(value);
        if (note != null) {
            noteLabel.setText(note);
        }
        dialog.pack();
    }

    public void show() {
        dialog.setVisible(true);
    }

    public void close() {
        dialog.dispose();
    }
}

