package io.github.rainhawkgames.raintnt.entity;

import io.github.rainhawkgames.raintnt.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ConcussionArrowEntity extends Arrow {

    public ConcussionArrowEntity(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
    }

    public ConcussionArrowEntity(EntityType<? extends ConcussionArrowEntity> entityType, Level level, LivingEntity shooter, ItemStack stack, ItemStack weaponStack) {
        super(entityType, level);
        this.setPos(shooter.getX(), shooter.getY() + (double) shooter.getEyeHeight() - 0.1, shooter.getZ());
        this.setOwner(shooter);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        Level level = this.level();
        if (!level.isClientSide()) {
            // Spawn Concussion TNT with a 0 fuse for instant shockwave
            ConcussionTNTEntity tnt = new ConcussionTNTEntity(ModRegistry.CONCUSSION_TNT_ENTITY.get(), level);
            tnt.setPos(this.getX(), this.getY(), this.getZ());
            tnt.setFuse(0);
            level.addFreshEntity(tnt);

            // Remove the arrow on impact
            this.discard();
        }
    }
}