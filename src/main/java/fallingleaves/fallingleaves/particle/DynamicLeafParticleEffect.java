package fallingleaves.fallingleaves.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fallingleaves.fallingleaves.client.FallingLeavesClient;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class DynamicLeafParticleEffect implements ParticleEffect {
    public final String state;

    public static final Codec<DynamicLeafParticleEffect> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("state").forGetter((particleEffect) -> particleEffect.state))
            .apply(instance, DynamicLeafParticleEffect::new));

    public DynamicLeafParticleEffect(String state) {
        this.state = state;
    }

    @Override
    public ParticleType<?> getType() {
        return FallingLeavesClient.DYNAMIC_FALLING_LEAF;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.state.toString());
    }

    @Override
    public String asString() {
        return "DynamicLeaf";
    }

    public static final Factory<DynamicLeafParticleEffect> FACTORY = new Factory<DynamicLeafParticleEffect>() {
        @Override
        public DynamicLeafParticleEffect read(ParticleType<DynamicLeafParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            String st = reader.readString();
            return new DynamicLeafParticleEffect(st);
        }

        @Override
        public DynamicLeafParticleEffect read(ParticleType<DynamicLeafParticleEffect> type, PacketByteBuf buf) {
            return new DynamicLeafParticleEffect(buf.readString());
        }
    };

}
