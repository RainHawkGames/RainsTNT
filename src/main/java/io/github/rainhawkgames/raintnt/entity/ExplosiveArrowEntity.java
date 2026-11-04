package io.github.rainhawkgames.raintnt.entity;

import io.github.rainhawkgames.raintnt.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ExplosiveArrowEntity extends Arrow {

    public ExplosiveArrowEntity(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
    }

    public ExplosiveArrowEntity(EntityType<? extends ExplosiveArrowEntity> entityType, Level level, LivingEntity shooter, ItemStack stack, ItemStack weaponStack) {
        super(entityType, level);
        this.setPos(shooter.getX(), shooter.getY() + (double) shooter.getEyeHeight() - 0.1, shooter.getZ());
        this.setOwner(shooter);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        Level level = this.level();
        if (!level.isClientSide()) {
            // Spawn HE TNT with a 0 fuse for instant explosion
            PrimedTnt tnt = EntityType.TNT.create(level);
            tnt.setPos(this.getX(), this.getY(), this.getZ());
            tnt.setFuse(0);
            level.addFreshEntity(tnt);

            // Remove the arrow on impact
            this.discard();
        }
    }
}