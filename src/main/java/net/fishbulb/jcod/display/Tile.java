package net.fishbulb.jcod.display;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Tile {

    @Getter @Setter @NonNull
    private Drawable glyph;

    @Getter @Setter @NonNull
    private Color fgColor;

    @Getter @Setter @NonNull
    private Color bgColor;

    public Tile(Drawable glyph, Color color, Color bgColor) {
        this.glyph = glyph;
        this.fgColor = color.cpy();
        this.bgColor = bgColor.cpy();
    }

    public Tile(Drawable glyph, Color color) {
        this(glyph, color, Color.BLACK.cpy());
    }

    public Tile(Drawable glyph) {
        this(glyph, Color.WHITE.cpy(), Color.BLACK.cpy());
    }

    public Tile(Tile that) {
        this.glyph = that.glyph;
        this.fgColor = that.fgColor.cpy();
        this.bgColor = that.bgColor.cpy();
    }

    public Tile copy() {
        return new Tile(glyph, fgColor, bgColor);
    }

    // fluent API
    public Drawable glyph() {
        return glyph;
    }

    public Tile glyph(Drawable g) {
        glyph = g;
        return this;
    }

    public Color fg() {
        return fgColor;
    }

    public Tile fg(Color color) {
        this.fgColor.set(color);
        return this;
    }


    public Color bg() {
        return bgColor;
    }

    public Tile bg(Color bgColor) {
        this.bgColor.set(bgColor);
        return this;
    }

}

