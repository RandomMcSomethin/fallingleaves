package randommcsomethin.fallingleaves.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import randommcsomethin.fallingleaves.init.Config;

public class LogUtil {
    public static final Logger LOGGER = LogManager.getLogger("fallingleaves");
    public static final Marker INTERNAL = MarkerManager.getMarker("INTERNAL");

    public String name;

    public LogUtil(String name) {
        this.name = name;
    }

    public Logger getLogger() {
        return LogManager.getLogger(this.name);
    }

    // Elevates debug messages to info-messages if config value "displayDebugData" is set to true.
    public void debug(String debugMessage) {
        if (ConfigUtil.shouldDebugDataBeDisplayed()) {
            info(debugMessage);
        } else {
            debug(INTERNAL, debugMessage);
        }
    }

    public void debug(Marker marker, String debugMessage) {
        getLogger().debug(marker, debugMessage);
    }

    public void info(String infoMessage) {
        info(INTERNAL, infoMessage);
    }

    public void info(Marker marker, String infoMessage) {
        getLogger().info(marker, infoMessage);
    }

    public void warn(String warningMessage) {
        warn(INTERNAL, warningMessage);
    }

    public void warn(Marker marker, String warningMessage) {
        getLogger().warn(marker, warningMessage);
    }

    public void error(String errorMessage) {
        error(INTERNAL, errorMessage);
    }

    public void error(Marker marker, String errorMessage) {
        getLogger().error(marker, errorMessage);
    }

}
