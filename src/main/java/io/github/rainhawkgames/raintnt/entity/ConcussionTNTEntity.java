package io.github.rainhawkgames.raintnt.entity;

import io.github.rainhawkgames.raintnt.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConcussionTNTEntity extends PrimedTnt {

    public ConcussionTNTEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
    }

    public ConcussionTNTEntity(Level level, double x, double y, double z, LivingEntity igniter) {
        this(ModRegistry.CONCUSSION_TNT_ENTITY.get(), level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * 6.2831854820251465D;
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, 0.20000000298023224D, -Math.cos(d0) * 0.02D);
        this.setFuse(40);
        this.setBlockState(ModRegistry.CONCUSSION_TNT_BLOCK.get().defaultBlockState());
    }

    @Override
    protected void explode() {
        Level level = this.level();
        if (level.isClientSide()) return;

        float explosionPower = 4.0f;

        // 1. Play sound and spawn particles
        level.playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                net.minecraft.sounds.SoundEvent.createVariableRangeEvent(
                        net.minecraft.resources.ResourceLocation.withDefaultNamespace("entity.generic.explode")
                ),
                SoundSource.BLOCKS,
                3.0F,
                0.6F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0
            );

            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    this.getX(), this.getY() + 0.5D, this.getZ(),
                    35, 1.8D, 0.5D, 1.8D, 0.15D
            );
        }

        // 2. Trigger native explosion event so Sable detects it
        level.explode(
                this,
                this.getX(),
                this.getY(0.0625D),
                this.getZ(),
                explosionPower,
                false,
                Level.ExplosionInteraction.NONE
        );

        // 3. Custom physical velocity push for entities and items
        Vec3 explosionPos = this.position();
        double radius = explosionPower * 2.5D;
        AABB searchBox = new AABB(
                explosionPos.x - radius, explosionPos.y - radius, explosionPos.z - radius,
                explosionPos.x + radius, explosionPos.y + radius, explosionPos.z + radius
        );

        for (Entity entity : level.getEntitiesOfClass(Entity.class, searchBox)) {
            if (entity == this) continue;

            double distanceSqr = entity.distanceToSqr(explosionPos);

            if (distanceSqr < radius * radius && distanceSqr > 0.0001D) {
                double distance = Math.sqrt(distanceSqr);
                double distanceFactor = 1.0D - (distance / radius);

                Vec3 knockbackDir = entity.position().subtract(explosionPos).normalize();

                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * distanceFactor * 3.0D,
                        Math.abs(knockbackDir.y) * distanceFactor * 1.5D + 0.4D,
                        knockbackDir.z * distanceFactor * 3.0D
                ));
                entity.hurtMarked = true;
            }
        }

        this.discard();
    }
}
