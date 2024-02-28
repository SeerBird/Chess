package game.output.ui.rectangles;


import game.output.audio.Audio;
import game.output.audio.Sound;

import java.awt.*;

public class Button extends Label {
    private Runnable action;
    private boolean pressed;
    public Color color;

    public Button(int x, int y, int width, int height, Runnable action, Color color) {
        super(x, y, width, height, "", Color.BLACK);
        this.color = color;
        this.action = action;
        pressed = false;
    }

    @Override
    public boolean press(double x, double y) {
        pressed = super.press(x, y);
        return pressed;
    }

    @Override
    public void release() {
        if (pressed) {//unnecessary?
            action.run();
        }
        pressed = false;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public boolean isPressed() {
        return pressed;
    }
}
