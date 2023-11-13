package com.ultreon.craft.client.gui.screens;

import com.ultreon.craft.client.gui.*;
import com.ultreon.craft.client.gui.widget.Button;
import com.ultreon.craft.client.gui.widget.Label;
import com.ultreon.craft.client.gui.widget.SelectionList;
import com.ultreon.craft.text.TextObject;
import com.ultreon.craft.util.Color;
import com.ultreon.craft.world.WorldStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldSelectionScreen extends Screen {
    public static final Path WORLDS_DIR = Paths.get("worlds");
    private SelectionList<WorldStorage> worldList;
    private WorldStorage selected;
    private Button createButton;
    private Button playButton;
    private Button deleteWorld;

    public WorldSelectionScreen() {
        super(TextObject.translation("ultracraft.screen.world_selection.title"));
    }

    @Override
    public void build(GuiBuilder builder) {
        var titleLabel = builder.addWithPos(new Label(Alignment.CENTER), () -> new Position(this.getWidth() / 2, 10));
        titleLabel.text().set(this.getTitle());
        titleLabel.scale().set(2);

        this.worldList = builder.addWithBounds(new SelectionList<WorldStorage>().withItemHeight(20), () -> new Bounds(170, 40, this.getWidth() - 170, this.getHeight()));
        this.worldList.withEntries(this.locateWorlds())
                .withItemRenderer(this::renderItem)
                .withSelectable(true)
                .withCallback(this::selectWorld);

        this.createButton = builder.addWithPos(new Button(150), () -> new Position(10, this.getHeight() / 2 - 20));
        this.createButton.callback().set(this::createWorld);
        this.createButton.text().translate("ultracraft.screen.world_selection.create");

        this.playButton = builder.addWithPos(new Button(150), () -> new Position(10, this.getHeight() / 2 + 5));
        this.playButton.callback().set(this::playWorld);
        this.playButton.text().translate("ultracraft.screen.world_selection.play");

        this.deleteWorld = builder.addWithPos(new Button(150), () -> new Position(10, this.getHeight() / 2 + 30));
        this.deleteWorld.callback().set(this::deleteWorld);
        this.deleteWorld.text().translate("ultracraft.screen.world_selection.delete");
    }

    private void deleteWorld(Button caller) {
        if (this.selected == null) return;

        this.client.showScreen(new WorldDeleteConfirmScreen(this.selected));
    }

    private void playWorld(Button t) {
        if (this.selected == null) return;

        WorldStorage selected = this.selected;
        this.client.startWorld(selected);

    }

    private void createWorld(Button caller) {
        this.client.showScreen(new WorldCreationScreen());
    }

    private void selectWorld(WorldStorage storage) {
        this.selected = storage;
    }

    private void renderItem(Renderer renderer, WorldStorage storage, int y, int mouseX, int mouseY, boolean selected, float deltaTime) {
        renderer.drawTextLeft(storage.getDirectory().getFileName().toString(), this.worldList.getX(), y + 5, selected ? Color.WHITE : Color.GRAY);
    }

    public List<WorldStorage> locateWorlds() {
        var worlds = new ArrayList<WorldStorage>();
        try (Stream<Path> worldPaths = Files.list(WorldSelectionScreen.WORLDS_DIR)) {
            worlds = worldPaths.map(WorldStorage::new).sorted(Comparator.comparing(o -> o.getDirectory().getFileName().toString())).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException ignored) {
            // ignored
        }

        return worlds;
    }

    public SelectionList<WorldStorage> getWorldList() {
        return this.worldList;
    }

    public WorldStorage getSelected() {
        return this.selected;
    }

    public Button getCreateButton() {
        return this.createButton;
    }

    public Button getDeleteWorld() {
        return this.deleteWorld;
    }

    public Button getPlayButton() {
        return this.playButton;
    }
}