package randommcsomethin.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;
import randommcsomethin.fallingleaves.FallingLeavesClient;
import randommcsomethin.fallingleaves.util.SmoothNoise;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class FallingLeafParticle extends SpriteBillboardParticle {

    // how particle age/maxAge works:
    // particles start out with age 0 when constructed
    // tick() is first called when age is 1
    // tick() is last called when age = maxAge + 1 (Mojang, pls fix)
    // therefore, the lifespan of a particle is actually maxAge + 2

    protected static final double AIR_DENSITY = 1.2041;
    protected static final double DT = 1 / 20.0;

    protected static final float GRAVITY_STRENGTH = 0.1F;
    protected static final int FADE_DURATION = 16; // in ticks
    protected double mass;
    protected double dragCoefficient = 0.1;

    protected final float rotateFactor;

    // noise functions used for global wind
    protected static final SmoothNoise xNoise = new SmoothNoise();
    protected static final SmoothNoise zNoise = new SmoothNoise();
    protected static int windTicks = 0;
    protected static double windX, windZ;

    public static void tickWind() {
        windTicks++;

        // calculate wind force (divide it by mass to get acceleration in blocks/s²)
        windX = xNoise.apply((windTicks * DT) / 3.0);
        windZ = zNoise.apply((windTicks * DT) / 3.0);

        windX *= 1;
        windZ *= 1;
    }

    protected FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y - 0.2, z, 0.0, 0.0, 0.0); // TODO: temporary collision workaround
        this.setSprite(provider);

        String path = this.sprite.getId().getPath();
        char id = path.charAt(path.length() - 1); // '1'-'5'

        switch (id) {
            case '2':
                dragCoefficient = 0.2F;
                mass = 0.3;
                break;
            case '4':
                dragCoefficient = 0.3F;
                mass = 0.3;
                break;
            case '5':
                dragCoefficient = 0.25F;
                mass = 0.7;
                break;
            default:
                dragCoefficient = 0.1F;
                mass = 0.1;
                break;
        }

        // the Particle constructor adds random noise to the velocity which we don't want
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;

        this.collidesWithWorld = true; // TODO: is it possible to turn off collisions with leaf blocks?
        this.gravityStrength = GRAVITY_STRENGTH;
        this.maxAge = CONFIG.leafLifespan;

        this.colorRed   = (float) r;
        this.colorGreen = (float) g;
        this.colorBlue  = (float) b;
        // TODO: the longer the leaves live the faster they spin, which is an issue for long lived leaves
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        this.scale = CONFIG.getLeafSize();

        this.colorAlpha = 0.0F; // will be faded in
    }

    @Override
    public void tick() {
        super.tick();

        // fade-in animation
        if (this.age <= FADE_DURATION) {
            this.colorAlpha += 1F / FADE_DURATION;
        }

        // fade-out animation
        if (this.age >= this.maxAge + 1 - FADE_DURATION) {
            this.colorAlpha -= 1F / FADE_DURATION;
        }

        // prevent tick() from being called when age = maxAge + 1 (prevent negative colorAlpha)
        if (this.age == this.maxAge)
            this.markDead();

        this.prevAngle = this.angle;

        if (this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            // float on water
            this.velocityY = 0.0;
            this.gravityStrength = 0.0F;
        } else {
            // slowly fall to the ground
            this.gravityStrength = GRAVITY_STRENGTH;

            // spin when in the air
            if (!this.onGround) {
                this.angle += Math.PI * MathHelper.sin(this.rotateFactor * this.age) / 2F;
            }

            // TODO: field_21507 inside move() makes particles stop permanently once they fall on the ground
            //       that is nice sometimes, but some leaves should still get blown along the ground

            // apply wind force / integrate wind acceleration
            this.velocityX += ((windX / mass) * DT) * DT; // the last DT is because velocity is in blocks / tick
            this.velocityZ += ((windZ / mass) * DT) * DT;

            // calculate drag force (air resistance)
            // TODO: the drag seems to be practically negligible, it might be better to only use different leaf masses
            // TODO: not sure about using the particle velocity here because flow velocity should be the
            //       velocity relative to the medium, i.e. the wind
            double flowVelocityX = velocityX / DT;
            double flowVelocityZ = velocityZ / DT;
            double flowVelocitySq = flowVelocityX*flowVelocityX + flowVelocityZ*flowVelocityZ;

            if (flowVelocitySq > 0.001) {
                double area = 1; // CONFIG.getLeafSize();
                double dragMagnitude = dragCoefficient * area * AIR_DENSITY * flowVelocitySq / 2.0;

                // drag force points in the opposite direction of
                double invNorm = MathHelper.fastInverseSqrt(flowVelocitySq); // 1 / |flow|
                double dragX = -(flowVelocityX * invNorm) * dragMagnitude;
                double dragZ = -(flowVelocityZ * invNorm) * dragMagnitude;

//                FallingLeavesClient.LOGGER.printf(Level.DEBUG, "%.6f -> %.6f", Math.sqrt(flowVelocitySq), dragMagnitude);

                // apply drag force (air resistance)
                this.velocityX += ((dragX / mass) * DT) * DT;
                this.velocityZ += ((dragZ / mass) * DT) * DT;
            }
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
            return new FallingLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.provider);
        }
    }
}
