package net.ssum.flains.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.ssum.flains.Flains;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> BLOOM_FLOWERS = createTag("bloom_flowers");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> DEAD_FLOWERS = createTag("dead_flowers");
        public static final TagKey<Item> BLOOM_FLOWERS = createTag("bloom_flowers");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, name));
        }
    }
}
