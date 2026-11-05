package io.github.rainhawkgames.raintnt.dispenser;

import io.github.rainhawkgames.raintnt.entity.ExplosiveArrowEntity;
import io.github.rainhawkgames.raintnt.entity.HighExplosiveArrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DispenseHighExplosiveArrow extends DefaultDispenseItemBehavior {

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        Level level = source.level();
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        BlockPos pos = source.pos().relative(direction);

        // 1. Instantiate your custom arrow entity at the nozzle position
        HighExplosiveArrowEntity arrow = new HighExplosiveArrowEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                stack.copyWithCount(1),
                ItemStack.EMPTY
        );

        // 2. Aim and shoot in the dispenser's facing direction
        arrow.shoot(
                direction.getStepX(),
                direction.getStepY() + 0.1D, // slight elevation lift
                direction.getStepZ(),
                1.1F, // velocity (matches standard arrow dispenser speed)
                6.0F  // inaccuracy/spread
        );

        // 3. Add to the world
        level.addFreshEntity(arrow);

        stack.shrink(1);
        return stack;
    }
}