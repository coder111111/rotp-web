package rotp.tea;

import eu.bebendorf.threejava.math.Color;
import eu.bebendorf.threejava.texture.Texture;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

// TODO: Move to ThreeJava?
public interface MeshBasicMaterialParameters extends JSObject {

    @JSProperty
    public Color getColor();

    @JSProperty
    public void setColor(Color color);

    @JSProperty
    public Texture getMap();

    @JSProperty
    public void setMap(Texture tx);

    @JSProperty
    public boolean isTransparent();

    @JSProperty
    public void setTransparent(boolean transparent);

    @JSProperty
    public float getOpacity();

    @JSProperty
    public void setOpacity(float opacity);

    @JSProperty
    public int getSide();

    @JSProperty
    public void setSide(int side);
    
}
