package randommcsomethin.fallingleaves.config;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** Old v0 config format used in versions 1.0 to 1.4 */
@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
public class FallingLeavesConfigV0 {
    public int version = 0; // added to be able to differentiate between v1
    public double leafSize = 0.10;
    public int leafLifespan = 200;
    public double leafRate = 1.0;
    public double coniferLeafRate = 0.0;
    // If coniferLeafIds and rateOverrides somehow weren't defined, use new v1 defaults
    public Set<String> coniferLeafIds = Collections.emptySet();
    public Map<String, Double> rateOverrides = Collections.emptyMap();
}