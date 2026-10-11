package com.jousafio.item;

import com.jousafio.JousafioMod;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JousafioMod.MOD_ID);

    public static final ItemGroup JOUSAFIO_TAB = new ItemGroup("jousafio") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CUCHARA.get());
        }
    };

    // Special items
    public static final RegistryObject<Item> CUCHARA = ITEMS.register("cuchara_resurreccion",
        () -> new Item(new Item.Properties().group(JOUSAFIO_TAB).maxStackSize(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> TOTEM_SAFIO = ITEMS.register("totem_safio",
        () -> new Item(new Item.Properties().group(JOUSAFIO_TAB).maxStackSize(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> ALMA = ITEMS.register("alma",
        () -> new Item(new Item.Properties().group(JOUSAFIO_TAB).maxStackSize(16).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RULETA_ITEM = ITEMS.register("ruleta_item",
        () -> new Item(new Item.Properties().group(JOUSAFIO_TAB).maxStackSize(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FRAGMENTO_SAFIO = ITEMS.register("fragmento_safio",
        () -> new Item(new Item.Properties().group(JOUSAFIO_TAB).rarity(Rarity.RARE)));

    // Armor material
    public static final IArmorMaterial SAFIO_ARMOR = new IArmorMaterial() {
        @Override public int getDurability(EquipmentSlotType slot) { return new int[]{13,15,16,11}[slot.getIndex()] * 40; }
        @Override public int getDamageReductionAmount(EquipmentSlotType slot) { return new int[]{4,7,9,4}[slot.getIndex()]; }
        @Override public int getEnchantability() { return 20; }
        @Override public net.minecraft.util.SoundEvent getSoundEvent() { return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE; }
        @Override public Ingredient getRepairMaterial() { return Ingredient.fromItems(FRAGMENTO_SAFIO.get()); }
        @Override public String getName() { return "jousafio:safio"; }
        @Override public float getToughness() { return 3.5f; }
        @Override public float getKnockbackResistance() { return 0.15f; }
    };

    public static final RegistryObject<Item> SAFIO_HELMET = ITEMS.register("safio_helmet",
        () -> new ArmorItem(SAFIO_ARMOR, EquipmentSlotType.HEAD, new Item.Properties().group(JOUSAFIO_TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SAFIO_CHESTPLATE = ITEMS.register("safio_chestplate",
        () -> new ArmorItem(SAFIO_ARMOR, EquipmentSlotType.CHEST, new Item.Properties().group(JOUSAFIO_TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SAFIO_LEGGINGS = ITEMS.register("safio_leggings",
        () -> new ArmorItem(SAFIO_ARMOR, EquipmentSlotType.LEGS, new Item.Properties().group(JOUSAFIO_TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SAFIO_BOOTS = ITEMS.register("safio_boots",
        () -> new ArmorItem(SAFIO_ARMOR, EquipmentSlotType.FEET, new Item.Properties().group(JOUSAFIO_TAB).rarity(Rarity.EPIC)));

    // Weapons
    public static final RegistryObject<Item> ESPADA_SAFIO = ITEMS.register("espada_safio",
        () -> new SwordItem(ItemTier.NETHERITE, 5, -2.2f, new Item.Properties().group(JOUSAFIO_TAB).rarity(Rarity.EPIC)));
}
