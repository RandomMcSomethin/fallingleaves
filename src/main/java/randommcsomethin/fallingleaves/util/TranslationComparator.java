package randommcsomethin.fallingleaves.util;

import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;

/** Compares translation keys by their actual translation */
public class TranslationComparator implements Comparator<String> {
    public static final TranslationComparator INST = new TranslationComparator();

    private final Map<String, String> translations = new WeakHashMap<>();
    private Language cachedLanguage = null;

    private TranslationComparator() { }

    @Nullable
    public String getTranslation(String translationKey) {
        Language language = Language.getInstance();
        String cachedTranslation = translations.get(translationKey);

        // Update translation when needed
        if (cachedTranslation == null || language != cachedLanguage) {
            cachedLanguage = language;

            if (language.hasTranslation(translationKey)) {
                String translation = language.get(translationKey);
                translations.put(translationKey, translation);
                return translation;
            }
        }

        return cachedTranslation;
    }

    @Override
    public int compare(String key1, String key2) {
        String t1 = getTranslation(key1);
        String t2 = getTranslation(key2);

        if (t1 == null || t2 == null) {
            LOGGER.warn("trying to compare non-translated ids {} -> {} with {} -> {}", key1, t1, key2, t2);
            return key1.compareTo(key2);
        }

        return t1.compareTo(t2);
    }
}
