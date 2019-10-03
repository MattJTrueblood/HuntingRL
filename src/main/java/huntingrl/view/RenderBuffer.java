package huntingrl.view;

import asciiPanel.AsciiPanel;
import huntingrl.util.Constants;
import huntingrl.view.panel.Panel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class RenderBuffer {

    private AsciiPanel terminal;
    private RenderBufferTile[][] buffer = new RenderBufferTile[Constants.TERMINAL_WIDTH][Constants.TERMINAL_HEIGHT];
    private final RenderBufferTile defaultTile = new RenderBufferTile(Color.BLACK, Color.WHITE, (char) 0);

    public RenderBuffer(AsciiPanel terminal) {
        this.terminal = terminal;
        clearBuffer();
    }

    public void clearBuffer() {
        for(int i = 0; i < Constants.TERMINAL_WIDTH; i++) {
            for(int j = 0; j < Constants.TERMINAL_HEIGHT; j++) {
                buffer[i][j] = defaultTile.copy();
            }
        }
    }

    public void write(Character character, int x, int y, Color fgColor, Color bgColor) {
        if(x < 0 || x >= Constants.TERMINAL_WIDTH || y < 0 || y >= Constants.TERMINAL_HEIGHT) {
            throw new ArrayIndexOutOfBoundsException("attempting to write to buffer outside of terminal bounds.");
        }
        if(character == null) {
            buffer[x][y].applyColor(bgColor, fgColor);
        }
        else {
            buffer[x][y].apply(bgColor, fgColor, character);
        }
    }

    public void draw() {
        for(int i = 0; i < Constants.TERMINAL_WIDTH; i++) {
            for(int j = 0; j < Constants.TERMINAL_HEIGHT; j++) {
                RenderBufferTile tile = buffer[i][j];
                terminal.write(tile.character, i, j, tile.fgColor, tile.bgColor);
            }
        }
    }

    /**
     * Try not to use this too much unless you're doing a simple scene and need weird special functions from AsciiPanel.
     * @return
     */
    public AsciiPanel getTerminal() {
        return terminal;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class RenderBufferTile implements Cloneable{
        Color bgColor;
        Color fgColor;
        char character;

        /**
         * Applies a new tile over an existing one.  Overwrites the character, blends the new color on top of the old one
         * using alpha compositing.
         * @param newBgColor
         * @param newFgColor
         * @param newCharacter
         */
        public void apply(Color newBgColor, Color newFgColor, char newCharacter) {
            this.character = newCharacter;
            applyColor(newBgColor, newFgColor);
        }

        public void applyColor(Color newBgColor, Color newFgColor) {
            bgColor = combineColors(bgColor, newBgColor);
            fgColor = combineColors(bgColor, newFgColor); //experimental
        }

        private Color combineColors(Color bottomColor, Color topColor) {
            if(topColor.getAlpha() == 255) {
                return topColor;
            }
            else if(topColor.getAlpha() == 0) {
                return bottomColor;
            }
            else if(topColor.equals(bottomColor)) {
                return topColor;
            }
            else if(topColor.getAlpha() == bottomColor.getAlpha()) {
                return new Color((topColor.getRed() + bottomColor.getRed()) / 2,
                        (topColor.getGreen() + bottomColor.getGreen()) / 2,
                        (topColor.getBlue() + bottomColor.getBlue()) / 2,
                        topColor.getAlpha());

            }
            else {
                //This is expensive and annoying.  Try not to do this too much.
                float bottomAlpha = intColorValToFloat(bottomColor.getAlpha());
                float topAlpha = intColorValToFloat(topColor.getAlpha());
                float bottomRed = intColorValToFloat(bottomColor.getRed());
                float topRed = intColorValToFloat(topColor.getRed());
                float bottomGreen = intColorValToFloat(bottomColor.getGreen());
                float topGreen = intColorValToFloat(topColor.getGreen());
                float bottomBlue = intColorValToFloat(bottomColor.getBlue());
                float topBlue = intColorValToFloat(topColor.getBlue());

                float rAlpha = ((1 - topAlpha) * bottomAlpha) + topAlpha;
                float rRed = (((1 - topAlpha) * (bottomAlpha * bottomRed)) + (topAlpha * topRed)) / rAlpha;
                float rGreen = (((1 - topAlpha) * (bottomAlpha * bottomGreen)) + (topAlpha * topGreen)) / rAlpha;
                float rBlue = (((1 - topAlpha) * (bottomAlpha * bottomBlue)) + (topAlpha * topBlue)) / rAlpha;
                return new Color(rRed, rGreen, rBlue, rAlpha);
            }
        }

        private float intColorValToFloat(int val) {
            return ((float) val) / ((float) 255);
        }

        public RenderBufferTile copy() {
            return new RenderBufferTile(this.bgColor, this.fgColor, this.character);
        }
    }
}
