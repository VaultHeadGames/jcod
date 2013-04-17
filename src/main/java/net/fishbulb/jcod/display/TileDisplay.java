package net.fishbulb.jcod.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.abs;

public class TileDisplay extends Widget {

    // we can't call these width or height because those are Actor properties
    @Getter
    private final int columns;

    @Getter
    private final int rows;

    @Getter
    private final int tileWidth;

    @Getter
    private final int tileHeight;

    @Getter @Setter
    private Tile bgTile;

    private final Tile[][] tiles;

    public TileDisplay(int columns, int rows, int tileWidth, int tileHeight, Tile bgTile) {
        this.columns = columns;
        this.rows = rows;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.bgTile = bgTile;
        tiles = new Tile[columns][rows];
    }

    public TileDisplay(int columns, int rows, int tileWidth, int tileHeight) {
        this(columns, rows, tileWidth, tileHeight, defaultWhiteTile());
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[x][y] = tile;
    }

    @SuppressWarnings("UnusedDeclaration") // Not an accessor, so I have to suppress the warning
    public void clearTile(int x, int y, Tile tile) {
        tiles[x][y] = null;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        // Color.equals compares int components and doesn't need an epsilon
        if ((abs(1.0 - parentAlpha) < 0.0001) && getColor().equals(Color.WHITE)) {
            drawSimple(batch);
        } else {
            drawHairy(batch, parentAlpha);
        }
    }

    // Draws tiles without any adjustment to sprite batch color
    private void drawSimple(SpriteBatch batch) {
        float xPos = getX();
        float yPos = getY();
        // draw background
        if (!((bgTile == null) || (bgTile.getGlyph() == null))) {
            Drawable bgGlyph = bgTile.getGlyph();
            for (int x = 0; x < columns; x++) {
                for (int y = 0; y < rows; y++) {
                    int yy = (rows - y - 1);
                    Tile tile = tiles[x][y];
                    if (tile == null) continue;
                    batch.setColor(tile.bg());
                    bgGlyph.draw(batch, xPos + (x * tileWidth), yPos + (yy * tileHeight), tileWidth, tileHeight);
                }
            }
        }

        // draw foregrounds
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                int yy = (rows - y - 1);
                Tile tile = tiles[x][y];
                if (tile == null) continue;
                Drawable glyph = tile.getGlyph();
                batch.setColor(tile.fg());
                glyph.draw(batch, xPos + (x * tileWidth), yPos + (yy * tileHeight), tileWidth, tileHeight);
            }
        }
    }

    // drawSimple specialized to adjust colors
    private void drawHairy(SpriteBatch batch, float parentAlpha) {
        float xPos = getX();
        float yPos = getY();
        // draw background
        Color base = getColor();
        base.a *= parentAlpha;
        Color cur = new Color();

        if (!((bgTile == null) || (bgTile.getGlyph() == null))) {
            Drawable bgGlyph = bgTile.getGlyph();
            for (int x = 0; x < columns; x++) {
                for (int y = 0; y < rows; y++) {
                    int yy = (rows - y - 1);
                    Tile tile = tiles[x][y];
                    if (tile == null) continue;
                    cur.set(tile.bg());
                    batch.setColor(cur.mul(base));
                    bgGlyph.draw(batch, xPos + (x * tileWidth), yPos + (yy * tileHeight), tileWidth, tileHeight);
                }
            }
        }

        // draw foregrounds
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                int yy = (rows - y - 1);
                Tile tile = tiles[x][y];
                if (tile == null) continue;
                Drawable glyph = tile.getGlyph();
                cur.set(tile.fg());
                batch.setColor(cur.mul(base));
                glyph.draw(batch, xPos + (x * tileWidth), yPos + (yy * tileHeight), tileWidth, tileHeight);
            }
        }
    }

    @Override
    public float getPrefWidth() {
        return columns * tileWidth;
    }

    @Override
    public float getPrefHeight() {
        return rows * tileHeight;
    }

    /** Returns a tile of 1 white pixel for use in solid colors such as backgrounds. */
    public static Tile defaultWhiteTile() {
        return LazyHolder.WHITE;
    }

    private static final class LazyHolder {
        private static final Tile WHITE;

        static {
            Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pix.setColor(1, 1, 1, 1);
            pix.fill();
            WHITE = new Tile(new TextureRegionDrawable(new TextureRegion(new Texture(pix))));

        }
    }
}
