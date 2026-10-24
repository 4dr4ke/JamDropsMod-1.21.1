package net.ssum.flains.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class ItemSearchScreen extends Screen {
    private final List<Item> allItems = BuiltInRegistries.ITEM.stream().toList();
    private List<Item> filteredItems = allItems;

    public ItemSearchScreen() {
        super(Component.literal("Recherche d'items"));
    }

    private void onSearchChanged(String query) {
        String lowerQuery = query.toLowerCase();
        filteredItems = allItems.stream()
                .filter(item -> item.getDescriptionId().toLowerCase().contains(lowerQuery)
                        || item.getDescription().getString().toLowerCase().contains(lowerQuery))
                .toList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, 10, 0xFFFFFF);

        int startX = 40, startY = 50, itemsPerRow = 9, size = 18;
        int index = 0;

        for (Item item : filteredItems) {
            int x = startX + (index % itemsPerRow) * size;
            int y = startY + (index / itemsPerRow) * size;

            if (y > this.height - 30) break;

            ItemStack stack = new ItemStack(item);
            guiGraphics.renderItem(stack, x, y);
            guiGraphics.renderItemDecorations(this.font, stack, x, y);

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(this.font, stack, mouseX, mouseY);
            }
            index++;
        }
    }
}