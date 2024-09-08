package de.psjahn.psregistry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class PSRegistry {
    protected static final Logger LOGGER = LoggerFactory.getLogger("psregistry");

    protected final String namespace;

    protected PSRegistry(String namespace) {
        this.namespace = namespace;
    }

    public static PSRegistry of(String namespace) {
        return new PSRegistry(namespace);
    }

    private static boolean isIdentifierInvalid(Identifier identifier) {
        if(identifier.equals(Identifier.ofVanilla("air"))) {
            LOGGER.error("Couldn't get an identifier whilst registering a block item!");
            return true;
        }
        return false;
    }

    //region Items

    public Item item(String name) {
        return this.item(name, s -> s);
    }

    public Item item(String name, Function<Item.Settings, Item.Settings> settings) {
        return this.item(name, new Item(settings.apply(new Item.Settings())));
    }

    public Item item(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(namespace, name), item);
    }

    public Item blockItem(Block block) {
        return this.blockItem(block, s -> s);
    }

    public Item blockItem(Block block, Function<Item.Settings, Item.Settings> settings) {
        Identifier id = Registries.BLOCK.getId(block);
        if(isIdentifierInvalid(id)) return null;
        return this.blockItem(id.getPath(), block, settings);
    }

    public Item blockItem(String name, Block block) {
        return this.blockItem(name, block, s -> s);
    }

    public Item blockItem(String name, Block block, Function<Item.Settings, Item.Settings> settings) {
        return Registry.register(Registries.ITEM, Identifier.of(namespace, name), new BlockItem(block, settings.apply(new Item.Settings())));
    }

    //endregion

    //region ItemGroups

    public ItemGroup itemGroup(String name, Text displayName, Item icon, Item... items) {
        return this.itemGroup(name, displayName, icon, entries -> Arrays.stream(items).forEach(item -> entries.add(new ItemStack(item))));
    }

    public ItemGroup itemGroup(String name, Text displayName, Item icon, Consumer<FabricItemGroupEntries> entries) {
        return this.itemGroup(name, s -> s.displayName(displayName).icon(() -> new ItemStack(icon)), entries);
    }

    public ItemGroup itemGroup(String name, Function<ItemGroup.Builder, ItemGroup.Builder> settings, Item... items) {
        return this.itemGroup(name, settings, entries -> Arrays.stream(items).forEach(item -> entries.add(new ItemStack(item))));
    }

    public ItemGroup itemGroup(String name, Function<ItemGroup.Builder, ItemGroup.Builder> settings, Consumer<FabricItemGroupEntries> entries) {
        RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(namespace, name));
        ItemGroup group = Registry.register(Registries.ITEM_GROUP, key, settings.apply(FabricItemGroup.builder()).build());
        ItemGroupEvents.modifyEntriesEvent(key).register(entries::accept);
        return group;
    }

    //endregion

    //region Blocks

    public Block block(String name) {
        return this.block(name, s -> s);
    }

    public Block block(String name, Function<AbstractBlock.Settings, AbstractBlock.Settings> settings) {
        return this.block(name, new Block(settings.apply(AbstractBlock.Settings.create())));
    }

    public Block block(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(namespace, name), block);
    }

    //endregion

    //region Block Entity Types

    public BlockEntityType<?> blockEntityType(BlockEntityType.BlockEntityFactory<?> factory, Block block) {
        Identifier id = Registries.BLOCK.getId(block);
        if(isIdentifierInvalid(id)) return null;
        return this.blockEntityType(id.getPath(), BlockEntityType.Builder.create(factory, block).build());
    }

    public BlockEntityType<?> blockEntityType(String name, BlockEntityType.BlockEntityFactory<?> factory, Block block) {
        return this.blockEntityType(name, BlockEntityType.Builder.create(factory, block).build());
    }

    public BlockEntityType<?> blockEntityType(String name, BlockEntityType<?> blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(namespace, name), blockEntityType);
    }

    //endregion

    //region Entity Types
    public EntityType<?> entityType(String name, EntityType.EntityFactory<?> factory, SpawnGroup spawnGroup) {
        return this.entityType(name, factory, spawnGroup, s -> s);
    }

    public EntityType<?> entityType(String name, EntityType.EntityFactory<?> factory, SpawnGroup spawnGroup, Function<EntityType.Builder<?>, EntityType.Builder<?>> settings) {
        return this.entityType(name, settings.apply(EntityType.Builder.create(factory, spawnGroup)));
    }

    public EntityType<?> entityType(String name, EntityType.Builder<?> builder) {
        return this.entityType(name, builder.build(name));
    }

    public EntityType<?> entityType(String name, EntityType<?> entityType) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(namespace, name), entityType);
    }

    //endregion

    //region SoundEvents

    public SoundEvent sound(String name) {
        Identifier id = Identifier.of(namespace, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    //endregion
}
