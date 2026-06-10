package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.model.ClassCycle;

import javax.swing.*;
import java.util.List;

public class CyclesList extends JList<ClassCycle> {

    public CyclesList(List<ClassCycle> cycles) {
        super();
        DefaultListModel<ClassCycle> model = new DefaultListModel<>();
        model.addAll(cycles);
        setModel(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
