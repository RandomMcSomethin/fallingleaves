package randommcsomethin.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.*;
import randommcsomethin.fallingleaves.util.Wind;

import java.util.stream.Stream;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class FallingLeafParticle extends SpriteBillboardParticle {

    protected static final int FADE_DURATION = 16; // in ticks
    protected static final double FRICTION       = 1 - 0.30;
    protected static final double WATER_FRICTION = 1 - 0.05;

    protected final float windCoefficient; // to emulate drag/lift

    protected final float rotateFactor;

    protected FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, 0.0, 0.0, 0.0);
        this.setSprite(provider);

        this.gravityStrength = 0.08f + (float)Math.random() * 0.04f;
        this.windCoefficient = 0.6f + (float)Math.random() * 0.4f;

        // the Particle constructor adds random noise to the velocity which we don't want
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;

        this.collidesWithWorld = true; // TODO: is it possible to turn off collisions with leaf blocks?
        this.maxAge = CONFIG.leafLifespan;

        this.colorRed   = (float) r;
        this.colorGreen = (float) g;
        this.colorBlue  = (float) b;
        // TODO: the longer the leaves live the faster they spin, which is an issue for long lived leaves
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        this.scale = CONFIG.getLeafSize();
    }

    @Override
    public void tick() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        prevAngle = angle;

        age++;

        // fade-out animation
        if (age >= maxAge + 1 - FADE_DURATION) {
            colorAlpha -= 1F / FADE_DURATION;
        }

        if (age >= maxAge) {
            markDead();
            return;
        }

        if (world.getFluidState(new BlockPos(x, y, z)).isIn(FluidTags.WATER)) {
            // float on water
            velocityY = 0.0;

            velocityX *= WATER_FRICTION;
            velocityZ *= WATER_FRICTION;
        } else {
            // apply gravity
            velocityY -= 0.04 * gravityStrength;

            // spin when in the air
            if (!onGround) {
                angle += Math.PI * MathHelper.sin(rotateFactor * age) / 2F;
            } else {
                velocityX *= FRICTION;
                velocityZ *= FRICTION;
            }

            // TODO: field_21507 inside move() makes particles stop permanently once they fall on the ground
            //       that is nice sometimes, but some/most leaves should still get blown along the ground by the wind

            // approach the wind speed over time
            velocityX += (Wind.windX - velocityX) * windCoefficient / 60.0f;
            velocityZ += (Wind.windZ - velocityZ) * windCoefficient / 60.0f;
        }

        move(velocityX, velocityY, velocityZ);
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
            return new FallingLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.provider);
        }
    }

}
