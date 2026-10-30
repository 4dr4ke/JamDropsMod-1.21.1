package net.ssum.flains.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.ssum.flains.Flains;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> DEAD_FLOWERS = createTag("dead_flowers");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Flains.MOD_ID, name));
        }
    }
}
