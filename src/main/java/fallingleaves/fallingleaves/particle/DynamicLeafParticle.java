package fallingleaves.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class DynamicLeafParticle extends SpriteBillboardParticle {
    private float rotateFactor;

    public DynamicLeafParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, String blockState) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        //int j = MinecraftClient.getInstance().getBlockColors().getColor(getBLo, world, new BlockPos(x, y, z), 0);
        //float k = (float)(j >> 16 & 255) / 255.0F;
        //float l = (float)(j >> 8 & 255) / 255.0F;
        //float m = (float)(j & 255) / 255.0F;

        this.collidesWithWorld = true;
        this.gravityStrength = 0.1F;
        this.maxAge = 200;

        this.velocityX *= 0.3F;
        this.velocityY *= 0.0F;
        this.velocityZ *= 0.3F;

        this.rotateFactor = ((float)Math.random() - 0.5F) * 0.01F;
        this.scale = 0.15F;
    }

    public void tick() {
        super.tick();
        if (this.age < 2) {
            this.velocityY = 0;
        }
        if (this.age > this.maxAge - 1/0.06F) {
            if (this.colorAlpha > 0.06F) {
                this.colorAlpha -= 0.06F;
            } else {
                this.markDead();
            }
        }
        this.prevAngle = this.angle;
        if (!this.onGround && !this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            this.angle += Math.PI * Math.sin(this.rotateFactor * this.age)/2;
        }
        if (this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            this.velocityY = 0;
            this.gravityStrength = 0;
        } else {
            this.gravityStrength = 0.1F;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DynamicLeafParticleEffect> {
        public Factory(SpriteProvider spriteProvider) {
        }

        @Override
        public @Nullable Particle createParticle(DynamicLeafParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new DynamicLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, parameters.state);
        }
    }
}
