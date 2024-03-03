package game.util;

import java.awt.*;

public class DevConfig {
    //region colors
    public static final int tileSize = 95;
    public static Color black = new Color(56, 53, 44, 255);
    public static Color white = new Color(241, 223, 171, 255);
    public static Color green = new Color(49, 250, 4, 82);
    public static Color red = new Color(255, 1, 1, 115);
    public static Color promotion = new Color(73, 0, 248, 115);
    //endregion
    //region board
    public static int turnLimit = 7500;
    public static final int mandatoryOutputPeriod = 21;
    public static boolean randomStart = true;
    //endregion
    //region ml
    public static final int layers = 3;
    public static final int layerWidth = 50;
    //endregion
}
