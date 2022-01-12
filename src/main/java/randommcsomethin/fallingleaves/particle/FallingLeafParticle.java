package randommcsomethin.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import randommcsomethin.fallingleaves.util.Wind;

import java.util.List;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class FallingLeafParticle extends SpriteBillboardParticle {

    public static final float TAU = (float)(2 * Math.PI); // 1 rotation

    public static final int FADE_DURATION = 16; // ticks
    // public static final double FRICTION       = 0.30;
    public static final double WATER_FRICTION = 0.05;

    protected final float windCoefficient; // to emulate drag/lift

    protected final float maxRotateSpeed; // rotations / tick
    protected final int maxRotateTime;
    protected int rotateTime = 0;
    protected boolean stuckInGround = false;

    public FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, 0.0, 0.0, 0.0);
        this.setSprite(provider);

        this.gravityStrength = 0.08f + random.nextFloat() * 0.04f;
        this.windCoefficient = 0.6f + random.nextFloat() * 0.4f;

        // the Particle constructor adds random noise to the velocity which we don't want
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;

        this.maxAge = CONFIG.leafLifespan;

        this.red   = (float) r;
        this.green = (float) g;
        this.blue  = (float) b;

        // accelerate over 3-7 seconds to at most 2.5 rotations per second
        this.maxRotateTime = (3 + random.nextInt(4 + 1)) * 20;
        this.maxRotateSpeed = (random.nextBoolean() ? -1 : 1) * (0.1f + 2.4f * random.nextFloat()) * TAU / 20f;

        this.angle = this.prevAngle = random.nextFloat() * TAU;

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
            alpha -= 1F / FADE_DURATION;
        }

        if (age >= maxAge) {
            markDead();
            return;
        }

        if (world.getFluidState(new BlockPos(x, y, z)).isIn(FluidTags.WATER)) {
            // float on water
            velocityY = 0.0;
            rotateTime = 0;

            velocityX *= (1 - WATER_FRICTION);
            velocityZ *= (1 - WATER_FRICTION);
        } else {
            // apply gravity
            velocityY -= 0.04 * gravityStrength;

            if (!onGround) {
                // spin when in the air
                rotateTime = Math.min(rotateTime + 1, maxRotateTime);
                angle += (rotateTime / (float) maxRotateTime) * maxRotateSpeed;
            } else {
                rotateTime = 0;

                // TODO: leaves get stuck in the ground which is nice sometimes, but some/most leaves should
                //       still get blown by the wind / tumble over the ground
                // velocityX *= (1 - FRICTION);
                // velocityZ *= (1 - FRICTION);
            }

            // approach the target wind velocity over time via vel += (target - vel) * f, where f is in (0, 1)
            // after n ticks, the distance closes to a factor of 1 - (1 - f)^n.
            // for f = 1 / 2, it would only take 4 ticks to close the distance by 90%
            // for f = 1 / 60, it takes ~2 seconds to halve the distance, ~5 seconds to reach 80%
            //
            // the wind coefficient is just another factor in (0, 1) to add some variance between leaves.
            // this implementation lags behind the actual wind speed and will never reach it fully,
            // so wind speeds needs to be adjusted accordingly
            velocityX += (Wind.windX - velocityX) * windCoefficient / 60.0f;
            velocityZ += (Wind.windZ - velocityZ) * windCoefficient / 60.0f;
        }

        move(velocityX, velocityY, velocityZ);
    }

    @Override
    public void move(double dx, double dy, double dz) {
        if (dx == 0.0 && dy == 0.0 && dz == 0.0) return;

        double oldDx = dx;
        double oldDy = dy;
        double oldDz = dz;

        // TODO: is it possible to turn off collisions with leaf blocks?
        Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), getBoundingBox(), world, List.of());
        dx = vec3d.x;
        dy = vec3d.y;
        dz = vec3d.z;

        // lose horizontal velocity on collision
        if (oldDx != dx) velocityX = 0.0;
        if (oldDz != dz) velocityZ = 0.0;

        onGround = oldDy != dy && oldDy < 0.0;

        if (!onGround) {
            stuckInGround = false;
        } else {
            // get stuck if slow enough
            if (!stuckInGround && Math.abs(dy) < 1E-5) {
                stuckInGround = true;
            }
        }

        if (stuckInGround) {
            // don't accumulate speed over time
            velocityX = 0.0;
            velocityY = 0.0;
            velocityZ = 0.0;

            // don't move
            return;
        }

        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            setBoundingBox(getBoundingBox().offset(dx, dy, dz));
            repositionFromBoundingBox();
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
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new FallingLeafParticle(world, x, y, z, r, g, b, provider);
        }
    }

}
