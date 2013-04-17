package net.fishbulb.jcod.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lombok.Getter;
import lombok.NonNull;

public class ImageTileset extends BaseTileset {

    @NonNull @Getter
    private final Texture texture;

    @Getter
    private final int tileWidth;

    @Getter
    private final int tileHeight;

    public static enum Layout {InRows, InColumns, TCOD}

    @NonNull @Getter
    private final Layout layout;

    @Override public int columns() {
        return tiles[0].length;
    }

    @Override public int rows() {
        return tiles.length;
    }

    private Tile[][] tiles;

    public ImageTileset(Texture texture, int tileWidth, int tileHeight, Layout layout) {
        this.texture = texture;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.layout = layout;

        int rows = texture.getHeight() / tileHeight;
        int cols = texture.getWidth() / tileWidth;

        tiles = new Tile[rows][cols];
        TextureRegion[][] split = TextureRegion.split(texture, tileWidth, tileHeight);

        assert split.length == tiles.length;
        assert split[0].length == tiles[0].length;

        for (int r = 0; r < split.length; r++) {
            for (int c = 0; c < split[0].length; c++) {
                tiles[r][c] = new Tile(new TextureRegionDrawable(split[r][c]));
            }
        }
    }

    public ImageTileset(String path, int tileWidth, int tileHeight, Layout layout) {
        this(loadTexture(path), tileWidth, tileHeight, layout);
    }

    protected static Texture loadTexture(String path) {
        Texture tex = new Texture(Gdx.files.internal(path), false);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return tex;
    }

    @Override public Tile get(int x, int y) {
        if ((x >= columns()) || (y >= rows())) return null;
        return tiles[y][x];
    }

    @Override public Tile get(char idx) {
        Tile t = getTileMap().get(idx);
        if (t != null)
            return t;
        else if (layout == Layout.TCOD)
            return (idx > 255) ? null : get(ascii_to_tcod_table[idx], Layout.InRows);
        else
            return get(idx, layout);
    }

    protected Tile get(char idx, Layout layout) {
        switch (layout) {
            case InColumns:
                return get(idx / rows(), idx % rows());
            case InRows:
            default:
                return get(idx % columns(), idx / columns());
        }
    }

    // Used to translate ASCII to TCOD layout
    protected static final char[] ascii_to_tcod_table = new char[]{
            // 0-31
            0, 0, 0, 0, 0, 0, 0, 0, 0, 76, 77, 0, 0, 0, 0, 0,
            71, 70, 72, 0, 0, 0, 0, 0, 64, 65, 67, 66, 0, 73, 68, 69,
            // 32-63
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
            // 64-95
            32, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
            111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 33, 34, 35, 36, 37,
            // 96-127
            38, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142,
            143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 39, 40, 41, 42, 0,
            // 128-159
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            // 160-191
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            43, 44, 45, 46, 49, 0, 0, 0, 0, 81, 78, 87, 88, 0, 0, 55,
            // 192-223
            53, 50, 52, 51, 47, 48, 0, 0, 85, 86, 82, 84, 83, 79, 80, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 54, 0, 0, 0, 0, 0,
            // 224-255
            74, 75, 57, 58, 59, 60, 61, 62, 63, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    };


}
