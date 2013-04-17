package net.fishbulb.jcod.display;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class BaseTileset implements Tileset {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Getter @Setter
    private Map<Character, Tile> tileMap = new HashMap<>();

    @Override public int columns() {
        return 0;
    }

    @Override public int rows() {
        return 0;
    }

    @Override public void mapChar(char from, Tile to) {
        tileMap.put(from, to);
    }

    @Override public void unmapChar(char c) {
        tileMap.remove(c);
    }

    @Override public Tile get(int x, int y) {
        return null;
    }

    @Override public Tile get(char idx) {
        return null;
    }

    @Override public int getTileWidth() {
        return 0;
    }

    @Override public int getTileHeight() {
        return 0;
    }

}
