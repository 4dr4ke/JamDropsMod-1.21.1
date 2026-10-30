package net.ssum.flains.common.particle.advanced;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class ButterflyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected ButterflyParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = spriteSet;

        RandomSource random = level.getRandom();
        this.lifetime = 80 + random.nextInt(40);
        this.quadSize = 0.2f + random.nextFloat() * 0.2f;
        this.hasPhysics = true;

        this.xd = xSpeed + (random.nextDouble() * 2.0d - 1.0d) * 0.05d;
        this.yd = ySpeed + random.nextDouble() * 0.05d + 0.02d;
        this.zd = zSpeed + (random.nextDouble() * 2.0d - 1.0d) * 0.05d;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.removed) return;

        int ticksPerFrame = 2;
        int totalFrames = 6;
        int currentFrame = (this.age / ticksPerFrame) % totalFrames;

        this.setSprite(this.sprites.get(currentFrame, totalFrames - 1));
        RandomSource random = level.getRandom();

        this.xd += (random.nextFloat() - random.nextFloat()) * 0.02f * 0.9f;
        this.yd += (random.nextFloat() - 0.3f) * 0.005f;
        this.zd += (random.nextFloat() - random.nextFloat()) * 0.02f * 0.9f;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ButterflyParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
