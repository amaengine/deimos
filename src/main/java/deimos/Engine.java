package deimos;

import deimos.listener.OnInit;
import deimos.listener.OnStart;
import deimos.listener.OnStop;
import deimos.listener.OnTick;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    // Singleton
    static Engine o;

    // Testing Mode
    private static boolean testMode;

    private final Game game;
    private boolean running;
    private List<OnTick> tickListeners;
    private List<Component> newComponents;

    public static void start(Game game) {
        if (o != null)
            throw new IllegalStateException("Engine can only be started once.");
        newEngine(game);
    }

    public static Engine test(Game game) {
        testMode = true;
        newEngine(game);
        return o;
    }

    private static void newEngine(Game game) {
        o = new Engine(game);
        o.run();
    }

    private Engine(Game game) {
        this.game = game;
    }

    private void run() {
        running = true;
        try {
            init();
            if (testMode) return;
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void init() throws Exception {
        newComponents = new ArrayList<>();
        tickListeners = new ArrayList<>();
        game.load();
    }

    private void loop() {
        while (running) {
            tick();
        }
    }

    private void cleanup() {

    }

    public void tick() {
        if (!newComponents.isEmpty()) {
            List<Component> temp = new ArrayList<>(newComponents);
            newComponents.clear();

            for (Component component : temp) {
                if (component instanceof OnStart)
                    ((OnStart) component).onStart();

                if (component instanceof OnTick)
                    tickListeners.add((OnTick) component);
            }
        }

        new ArrayList<>(tickListeners).forEach(OnTick::onTick);
    }

    static void initComponent(Component component) {
        if (component instanceof OnInit)
            ((OnInit) component).onInit();
        o.newComponents.add(component);
    }

    static void stopComponent(Component component) {
        if (component instanceof OnTick)
            o.tickListeners.remove(component);
        if (component instanceof OnStop)
            ((OnStop) component).onStop();
    }
}
