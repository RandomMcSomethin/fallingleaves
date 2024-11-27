package randommcsomethin.fallingleaves.seasons;

public class FabricSeasonsCompat {
	public static Season convertSeason(io.github.lucaargolo.seasons.utils.Season season) {
		return switch (season) {
			case SPRING -> Season.SPRING;
			case SUMMER -> Season.SUMMER;
			case FALL -> Season.FALL;
			case WINTER -> Season.WINTER;
		};
	}
}
