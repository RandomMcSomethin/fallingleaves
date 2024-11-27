package randommcsomethin.fallingleaves.seasons;

public class SereneSeasonsCompat {
	public static Season convertSeason(sereneseasons.api.season.Season season) {
		return switch (season) {
			case SPRING -> Season.SPRING;
			case SUMMER -> Season.SUMMER;
			case AUTUMN -> Season.FALL;
			case WINTER -> Season.WINTER;
		};
	}
}
