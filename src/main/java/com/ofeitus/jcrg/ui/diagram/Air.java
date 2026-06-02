package com.ofeitus.jcrg.ui.diagram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Air extends Body {

    private final double density = 0.5;

    public Air(int worldId) {
        super(worldId, 0);
    }
}
