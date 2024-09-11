## PSRegistry
PSRegistry is a library which simplifies the process of registering Items, Blocks, and more.

<br>

---

<br>

### With PSRegistry
```java
public class ModRegistry {
  private static final PSRegistry reg = PSRegistry.of(MyMod.MOD_ID);

  //Nothing but the identifier is required for a simple item
  public static final Item NEW_ITEM = reg.item("new_item");

  //Settings can be applied very easily too
  public static final Item ITEM_WITH_SETTINGS = reg.item("item_with_settings", s -> s.maxCount(1).fireproof());

  public static final Block NEW_BLOCK = reg.block("new_block");
  public static final Block BLOCK_WITH_SETTINGS = reg.block("block_with_settings", s -> s.dropsNothing());

  //Some types don't even need any identifiers
  public static final Item BLOCK_ITEM = reg.blockItem(NEW_BLOCK);

  //Especially Item Groups are made a lot simpler with this library (See the vanilla implementation below).
  public static final ItemGroup MOD_GROUP = reg.itemGroup("mod_group", Text.literal("My new group!"), NEW_ITEM, ITEM_WITH_SETTINGS, BLOCK_ITEM);

  public static void initialize() { }
}
```

### Without PSRegistry
```java
public class ModRegistry {
  public static Item SIMPLE_ITEM = Registry.register(Registries.ITEM, Identifier.of(MyMod.MOD_ID, "simple_item"), new Item(new Item.Settings()));

  public static Item ITEM_WITH_SETTINGS = Registry.register(Registries.ITEM, Identifier.of(MyMod.MOD_ID, "item_with_settings"), new Item(new Item.Settings().maxCount(1).fireproof()));

  public static Block NEW_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(MyMod.MOD_ID, "new_block"), new Block(AbstractBlock.Settings.create()));
  public static Block BLOCK_WITH_SETTINGS = Registry.register(Registries.BLOCK, Identifier.of(MyMod.MOD_ID, "block_with_settings"), new Block(AbstractBlock.Settings.create().dropsNothing()));

  public static Item BLOCK_ITEM = Registry.register(Registries.ITEM, Identifier.of(MyMod.MOD_ID, "new_block"), new BlockItem(NEW_BLOCK, new Item.Settings()));

  private static final RegistryKey<ItemGroup> MOD_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MyMod.MOD_ID, "mod_group"));
  public static ItemGroup MOD_GROUP = FabricItemGroup.builder().displayName(Text.literal("This is so tedious!")).icon(() -> new ItemStack(SIMPLE_ITEM)).build();

  public static void initialize() {
    ItemGroupEvents.modifyEntriesEvent(MOD_GROUP_KEY).register(itemGroup -> {
      itemGroup.add(new ItemStack(ITEM_WITH_SETTINGS));
      itemGroup.add(new ItemStack(BLOCK_ITEM));
    });
  }
}
```
That's more than twice as many characters! (214% more, excluding comments)

<br>

---

<br>

## A note on registration without identifiers
For methods, which don't require an object instead of an identifier, the object must already be registered.
Since java initializes static fields in a class from top to bottom, the example above works without any issues.

### ✅
```java
public static final Block NEW_BLOCK = reg.block("new_block");
public static final Item BLOCK_ITEM = reg.blockItem(NEW_BLOCK);
```

### ❌
```java
public static final Item BLOCK_ITEM = reg.blockItem(NEW_BLOCK);
public static final Block NEW_BLOCK = reg.block("new_block");
```

<br>

---

<br>

## Installation
This library can be installed using [jitpack](https://jitpack.io/)

Add the following repository:
```gradle
repositories {
  maven {
    name = 'JitPack'
    url = 'https://jitpack.io'
  }
}
```

And add the dependency like this:
```gradle
dependencies {
  implementation "com.github.PSJahn:PSRegistry:1.1.0"
}
```
