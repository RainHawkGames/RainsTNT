package io.github.rainhawkgames.raintnt.block;

import io.github.rainhawkgames.raintnt.entity.ConcussionTNTEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ConcussionTNTBlock extends TntBlock {

    public ConcussionTNTBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable net.minecraft.core.Direction face, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            ConcussionTNTEntity entity = new ConcussionTNTEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, igniter);
            level.addFreshEntity(entity);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, java.util.function.BiConsumer<net.minecraft.world.item.ItemStack, BlockPos> dropConsumer) {
        if (!level.isClientSide) {
            ConcussionTNTEntity entity = new ConcussionTNTEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, explosion.getIndirectSourceEntity());
            int currentFuse = entity.getFuse();
            entity.setFuse((short)(level.random.nextInt(currentFuse / 4) + currentFuse / 8));
            level.addFreshEntity(entity);
            level.removeBlock(pos, false);
        }
    }
}