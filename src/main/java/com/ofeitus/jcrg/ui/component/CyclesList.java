package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.model.ClassCycle;

import javax.swing.*;
import java.util.List;

public class CyclesList extends JList<ClassCycle> {

    public CyclesList() {
        super(new DefaultListModel<>());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public DefaultListModel<ClassCycle> getModel() {
        return (DefaultListModel<ClassCycle>) super.getModel();
    }
}
