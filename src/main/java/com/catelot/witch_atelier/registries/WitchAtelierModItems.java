package com.catelot.witch_atelier.registries;

import com.catelot.witch_atelier.WitchAtelier;
import com.catelot.witch_atelier.item.CreativeInkItem;
import com.catelot.witch_atelier.item.DrawingDataItem;
import com.catelot.witch_atelier.item.MagicGuideItem;
import com.catelot.witch_atelier.item.MagicInkItem;
import com.catelot.witch_atelier.item.MagicPageItemItem;
import com.catelot.witch_atelier.item.MagicPenItem;
import com.catelot.witch_atelier.item.SilentCityDimensionItem;
import com.catelot.witch_atelier.item.SilverwoodFiberItem;
import com.catelot.witch_atelier.item.SilverwoodKnifeItem;
import com.catelot.witch_atelier.item.SilverwoodSeedItem;
import com.catelot.witch_atelier.item.SilverwoodStickItem;
import com.catelot.witch_atelier.item.SilverwoodTwigItem;
import com.catelot.witch_atelier.item.SylphShoesItem;
import com.catelot.witch_atelier.item.WoodBloodStickItem;
import com.catelot.witch_atelier.item.inventory.DrawingDataInventoryCapability;
import com.catelot.witch_atelier.item.inventory.MagicGuideInventoryCapability;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = WitchAtelier.MODID)
public final class WitchAtelierModItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(WitchAtelier.MODID);

    public static final DeferredItem<BlockItem> SILVERWOOD_LOG = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILVERWOOD_LOG);
    public static final DeferredItem<BlockItem> SILVERWOOD_LEAVES = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILVERWOOD_LEAVES);
    public static final DeferredItem<Item> SILVERWOOD_SEED = REGISTRY.register("silverwood_seed", () -> new SilverwoodSeedItem(new Item.Properties().rarity(Rarity.EPIC).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.3F).alwaysEdible().build())));
    public static final DeferredItem<BlockItem> SILVERWOOD_SAPLING = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILVERWOOD_SAPLING, new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> SILVERWOOD_BRANCH_YOUNG = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILVERWOOD_BRANCH_YOUNG);
    public static final DeferredItem<BlockItem> SILVERWOOD_BRANCH_MATURE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILVERWOOD_BRANCH_MATURE);
    public static final DeferredItem<Item> SILVERWOOD_KNIFE = REGISTRY.register("silverwood_knife", () -> new SilverwoodKnifeItem(new Item.Properties().durability(100).attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build())));
    public static final DeferredItem<Item> SILVERWOOD_FIBER = REGISTRY.register("silverwood_fiber", () -> new SilverwoodFiberItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> SILVERWOOD_TWIG = REGISTRY.register("silverwood_twig", () -> new SilverwoodTwigItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<BlockItem> EVAPORATIONFLASK = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.EVAPORATIONFLASK);
    public static final DeferredItem<Item> DRAWING_DATA = REGISTRY.register("drawing_data", () -> new DrawingDataItem(new Item.Properties().stacksTo(1).durability(12).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> MAGIC_PAGE_ITEM = REGISTRY.register("magic_page_item", () -> new MagicPageItemItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MAGIC_PEN = REGISTRY.register("magic_pen", () -> new MagicPenItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MAGIC_INK = REGISTRY.register("magic_ink", () -> new MagicInkItem(new Item.Properties().durability(1000).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> CREATIVE_INK = REGISTRY.register("creative_ink", () -> new CreativeInkItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> MAGIC_GUIDE = REGISTRY.register("magic_guide", () -> new MagicGuideItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> SILVERWOOD_PLANKS = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILVERWOOD_PLANKS);
    public static final DeferredItem<Item> SILVERWOOD_STICK = REGISTRY.register("silverwood_stick", () -> new SilverwoodStickItem(new Item.Properties()));
    public static final DeferredItem<Item> SILENT_CITY_DIMENSION = REGISTRY.register("silent_city_dimension", () -> new SilentCityDimensionItem(new Item.Properties().durability(64)));
    public static final DeferredItem<BlockItem> DEPARTURE_ALTAR = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.DEPARTURE_ALTAR);
    public static final DeferredItem<BlockItem> SILENT_CITY_PORTAL_BLOCK = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SILENT_CITY_PORTAL_BLOCK);
    public static final DeferredItem<BlockItem> SYMBOL_SQUARE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SYMBOL_SQUARE);
    public static final DeferredItem<BlockItem> SYMBOL_CIRCLE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SYMBOL_CIRCLE);
    public static final DeferredItem<BlockItem> SYMBOL_TRIANGLE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SYMBOL_TRIANGLE);
    public static final DeferredItem<BlockItem> SYMBOL_RHOMBUS = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.SYMBOL_RHOMBUS);
    public static final DeferredItem<BlockItem> STONE_FRAME = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.STONE_FRAME);
    public static final DeferredItem<BlockItem> STONE_SYMBOL_CIRCLE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.STONE_SYMBOL_CIRCLE);
    public static final DeferredItem<BlockItem> STONE_SYMBOL_SQUARE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.STONE_SYMBOL_SQUARE);
    public static final DeferredItem<BlockItem> STONE_SYMBOL_TRIANGLE = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.STONE_SYMBOL_TRIANGLE);
    public static final DeferredItem<BlockItem> STONE_SYMBOL_RHOMBUS = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.STONE_SYMBOL_RHOMBUS);
    public static final DeferredItem<BlockItem> PORTAL_CORE_NEW = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.PORTAL_CORE_NEW);
    public static final DeferredItem<BlockItem> WOOD_FRAME = REGISTRY.registerSimpleBlockItem(WitchAtelierModBlocks.WOOD_FRAME);
    public static final DeferredItem<Item> WOOD_BLOOD_STICK = REGISTRY.register("wood_blood_stick", () -> new WoodBloodStickItem(new Item.Properties().durability(64)));
    public static final DeferredItem<Item> SYLPH_SHOES_BOOTS = REGISTRY.register("sylph_shoes_boots", () -> new SylphShoesItem.Boots(new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(100)).rarity(Rarity.RARE)));

    private WitchAtelierModItems() {
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.ItemHandler.ITEM,
                (stack, context) -> new DrawingDataInventoryCapability(stack),
                DRAWING_DATA.get()
        );
        event.registerItem(
                Capabilities.ItemHandler.ITEM,
                (stack, context) -> new MagicGuideInventoryCapability(stack),
                MAGIC_GUIDE.get()
        );
    }
}
