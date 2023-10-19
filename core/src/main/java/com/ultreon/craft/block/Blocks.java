package com.ultreon.craft.block;

import com.ultreon.craft.block.Block.Properties;
import com.ultreon.craft.item.tool.ToolType;
import com.ultreon.craft.registry.Registries;
import com.ultreon.craft.util.UtilityClass;
import com.ultreon.libs.commons.v0.Identifier;

public final class Blocks extends UtilityClass {
    public static final Block AIR = Blocks.register("air", new Block());
    public static final Block GRASS_BLOCK = Blocks.register("grass_block", new Block(new Properties().hardness(3F).effectiveTool(ToolType.SHOVEL)));
    public static final Block DIRT = Blocks.register("dirt", new Block(new Properties().hardness(3F).effectiveTool(ToolType.SHOVEL)));
    public static final Block SAND = Blocks.register("sand", new Block(new Properties().hardness(2.5F).effectiveTool(ToolType.SHOVEL)));
    public static final Block STONE = Blocks.register("stone", new Block(new Properties().hardness(15.0F).effectiveTool(ToolType.PICKAXE).requiresTool()));
    public static final Block COBBLESTONE = Blocks.register("cobblestone", new Block(new Properties().hardness(15.0F).effectiveTool(ToolType.PICKAXE).requiresTool()));
    public static final Block WATER = Blocks.register("water", new Block(new Properties().transparent().noCollision().fluid().hardness(16.0F)));

    private static <T extends Block> T register(String name, T block) {
        Registries.BLOCK.register(new Identifier(name), block);
        return block;
    }

    public static void nopInit() {

    }
}
