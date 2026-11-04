package io.github.rainhawkgames.raintnt.event;

import io.github.rainhawkgames.raintnt.ModRegistry;
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

        if (source instanceof HETNTEntity hetnt) {
            event.getAffectedEntities().clear();

            double damageRadius = 10.0;
            float maxDamage = 80.0f;

            AABB searchBox = hetnt.getBoundingBox().inflate(damageRadius);
            List<LivingEntity> nearbyEntities = hetnt.level().getEntitiesOfClass(
                    LivingEntity.class,
                    searchBox,
                    entity -> entity.distanceToSqr(hetnt) <= damageRadius * damageRadius
            );

            Vec3 explosionPos = hetnt.position().add(0.0, 0.0625D, 0.0);

            for (LivingEntity living : nearbyEntities) {
                Vec3 entityEyesPos = living.getEyePosition();

                // Check line of sight
                var rayTrace = hetnt.level().clip(new ClipContext(
                        explosionPos,
                        entityEyesPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        hetnt
                ));

                double distance = living.distanceTo(hetnt);
                float damage = (float) (maxDamage * (1.0 - (distance / damageRadius)));

                // If a block is in the way, reduce the damage significantly (e.g., to 30% of normal)
                // instead of completely zeroing it out. You can adjust this mitigation multiplier!
                if (rayTrace.getType() == HitResult.Type.BLOCK) {
                    damage *= 0.3f;
                }

                if (damage > 0.5f) {
                    living.hurt(hetnt.level().damageSources().explosion(hetnt, hetnt), damage);
                }
            }
        }
    }
}