package com.github.crimsondawn45.fabricshieldlib.lib.object;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Pre-made class for quickly making custom shields
 */
public class FabricShieldItem extends Item implements FabricShield {

    private int cooldownTicks;
    private ItemStack[] repairItems;
    private int enchantability;
    private boolean supportsBanners;

    /**
     * Used to simplify the mixin on the user end to make their shield render banner
     *
     * Uses params from the mixin method, and the model and sprite identifiers made by the player
     */

    public static void renderBanner(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ShieldEntityModel model, SpriteIdentifier base, SpriteIdentifier base_nopattern){
        boolean bl = stack.getSubNbt("BlockEntityTag") != null;
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        SpriteIdentifier spriteIdentifier = bl ? base : base_nopattern;
        VertexConsumer vertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, model.getLayer(spriteIdentifier.getAtlasId()), true, stack.hasGlint()));
        model.getHandle().render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        if (bl) {
            List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.getPatternsFromNbt(FabricShieldItem.getColor(stack), BannerBlockEntity.getPatternListTag(stack));
            BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, model.getPlate(), spriteIdentifier, false, list, stack.hasGlint());
        } else {
            model.getPlate().render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrices.pop();
    }


    public String getTranslationKey(ItemStack stack) {
        if (stack.getSubNbt("BlockEntityTag") != null) {
            String var10000 = this.getTranslationKey();
            return var10000 + "." + getColor(stack).getName();
        } else {
            return super.getTranslationKey(stack);
        }
    }

    public static DyeColor getColor(ItemStack stack) {
        return DyeColor.byId(stack.getOrCreateSubNbt("BlockEntityTag").getInt("Base"));
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
    /**
     * @param settings item settings.
     * @param cooldownTicks ticks shield will be disabled for when it with axe. Vanilla: 100
     * @param enchantability enchantability of shield. Vanilla: 9
     * @param repairItem item for repairing shield.
     */
    public FabricShieldItem(Settings settings, int cooldownTicks, int enchantability, Item repairItem, boolean supportsBanners) {
        super(settings);

        //Register dispenser equip behavior
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        //Register that item has a blocking model
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			FabricModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
		         return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		    });
		}

        this.cooldownTicks = cooldownTicks;

        ItemStack[] repairItems = {new ItemStack(repairItem)};

        this.repairItems = repairItems;
		this.enchantability = enchantability;
        this.supportsBanners = supportsBanners;
    }

    /**
     * @param settings item settings.
     * @param cooldownTicks ticks shield will be disabled for when it with axe. Vanilla: 100
     * @param material tool material.
     */
    public FabricShieldItem(Settings settings, int cooldownTicks, ToolMaterial material, boolean supportsBanners) {
        super(settings.maxDamage(material.getDurability())); //Make durability match material

        //Register dispenser equip behavior
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        //Register that item has a blocking model
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			FabricModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
		         return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		    });
		}

        this.cooldownTicks = cooldownTicks;
        this.repairItems = material.getRepairIngredient().getMatchingStacks();
        this.enchantability = material.getEnchantability();
        this.supportsBanners = supportsBanners;
    }

    @Override
    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

    @Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      user.setCurrentHand(hand);
      return TypedActionResult.consume(itemStack);
	}

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        for (ItemStack itemStack : repairItems) {
            if(itemStack.getItem() == ingredient.getItem()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.hasEnchantments();
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    //Checks if the shield will support banners, only used internally
    public boolean doesSupportBanners() {
        return supportsBanners;
    }
}