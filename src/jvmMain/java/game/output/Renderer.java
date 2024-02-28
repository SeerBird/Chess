package game.output;


import game.GameHandler;
import game.Resources;
import game.model.Board;
import game.model.Piece;
import game.output.animations.Animation;
import game.output.ui.IElement;
import game.output.ui.Menu;
import game.output.ui.rectangles.Button;
import game.output.ui.rectangles.Label;
import game.output.ui.rectangles.*;
import game.util.DevConfig;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

import static game.model.Position.pos;
import static game.util.DevConfig.tileSize;

public class Renderer {
    static Graphics g;
    static final ArrayList<Animation> animations = new ArrayList<>();
    static int x = 0;
    static int y = 0;

    public static void update() { //get new info and progress animations
        for (Animation animation : new ArrayList<>(animations)) {
            if (!animation.drawNext(g)) {
                removeAnimation(animation);
            }
        }
    }

    public static void drawImage(@NotNull Graphics g) { //I'm just gonna call this whenever something changes. optimize? never.
        Renderer.g = g;
        g.translate(x, y);
        Board board = GameHandler.getBoard();
        //region board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i % 2 == 0 ^ j % 2 == 0) {
                    g.setColor(DevConfig.black);
                } else {
                    g.setColor(DevConfig.white);
                }
                /*
                if (board.isAttacked(false, pos(i, j))){
                    g.setColor(Color.magenta);
                }

                 */
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        //endregion
        update();
        drawMenu();
        for (Piece piece : GameHandler.getBoard().getPieces()) {
            g.drawImage(getImage(piece), piece.pos.x * tileSize, piece.pos.y * tileSize, tileSize, tileSize, null);
        }
        g.dispose();
    }

    //region Menu
    private static void drawMenu() {
        if (Menu.getMoves() != null) {
            for (Button move : Menu.getMoves()) {
                drawButton(move);
            }
        }
    }

    //region draw elements
    private static void drawRect(@NotNull RectElement e, Color color) {
        g.setColor(color);
        g.drawRect(e.x, e.y, e.width, e.height);
    }


    private static void drawButton(@NotNull Button button) {
        if (button.isPressed()) {
            g.setColor(button.color.darker());
        } else {
            g.setColor(button.color);
        }
        g.fillRect(button.x, button.y, button.width, button.height);
    }

    private static void drawToggleable(@NotNull Toggleable toggle) {
        if (toggle.getState()) {
            g.setColor(toggle.textColor.darker());
        } else {
            g.setColor(toggle.textColor);
        }
        g.drawRect(toggle.x, toggle.y, toggle.width, toggle.height);
        g.drawRect(toggle.x + 4, toggle.y + 4, toggle.width - 8, toggle.height - 8);
        drawLabelText(toggle, toggle.textColor);
    }

    private static void drawLabel(@NotNull Label label) {
        drawLabelText(label, label.textColor);
    }

    private static void drawTextbox(@NotNull Textbox textbox) {
        drawLabelText(textbox, textbox.textColor);
        drawRect(textbox, textbox.textColor);
    }

    private static void drawLabelText(@NotNull Label label, Color color) {
        g.setColor(color);
        g.drawString(label.text, label.x + label.width / 2 - g.getFontMetrics().stringWidth(label.text) / 2, label.y + label.height / 2);
    }

    //endregion
    //endregion

    //region Animations
    public static Animation addAnimation(Animation animation) {
        animations.add(animation);
        return animation;
    }

    public static void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    //endregion
    private static Image getImage(Piece piece) {
        if (piece.color) {
            switch (piece.type) {
                case pawn -> {
                    return Resources.bp;
                }
                case rook -> {
                    return Resources.br;
                }
                case knight -> {
                    return Resources.bn;
                }
                case bishop -> {
                    return Resources.bb;
                }
                case queen -> {
                    return Resources.bq;
                }
                case king -> {
                    return Resources.bk;
                }
            }
        } else {
            switch (piece.type) {
                case pawn -> {
                    return Resources.wp;
                }
                case rook -> {
                    return Resources.wr;
                }
                case knight -> {
                    return Resources.wn;
                }
                case bishop -> {
                    return Resources.wb;
                }
                case queen -> {
                    return Resources.wq;
                }
                case king -> {
                    return Resources.wk;
                }
            }
        }
        return null;
    }


    private static void fill(Color c) {
        g.setColor(c);
        g.fillRect(-200, -200, tileSize * 8 + 400, tileSize * 8 + 400);
    }

    public static int getStringWidth(String string) {
        if (g != null) {
            return g.getFontMetrics().stringWidth(string);
        } else {
            return -1;
        }
    }

    public static void setPos(@NotNull ArrayRealVector p) {
        x = (int) p.getEntry(0);
        y = (int) p.getEntry(1);
    }
}

