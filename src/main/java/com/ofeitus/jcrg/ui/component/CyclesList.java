package com.ofeitus.jcrg.ui.component;

import com.ofeitus.jcrg.model.ClassCycle;

import javax.swing.*;

public class CyclesList extends JList<ClassCycle> {

    public CyclesList(ListModel<ClassCycle> dataModel) {
        super(dataModel);
    }
}
