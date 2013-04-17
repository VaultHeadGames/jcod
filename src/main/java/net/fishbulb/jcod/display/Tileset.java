package net.fishbulb.jcod.display;

public interface Tileset {
    public int columns();

    public int rows();

    public void mapChar(char from, Tile to);

    public void unmapChar(char c);

    public Tile get(int x, int y);

    public Tile get(char idx);

    public int getTileWidth();

    public int getTileHeight();

}
