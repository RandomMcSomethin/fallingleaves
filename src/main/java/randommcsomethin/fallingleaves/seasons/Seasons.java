package randommcsomethin.fallingleaves.seasons;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import randommcsomethin.fallingleaves.FallingLeavesClient;
import sereneseasons.api.season.SeasonHelper;

public class Seasons {
	@Nullable
	public static Season currentSeason = null;

	public static void tick(ClientWorld world) {
		Season newSeason = null;

		if (FabricLoader.getInstance().isModLoaded("seasons")) {
			newSeason = FabricSeasonsCompat.convertSeason(FabricSeasons.getCurrentSeason());
		} else if (FabricLoader.getInstance().isModLoaded("sereneseasons")) {
			newSeason = SereneSeasonsCompat.convertSeason(SeasonHelper.getSeasonState(world).getSeason());
		}

		if (currentSeason != newSeason) {
			FallingLeavesClient.LOGGER.debug("changed season {} -> {}", currentSeason, newSeason);
		}

		currentSeason = newSeason;
	}
}
