package com.ultreon.craft.menu;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.craft.entity.Entity;
import com.ultreon.craft.entity.Player;
import com.ultreon.craft.item.ItemStack;
import com.ultreon.craft.world.BlockPos;
import com.ultreon.craft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Inventory extends ContainerMenu {
    private final ItemSlot[] hotbar = new ItemSlot[9];
    private Player holder;

    public Inventory(Player holder) {
        this(MenuTypes.INVENTORY, holder.getWorld(), holder, null);
        this.holder = holder;
    }

    public Inventory(MenuType<?> type, World world, Entity entity, @Nullable BlockPos pos) {
        super(type, world, entity, pos);

        if (!(entity instanceof Player)) {
            throw new IllegalArgumentException("Entity must be a player!");
        }
    }

    @Override
    protected void buildContainer(List<ItemSlot> slots) {
        for (int x = 0; x < 9; x++) {
            int idx = slots.size();
            ItemSlot itemSlot = new ItemSlot(this, new ItemStack(), x * 19 + 6, 83);
            this.hotbar[idx] = itemSlot;
            slots.add(itemSlot);
        }

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                slots.add(new ItemSlot(this, new ItemStack(), x * 19 + 6, y * 19 + 6));
            }
        }
    }

    public ItemSlot getHotbarSlot(int index) {
        Preconditions.checkElementIndex(index, this.hotbar.length, "Invalid hotbar index");
        return this.hotbar[index];
    }

    public List<ItemSlot> getHotbarSlots() {
        return List.of(this.hotbar);
    }

    /**
     * Adds a list of item stacks to the inventory.
     *
     * @param stacks the list of item stacks.
     * @return true if all items could fit.
     */
    @CanIgnoreReturnValue
    public boolean addItems(List<ItemStack> stacks) {
        boolean fit = true;
        for (ItemStack stack : stacks) {
            fit &= this.addItem(stack.copy());
        }
        return fit;
    }

    /**
     * Adds an item stack to the inventory holder.
     *
     * @param stack the item stack to add.
     * @return true if the item stack could fully fit in the inventory.
     */
    @CanIgnoreReturnValue
    public boolean addItem(ItemStack stack) {
        for (ItemSlot slot : this.slots) {
            ItemStack slotItem = slot.getItem();

            if (slotItem.isEmpty()) {
                int maxStackSize = stack.getItem().getMaxStackSize();
                int transferAmount = Math.min(stack.getCount(), maxStackSize);
                stack.transferTo(slotItem, transferAmount);
                this.onItemChanged(slot);
            } else if (slotItem.isSameItemSameTag(stack)) {
                stack.transferTo(slotItem, stack.getCount());
                this.onItemChanged(slot);
            }

            // If the stack is fully distributed, exit the loop
            if (stack.isEmpty()) {
                return true;
            }
        }

        // If the loop completes and there's still some stack remaining, it means it couldn't be fully added to slots.
        return stack.isEmpty();
    }

    public Player getHolder() {
        return this.holder;
    }
}