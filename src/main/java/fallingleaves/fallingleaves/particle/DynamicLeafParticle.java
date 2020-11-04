package fallingleaves.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DynamicLeafParticle extends BlockDustParticle {
    private float rotateFactor;

    public DynamicLeafParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState blockState) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, blockState);

        int j = MinecraftClient.getInstance().getBlockColors().getColor(blockState, world, new BlockPos(x, y, z), 0);
        float k = (float)(j >> 16 & 255) / 255.0F;
        float l = (float)(j >> 8 & 255) / 255.0F;
        float m = (float)(j & 255) / 255.0F;

        this.collidesWithWorld = true;
        this.gravityStrength = 0.1F;
        this.maxAge = 200;

        this.velocityX *= 0.3F;
        this.velocityY *= 0.0F;
        this.velocityZ *= 0.3F;

        this.colorRed = (float) k;
        this.colorGreen = (float) l;
        this.colorBlue = (float) m;
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

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<BlockStateParticleEffect> {
        public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BlockState blockState = blockStateParticleEffect.getBlockState();
            return !blockState.isAir() && !blockState.isOf(Blocks.MOVING_PISTON) ? (new BlockDustParticle(clientWorld, d, e, f, g, h, i, blockState)).setBlockPosFromPosition() : null;
        }
    }
}
