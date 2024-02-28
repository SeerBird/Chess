package game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Resources {
    public static final Image bp;
    public static final Image br;
    public static final Image bn;
    public static final Image bb;
    public static final Image bq;
    public static final Image bk;
    public static final Image wp;
    public static final Image wr;
    public static final Image wn;
    public static final Image wb;
    public static final Image wq;
    public static final Image wk;

    static {
        try {
            bp = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("bp.png")));
            br = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("br.png")));
            bn = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("bn.png")));
            bb = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("bb.png")));
            bq = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("bq.png")));
            bk = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("bk.png")));
            wp = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("wp.png")));
            wr = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("wr.png")));
            wn = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("wn.png")));
            wb = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("wb.png")));
            wq = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("wq.png")));
            wk = ImageIO.read(Objects.requireNonNull(Resources.class.getResource("wk.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
