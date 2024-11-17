## PSRegistry
PSRegistry is a library which simplifies the process of registering Items, Blocks, and more.

<br>

---

<br>

### Example usage
```java
public class ModRegistry {
  private static final PSRegistry reg = PSRegistry.of(MyMod.MOD_ID);

  //Nothing but the identifier is required for a simple item
  public static final Item NEW_ITEM = reg.item("new_item");

  //Settings can be applied very easily too
  public static final Item ITEM_WITH_SETTINGS = reg.item("item_with_settings", s -> s.maxCount(1).fireproof());

  public static final Block NEW_BLOCK = reg.block("new_block");
  public static final Block BLOCK_WITH_SETTINGS = reg.block("block_with_settings", s -> s.dropsNothing());

  //Custom Block class example
  public static final CustomBlock CUSTOM_BLOCK = reg.block("custom_block", s->s, CustomBlock::new);

  //Custom Item class example
  public static final CustomItem CUSTOM_ITEM = reg.block("custom_item", s->s, CustomItem::new);

  //Some types don't even need any identifiers
  public static final Item BLOCK_ITEM = reg.blockItem(NEW_BLOCK);

  //Especially Item Groups are made a lot simpler with this library
  public static final ItemGroup MOD_GROUP = reg.itemGroup("mod_group", Text.literal("My new group!"), NEW_ITEM, ITEM_WITH_SETTINGS, BLOCK_ITEM, CUSTOM_ITEM);

  public static void initialize() { }
}
```

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
  modImplementation "com.github.PSJahn:PSRegistry:1.3.0"
  include "com.github.PSJahn:PSRegistry:1.3.0"
}
```
