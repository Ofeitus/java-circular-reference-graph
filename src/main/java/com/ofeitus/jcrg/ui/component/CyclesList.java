package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.model.ClassCycle;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class CyclesList extends JPanel {

    private final JList<ClassCycle> list;

    public CyclesList() {
        super(new BorderLayout());
        list = new JList<>(new DefaultListModel<>());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JToolBar toolBar = new JToolBar();
        toolBar.add(new JLabel("Cycles"));

        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }

    public ClassCycle getSelectedValue() {
        return list.getSelectedValue();
    }

    public void clearSelection() {
        list.clearSelection();
    }

    public void addAll(List<ClassCycle> cycles) {
        ((DefaultListModel<ClassCycle>) list.getModel()).addAll(cycles);
    }

    public void clear() {
        ((DefaultListModel<ClassCycle>) list.getModel()).clear();
    }
}
