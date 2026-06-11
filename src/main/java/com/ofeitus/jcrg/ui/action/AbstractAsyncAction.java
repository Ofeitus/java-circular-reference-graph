package com.ofeitus.jcrg.ui.action;

import com.ofeitus.jcrg.ui.dialog.ModalProgressMonitor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractAsyncAction extends AbstractAction {

    private final String title;

    public AbstractAsyncAction(String name, String title) {
        super(name);
        this.title = title;
    }

    protected abstract void actionPerformedAsync(Consumer<IntermediateResult> resultsCallback) throws Exception;

    @Override
    public final void actionPerformed(ActionEvent e) {
        ModalProgressMonitor dialog = new ModalProgressMonitor(null, title, "");
        SwingWorker<Void, IntermediateResult> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                actionPerformedAsync(this::publish);
                return null;
            }

            @Override
            protected void process(List<IntermediateResult> chunks) {
                dialog.setProgress(chunks.getLast().progress, chunks.getLast().message);
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(e);
                } finally {
                    dialog.close();
                }
            }
        };
        worker.execute();
        dialog.show();
    }

    public record IntermediateResult(Integer progress, String message) {}
}
