package com.rekindled.embers.datagen;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.StoneDecoBlocks;
import com.rekindled.embers.RegistryManager.ToolSet;
import com.rekindled.embers.recipe.AlchemyRecipeBuilder;
import com.rekindled.embers.recipe.BoilingRecipeBuilder;
import com.rekindled.embers.recipe.BoringRecipeBuilder;
import com.rekindled.embers.recipe.EmberActivationRecipeBuilder;
import com.rekindled.embers.recipe.GaseousFuelRecipeBuilder;
import com.rekindled.embers.recipe.MeltingRecipeBuilder;
import com.rekindled.embers.recipe.MetalCoefficientRecipeBuilder;
import com.rekindled.embers.recipe.MixingRecipeBuilder;
import com.rekindled.embers.recipe.StampingRecipeBuilder;
import com.rekindled.embers.util.ConsumerWrapperBuilder;
import com.rekindled.embers.util.FluidAmounts;
import com.rekindled.embers.util.MeltingBonus;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

public class EmbersRecipes extends RecipeProvider implements IConditionBuilder {

	public static String boringFolder = "boring";
	public static String activationFolder = "ember_activation";
	public static String meltingFolder = "melting";
	public static String stampingFolder = "stamping";
	public static String mixingFolder = "mixing";
	public static String coefficientFolder = "metal_coefficient";
	public static String alchemyFolder = "alchemy";
	public static String boilingFolder = "boiling";

	public EmbersRecipes(PackOutput gen) {
		super(gen);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> consumer) {
		//boring
		ResourceLocation overworld = new ResourceLocation("overworld");
		BoringRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(boringFolder).dimension(overworld).require(EmbersBlockTags.WORLD_BOTTOM, 3).weight(20).maxHeight(-57).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(boringFolder).dimension(overworld).require(EmbersBlockTags.WORLD_BOTTOM, 3).weight(60).maxHeight(-57).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(boringFolder).dimension(overworld).require(EmbersBlockTags.WORLD_BOTTOM, 3).weight(20).maxHeight(-57).save(consumer);
		ResourceLocation nether = new ResourceLocation("the_nether");
		BoringRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(boringFolder + "/nether").dimension(nether).require(EmbersBlockTags.WORLD_BOTTOM, 3).weight(20).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(boringFolder + "/nether").dimension(nether).require(EmbersBlockTags.WORLD_BOTTOM, 3).weight(60).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(boringFolder + "/nether").dimension(nether).require(EmbersBlockTags.WORLD_BOTTOM, 3).weight(20).maxHeight(7).save(consumer);

		//activation
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(activationFolder).ember(2400).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(activationFolder).ember(400).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(activationFolder).ember(0).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL_CLUSTER.get()).folder(activationFolder).ember(4400).save(consumer);

		//metals
		fullOreRecipes("lead", ImmutableList.of(RegistryManager.LEAD_ORE_ITEM.get(), RegistryManager.DEEPSLATE_LEAD_ORE_ITEM.get(), RegistryManager.RAW_LEAD.get()), RegistryManager.MOLTEN_LEAD.FLUID.get(), RegistryManager.RAW_LEAD.get(), RegistryManager.RAW_LEAD_BLOCK_ITEM.get(), RegistryManager.LEAD_BLOCK_ITEM.get(), RegistryManager.LEAD_INGOT.get(), RegistryManager.LEAD_NUGGET.get(), RegistryManager.LEAD_PLATE.get(), consumer, MeltingBonus.SILVER);

		fullOreRecipes("silver", ImmutableList.of(RegistryManager.SILVER_ORE_ITEM.get(), RegistryManager.DEEPSLATE_SILVER_ORE_ITEM.get(), RegistryManager.RAW_SILVER.get()), RegistryManager.MOLTEN_SILVER.FLUID.get(), RegistryManager.RAW_SILVER.get(), RegistryManager.RAW_SILVER_BLOCK_ITEM.get(), RegistryManager.SILVER_BLOCK_ITEM.get(), RegistryManager.SILVER_INGOT.get(), RegistryManager.SILVER_NUGGET.get(), RegistryManager.SILVER_PLATE.get(), consumer, MeltingBonus.LEAD);

		fullMetalRecipes("dawnstone", RegistryManager.MOLTEN_DAWNSTONE.FLUID.get(), RegistryManager.DAWNSTONE_BLOCK_ITEM.get(), RegistryManager.DAWNSTONE_INGOT.get(), RegistryManager.DAWNSTONE_NUGGET.get(), RegistryManager.DAWNSTONE_PLATE.get(), consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.COPPER_INGOT)
		.pattern("XXX")
		.pattern("XXX")
		.pattern("XXX")
		.define('X', itemTag("forge", "nuggets/copper"))
		.unlockedBy("has_nugget", has(itemTag("forge", "nuggets/copper")))
		.save(consumer, getResource("copper_nugget_to_ingot"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RegistryManager.COPPER_NUGGET.get(), 9)
		.requires(itemTag("forge", "ingots/copper"))
		.group("")
		.unlockedBy("has_ingot", has(itemTag("forge", "ingots/copper")))
		.save(consumer, getResource("copper_ingot_to_nugget"));

		plateHammerRecipe("iron", RegistryManager.IRON_PLATE.get(), consumer);
		//plateHammerRecipe("gold", RegistryManager.GOLD_PLATE.get(), consumer);
		plateHammerRecipe("copper", RegistryManager.COPPER_PLATE.get(), consumer);

		//melting and stamping
		fullOreMeltingStampingRecipes("iron", RegistryManager.MOLTEN_IRON.FLUID.get(), consumer, MeltingBonus.NICKEL, MeltingBonus.ALUMINUM, MeltingBonus.COPPER);
		fullOreMeltingStampingRecipes("gold", RegistryManager.MOLTEN_GOLD.FLUID.get(), consumer, MeltingBonus.SILVER);
		fullOreMeltingStampingRecipes("copper", RegistryManager.MOLTEN_COPPER.FLUID.get(), consumer, MeltingBonus.GOLD);
		fullOreMeltingStampingRecipes("nickel", RegistryManager.MOLTEN_NICKEL.FLUID.get(), consumer, MeltingBonus.IRON);
		fullOreMeltingStampingRecipes("tin", RegistryManager.MOLTEN_TIN.FLUID.get(), consumer, MeltingBonus.LEAD);
		fullOreMeltingStampingRecipes("aluminum", RegistryManager.MOLTEN_ALUMINUM.FLUID.get(), consumer, MeltingBonus.IRON);
		fullMeltingStampingRecipes("bronze", RegistryManager.MOLTEN_BRONZE.FLUID.get(), consumer);
		fullMeltingStampingRecipes("electrum", RegistryManager.MOLTEN_ELECTRUM.FLUID.get(), consumer);
		MeltingRecipeBuilder.create(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL)).id(new ResourceLocation(Embers.MODID, meltingFolder + "/soul_crude")).output(RegistryManager.SOUL_CRUDE.FLUID.get(), 100).save(consumer);

		//stamper crushing
		StampingRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.FLAT_STAMP.get()).input(RegistryManager.EMBER_SHARD.get()).save(ConsumerWrapperBuilder.wrap().build(consumer)); //today is the day
		StampingRecipeBuilder.create(new ItemStack(RegistryManager.EMBER_SHARD.get(), 6)).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.FLAT_STAMP.get()).input(RegistryManager.EMBER_CRYSTAL.get()).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(new ItemStack(RegistryManager.ASH.get(), 8)).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.FLAT_STAMP.get()).input(RegistryManager.ALCHEMICAL_WASTE.get()).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(new ItemStack(Items.BLAZE_POWDER, 4)).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.FLAT_STAMP.get()).input(Tags.Items.RODS_BLAZE).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(Items.SAND).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.FLAT_STAMP.get()).input(Items.GRAVEL).save(ConsumerWrapperBuilder.wrap().build(consumer));

		//aspectus recipes
		StampingRecipeBuilder.create(RegistryManager.IRON_ASPECTUS.get()).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).input(RegistryManager.EMBER_SHARD.get()).fluid(fluidTag("forge", "molten_iron"), FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(RegistryManager.COPPER_ASPECTUS.get()).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).input(RegistryManager.EMBER_SHARD.get()).fluid(fluidTag("forge", "molten_copper"), FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(RegistryManager.LEAD_ASPECTUS.get()).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).input(RegistryManager.EMBER_SHARD.get()).fluid(fluidTag("forge", "molten_lead"), FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(RegistryManager.SILVER_ASPECTUS.get()).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).input(RegistryManager.EMBER_SHARD.get()).fluid(fluidTag("forge", "molten_silver"), FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().build(consumer));
		StampingRecipeBuilder.create(RegistryManager.DAWNSTONE_ASPECTUS.get()).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).input(RegistryManager.EMBER_SHARD.get()).fluid(fluidTag("forge", "molten_dawnstone"), FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().build(consumer));

		//mixing
		MixingRecipeBuilder.create(RegistryManager.MOLTEN_DAWNSTONE.FLUID.get(), 4).folder(mixingFolder).input(fluidTag("forge", "molten_copper"), 2).input(fluidTag("forge", "molten_gold"), 2).save(consumer);
		MixingRecipeBuilder.create(RegistryManager.MOLTEN_BRONZE.FLUID.get(), 4).folder(mixingFolder).input(fluidTag("forge", "molten_copper"), 3).input(fluidTag("forge", "molten_tin"), 1).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(itemTag("forge", "ingots/bronze"))).build(consumer));
		MixingRecipeBuilder.create(RegistryManager.MOLTEN_ELECTRUM.FLUID.get(), 4).folder(mixingFolder).input(fluidTag("forge", "molten_silver"), 2).input(fluidTag("forge", "molten_gold"), 2).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(itemTag("forge", "ingots/electrum"))).build(consumer));
		MixingRecipeBuilder.create(RegistryManager.DWARVEN_OIL.FLUID.get(), 10).id(new ResourceLocation(Embers.MODID, mixingFolder + "/dwarven_oil_steam")).input(RegistryManager.SOUL_CRUDE.FLUID.get(), 5).input(RegistryManager.STEAM.FLUID.get(), 20).save(consumer);
		MixingRecipeBuilder.create(RegistryManager.DWARVEN_OIL.FLUID.get(), 30).id(new ResourceLocation(Embers.MODID, mixingFolder + "/dwarven_oil")).input(RegistryManager.SOUL_CRUDE.FLUID.get(), 10).input(RegistryManager.DWARVEN_GAS.FLUID.get(), 5).save(consumer);

		//metal coefficient
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.DAWNSTONE_BLOCK).folder(coefficientFolder).coefficient(1.5).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.LEAD_BLOCK).folder(coefficientFolder).coefficient(2.625).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.BRONZE_BLOCK).folder(coefficientFolder).coefficient(2.625).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(EmbersBlockTags.BRONZE_BLOCK)).build(consumer));
		MetalCoefficientRecipeBuilder.create(Tags.Blocks.STORAGE_BLOCKS_IRON).folder(coefficientFolder).coefficient(2.625).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.NICKEL_BLOCK).folder(coefficientFolder).coefficient(2.85).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(EmbersBlockTags.NICKEL_BLOCK)).build(consumer));
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.TIN_BLOCK).folder(coefficientFolder).coefficient(2.85).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(EmbersBlockTags.TIN_BLOCK)).build(consumer));
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.ALUMINUM_BLOCK).folder(coefficientFolder).coefficient(2.85).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(EmbersBlockTags.ALUMINUM_BLOCK)).build(consumer));
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.SILVER_BLOCK).folder(coefficientFolder).coefficient(3.0).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.ELECTRUM_BLOCK).folder(coefficientFolder).coefficient(3.0).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(EmbersBlockTags.ELECTRUM_BLOCK)).build(consumer));
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.PRISTINE_COPPER).folder(coefficientFolder).coefficient(3.0).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.EXPOSED_COPPER).folder(coefficientFolder).coefficient(2.5).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.WEATHERED_COPPER).folder(coefficientFolder).coefficient(2.0).save(consumer);
		MetalCoefficientRecipeBuilder.create(EmbersBlockTags.OXIDIZED_COPPER).folder(coefficientFolder).coefficient(1.5).save(consumer);
		MetalCoefficientRecipeBuilder.create(Tags.Blocks.STORAGE_BLOCKS_GOLD).folder(coefficientFolder).coefficient(3.0).save(consumer);

		//alchemy
		AlchemyRecipeBuilder.create(new ItemStack(Items.NETHERRACK, 4)).tablet(RegistryManager.EMBER_GRIT.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Items.COBBLESTONE, Items.COBBLESTONE, Items.COBBLESTONE, Items.COBBLESTONE)
		.aspects(EmbersItemTags.COPPER_ASPECTUS, EmbersItemTags.IRON_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(new ItemStack(Items.SOUL_SAND, 4)).tablet(RegistryManager.ASH.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Items.SAND, Items.SAND, Items.SAND, Items.SAND)
		.aspects(EmbersItemTags.COPPER_ASPECTUS, EmbersItemTags.IRON_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(new ItemStack(RegistryManager.ARCHAIC_BRICK.get(), 5)).tablet(RegistryManager.ARCHAIC_BRICK.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Items.SOUL_SAND, Items.CLAY_BALL, Items.CLAY_BALL)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.ANCIENT_MOTIVE_CORE.get()).tablet(RegistryManager.EMBER_SHARD.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(RegistryManager.ARCHAIC_BRICK.get(), RegistryManager.ARCHAIC_BRICK.get(), RegistryManager.ARCHAIC_BRICK.get())
		.aspects(EmbersItemTags.DAWNSTONE_ASPECTUS, EmbersItemTags.LEAD_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.CODEBREAKING_SLATE.get()).tablet(RegistryManager.EMBER_GRIT.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(RegistryManager.CAMINITE_PLATE.get(), RegistryManager.ARCHAIC_BRICK.get(), RegistryManager.ARCHAIC_BRICK.get())
		.aspects(EmbersItemTags.COPPER_ASPECTUS, EmbersItemTags.LEAD_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.ASHEN_FABRIC.get()).tablet(ItemTags.WOOL).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(EmbersItemTags.ASH_DUST, EmbersItemTags.ASH_DUST, Tags.Items.STRING, Tags.Items.STRING)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.LEAD_ASPECTUS, EmbersItemTags.DAWNSTONE_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL_CLUSTER.get()).tablet(RegistryManager.EMBER_CRYSTAL.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Ingredient.of(Tags.Items.GUNPOWDER), Ingredient.of(RegistryManager.EMBER_SHARD.get()), Ingredient.of(RegistryManager.EMBER_SHARD.get()), Ingredient.of(RegistryManager.EMBER_SHARD.get()))
		.aspects(EmbersItemTags.COPPER_ASPECTUS, EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.DAWNSTONE_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.WILDFIRE_CORE.get()).tablet(RegistryManager.ANCIENT_MOTIVE_CORE.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Ingredient.of(EmbersItemTags.DAWNSTONE_INGOT), Ingredient.of(RegistryManager.EMBER_CRYSTAL_CLUSTER.get()), Ingredient.of(EmbersItemTags.DAWNSTONE_INGOT), Ingredient.of(EmbersItemTags.COPPER_PLATE))
		.aspects(EmbersItemTags.COPPER_ASPECTUS, EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS, EmbersItemTags.DAWNSTONE_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.TYRFING.get()).tablet(RegistryManager.LEAD_TOOLS.SWORD.get()).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Tags.Items.STORAGE_BLOCKS_COAL, Tags.Items.OBSIDIAN, EmbersItemTags.LEAD_INGOT, EmbersItemTags.LEAD_INGOT)
		.aspects(EmbersItemTags.LEAD_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(new ItemStack(RegistryManager.ISOLATED_MATERIA.get(), 4)).tablet(Tags.Items.INGOTS_IRON).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Ingredient.of(Tags.Items.GEMS_QUARTZ), Ingredient.of(Items.CLAY_BALL), Ingredient.of(Tags.Items.GEMS_LAPIS))
		.aspects(EmbersItemTags.COPPER_ASPECTUS, EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.LEAD_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.CATALYTIC_PLUG.get()).tablet(EmbersItemTags.SILVER_INGOT).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Ingredient.of(RegistryManager.FLUID_PIPE.get()), Ingredient.of(Tags.Items.GLASS_SILICA), Ingredient.of(RegistryManager.FLUID_PIPE.get()))
		.aspects(EmbersItemTags.DAWNSTONE_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.COPPER_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Tags.Items.INGOTS_COPPER, Tags.Items.INGOTS_COPPER, Tags.Items.INGOTS_COPPER)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.COPPER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.IRON_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_IRON)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.GOLD_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_GOLD)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.DAWNSTONE_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.LEAD_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(EmbersItemTags.LEAD_INGOT, EmbersItemTags.LEAD_INGOT, EmbersItemTags.LEAD_INGOT)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.LEAD_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.SILVER_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(EmbersItemTags.SILVER_INGOT, EmbersItemTags.SILVER_INGOT, EmbersItemTags.SILVER_INGOT)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(consumer);
		AlchemyRecipeBuilder.create(RegistryManager.ALUMINUM_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(EmbersItemTags.ALUMINUM_INGOT, EmbersItemTags.ALUMINUM_INGOT, EmbersItemTags.ALUMINUM_INGOT)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.COPPER_ASPECTUS).save(ConsumerWrapperBuilder.wrap().addCondition(new AndCondition(tagReal(EmbersItemTags.ALUMINUM_INGOT), tagReal(EmbersItemTags.ALUMINUM_NUGGET))).build(consumer));
		AlchemyRecipeBuilder.create(RegistryManager.NICKEL_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(EmbersItemTags.NICKEL_INGOT, EmbersItemTags.NICKEL_INGOT, EmbersItemTags.NICKEL_INGOT)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.LEAD_ASPECTUS).save(ConsumerWrapperBuilder.wrap().addCondition(new AndCondition(tagReal(EmbersItemTags.NICKEL_INGOT), tagReal(EmbersItemTags.NICKEL_NUGGET))).build(consumer));
		AlchemyRecipeBuilder.create(RegistryManager.TIN_CRYSTAL_SEED.ITEM.get()).tablet(EmbersItemTags.CRYSTAL_SEEDS).domain(Embers.MODID).folder(alchemyFolder)
		.inputs(EmbersItemTags.TIN_INGOT, EmbersItemTags.TIN_INGOT, EmbersItemTags.TIN_INGOT)
		.aspects(EmbersItemTags.IRON_ASPECTUS, EmbersItemTags.SILVER_ASPECTUS).save(ConsumerWrapperBuilder.wrap().addCondition(new AndCondition(tagReal(EmbersItemTags.TIN_INGOT), tagReal(EmbersItemTags.TIN_NUGGET))).build(consumer));

		//boiling
		BoilingRecipeBuilder.create(RegistryManager.STEAM.FLUID.get(), 5).folder(boilingFolder).input(FluidTags.WATER, 1).save(consumer);
		BoilingRecipeBuilder.create(RegistryManager.DWARVEN_GAS.FLUID.get(), 1).folder(boilingFolder).input(RegistryManager.DWARVEN_OIL.FLUID.get(), 1).save(consumer);

		//gaseous fuel
		GaseousFuelRecipeBuilder.create(RegistryManager.STEAM.FLUID.get(), 1).burnTime(1).powerMultiplier(2.0).save(consumer);
		GaseousFuelRecipeBuilder.create(RegistryManager.DWARVEN_GAS.FLUID.get(), 1).burnTime(5).powerMultiplier(2.5).save(consumer);

		//crafting
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_CRYSTAL.get())
		.pattern("XXX")
		.pattern("XXX")
		.define('X', RegistryManager.EMBER_SHARD.get())
		.unlockedBy("has_shard", has(RegistryManager.EMBER_SHARD.get()))
		.save(consumer, getResource("ember_shard_to_crystal"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RegistryManager.EMBER_SHARD.get(), 6)
		.requires(RegistryManager.EMBER_CRYSTAL.get())
		.unlockedBy("has_crystal", has(RegistryManager.EMBER_CRYSTAL.get()))
		.save(consumer, getResource("ember_crystal_to_shard"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RegistryManager.CAMINITE_BLEND.get(), 8)
		.requires(Items.CLAY_BALL)
		.requires(Items.CLAY_BALL)
		.requires(Items.CLAY_BALL)
		.requires(Items.CLAY_BALL)
		.requires(Tags.Items.SAND)
		.unlockedBy("has_clay", has(Items.CLAY_BALL))
		.save(consumer, getResource("caminite_blend"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.RAW_CAMINITE_PLATE.get())
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_caminite_plate"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.RAW_FLAT_STAMP.get())
		.pattern("XXX")
		.pattern("X X")
		.pattern("XXX")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_flat_stamp"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.RAW_INGOT_STAMP.get())
		.pattern(" X ")
		.pattern("X X")
		.pattern(" X ")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_ingot_stamp"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.RAW_NUGGET_STAMP.get())
		.pattern("X X")
		.pattern("X X")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_nugget_stamp"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.RAW_PLATE_STAMP.get())
		.pattern("X X")
		.pattern("   ")
		.pattern("X X")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_plate_stamp"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.CAMINITE_BRICKS.get())
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_brick", has(RegistryManager.CAMINITE_BRICK.get()))
		.save(consumer, getResource("caminite_bricks"));

		decoRecipes(RegistryManager.CAMINITE_BRICKS_DECO, consumer);

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.CAMINITE_BLEND.get()), RecipeCategory.MISC, RegistryManager.CAMINITE_BRICK.get(), 0.1F, 200)
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get())).save(consumer, getResource("caminite_brick"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_CAMINITE_PLATE.get()), RecipeCategory.MISC, RegistryManager.CAMINITE_PLATE.get(), 0.1F, 200)
		.unlockedBy("has_raw_plate", has(RegistryManager.RAW_CAMINITE_PLATE.get())).save(consumer, getResource("caminite_plate"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_FLAT_STAMP.get()), RecipeCategory.MISC, RegistryManager.FLAT_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_flat_stamp", has(RegistryManager.RAW_FLAT_STAMP.get())).save(consumer, getResource("flat_stamp"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_INGOT_STAMP.get()), RecipeCategory.MISC, RegistryManager.INGOT_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_ingot_stamp", has(RegistryManager.RAW_INGOT_STAMP.get())).save(consumer, getResource("ingot_stamp"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_NUGGET_STAMP.get()), RecipeCategory.MISC, RegistryManager.NUGGET_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_nugget_stamp", has(RegistryManager.RAW_NUGGET_STAMP.get())).save(consumer, getResource("nugget_stamp"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_PLATE_STAMP.get()), RecipeCategory.MISC, RegistryManager.PLATE_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_plate_stamp", has(RegistryManager.RAW_PLATE_STAMP.get())).save(consumer, getResource("plate_stamp"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_BRICKS.get())
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.ARCHAIC_BRICK.get())
		.unlockedBy("has_brick", has(RegistryManager.ARCHAIC_BRICK.get()))
		.save(consumer, getResource("archaic_bricks"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_LIGHT.get())
		.pattern(" X ")
		.pattern("XSX")
		.pattern(" X ")
		.define('X', RegistryManager.ARCHAIC_BRICK.get())
		.define('S', RegistryManager.EMBER_SHARD.get())
		.unlockedBy("has_shard", has(RegistryManager.EMBER_SHARD.get()))
		.save(consumer, getResource("archaic_light"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_EDGE.get(), 2)
		.pattern("XXX")
		.pattern("XSX")
		.pattern("XXX")
		.define('X', RegistryManager.ARCHAIC_BRICK.get())
		.define('S', RegistryManager.EMBER_SHARD.get())
		.unlockedBy("has_shard", has(RegistryManager.EMBER_SHARD.get()))
		.save(consumer, getResource("archaic_edge"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_TILE.get(), 4)
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.ARCHAIC_BRICKS.get())
		.unlockedBy("has_bricks", has(RegistryManager.ARCHAIC_BRICKS.get()))
		.save(consumer, getResource("archaic_tile"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_BRICKS.get(), 4)
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.ARCHAIC_TILE.get())
		.unlockedBy("has_tile", has(RegistryManager.ARCHAIC_TILE.get()))
		.save(consumer, getResource("archaic_bricks_2"));
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_TILE.get(), RegistryManager.ARCHAIC_BRICKS.get());
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ARCHAIC_BRICKS.get(), RegistryManager.ARCHAIC_TILE.get());
		decoRecipes(RegistryManager.ARCHAIC_BRICKS_DECO, consumer);
		decoRecipes(RegistryManager.ARCHAIC_TILE_DECO, consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_STONE.get(), 4)
		.pattern(" S ")
		.pattern("SAS")
		.pattern(" S ")
		.define('S', Tags.Items.STONE)
		.define('A', EmbersItemTags.ASH_DUST)
		.unlockedBy("has_ash", has(EmbersItemTags.ASH_DUST))
		.save(consumer, getResource("ashen_stone"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_BRICK.get(), 4)
		.pattern(" S ")
		.pattern("SAS")
		.pattern(" S ")
		.define('S', ItemTags.STONE_BRICKS)
		.define('A', EmbersItemTags.ASH_DUST)
		.unlockedBy("has_ash", has(EmbersItemTags.ASH_DUST))
		.save(consumer, getResource("ashen_brick"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_BRICK.get(), 4)
		.pattern("SS")
		.pattern("SS")
		.define('S', RegistryManager.ASHEN_STONE.get())
		.unlockedBy("has_ash", has(EmbersItemTags.ASH_DUST))
		.save(consumer, getResource("ashen_brick_2"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_TILE.get(), 4)
		.pattern("SS")
		.pattern("SS")
		.define('S', RegistryManager.ASHEN_BRICK.get())
		.unlockedBy("has_ash", has(EmbersItemTags.ASH_DUST))
		.save(consumer, getResource("ashen_tile"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_STONE.get(), 4)
		.pattern("SS")
		.pattern("SS")
		.define('S', RegistryManager.ASHEN_TILE.get())
		.unlockedBy("has_ash", has(EmbersItemTags.ASH_DUST))
		.save(consumer, getResource("ashen_stone_2"));
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_BRICK.get(), RegistryManager.ASHEN_STONE.get());
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_TILE.get(), RegistryManager.ASHEN_STONE.get());
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_STONE.get(), RegistryManager.ASHEN_BRICK.get());
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_TILE.get(), RegistryManager.ASHEN_BRICK.get());
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_STONE.get(), RegistryManager.ASHEN_TILE.get());
		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, RegistryManager.ASHEN_BRICK.get(), RegistryManager.ASHEN_TILE.get());
		decoRecipes(RegistryManager.ASHEN_STONE_DECO, consumer);
		decoRecipes(RegistryManager.ASHEN_BRICK_DECO, consumer);
		decoRecipes(RegistryManager.ASHEN_TILE_DECO, consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, RegistryManager.EMBER_LANTERN.get(), 4)
		.pattern("P")
		.pattern("E")
		.pattern("I")
		.define('E', RegistryManager.EMBER_SHARD.get())
		.define('P', itemTag("forge", "plates/iron"))
		.define('I', itemTag("forge", "ingots/iron"))
		.unlockedBy("has_shard", has(RegistryManager.EMBER_SHARD.get()))
		.save(consumer, getResource("ember_lantern"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ANCIENT_CODEX.get())
		.pattern(" X ")
		.pattern("XCX")
		.pattern(" X ")
		.define('X', RegistryManager.ARCHAIC_BRICK.get())
		.define('C', RegistryManager.ANCIENT_MOTIVE_CORE.get())
		.unlockedBy("has_core", has(RegistryManager.ANCIENT_MOTIVE_CORE.get()))
		.save(consumer, getResource("ancient_codex"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.TINKER_HAMMER.get())
		.pattern("IBI")
		.pattern("ISI")
		.pattern(" S ")
		.define('B', itemTag("forge", "ingots/lead"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_lead", has(itemTag("forge", "ingots/lead")))
		.save(consumer, getResource("tinker_hammer"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.TINKER_LENS.get())
		.pattern("BE ")
		.pattern("IPE")
		.pattern("BE ")
		.define('E', itemTag("forge", "nuggets/lead"))
		.define('I', itemTag("forge", "plates/lead"))
		.define('B', itemTag("forge", "ingots/iron"))
		.define('P', Tags.Items.GLASS_SILICA)
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("tinker_lens"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.ATMOSPHERIC_GAUGE.get())
		.pattern(" I ")
		.pattern("CRC")
		.pattern("CIC")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('R', itemTag("forge", "dusts/redstone"))
		.unlockedBy("has_redstone", has(itemTag("forge", "dusts/redstone")))
		.save(consumer, getResource("atmospheric_gauge"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.EMBER_JAR.get())
		.pattern(" C ")
		.pattern("ISI")
		.pattern(" G ")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('S', RegistryManager.EMBER_SHARD.get())
		.define('G', Tags.Items.GLASS_SILICA)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("ember_jar"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.EMBER_CARTRIDGE.get())
		.pattern("ICI")
		.pattern("GSG")
		.pattern(" G ")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('S', RegistryManager.EMBER_CRYSTAL.get())
		.define('G', Tags.Items.GLASS_SILICA)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("ember_cartridge"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.CLOCKWORK_PICKAXE.get())
		.pattern("ISI")
		.pattern(" C ")
		.pattern(" W ")
		.define('C', itemTag("forge", "ingots/copper"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('S', RegistryManager.EMBER_SHARD.get())
		.define('W', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("clockwork_pickaxe"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.CLOCKWORK_AXE.get())
		.pattern("PCP")
		.pattern("ISI")
		.pattern(" W ")
		.define('C', itemTag("forge", "plates/copper"))
		.define('P', itemTag("forge", "plates/dawnstone"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('S', RegistryManager.EMBER_SHARD.get())
		.define('W', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("clockwork_axe"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.GRANDHAMMER.get())
		.pattern("BIB")
		.pattern(" C ")
		.pattern(" W ")
		.define('C', itemTag("forge", "ingots/copper"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('B', itemTag("forge", "storage_blocks/dawnstone"))
		.define('W', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("grandhammer"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.BLAZING_RAY.get())
		.pattern(" DP")
		.pattern("DPI")
		.pattern("SW ")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('D', itemTag("forge", "ingots/dawnstone"))
		.define('P', itemTag("forge", "plates/dawnstone"))
		.define('S', RegistryManager.EMBER_SHARD.get())
		.define('W', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("blazing_ray"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RegistryManager.CINDER_STAFF.get())
		.pattern("SES")
		.pattern("IWI")
		.pattern(" W ")
		.define('S', itemTag("forge", "plates/silver"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('E', RegistryManager.EMBER_SHARD.get())
		.define('W', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("cinder_staff"));

		toolRecipes(RegistryManager.LEAD_TOOLS, EmbersItemTags.LEAD_INGOT, RegistryManager.LEAD_NUGGET.get(), consumer);
		toolRecipes(RegistryManager.SILVER_TOOLS, EmbersItemTags.SILVER_INGOT, RegistryManager.SILVER_NUGGET.get(), consumer);
		toolRecipes(RegistryManager.DAWNSTONE_TOOLS, EmbersItemTags.DAWNSTONE_INGOT, RegistryManager.DAWNSTONE_NUGGET.get(), consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.MECHANICAL_CORE.get())
		.pattern("IBI")
		.pattern(" P ")
		.pattern("I I")
		.define('P', itemTag("forge", "plates/lead"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("mechanical_core"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_BORE.get())
		.pattern("YCY")
		.pattern("YBY")
		.pattern("III")
		.define('B', RegistryManager.MECHANICAL_CORE.get())
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('Y', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_mech_core", has(RegistryManager.MECHANICAL_CORE.get()))
		.save(consumer, getResource("ember_bore"));

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RegistryManager.CAMINITE_LEVER.get(), 4)
		.pattern("S")
		.pattern("P")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.unlockedBy("has_caminite_plate", has(RegistryManager.CAMINITE_PLATE.get()))
		.save(consumer, getResource("caminite_lever"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_EMITTER.get(), 4)
		.pattern(" C ")
		.pattern(" C ")
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.unlockedBy("has_caminite_plate", has(RegistryManager.CAMINITE_PLATE.get()))
		.save(consumer, getResource("ember_emitter"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_RECEIVER.get(), 4)
		.pattern("I I")
		.pattern("CPC")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.unlockedBy("has_caminite_plate", has(RegistryManager.CAMINITE_PLATE.get()))
		.save(consumer, getResource("ember_receiver"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_ACTIVATOR.get())
		.pattern("CCC")
		.pattern("CCC")
		.pattern("IFI")
		.define('I', itemTag("forge", "plates/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('F', Items.FURNACE)
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("ember_activator"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.PRESSURE_REFINERY.get())
		.pattern("CCC")
		.pattern("IDI")
		.pattern("IBI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('B', itemTag("forge", "storage_blocks/copper"))
		.define('D', itemTag("forge", "ingots/dawnstone"))
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("pressure_refinery"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.COPPER_CELL.get())
		.pattern("BIB")
		.pattern("ICI")
		.pattern("BIB")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "storage_blocks/copper"))
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_caminite_bricks", has(RegistryManager.CAMINITE_BRICKS.get()))
		.save(consumer, getResource("copper_cell"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.MELTER.get())
		.pattern("BPB")
		.pattern("BCB")
		.pattern("IFI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.define('F', Items.FURNACE)
		.unlockedBy("has_caminite_bricks", has(RegistryManager.CAMINITE_BRICKS.get()))
		.save(consumer, getResource("melter"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.FLUID_VESSEL.get())
		.pattern("B B")
		.pattern("P P")
		.pattern("BIB")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', itemTag("forge", "plates/iron"))
		.define('B', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_caminite_brick", has(RegistryManager.CAMINITE_BRICK.get()))
		.save(consumer, getResource("fluid_vessel"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.STAMPER.get())
		.pattern("XCX")
		.pattern("XBX")
		.pattern("X X")
		.define('B', itemTag("forge", "storage_blocks/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('X', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_iron_block", has(itemTag("forge", "storage_blocks/iron")))
		.save(consumer, getResource("stamper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.STAMP_BASE.get())
		.pattern("I I")
		.pattern("XBX")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('B', Items.BUCKET)
		.define('X', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_bucket", has(Items.BUCKET))
		.save(consumer, getResource("stamp_base"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_DIAL.get())
		.pattern("P")
		.pattern("C")
		.define('P', Items.PAPER)
		.define('C', itemTag("forge", "plates/copper"))
		.unlockedBy("has_copper_plate", has(itemTag("forge", "plates/copper")))
		.save(consumer, getResource("ember_dial"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ITEM_DIAL.get())
		.pattern("P")
		.pattern("L")
		.define('P', Items.PAPER)
		.define('L', itemTag("forge", "plates/lead"))
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("item_dial"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.FLUID_DIAL.get())
		.pattern("P")
		.pattern("I")
		.define('P', Items.PAPER)
		.define('I', itemTag("forge", "plates/iron"))
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("fluid_dial"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.CLOCKWORK_ATTENUATOR.get())
		.pattern("P")
		.pattern("I")
		.define('P', Items.PAPER)
		.define('I', itemTag("forge", "plates/silver"))
		.unlockedBy("has_silver_plate", has(itemTag("forge", "plates/silver")))
		.save(consumer, getResource("clockwork_attenuator"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.FLUID_PIPE.get(), 8)
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', itemTag("forge", "plates/iron"))
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("fluid_pipe"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ITEM_PIPE.get(), 8)
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/lead"))
		.define('P', itemTag("forge", "plates/lead"))
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("item_pipe"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.FLUID_EXTRACTOR.get())
		.pattern(" R ")
		.pattern("PBP")
		.pattern(" R ")
		.define('P', RegistryManager.FLUID_PIPE.get())
		.define('B', RegistryManager.CAMINITE_PLATE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_fluid_pipe", has(RegistryManager.FLUID_PIPE.get()))
		.save(consumer, getResource("fluid_extractor"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ITEM_EXTRACTOR.get())
		.pattern(" R ")
		.pattern("PBP")
		.pattern(" R ")
		.define('P', RegistryManager.ITEM_PIPE.get())
		.define('B', RegistryManager.CAMINITE_PLATE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_item_pipe", has(RegistryManager.ITEM_PIPE.get()))
		.save(consumer, getResource("item_extractor"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ITEM_DROPPER.get())
		.pattern(" P ")
		.pattern("I I")
		.define('I', itemTag("forge", "ingots/lead"))
		.define('P', RegistryManager.ITEM_PIPE.get())
		.unlockedBy("has_item_pipe", has(RegistryManager.ITEM_PIPE.get()))
		.save(consumer, getResource("item_dropper"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.BIN.get())
		.pattern("I I")
		.pattern("I I")
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/lead"))
		.define('P', itemTag("forge", "plates/lead"))
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("bin"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.MIXER_CENTRIFUGE.get())
		.pattern("PPP")
		.pattern("PCP")
		.pattern("IMI")
		.define('P', itemTag("forge", "plates/iron"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('M', RegistryManager.MECHANICAL_CORE.get())
		.unlockedBy("has_melter", has(RegistryManager.MELTER.get()))
		.save(consumer, getResource("mixer_centrifuge"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_EJECTOR.get())
		.pattern("P")
		.pattern("E")
		.pattern("I")
		.define('P', itemTag("forge", "plates/dawnstone"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('E', RegistryManager.EMBER_EMITTER.get())
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("ember_ejector"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_FUNNEL.get())
		.pattern("P P")
		.pattern("CRC")
		.pattern(" P ")
		.define('P', itemTag("forge", "plates/dawnstone"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('R', RegistryManager.EMBER_RECEIVER.get())
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("ember_funnel"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_RELAY.get(), 4)
		.pattern(" C ")
		.pattern("C C")
		.pattern(" P ")
		.define('P', itemTag("forge", "plates/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("ember_relay"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.MIRROR_RELAY.get(), 4)
		.pattern(" P ")
		.pattern("S S")
		.define('P', itemTag("forge", "plates/lead"))
		.define('S', itemTag("forge", "ingots/silver"))
		.unlockedBy("has_silver", has(itemTag("forge", "ingots/silver")))
		.save(consumer, getResource("mirror_relay"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.BEAM_SPLITTER.get())
		.pattern(" D ")
		.pattern("CPC")
		.pattern(" I ")
		.define('C', itemTag("forge", "ingots/copper"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', itemTag("forge", "plates/iron"))
		.define('D', itemTag("forge", "ingots/dawnstone"))
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("beam_splitter"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ITEM_VACUUM.get())
		.pattern(" II")
		.pattern("P  ")
		.pattern(" II")
		.define('I', itemTag("forge", "ingots/lead"))
		.define('P', RegistryManager.ITEM_PIPE.get())
		.unlockedBy("has_item_pipe", has(RegistryManager.ITEM_PIPE.get()))
		.save(consumer, getResource("item_vacuum"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.HEARTH_COIL.get())
		.pattern("PPP")
		.pattern("ICI")
		.pattern(" B ")
		.define('B', RegistryManager.MECHANICAL_CORE.get())
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', itemTag("forge", "plates/copper"))
		.define('C', itemTag("forge", "storage_blocks/copper"))
		.unlockedBy("has_mech_core", has(RegistryManager.MECHANICAL_CORE.get()))
		.save(consumer, getResource("hearth_coil"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.RESERVOIR.get())
		.pattern("B B")
		.pattern("I I")
		.pattern("BTB")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.define('T', RegistryManager.FLUID_VESSEL.get())
		.unlockedBy("has_vessel", has(RegistryManager.FLUID_VESSEL.get()))
		.save(consumer, getResource("reservoir"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.CAMINITE_RING.get())
		.pattern("BBB")
		.pattern("B B")
		.pattern("BBB")
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_reservoir", has(RegistryManager.RESERVOIR.get()))
		.save(consumer, getResource("caminite_ring"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.CAMINITE_GAUGE.get())
		.pattern("BBB")
		.pattern("G G")
		.pattern("BBB")
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.define('G', Tags.Items.GLASS_SILICA)
		.unlockedBy("has_reservoir", has(RegistryManager.RESERVOIR.get()))
		.save(consumer, getResource("caminite_gauge"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.CAMINITE_VALVE.get())
		.pattern("BBB")
		.pattern("P P")
		.pattern("BBB")
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.define('P', RegistryManager.FLUID_PIPE.get())
		.unlockedBy("has_reservoir", has(RegistryManager.RESERVOIR.get()))
		.save(consumer, getResource("caminite_valve"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.CRYSTAL_CELL.get())
		.pattern(" E ")
		.pattern("DED")
		.pattern("CBC")
		.define('C', itemTag("forge", "storage_blocks/copper"))
		.define('B', itemTag("forge", "storage_blocks/dawnstone"))
		.define('D', itemTag("forge", "plates/dawnstone"))
		.define('E', RegistryManager.EMBER_CRYSTAL.get())
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("crystal_cell"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.GEOLOGIC_SEPARATOR.get())
		.pattern("  B")
		.pattern("GIG")
		.define('B', itemTag("forge", "storage_blocks/silver"))
		.define('G', RegistryManager.CAMINITE_BRICK.get())
		.define('I', RegistryManager.FLUID_VESSEL.get())
		.unlockedBy("has_silver", has(itemTag("forge", "ingots/silver")))
		.save(consumer, getResource("geologic_separator"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.COPPER_CHARGER.get())
		.pattern(" X ")
		.pattern("DCD")
		.pattern("IPI")
		.define('D', itemTag("forge", "ingots/dawnstone"))
		.define('P', itemTag("forge", "plates/copper"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('X', itemTag("forge", "plates/iron"))
		.define('I', itemTag("forge", "ingots/iron"))
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("copper_charger"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_SIPHON.get())
		.pattern("BGB")
		.pattern("XGX")
		.pattern("BBB")
		.define('G', itemTag("forge", "ingots/copper"))
		.define('X', itemTag("forge", "plates/silver"))
		.define('B', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_charger", has(RegistryManager.COPPER_CHARGER.get()))
		.save(consumer, getResource("ember_siphon"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ITEM_TRANSFER.get())
		.pattern("PLP")
		.pattern("ILI")
		.pattern("I I")
		.define('P', itemTag("forge", "plates/lead"))
		.define('I', itemTag("forge", "ingots/lead"))
		.define('L', RegistryManager.ITEM_PIPE.get())
		.unlockedBy("has_item_pipe", has(RegistryManager.ITEM_PIPE.get()))
		.save(consumer, getResource("item_transfer"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.FLUID_TRANSFER.get())
		.pattern("PLP")
		.pattern("ILI")
		.pattern("I I")
		.define('P', itemTag("forge", "plates/iron"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('L', RegistryManager.FLUID_PIPE.get())
		.unlockedBy("has_fluid_pipe", has(RegistryManager.FLUID_PIPE.get()))
		.save(consumer, getResource("fluid_transfer"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ALCHEMY_PEDESTAL.get())
		.pattern("D D")
		.pattern("ICI")
		.pattern("SBS")
		.define('D', itemTag("forge", "plates/dawnstone"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('B', itemTag("forge", "storage_blocks/copper"))
		.define('C', RegistryManager.EMBER_CRYSTAL.get())
		.define('S', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("alchemy_pedestal"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.ALCHEMY_TABLET.get())
		.pattern(" D ")
		.pattern("SXS")
		.pattern("SIS")
		.define('D', itemTag("forge", "plates/dawnstone"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('X', itemTag("forge", "plates/copper"))
		.define('S', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("alchemy_tablet"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.BEAM_CANNON.get())
		.pattern("PSP")
		.pattern("PSP")
		.pattern("IBI")
		.define('P', itemTag("forge", "plates/copper"))
		.define('I', itemTag("forge", "ingots/dawnstone"))
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.define('S', RegistryManager.EMBER_CRYSTAL.get())
		.unlockedBy("has_dawnstone", has(itemTag("forge", "ingots/dawnstone")))
		.save(consumer, getResource("beam_cannon"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.MECHANICAL_PUMP.get())
		.pattern("EPE")
		.pattern("PPP")
		.pattern("BIB")
		.define('E', RegistryManager.FLUID_PIPE.get())
		.define('I', RegistryManager.FLUID_EXTRACTOR.get())
		.define('P', itemTag("forge", "plates/iron"))
		.define('B', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_extractor", has(RegistryManager.FLUID_EXTRACTOR.get()))
		.save(consumer, getResource("mechanical_pump"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.MINI_BOILER.get())
		.pattern("PPP")
		.pattern("E P")
		.pattern("PPP")
		.define('E', itemTag("forge", "ingots/copper"))
		.define('P', itemTag("forge", "plates/iron"))
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("mini_boiler"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.WILDFIRE_STIRLING.get())
		.pattern("XGX")
		.pattern("XGX")
		.pattern("BPB")
		.define('G', itemTag("forge", "storage_blocks/copper"))
		.define('X', itemTag("forge", "plates/dawnstone"))
		.define('P', RegistryManager.WILDFIRE_CORE.get())
		.define('B', RegistryManager.EMBER_SHARD.get())
		.unlockedBy("has_wildfire_core", has(RegistryManager.WILDFIRE_CORE.get()))
		.save(consumer, getResource("wildfire_stirling"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistryManager.EMBER_INJECTOR.get())
		.pattern("S S")
		.pattern("DCD")
		.pattern("BPB")
		.define('S', itemTag("forge", "ingots/silver"))
		.define('P', itemTag("forge", "plates/silver"))
		.define('D', itemTag("forge", "plates/dawnstone"))
		.define('C', RegistryManager.WILDFIRE_CORE.get())
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_wildfire_core", has(RegistryManager.WILDFIRE_CORE.get()))
		.save(consumer, getResource("ember_injector"));
	}

	public void fullOreRecipes(String name, ImmutableList<ItemLike> ores, Fluid fluid, Item raw, Item rawBlock, Item block, Item ingot, Item nugget, Item plate, Consumer<FinishedRecipe> consumer, MeltingBonus... bonusses) {
		fullMetalRecipes(name, fluid, block, ingot, nugget, plate, consumer);
		oreMeltingRecipes(name, fluid, consumer, bonusses);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, rawBlock)
		.pattern("XXX")
		.pattern("XYX")
		.pattern("XXX")
		.define('X', itemTag("forge", "raw_materials/" + name))
		.define('Y', raw)
		.unlockedBy("has_raw", has(raw))
		.save(consumer, getResource(name + "_raw_to_raw_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, raw, 9)
		.requires(rawBlock)
		.unlockedBy("has_block", has(rawBlock))
		.save(consumer, getResource(name + "_raw_block_to_raw"));

		oreSmelting(consumer, ores, RecipeCategory.MISC, ingot, 0.7F, 200, name + "_ingot");
		oreBlasting(consumer, ores, RecipeCategory.MISC, ingot, 0.7F, 100, name + "_ingot");
	}

	public void fullMetalRecipes(String name, Fluid fluid, Item block, Item ingot, Item nugget, Item plate, Consumer<FinishedRecipe> consumer) {
		fullMeltingStampingRecipes(name, fluid, consumer);
		blockIngotNuggetCompression(name, block, ingot, nugget, consumer);
		plateHammerRecipe(name, plate, consumer);
	}

	public void plateHammerRecipe(String name, Item plate, Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, plate)
		.requires(itemTag("forge", "ingots/" + name))
		.requires(itemTag("forge", "ingots/" + name))
		.requires(EmbersItemTags.TINKER_HAMMER)
		.unlockedBy("has_ingot", has(itemTag("forge", "ingots/" + name)))
		.save(consumer, getResource(name + "_plate_hammering"));
	}

	public void fullOreMeltingStampingRecipes(String name, Fluid fluid, Consumer<FinishedRecipe> consumer, MeltingBonus... bonusses) {
		oreMeltingRecipes(name, fluid, consumer, bonusses);
		fullMeltingStampingRecipes(name, fluid, consumer);
	}

	public void oreMeltingRecipes(String name, Fluid fluid, Consumer<FinishedRecipe> consumer, MeltingBonus... bonusses) {
		TagKey<Item> raw = itemTag("forge", "raw_materials/" + name);
		TagKey<Item> ore = itemTag("forge", "ores/" + name);
		TagKey<Item> rawBlock = itemTag("forge", "storage_blocks/raw_" + name);

		bonusRecipe((condition, bonus) -> {
			MeltingRecipeBuilder.create(raw).domain(Embers.MODID).folder(meltingFolder).bonusName(bonus.name).output(fluid, FluidAmounts.RAW_AMOUNT).bonus(bonus.fluid, bonus.amount).save(ConsumerWrapperBuilder.wrap().addCondition(condition).build(consumer));
		}, tagReal(raw), bonusses);
		bonusRecipe((condition, bonus) -> {
			MeltingRecipeBuilder.create(ore).domain(Embers.MODID).folder(meltingFolder).bonusName(bonus.name).output(fluid, FluidAmounts.ORE_AMOUNT).bonus(bonus.fluid, bonus.amount * 2).save(ConsumerWrapperBuilder.wrap().addCondition(condition).build(consumer));
		}, tagReal(ore), bonusses);
		bonusRecipe((condition, bonus) -> {
			MeltingRecipeBuilder.create(rawBlock).domain(Embers.MODID).folder(meltingFolder).bonusName(bonus.name).output(fluid, FluidAmounts.RAW_BLOCK_AMOUNT).bonus(bonus.fluid, bonus.amount * 9).save(ConsumerWrapperBuilder.wrap().addCondition(condition).build(consumer));
		}, tagReal(rawBlock), bonusses);
	}

	public void bonusRecipe(BiConsumer<ICondition, MeltingBonus> recipe, ICondition baseCondition, MeltingBonus... bonusses) {
		ArrayList<ICondition> conditions = new ArrayList<ICondition>();
		for (MeltingBonus bonus : bonusses) {
			ICondition condition = new TagEmptyCondition(itemTag("forge", "ingots/" + bonus.name).location());

			ArrayList<ICondition> recipeConditions = new ArrayList<ICondition>();

			recipeConditions.addAll(conditions);
			recipeConditions.add(baseCondition);
			if (bonus.optional)
				recipeConditions.add(new NotCondition(condition));

			recipe.accept(new AndCondition(recipeConditions.toArray(new ICondition[recipeConditions.size()])), bonus);
			conditions.add(condition);
		}
	}

	public void fullMeltingStampingRecipes(String name, Fluid fluid, Consumer<FinishedRecipe> consumer) {
		TagKey<Item> ingot = itemTag("forge", "ingots/" + name);
		TagKey<Item> nugget = itemTag("forge", "nuggets/" + name);
		TagKey<Item> block = itemTag("forge", "storage_blocks/" + name);
		TagKey<Item> plate = itemTag("forge", "plates/" + name);
		//melting
		MeltingRecipeBuilder.create(ingot).domain(Embers.MODID).folder(meltingFolder).output(fluid, FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(ingot)).build(consumer));
		MeltingRecipeBuilder.create(nugget).domain(Embers.MODID).folder(meltingFolder).output(fluid, FluidAmounts.NUGGET_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(nugget)).build(consumer));
		MeltingRecipeBuilder.create(block).domain(Embers.MODID).folder(meltingFolder).output(fluid, FluidAmounts.BLOCK_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(block)).build(consumer));
		MeltingRecipeBuilder.create(plate).domain(Embers.MODID).folder(meltingFolder).output(fluid, FluidAmounts.PLATE_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(plate)).build(consumer));
		//stamping
		StampingRecipeBuilder.create(ingot).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).fluid(fluidTag("forge", "molten_" + name), FluidAmounts.INGOT_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(ingot)).build(consumer));
		StampingRecipeBuilder.create(nugget).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.NUGGET_STAMP.get()).fluid(fluidTag("forge", "molten_" + name), FluidAmounts.NUGGET_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(nugget)).build(consumer));
		StampingRecipeBuilder.create(plate).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.PLATE_STAMP.get()).fluid(fluidTag("forge", "molten_" + name), FluidAmounts.PLATE_AMOUNT).save(ConsumerWrapperBuilder.wrap().addCondition(tagReal(plate)).build(consumer));
	}

	public void blockIngotNuggetCompression(String name, Item block, Item ingot, Item nugget, Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
		.pattern("XXX")
		.pattern("XYX")
		.pattern("XXX")
		.define('X', itemTag("forge", "ingots/" + name))
		.define('Y', ingot)
		.unlockedBy("has_ingot", has(itemTag("forge", "ingots/" + name)))
		.save(consumer, getResource(name + "_ingot_to_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
		.requires(block)
		.unlockedBy("has_block", has(block))
		.save(consumer, getResource(name + "_block_to_ingot"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
		.pattern("XXX")
		.pattern("XYX")
		.pattern("XXX")
		.define('X', itemTag("forge", "nuggets/" + name))
		.define('Y', nugget)
		.unlockedBy("has_nugget", has(itemTag("forge", "nuggets/" + name)))
		.save(consumer, getResource(name + "_nugget_to_ingot"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
		.requires(ingot)
		.unlockedBy("has_ingot", has(ingot))
		.save(consumer, getResource(name + "_ingot_to_nugget"));
	}

	public void decoRecipes(StoneDecoBlocks deco, Consumer<FinishedRecipe> consumer) {
		Item item = deco.block.get().asItem();

		if (deco.stairs != null) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, deco.stairs.get(), 4)
			.pattern("X  ")
			.pattern("XX ")
			.pattern("XXX")
			.define('X', item)
			.unlockedBy("has_" + deco.name, has(item))
			.save(consumer, deco.stairs.getId());

			stonecutterResultFromBase(consumer, RecipeCategory.DECORATIONS, deco.stairs.get(), item);
		}

		if (deco.slab != null) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, deco.slab.get(), 6)
			.pattern("XXX")
			.define('X', item)
			.unlockedBy("has_" + deco.name, has(item))
			.save(consumer, deco.slab.getId());

			stonecutterResultFromBase(consumer, RecipeCategory.DECORATIONS, deco.slab.get(), item, 2);
		}

		if (deco.wall != null) {
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, deco.wall.get(), 6)
			.pattern("XXX")
			.pattern("XXX")
			.define('X', item)
			.unlockedBy("has_" + deco.name, has(item))
			.save(consumer, deco.wall.getId());

			stonecutterResultFromBase(consumer, RecipeCategory.DECORATIONS, deco.wall.get(), item);
		}
	}

	public void toolRecipes(ToolSet set, TagKey<Item> material, Item nugget, Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(set.SWORD.get()), RecipeCategory.TOOLS, nugget, 0.1F, 200)
		.unlockedBy("has_" + set.name + "_sword", has(set.SWORD.get())).save(consumer, getResource(set.name + "_sword_smelting"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(set.SWORD.get()), RecipeCategory.TOOLS, nugget, 0.1F, 100)
		.unlockedBy("has_" + set.name + "_sword", has(set.SWORD.get())).save(consumer, getResource(set.name + "_sword_blasting"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(set.SHOVEL.get()), RecipeCategory.TOOLS, nugget, 0.1F, 200)
		.unlockedBy("has_" + set.name + "_shovel", has(set.SHOVEL.get())).save(consumer, getResource(set.name + "_shovel_smelting"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(set.SHOVEL.get()), RecipeCategory.TOOLS, nugget, 0.1F, 100)
		.unlockedBy("has_" + set.name + "_shovel", has(set.SHOVEL.get())).save(consumer, getResource(set.name + "_shovel_blasting"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(set.PICKAXE.get()), RecipeCategory.TOOLS, nugget, 0.1F, 200)
		.unlockedBy("has_" + set.name + "_pickaxe", has(set.PICKAXE.get())).save(consumer, getResource(set.name + "_pickaxe_smelting"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(set.PICKAXE.get()), RecipeCategory.TOOLS, nugget, 0.1F, 100)
		.unlockedBy("has_" + set.name + "_pickaxe", has(set.PICKAXE.get())).save(consumer, getResource(set.name + "_pickaxe_blasting"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(set.AXE.get()), RecipeCategory.TOOLS, nugget, 0.1F, 200)
		.unlockedBy("has_" + set.name + "_axe", has(set.AXE.get())).save(consumer, getResource(set.name + "_axe_smelting"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(set.AXE.get()), RecipeCategory.TOOLS, nugget, 0.1F, 100)
		.unlockedBy("has_" + set.name + "_axe", has(set.AXE.get())).save(consumer, getResource(set.name + "_axe_blasting"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(set.HOE.get()), RecipeCategory.TOOLS, nugget, 0.1F, 200)
		.unlockedBy("has_" + set.name + "_hoe", has(set.HOE.get())).save(consumer, getResource(set.name + "_hoe_smelting"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(set.HOE.get()), RecipeCategory.TOOLS, nugget, 0.1F, 100)
		.unlockedBy("has_" + set.name + "_hoe", has(set.HOE.get())).save(consumer, getResource(set.name + "_hoe_blasting"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, set.SWORD.get())
		.pattern("M")
		.pattern("M")
		.pattern("S")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_" + set.name, has(material))
		.save(consumer, set.name + "_sword");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, set.SHOVEL.get())
		.pattern("M")
		.pattern("S")
		.pattern("S")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_" + set.name, has(material))
		.save(consumer, set.name + "_shovel");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, set.PICKAXE.get())
		.pattern("MMM")
		.pattern(" S ")
		.pattern(" S ")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_" + set.name, has(material))
		.save(consumer, set.name + "_pickaxe");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, set.AXE.get())
		.pattern("MM")
		.pattern("MS")
		.pattern(" S")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_" + set.name, has(material))
		.save(consumer, set.name + "_axe");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, set.HOE.get())
		.pattern("MM")
		.pattern(" S")
		.pattern(" S")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_" + set.name, has(material))
		.save(consumer, set.name + "_hoe");
	}

	public TagKey<Item> itemTag(String modId, String name) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(modId, name));
	}

	public TagKey<Fluid> fluidTag(String modId, String name) {
		return TagKey.create(Registries.FLUID, new ResourceLocation(modId, name));
	}

	public ICondition tagReal(TagKey<?> tag) {
		return new NotCondition(new TagEmptyCondition(tag.location()));
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
	}
}
