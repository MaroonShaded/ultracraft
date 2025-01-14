package com.ultreon.craft.text;

import com.ultreon.craft.util.Color;
import com.ultreon.craft.util.ElementID;

public interface FormatSequence extends Iterable<TextElement> {
    boolean isBoldAt(int index);
    boolean isItalicAt(int index);
    boolean isUnderlinedAt(int index);
    boolean isStrikethroughAt(int index);
    Color getColorAt(int index);
    HoverEvent<?> getHoverEventAt(int index);
    ClickEvent getClickEventAt(int index);
    ElementID getFontAt(int index);
    TextObject getTextAt(int index);
}
