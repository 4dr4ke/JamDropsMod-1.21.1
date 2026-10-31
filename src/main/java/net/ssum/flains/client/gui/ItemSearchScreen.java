package net.ssum.flains.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

public class ItemSearchScreen extends Screen {
    private final List<Item> allItems = BuiltInRegistries.ITEM.stream().toList();
    private List<Item> filteredItems = allItems;
    private EditBox searchBox;

    private static class RecipeVariant {
        final List<ItemStack> inputs;
        final ItemStack result;
        final String recipeType;
        RecipeVariant(List<ItemStack> inputs, ItemStack result, String recipeType) {
            this.inputs = inputs; this.result = result; this.recipeType = recipeType;
        }
    }

    private List<RecipeVariant> activeVariants = null;
    private int currentRecipeIndex = 0;
    private ItemStack inspectedItem = null;

    public ItemSearchScreen() {
        super(Component.literal("Recherche d'items"));
    }

    @Override
    protected void init() {
        super.init();
        searchBox = new EditBox(this.font, (this.width - 160) / 2, this.height - 35, 160, 20, Component.literal("Recherche"));
        searchBox.setMaxLength(50);
        searchBox.setResponder(q -> filteredItems = q.isEmpty() ? allItems : allItems.stream().filter(i -> i.getDescriptionId().toLowerCase().contains(q.toLowerCase()) || i.getDescription().getString().toLowerCase().contains(q.toLowerCase())).toList());
        this.addRenderableWidget(searchBox);
        this.setInitialFocus(searchBox);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = (this.width - 162) / 2, panelX = startX + 172, panelY = (this.height - 90) / 2;
        if (activeVariants != null) {
            if (activeVariants.size() > 1) {
                if (mouseX >= panelX + 10 && mouseX < panelX + 25 && mouseY >= panelY + 65 && mouseY < panelY + 80) {
                    currentRecipeIndex = (currentRecipeIndex - 1 + activeVariants.size()) % activeVariants.size();
                    return true;
                }
                if (mouseX >= panelX + 140 && mouseX < panelX + 155 && mouseY >= panelY + 65 && mouseY < panelY + 80) {
                    currentRecipeIndex = (currentRecipeIndex + 1) % activeVariants.size();
                    return true;
                }
            }
            if (mouseX < panelX || mouseX > panelX + 160 || mouseY < panelY || mouseY > panelY + 90) {
                int index = 0;
                for (Item item : filteredItems) {
                    int x = startX + (index % 9) * 18, y = 30 + (index / 9) * 18;
                    if (y > this.height - 60) break;
                    if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                        openRecipeScreen(new ItemStack(item), button == 0);
                        return true;
                    }
                    index++;
                }
                activeVariants = null; inspectedItem = null;
            }
            return true;
        }

        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        int index = 0;
        for (Item item : filteredItems) {
            int x = startX + (index % 9) * 18, y = 30 + (index / 9) * 18;
            if (y > this.height - 60) break;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                openRecipeScreen(new ItemStack(item), button == 0);
                return true;
            }
            index++;
        }
        return false;
    }

    private void openRecipeScreen(ItemStack stack, boolean isCraft) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var rm = level.getRecipeManager();
        var raw = rm.getRecipes().stream().filter(h -> isCraft ? h.value().getResultItem(level.registryAccess()).is(stack.getItem()) : h.value().getIngredients().stream().anyMatch(ing -> ing.test(stack))).toList();

        activeVariants = new ArrayList<>();
        for (var h : raw) {
            var rValue = h.value();
            var ings = rValue.getIngredients();
            var res = rValue.getResultItem(level.registryAccess());

            RecipeType<?> type = rValue.getType();
            String typeStr = "crafting";
            if (type == RecipeType.SMELTING || type == RecipeType.BLASTING || type == RecipeType.SMOKING || type == RecipeType.CAMPFIRE_COOKING) {
                typeStr = "furnace";
            } else if (type == RecipeType.STONECUTTING) {
                typeStr = "stonecutter";
            }

            List<List<ItemStack>> choices = new ArrayList<>();
            for (Ingredient ing : ings) {
                ItemStack[] match = ing.getItems();
                List<ItemStack> list = new ArrayList<>();
                if (match.length > 0) for (ItemStack m : match) list.add(m); else list.add(ItemStack.EMPTY);
                choices.add(list);
            }

            List<RecipeVariant> varList = new ArrayList<>();
            varList.add(new RecipeVariant(new ArrayList<>(), res, typeStr));
            for (List<ItemStack> ch : choices) {
                List<RecipeVariant> next = new ArrayList<>();
                for (RecipeVariant v : varList) {
                    for (ItemStack c : ch) {
                        List<ItemStack> ni = new ArrayList<>(v.inputs);
                        ni.add(c);
                        next.add(new RecipeVariant(ni, v.result, v.recipeType));
                    }
                }
                varList = next;
                if (varList.size() > 20) break;
            }
            activeVariants.addAll(varList);
        }
        currentRecipeIndex = 0; inspectedItem = stack;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float pt) {
        super.render(gfx, mouseX, mouseY, pt);
        gfx.drawCenteredString(this.font, this.getTitle(), this.width / 2, 10, 0xFFFFFF);

        int startX = (this.width - 162) / 2, index = 0;
        for (Item item : filteredItems) {
            int x = startX + (index % 9) * 18, y = 30 + (index / 9) * 18;
            if (y > this.height - 60) break;
            ItemStack stack = new ItemStack(item);
            gfx.renderItem(stack, x, y); gfx.renderItemDecorations(this.font, stack, x, y);
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) gfx.renderTooltip(this.font, stack, mouseX, mouseY);
            index++;
        }

        if (activeVariants != null && inspectedItem != null) {
            int panelX = startX + 172, panelY = (this.height - 90) / 2;
            gfx.fill(panelX, panelY, panelX + 160, panelY + 90, 0xC0101010);
            gfx.renderOutline(panelX, panelY, 160, 90, 0xFF555555);

            if (activeVariants.isEmpty()) {
                gfx.drawCenteredString(this.font, "Aucune recette", panelX + 80, panelY + 40, 0xFF5555);
            } else {
                gfx.drawCenteredString(this.font, (currentRecipeIndex + 1) + " / " + activeVariants.size(), panelX + 80, panelY + 10, 0xFFFFFF);
                if (activeVariants.size() > 1) {
                    gfx.drawString(this.font, "<", panelX + 15, panelY + 68, 0xFFFF55, true);
                    gfx.drawString(this.font, ">", panelX + 145, panelY + 68, 0xFFFF55, true);
                }

                RecipeVariant v = activeVariants.get(currentRecipeIndex);
                ItemStack station = switch (v.recipeType) { case "furnace" -> new ItemStack(Items.FURNACE); case "stonecutter" -> new ItemStack(Items.STONECUTTER); default -> new ItemStack(Items.CRAFTING_TABLE); };
                gfx.renderItem(station, panelX + 10, panelY + 8); gfx.renderItemDecorations(this.font, station, panelX + 10, panelY + 8);

                if (v.inputs.size() <= 1) {
                    if (!v.inputs.isEmpty() && !v.inputs.get(0).isEmpty()) {
                        gfx.renderItem(v.inputs.get(0), panelX + 45, panelY + 44);
                        gfx.renderItemDecorations(this.font, v.inputs.get(0), panelX + 45, panelY + 44);
                    }
                    gfx.drawString(this.font, "->", panelX + 72, panelY + 49, 0xAAAAAA, false);
                    gfx.renderItem(v.result, panelX + 105, panelY + 44);
                    gfx.renderItemDecorations(this.font, v.result, panelX + 105, panelY + 44);
                } else {
                    for (int i = 0; i < v.inputs.size() && i < 9; i++) {
                        ItemStack ing = v.inputs.get(i);
                        if (!ing.isEmpty()) {
                            int ix = panelX + 30 + (i % 3) * 18, iy = panelY + 35 + (i / 3) * 18;
                            gfx.renderItem(ing, ix, iy); gfx.renderItemDecorations(this.font, ing, ix, iy);
                        }
                    }
                    gfx.renderItem(v.result, panelX + 120, panelY + 44);
                    gfx.renderItemDecorations(this.font, v.result, panelX + 120, panelY + 44);
                }
            }
        }
    }
}