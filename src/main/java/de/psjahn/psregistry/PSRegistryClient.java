package de.psjahn.psregistry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class PSRegistryClient {
	protected static final Logger LOGGER = LoggerFactory.getLogger("psregistry");

	protected final String namespace;

    protected PSRegistryClient(String namespace) {
        this.namespace = namespace;
    }

	public static PSRegistryClient of(String namespace) {
		return new PSRegistryClient(namespace);
	}

	//region Entity Renderers

	public <E extends Entity> void entityRenderer(EntityType<E> entityType, EntityRendererFactory<E> entityRendererFactory) {
		EntityRendererRegistry.register(entityType, entityRendererFactory);
	}

	//endregion

	//region Block Entity Rendereres

	public <E extends BlockEntity> void blockEntityRenderer(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<E> blockEntityRendererFactory) {
		BlockEntityRendererFactories.register(blockEntityType, blockEntityRendererFactory);
	}

	//endregion
}