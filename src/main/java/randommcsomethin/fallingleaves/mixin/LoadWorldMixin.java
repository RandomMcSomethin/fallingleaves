package randommcsomethin.fallingleaves.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Config;
import randommcsomethin.fallingleaves.util.LeafUtil;

import java.util.Map;

import static randommcsomethin.fallingleaves.FallingLeavesClient.LOGGER;
import static randommcsomethin.fallingleaves.init.Config.CONFIG;

@Mixin(MinecraftServer.class)
public abstract class LoadWorldMixin {

    @Inject(at = @At("RETURN"), method = "loadWorld")
    protected void loadWorld(CallbackInfo ci) {
        LOGGER.info("Loading all registered leaf blocks.");

        // At this point it has to be guaranteed that all modded blocks are registered and block tags are useable
        // (This is actually pretty much the earliest point in time where we can use block tags)
        // So we add all leaf blocks that weren't already read from the config file or preloaded in our ReloadListener
        for (Map.Entry<Identifier, LeafSettingsEntry> registered : LeafUtil.getRegisteredLeafBlocks(true).entrySet())
            CONFIG.leafSettings.computeIfAbsent(registered.getKey(), k -> registered.getValue());

        Config.save();
    }

}
