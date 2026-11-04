package io.github.rainhawkgames.raintnt.entity;

import io.github.rainhawkgames.raintnt.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
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

        Vec3 explosionPos = this.position();
        double radius = 6.0D; // Adjust concussion blast radius here

        // Play an explosion/boom sound without breaking blocks
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
            // Big vanilla explosion flash/emitter effect
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY(), this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0
            );

            // Expanding cloud of poof/smoke particles to simulate the shockwave burst
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    this.getX(), this.getY() + 0.5D, this.getZ(),
                    35, 1.8D, 0.5D, 1.8D, 0.15D
            );
        }
        // AABB search to push entities away with zero block damage or health damage
        AABB searchBox = new AABB(
                explosionPos.x - radius, explosionPos.y - radius, explosionPos.z - radius,
                explosionPos.x + radius, explosionPos.y + radius, explosionPos.z + radius
        );

        for (Entity entity : level.getEntitiesOfClass(Entity.class, searchBox)) {
            // Don't push self
            if (entity == this) continue;

            double distanceSqr = entity.distanceToSqr(explosionPos);

            if (distanceSqr < radius * radius && distanceSqr > 0.0001D) {
                double distance = Math.sqrt(distanceSqr);
                double distanceFactor = 1.0D - (distance / radius);

                Vec3 knockbackDir = entity.position().subtract(explosionPos).normalize();

                // Apply velocity push to any entity caught in the blast
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * distanceFactor * 2.5D,
                        Math.abs(knockbackDir.y) * distanceFactor * 1.2D + 0.3D,
                        knockbackDir.z * distanceFactor * 2.5D
                ));
                entity.hurtMarked = true; // Sync velocity packet to client
            }
        }

        // Remove the concussion TNT entity from the world since it doesn't call level.explode()
        this.discard();
    }
}
