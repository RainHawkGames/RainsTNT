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

            // Expanding cloud of smoke particles to simulate the shockwave burst
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    this.getX(), this.getY() + 0.5D, this.getZ(),
                    35, 1.8D, 0.5D, 1.8D, 0.15D
            );
        }

        // Trigger the non-destructive explosion with proper shooter attribution for Sable physics
        Entity shooter = this.getOwner();
        net.minecraft.world.damagesource.DamageSource damageSource;
        if (shooter instanceof LivingEntity livingShooter) {
            damageSource = level.damageSources().explosion(this, livingShooter);
        } else {
            damageSource = level.damageSources().explosion(this, null);
        }

        level.explode(
                this,                           // direct entity
                damageSource,                   // custom damage source with shooter attribution
                null,                           // explosion damage calculator
                this.getX(), this.getY(), this.getZ(),
                4.0f,                           // explosion power / radius
                false,                          // creates fire
                Level.ExplosionInteraction.NONE // prevents breaking blocks while preserving force and ragdolls
        );

        // Remove the concussion entity from the world once triggered
        this.discard();
    }
}

