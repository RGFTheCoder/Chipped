package com.grimbo.chipped.recipe;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChippedRecipe extends SingleItemRecipe {

	private Block icon;

	public ChippedRecipe(int serializerId, IRecipeType<?> type, ResourceLocation id, String group,
			Ingredient ingredient, ItemStack result, Block block) {
		this(type, fromId(serializerId), id, group, ingredient, result, block);
	}

	public ChippedRecipe(IRecipeType<?> type, IRecipeSerializer<?> serializer, ResourceLocation id, String group,
			Ingredient ingredient, ItemStack result, Block block) {
		super(type, serializer, id, group, ingredient, result);
		icon = block;
	}

	public static IRecipeSerializer<?> fromId(int serializerId) {
		List<?> serializers = ChippedSerializer.SERIALIZER.getEntries().stream().map(RegistryObject::get)
				.collect(Collectors.toList());
		return (IRecipeSerializer<?>) serializers.get(serializerId);
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return this.ingredient.test(inv.getItem(0));
	}

	public ItemStack getIcon() {
		return new ItemStack(icon);
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<ChippedRecipe> {

		private int id;
		private IRecipeType<?> type;
		private Block icon;

		public Serializer(int id, IRecipeType<?> type, Block icon) {
			this.id = id;
			this.type = type;
			this.icon = icon;
		}

		public ChippedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			String s = JSONUtils.getAsString(json, "group", "");
			Ingredient ingredient;
			if (JSONUtils.isArrayNode(json, "ingredient")) {
				ingredient = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "ingredient"));
			} else {
				ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
			}

			String s1 = JSONUtils.getAsString(json, "result");
			int i = JSONUtils.getAsInt(json, "count");
			ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
			return new ChippedRecipe(id, type, recipeId, s, ingredient, itemstack, icon);
		}

		public ChippedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			String s = buffer.readUtf(32767);
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			return new ChippedRecipe(id, type, recipeId, s, ingredient, itemstack, icon);
		}

		public void toNetwork(PacketBuffer buffer, ChippedRecipe recipe) {
			buffer.writeUtf(recipe.group);
			recipe.ingredient.toNetwork(buffer);
			buffer.writeItem(recipe.result);
		}
	}
}