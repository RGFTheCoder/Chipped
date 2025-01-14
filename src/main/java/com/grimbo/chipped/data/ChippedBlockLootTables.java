package com.grimbo.chipped.data;

import java.util.stream.Collectors;

import com.grimbo.chipped.block.ChippedBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraftforge.fml.RegistryObject;

public class ChippedBlockLootTables extends BlockLootTables {

	@Override
	protected void addTables() {
		for (RegistryObject<Block> block : ChippedBlocks.blocksMap.values()) {
			dropSelf(block.get());
		}

		dropSelf(ChippedBlocks.BOTANIST_WORKBENCH.get());
		dropSelf(ChippedBlocks.GLASSBLOWER.get());
		dropSelf(ChippedBlocks.CARPENTERS_TABLE.get());
		dropSelf(ChippedBlocks.LOOM_TABLE.get());
		dropSelf(ChippedBlocks.MASON_TABLE.get());
		dropSelf(ChippedBlocks.ALCHEMY_BENCH.get());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ChippedBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
	}
}