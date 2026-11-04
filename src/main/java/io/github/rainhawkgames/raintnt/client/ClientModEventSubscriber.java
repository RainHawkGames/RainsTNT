package io.github.rainhawkgames.raintnt.client;

import io.github.rainhawkgames.raintnt.ModRegistry;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "raintnt", value = Dist.CLIENT)
public class ClientModEventSubscriber {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.HE_TNT_ENTITY.get(), TntRenderer::new);
        event.registerEntityRenderer(ModRegistry.CONCUSSION_TNT_ENTITY.get(), TntRenderer::new);
        event.registerEntityRenderer(ModRegistry.EXPLOSIVE_ARROW_ENTITY.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModRegistry.HIGH_EXPLOSIVE_ARROW_ENTITY.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModRegistry.CONCUSSION_ARROW_ENTITY.get(), TippableArrowRenderer::new);
    }
}