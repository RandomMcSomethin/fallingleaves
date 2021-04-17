package randommcsomethin.fallingleaves.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import randommcsomethin.fallingleaves.math.SmoothNoise;
import randommcsomethin.fallingleaves.math.TriangularDistribution;

import java.util.Random;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class Wind {
    protected static Random rng = new Random();

    public static void debug() {
        state = State.values()[(state.ordinal() + 1) % State.values().length];
        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        chatHud.addMessage(new LiteralText("set wind state to " + state));
    }

    protected enum State {
        CALM(  0.05f, 0.05f, 0.2f),
        WINDY( 0.05f, 0.3f,  0.7f),
        STORMY(0.05f, 0.6f,  1.1f);

        public TriangularDistribution velocityDistribution;

        State(float minSpeed, float likelySpeed, float maxSpeed) {
            this.velocityDistribution = new TriangularDistribution(minSpeed, maxSpeed, likelySpeed, rng);
        }
    }

    protected static float TAU = (float)(2 * Math.PI);

    public static float windX;
    public static float windZ;

    protected static SmoothNoise velocityNoise;
    protected static SmoothNoise directionTrendNoise;
    protected static SmoothNoise directionNoise;

    protected static boolean wasRaining;
    protected static boolean wasThundering;
    protected static State state;
    protected static State originalState;
    protected static int stateDuration; // ticks

    public static void init() {
        LOGGER.debug("Wind.init");

        wasRaining = false;
        wasThundering = false;
        state = State.CALM;
        stateDuration = 0;

        windX = windZ = 0;

        velocityNoise = new SmoothNoise(2 * 20, 0, (old) -> {
            return state.velocityDistribution.sample();
        });
        directionTrendNoise = new SmoothNoise(30 * 60 * 20, rng.nextFloat() * TAU, (old) -> {
            return rng.nextFloat() * TAU;
        });
        directionNoise = new SmoothNoise(10 * 20, 0, (old) -> {
            return (2f * rng.nextFloat() - 1f) * TAU / 8f;
        });
    }

    protected static void tickState(ClientWorld world) {
        --stateDuration;

        Identifier dimension = world.getRegistryKey().getValue();

        if (!CONFIG.windEnabled || CONFIG.windlessDimensions.contains(dimension)) {
            // override state to calm when there is no wind
            originalState = state;
            state = State.CALM;
            return;
        }

        // restore overridden state
        if (originalState != null) {
            state = originalState;
            originalState = null;
        }

        boolean isRaining = world.getLevelProperties().isRaining();
        boolean isThundering = world.isThundering();
        boolean weatherChanged = wasRaining != isRaining || wasThundering != isThundering;

        if (weatherChanged || stateDuration <= 0) {
            if (isThundering) {
                state = State.STORMY;
            } else {
                // windy and stormy when raining, calm and windy otherwise
                int index = rng.nextInt(2);
                state = State.values()[(isRaining ? index + 1 : index)];
            }

            stateDuration = 6 * 60 * 20; // change state every 6 minutes
            LOGGER.debug("new wind state {}", state);
        }

        wasRaining = isRaining;
        wasThundering = isThundering;
    }

    public static void tick(ClientWorld world) {
        tickState(world);

        velocityNoise.tick();
        directionTrendNoise.tick();
        directionNoise.tick();

        float strength = velocityNoise.getNoise();
        float direction = directionTrendNoise.getLerp() + directionNoise.getNoise();

        /**
        LOGGER.printf(Level.DEBUG, "state %s strength %.2f -> %.2f direction var %.2f° -> %.2f°, trend %.2f° -> %.2f°",
            state.toString(),
            strengthNoise.getNoise(),
            strengthNoise.getRightNoise(),
            directionNoise.getNoise() * 360.0 / TAU,
            directionNoise.getRightNoise() * 360.0 / TAU,
            directionTrendNoise.getLerp() * 360.0 / TAU,
            directionTrendNoise.getRightNoise() * 360.0 / TAU);
        /**/

        // calculate wind velocity (in blocks / tick)
        windX = strength * MathHelper.cos(direction);
        windZ = strength * MathHelper.sin(direction);
    }
}
