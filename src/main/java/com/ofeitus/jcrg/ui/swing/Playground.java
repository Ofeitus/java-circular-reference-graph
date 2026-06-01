package com.ofeitus.jcrg.ui.swing;

import javax.swing.*;
import java.util.Random;

public class Playground {

    static void main() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Playground");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            int spaceWidth = 800;
            int spaceHeight = 800;
            frame.setSize(spaceWidth, spaceHeight);

            Random random = new Random();

            Space space = new Space(spaceWidth, spaceHeight);

            ChargedParticle chargedParticle1 = new ChargedParticle(new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), new Vector2D(0, 0));
            ChargedParticle chargedParticle2 = new ChargedParticle(new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), new Vector2D(0, 0));
            ChargedParticle chargedParticle3 = new ChargedParticle(new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), new Vector2D(0, 0));
            ChargedParticle chargedParticle4 = new ChargedParticle(new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), new Vector2D(0, 0));
            ChargedParticle chargedParticle5 = new ChargedParticle(new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), new Vector2D(0, 0));
            ChargedParticle chargedParticle6 = new ChargedParticle(new Vector2D(random.nextInt(spaceWidth), random.nextInt(spaceHeight)), new Vector2D(0, 0));
            Spring spring1 = new Spring(chargedParticle1, chargedParticle2);
            Spring spring2 = new Spring(chargedParticle2, chargedParticle3);
            Spring spring3 = new Spring(chargedParticle3, chargedParticle4);
            Spring spring4 = new Spring(chargedParticle4, chargedParticle5);
            Spring spring5 = new Spring(chargedParticle5, chargedParticle6);
            Spring spring6 = new Spring(chargedParticle6, chargedParticle1);

            space.addBody(new Air());
            space.addBody(chargedParticle1);
            space.addBody(chargedParticle2);
            space.addBody(chargedParticle3);
            space.addBody(chargedParticle4);
            space.addBody(chargedParticle5);
            space.addBody(chargedParticle6);
            space.addBody(spring1);
            space.addBody(spring2);
            space.addBody(spring3);
            space.addBody(spring4);
            space.addBody(spring5);
            space.addBody(spring6);

            JScrollPane scrollPane = new JScrollPane(space);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            frame.add(scrollPane);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
