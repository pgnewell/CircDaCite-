package com.pgnewell.cdc.circdacite.pathmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class Content {

    /**
     * An array of sample (dummy) items.
     */
    public static List<PathMenuItem> ITEMS = new ArrayList<PathMenuItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, PathMenuItem> ITEM_MAP = new HashMap<String, PathMenuItem>();

    static {
        // Add 3 sample items.
        addItem(new PathMenuItem("1", "Create path starting here"));
        addItem(new PathMenuItem("2", "Add to path"));
        addItem(new PathMenuItem("3", "Remove from path"));
        addItem(new PathMenuItem("4", "Remove from map"));
    }

    private static void addItem(PathMenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class PathMenuItem {
        public String id;
        public String content;

        public PathMenuItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
