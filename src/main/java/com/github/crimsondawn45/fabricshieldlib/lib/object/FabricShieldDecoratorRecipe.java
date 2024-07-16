package com.github.crimsondawn45.fabricshieldlib.lib.object;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

/**
 * Handles banner crafting for modded shield's that support banners.
 */
public class FabricShieldDecoratorRecipe extends ShieldDecorationRecipe {

    public FabricShieldDecoratorRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(CraftingRecipeCategory.EQUIPMENT);
    }

    @Override
    public boolean matches(CraftingRecipeInput inventory, World world) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack itemStack2 = ItemStack.EMPTY;

        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack itemStack3 = inventory.getStackInSlot(i);
            if (!itemStack3.isEmpty()) {
                if (itemStack3.getItem() instanceof BannerItem) {
                    if (!itemStack2.isEmpty()) {
                        return false;
                    }

                    itemStack2 = itemStack3;
                } else {

                    if (!(itemStack3.getItem() instanceof FabricShield)) {
                        return false;
                    }

                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    BannerPatternsComponent bannerPatternsComponent = itemStack3.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
                    if (!bannerPatternsComponent.layers().isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack3;
                }
            }
        }

        return !itemStack.isEmpty() && !itemStack2.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack itemStack2 = ItemStack.EMPTY;

        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack itemStack3 = inventory.getStackInSlot(i);
            if (!itemStack3.isEmpty()) {
                if (itemStack3.getItem() instanceof BannerItem) {
                    itemStack = itemStack3;
                } else if (itemStack3.getItem() instanceof FabricShield theShieldItem) {
                    if (theShieldItem.supportsBanner()) {
                        itemStack2 = itemStack3.copy();
                    }
                }
            }
        }

        if (!itemStack2.isEmpty()) {
            itemStack2.set(DataComponentTypes.BANNER_PATTERNS, (BannerPatternsComponent)itemStack.get(DataComponentTypes.BANNER_PATTERNS));
            itemStack2.set(DataComponentTypes.BASE_COLOR, ((BannerItem)itemStack.getItem()).getColor());
        }
        return itemStack2;
    }

    public boolean fits(int width, int height) {
        return width * height >= 2;
    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return FabricShieldLib.FABRIC_SHIELD_DECORATION_SERIALIZER;
//    }
}