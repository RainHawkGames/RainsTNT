package io.github.rainhawkgames.raintnt;

import io.github.rainhawkgames.raintnt.block.ConcussionTNTBlock;
import io.github.rainhawkgames.raintnt.block.HETNTBlock;
import io.github.rainhawkgames.raintnt.entity.*;
import io.github.rainhawkgames.raintnt.item.ConcussionArrowItem;
import io.github.rainhawkgames.raintnt.item.ExplosiveArrowItem;
import io.github.rainhawkgames.raintnt.item.HighExplosiveArrowItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistry {
    public static final String MODID = "raintnt";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    // Register HE TNT Block
    public static final DeferredBlock<Block> HE_TNT_BLOCK = BLOCKS.register("he_tnt",
            () -> new HETNTBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));
    public static final DeferredBlock<Block> CONCUSSION_TNT_BLOCK = BLOCKS.register("concussion_tnt",
            () -> new ConcussionTNTBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)));


    // Register HE TNT Item (so you can hold/place it)
    public static final DeferredItem<Item> HE_TNT_ITEM = ITEMS.register("he_tnt",
            () -> new BlockItem(HE_TNT_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> CONCUSSION_TNT_ITEM = ITEMS.register("concussion_tnt",
            () -> new BlockItem(CONCUSSION_TNT_BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> HIGH_EXPLOSIVE_ARROW_ITEM =
            ITEMS.register("high_explosive_arrow", () ->
                    new HighExplosiveArrowItem(new Item.Properties())
            );
    public static final DeferredHolder<Item, Item> CONCUSSION_ARROW_ITEM =
            ITEMS.register("concussion_arrow", () ->
                    new ConcussionArrowItem(new Item.Properties())
            );
    public static final DeferredHolder<Item, Item> EXPLOSIVE_ARROW_ITEM =
            ITEMS.register("explosive_arrow", () ->
                    new ExplosiveArrowItem(new Item.Properties())
            );

    // Register HE TNT Entity
    public static final DeferredHolder<EntityType<?>, EntityType<HETNTEntity>> HE_TNT_ENTITY = ENTITIES.register("he_tnt",
            () -> EntityType.Builder.<HETNTEntity>of(HETNTEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.98F, 0.98F)
                    .build("he_tnt"));
    public static final DeferredHolder<EntityType<?>, EntityType<ConcussionTNTEntity>> CONCUSSION_TNT_ENTITY = ENTITIES.register("concussion_tnt",
            () -> EntityType.Builder.<ConcussionTNTEntity>of(ConcussionTNTEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.98F, 0.98F)
                    .build("concussion_tnt"));
    public static final DeferredHolder<EntityType<?>, EntityType<HighExplosiveArrowEntity>> HIGH_EXPLOSIVE_ARROW_ENTITY =
            ENTITIES.register("high_explosive_arrow", () ->
                    EntityType.Builder.<HighExplosiveArrowEntity>of(HighExplosiveArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("high_explosive_arrow")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<ConcussionArrowEntity>> CONCUSSION_ARROW_ENTITY =
            ENTITIES.register("concussion_arrow", () ->
                    EntityType.Builder.<ConcussionArrowEntity>of(ConcussionArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("concussion_arrow")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveArrowEntity>> EXPLOSIVE_ARROW_ENTITY =
            ENTITIES.register("explosive_arrow", () ->
                    EntityType.Builder.<ExplosiveArrowEntity>of(ExplosiveArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("explosive_arrow")
            );



    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        ENTITIES.register(eventBus);
    }
}