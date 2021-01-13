package randommcsomethin.fallingleaves.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigDefaults {

    final public static Map<String, Integer> SPAWNRATE = Stream.of(new Object[][]{
        // Shrubs and large leaves
        {"minecraft:jungle_leaves", 0},
        {"terrestria:japenese_maple_shrub_leaves", 0},
        {"terrestria:jungle_palm_leaves", 0},
        {"terrestria:yucca_palm_leaves", 0},

        // Autumn Leaves
        {"traverse:brown_autumnal_leaves", 3},
        {"traverse:orange_autumnal_leaves", 3},
        {"traverse:red_autumnal_leaves", 3},

        // Other / Opinionated
        {"terrestria:sakura_leaves", 2}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));

    final public static Collection<String> CONIFER = Arrays.asList(
        "byg:blue_spruce_leaves",
        "byg:cypress_leaves",
        "byg:fir_leaves",
        "byg:orange_spruce_leaves",
        "byg:pine_leaves",
        "byg:red_spruce_leaves",
        "byg:yellow_spruce_leaves",
        "minecraft:spruce_leaves",
        "terrestria:cypress_leaves",
        "terrestria:hemlock_leaves",
        "terrestria:redwood_leaves",
        "traverse:fir_leaves",
        "woods_and_mires:pine_leaves"
    );

    public static boolean isConifer(OverrideEntry entry) {
        return CONIFER.contains(entry.identifier);
    }

    public static int spawnRate(OverrideEntry entry) {
        if (SPAWNRATE.containsKey(entry.identifier)) {
            return SPAWNRATE.get(entry.identifier);
        }

        return 1;
    }

}
