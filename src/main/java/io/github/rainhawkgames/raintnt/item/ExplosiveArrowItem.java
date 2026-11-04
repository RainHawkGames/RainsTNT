package io.github.rainhawkgames.raintnt.item;

import io.github.rainhawkgames.raintnt.ModRegistry;
import io.github.rainhawkgames.raintnt.entity.ExplosiveArrowEntity;
import io.github.rainhawkgames.raintnt.entity.HighExplosiveArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ExplosiveArrowItem extends ArrowItem {

    public ExplosiveArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weaponStack) {
        return new ExplosiveArrowEntity(
                ModRegistry.EXPLOSIVE_ARROW_ENTITY.get(),
                level,
                shooter,
                stack,
                weaponStack != null ? weaponStack : ItemStack.EMPTY
        );
    }
}
