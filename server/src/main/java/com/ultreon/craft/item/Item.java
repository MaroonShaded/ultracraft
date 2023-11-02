package com.ultreon.craft.item;

import com.ultreon.craft.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v1.Language;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Item {
    private final int maxStackSize;

    public Item(Properties properties) {
        this.maxStackSize = properties.maxStackSize;
    }

    public void use(UseItemContext useItemContext) {

    }

    public String getTranslation() {
        return Language.translate(this.getTranslationId());
    }

    @NotNull
    public String getTranslationId() {
        Identifier id = this.getId();
        return id == null ? "craft.item.air.name" : id.location() + ".item." + id.path() + ".name";
    }

    public Identifier getId() {
        return Registries.ITEMS.getKey(this);
    }

    public List<String> getDescription(ItemStack itemStack) {
        return Collections.emptyList();
    }

    public ItemStack defaultStack() {
        return new ItemStack(this);
    }

    /**
     * Get the maximum item stack size.
     *
     * @return the maximum stack size.
     * @see ItemStack#getCount() 
     */
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    /**
     * Item properties.
     */
    public static class Properties {
        private int maxStackSize = 64;

        /**
         * Set the max stack size.
         *
         * @param size the stack size.
         */
        public @This Properties stackSize(int size) {
            this.maxStackSize = size;
            return this;
        }
    }
}