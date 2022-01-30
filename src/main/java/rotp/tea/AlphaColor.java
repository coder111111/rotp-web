package rotp.tea;

import eu.bebendorf.threejava.Three;
import eu.bebendorf.threejava.math.Color;

/**
 * Color with alpha channel.
 */
public class AlphaColor {
    private Color color;
    private float alpha; // 0 to 1.0

    public AlphaColor(Color color, float alpha) {
        this.color = color;
        this.alpha = alpha;
    }

    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    
    // dim according to alpha
    public Color dimColor() {
        float r = this.color.getRed();
        float g = this.color.getGreen();
        float b = this.color.getBlue();
        r *= this.alpha;
        g *= this.alpha;
        b *= this.alpha;
        return Three.Color(r, g, b);
    }
    
    public static AlphaColor parse(String s) {
        String color = s.substring(0, "#rrggbb".length());
        String alpha = s.substring("#rrggbb".length(), s.length());
//        System.out.println("ca="+s+" c="+color+" a="+alpha);
        int alphaHex = Integer.parseInt(alpha, 16);
        Color c = Three.Color(color);
        float a = alphaHex / 255f;
        return new AlphaColor(c, a);
    }
    
}
