package com.ofeitus.jcrg.ui.swing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Air extends Body {

    private final double density = 1;

    public Air(int worldId) {
        super(worldId, 0);
    }
}
