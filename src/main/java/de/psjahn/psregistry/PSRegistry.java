package de.psjahn.psregistry;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static boolean isPoiKeyInvalid(Optional<RegistryKey<PointOfInterestType>> optionalKey) {
        if(optionalKey.isEmpty()) {
            LOGGER.error("Couldn't get a registry key whilst registering a villager profession!");
            return true;
        }
        return false;
    }

    //region Items

    public Item item(String name) {
        return this.item(name, s -> s, Item::new);
    }

    public Item item(String name, Function<Item.Settings, Item.Settings> settings) {
        return this.item(name, settings, Item::new);
    }

    public <T extends Item> T item(String name, Function<Item.Settings, Item.Settings> settings, Function<Item.Settings, T> constructor) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(namespace, name));
        return this.item(key, constructor.apply(settings.apply(new Item.Settings()).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(namespace, name)))));
    }

    public <T extends Item> T item(RegistryKey<Item> key, T item) {
        return Registry.register(Registries.ITEM, key, item);
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
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(namespace, name));
        return Registry.register(Registries.ITEM, key, new BlockItem(block, settings.apply(new Item.Settings()).useBlockPrefixedTranslationKey().registryKey(key)));
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
        return this.block(name, s -> s, Block::new);
    }

    public Block block(String name, Function<AbstractBlock.Settings, AbstractBlock.Settings> settings) {
        return this.block(name, settings, Block::new);
    }

    public <T extends Block> T block(String name, Function<AbstractBlock.Settings, AbstractBlock.Settings> settings, Function<AbstractBlock.Settings, T> constructor) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(namespace, name));
        return this.block(key, constructor.apply(settings.apply(AbstractBlock.Settings.create()).registryKey(key)));
    }

    public <T extends Block> T block(RegistryKey<Block> key, T block) {
        return Registry.register(Registries.BLOCK, key, block);
    }

    //endregion

    //region TODO Block Entity Types

    /*public BlockEntityType<?> blockEntityType(BlockEntityType.BlockEntityFactory<?> factory, Block block) {
        Identifier id = Registries.BLOCK.getId(block);
        if(isIdentifierInvalid(id)) return null;
        return this.blockEntityType(id.getPath(), BlockEntityType.Builder.create(factory, block).build());
    }

    public BlockEntityType<?> blockEntityType(String name, BlockEntityType.BlockEntityFactory<?> factory, Block block) {
        return this.blockEntityType(name, BlockEntityType.Builder.create(factory, block).build());
    }

    public BlockEntityType<?> blockEntityType(String name, BlockEntityType<?> blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(namespace, name), blockEntityType);
    }*/

    //endregion

    //region
    public EntityType<?> entityType(String name, EntityType.EntityFactory<?> factory, SpawnGroup spawnGroup) {
        return this.entityType(name, factory, spawnGroup, s -> s);
    }

    public EntityType<?> entityType(String name, EntityType.EntityFactory<?> factory, SpawnGroup spawnGroup, Function<EntityType.Builder<?>, EntityType.Builder<?>> settings) {
        return this.entityType(name, settings.apply(EntityType.Builder.create(factory, spawnGroup)));
    }

    public EntityType<?> entityType(String name, EntityType.Builder<?> builder) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(namespace, name));
        return this.entityType(key, builder.build(key));
    }

    public EntityType<?> entityType(RegistryKey<EntityType<?>> key, EntityType<?> entityType) {
        return Registry.register(Registries.ENTITY_TYPE, key, entityType);
    }

    //endregion

    //region SoundEvents

    public SoundEvent sound(String name) {
        Identifier id = Identifier.of(namespace, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    //endregion

    //region Point of Interests

    public PointOfInterestType pointOfInterest(String name, Block... blocks) {
        return this.pointOfInterest(name, 1, 1, blocks);
    }

    public PointOfInterestType pointOfInterest(String name, int ticketCount, int searchDistance, Block... blocks) {
        return PointOfInterestHelper.register(Identifier.of(namespace, name), ticketCount, searchDistance, blocks);
    }

    //endregion

    //region Villager Professions

    public VillagerProfession villagerProfession(String name, PointOfInterestType workstation, @Nullable SoundEvent sounds) {
        return this.villagerProfession(name, workstation, ImmutableSet.of(), ImmutableSet.of(), sounds);
    }

    public VillagerProfession villagerProfession(String name, PointOfInterestType workstation, ImmutableSet<Block> secondaryJobSites, ImmutableSet<Item> gatherableItems, @Nullable SoundEvent sounds) {
        Optional<RegistryKey<PointOfInterestType>> key = Registries.POINT_OF_INTEREST_TYPE.getKey(workstation);
        return isPoiKeyInvalid(key) ? null : this.villagerProfession(name, key.orElse(null), secondaryJobSites, gatherableItems, sounds);
    }

    public VillagerProfession villagerProfession(String name, RegistryKey<PointOfInterestType> workstation, ImmutableSet<Block> secondaryJobSites, ImmutableSet<Item> gatherableItems, @Nullable SoundEvent sounds) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(namespace, name), new VillagerProfession(name, e -> e.matchesKey(workstation), e -> e.matchesKey(workstation), gatherableItems, secondaryJobSites, sounds));
    }

    public VillagerProfession villagerProfession(String name, Predicate<RegistryEntry<PointOfInterestType>> workstation, ImmutableSet<Block> secondaryJobSites, ImmutableSet<Item> gatherableItems, @Nullable SoundEvent sounds) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(namespace, name), new VillagerProfession(name, workstation, workstation, gatherableItems, secondaryJobSites, sounds));
    }

    //endregion

    //region Screen Handler Types

    public <T extends ScreenHandler> ScreenHandlerType<T> screenHandlerType(String name, ScreenHandlerType.Factory<T> screenHandlerFactory) {
        return this.screenHandlerType(name, screenHandlerFactory, FeatureSet.empty());
    }

    public <T extends ScreenHandler> ScreenHandlerType<T> screenHandlerType(String name, ScreenHandlerType.Factory<T> screenHandlerFactory, FeatureSet requiredFeatures) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(namespace, name), new ScreenHandlerType<>(screenHandlerFactory, requiredFeatures));
    }

    //endregion
}
