package game;

import game.output.GameWindow;
import game.output.Renderer;
import game.output.ui.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


public class GameHandler {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    //region Jobs
    private static final HashMap<Job, Runnable> job = new HashMap<>();
    private static final Player white = new Player("Player 1");
    private static final Player black = new Player("Player 2");

    private enum Job {
    }

    private static final ArrayList<Job> toRemove = new ArrayList<>();
    private static final ArrayList<Job> toAdd = new ArrayList<>();

    private static final ArrayList<Runnable> jobs = new ArrayList<>();
    //endregion
    static final GameWindow window = new GameWindow();
    private static GameState state;

    static {
        state = GameState.main;
        //region Define job dictionary
        job.clear();
        //endregion
        //region Set starting state
        //endregion
    }

    public static void out() {
        Renderer.drawImage(window.getCanvas());
        window.showCanvas();
    }

    public static void update() {
        //region remove and add jobs
        for (Job added : toAdd) {
            jobs.add(job.get(added));
        }
        toAdd.clear();
        for (Job removed : toRemove) {
            jobs.remove(job.get(removed));
        }
        toRemove.clear();
        //endregion
        //region get em done
        for (Runnable job : jobs) {
            job.run();
        }
        //endregion
    }

    private static void addJob(Job job) {
        toAdd.add(job);
    }

    private static void removeJob(Job job) {
        toRemove.add(job);
    }


    //region State traversal
    private static void setState(GameState gameState) {
        state = gameState;
        Menu.refreshGameState();
    }

    public static GameState getState() {
        return state;
    }

    public static void escape() {
    }
    //endregion
}
