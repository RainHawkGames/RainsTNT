package io.github.rainhawkgames.raintnt.event;

import io.github.rainhawkgames.raintnt.ModRegistry;
import io.github.rainhawkgames.raintnt.entity.ConcussionTNTEntity;
import io.github.rainhawkgames.raintnt.entity.HETNTEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.List;

@EventBusSubscriber(modid = ModRegistry.MODID)
public class ExplosionEventHandler {

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Entity source = event.getExplosion().getDirectSourceEntity();

        // 1. Handle HE TNT (High Damage)
        if (source instanceof HETNTEntity hetnt) {
            event.getAffectedEntities().clear();

            double damageRadius = 10.0;
            float maxDamage = 80.0f;

            applyCustomExplosionDamage(hetnt, damageRadius, maxDamage);
        }
        // 2. Handle Concussion TNT (Zero Damage for pure Sable physics)
        else if (source instanceof ConcussionTNTEntity concussion) {
            event.getAffectedEntities().clear();

            double damageRadius = 8.0;
            float maxDamage = 0.0f; // Guarantees zero health loss while preserving Sable triggers

            applyCustomExplosionDamage(concussion, damageRadius, maxDamage);
        }
    }

    private static void applyCustomExplosionDamage(Entity tntSource, double damageRadius, float maxDamage) {
        AABB searchBox = tntSource.getBoundingBox().inflate(damageRadius);
        List<LivingEntity> nearbyEntities = tntSource.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity.distanceToSqr(tntSource) <= damageRadius * damageRadius
        );

        Vec3 explosionPos = tntSource.position().add(0.0, 0.0625D, 0.0);

        for (LivingEntity living : nearbyEntities) {
            Vec3 entityEyesPos = living.getEyePosition();

            // Line of sight check
            var rayTrace = tntSource.level().clip(new ClipContext(
                    explosionPos,
                    entityEyesPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    tntSource
            ));

            double distance = living.distanceTo(tntSource);
            float damage = (float) (maxDamage * (1.0D - (distance / damageRadius)));

            if (rayTrace.getType() == HitResult.Type.BLOCK) {
                damage *= 0.3f;
            }

            // Always fire hurt for Concussion TNT (0.0f) to trigger Sable, or if HE TNT damage > 0.5f
            if (maxDamage == 0.0f || damage > 0.5f) {
                living.hurt(tntSource.level().damageSources().explosion(tntSource, tntSource), damage);
            }
        }
    }
}
