package io.github.rainhawkgames.raintnt.dispenser;

import io.github.rainhawkgames.raintnt.entity.ConcussionTNTEntity;
import io.github.rainhawkgames.raintnt.entity.HETNTEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class DispenseHETNT extends DefaultDispenseItemBehavior {

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        Level level = source.level();
        // Calculate the position in front of the dispenser based on its facing direction
        BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));

        // Spawn primed Concussion TNT with null igniter/owner
        HETNTEntity tnt = new HETNTEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                null
        );

        level.addFreshEntity(tnt);

        // Play vanilla TNT prime sound
        level.playSound(
                null,
                tnt.getX(),
                tnt.getY(),
                tnt.getZ(),
                SoundEvents.TNT_PRIMED,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
        level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);

        stack.shrink(1);
        return stack;
    }
}