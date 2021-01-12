package randommcsomethin.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import randommcsomethin.fallingleaves.LeafUtils;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class FallingLeafParticle extends SpriteBillboardParticle {

    private final float rotateFactor;

    protected FallingLeafParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider provider) {
        super(clientWorld, d, e, f, g, h, i);
        this.setSprite(provider);

        this.collidesWithWorld = true;
        this.gravityStrength = 0.1F;
        this.maxAge = CONFIG.leafLifespan;

        this.velocityX *= 0.3F;
        this.velocityY *= 0.0F;
        this.velocityZ *= 0.3F;

        this.colorRed = (float) g;
        this.colorGreen = (float) h;
        this.colorBlue = (float) i;
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        // As leaf size is now an integer, we divide by 10 to get the float.
        this.scale = (float) CONFIG.leafSize / 10;
    }

    public void tick() {
        super.tick();

        if (this.age < 2) {
            this.velocityY = 0;
        }

        if (this.age > this.maxAge - 1 / 0.06F) {
            if (this.colorAlpha > 0.06F) {
                this.colorAlpha -= 0.06F;
            } else {
                this.markDead();
            }
        }

        this.prevAngle = this.angle;

        if (!this.onGround && !this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            this.angle += Math.PI * Math.sin(this.rotateFactor * this.age) / 2;
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
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider provider;

        public DefaultFactory(SpriteProvider provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            LeafUtils.debugLog("Creating new leaf particle at " + x + " " + y + " " + z + ".");
            return new FallingLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.provider);
        }
    }
}
