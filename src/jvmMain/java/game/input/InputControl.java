package game.input;

import game.output.ui.Menu;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import static game.input.InputControl.Mousebutton.Left;
import static game.input.InputControl.Mousebutton.Right;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_SHIFT;

public class InputControl extends MouseAdapter implements KeyListener {
    //region Events
    private static final Map<Integer, KeyEvent> keyPressEvents = new HashMap<>();
    private static final Map<Integer, KeyEvent> keyReleaseEvents = new HashMap<>();
    private static final Map<Mousebutton, MouseEvent> mousePressEvents = new HashMap<>();
    private static final Map<Mousebutton, MouseEvent> mouseReleaseEvents = new HashMap<>();

    //endregion
    //region MouseListener methods
    @Override
    public void mousePressed(MouseEvent e) {
        if (getButton(e.getButton()) == Left) {
            Menu.press(mousepos);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (getButton(e.getButton()) == Left) {
            Menu.release();
        }
    }

    @Override
    public void mouseMoved(@NotNull MouseEvent e) {
        mousepos.setEntry(0, e.getPoint().x);
        mousepos.setEntry(1, e.getPoint().y);
    }

    @Override
    public void mouseDragged(@NotNull MouseEvent e) {
        mousepos.setEntry(0, e.getPoint().x);
        mousepos.setEntry(1, e.getPoint().y);
    }

    //endregion
    //region KeyListener methods
    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        keyPressEvents.put(e.getKeyCode(), e);
    }

    @Override
    public void keyReleased(@NotNull KeyEvent e) {
        keyReleaseEvents.put(e.getKeyCode(), e);
    }

    //region (not used)
    @Override
    public void keyTyped(KeyEvent e) {

    }
    //endregion
    //endregion

    enum Mousebutton {
        Left,
        Right
    }

    public static final ArrayRealVector mousepos = new ArrayRealVector(new Double[]{0.0, 0.0});

    static {
        for (int i = 0; i <= 0xE3; i++) {
            keyPressEvents.put(i, null);
            keyReleaseEvents.put(i, null);
        }
    }

    static String text = "";

    public static void handleInput() {
        if (pressed(Left)) {
            Menu.press(mousepos);
            dispatch(Left);
        }
        if (released(Left)) {
            Menu.release();
            dispatch(Left);
        }
    }


    private static void dispatchText() {
        for (int key = 0x2C; key < 0x69 + 1; key++) {
            dispatch(key);
        }
        dispatch(VK_ENTER);
        dispatch(VK_SHIFT);
        text = "";
    }

    @NotNull
    private static String getText() {
        StringBuilder textBuilder = new StringBuilder();
        for (int key = 0x2C; key < 0x69 + 1; key++) {
            if (pressed(key)) {
                textBuilder.append(keyPressEvents.get(key).getKeyChar());
                unpress(key);
            }
        }
        if (released(VK_SHIFT)) {
            dispatch(VK_SHIFT);
        }
        String text = textBuilder.toString();
        if (!pressed(VK_SHIFT)) {
            text = text.toLowerCase();
        }
        text = text.replaceAll("\\p{C}", "");
        return text;
    }

    private static Mousebutton getButton(int button) {
        if (button == MouseEvent.BUTTON1) {
            return Left;
        } else {
            return Right;
        }
    }

    //region Private key/mousebutton getters and setters
    private static boolean pressed(int key) {
        return keyPressEvents.get(key) != null;
    }

    private static boolean released(int key) {
        return keyReleaseEvents.get(key) != null;
    }

    private static void unrelease(int key) {
        keyReleaseEvents.put(key, null);
    }

    private static void unpress(int key) {
        keyPressEvents.put(key, null);
    }

    private static void dispatch(int key) {
        keyReleaseEvents.put(key, null);
        keyPressEvents.put(key, null);
    }

    private static boolean pressed(Mousebutton button) {
        return mousePressEvents.get(button) != null;
    }

    private static boolean released(Mousebutton button) {
        return mouseReleaseEvents.get(button) != null;
    }

    private static void unrelease(Mousebutton button) {
        mouseReleaseEvents.put(button, null);
    }

    private static void unpress(Mousebutton button) {
        mousePressEvents.put(button, null);
    }

    private static void dispatch(Mousebutton button) {
        mouseReleaseEvents.put(button, null);
        mousePressEvents.put(button, null);
    }
    //endregion
}
