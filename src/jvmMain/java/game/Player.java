package game;

import game.input.InputInfo;
import game.util.DevConfig;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Player {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    int score;
    public int deathTimer;
    InputInfo input;
    SocketChannel channel;
    String name;

    public Player(String name) {
        score = 0;
        deathTimer = DevConfig.deathFrames;
        input = new InputInfo();
        claimName(name);
    }


    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public InputInfo getInput() {
        return input;
    }

    public void claimName(String desiredName) {

    }

    public void connectInput(@NotNull InputInfo input) {
        this.input = input;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

}
