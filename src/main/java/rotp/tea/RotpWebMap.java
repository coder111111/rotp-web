package rotp.tea;

import java.io.IOException;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;

public class RotpWebMap {
    private static HTMLDocument document = Window.current().getDocument();

    public static void main(String[] args) {
//        System.out.println("MAIN ENTER");
        try {
            RotpWebMap client = new RotpWebMap();
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("MAIN EXIT");
    }


    private void run() throws IOException {
        // get rid of scrollbars
        document.getBody().getStyle().setProperty("overflow", "hidden");

        Galaxy galaxy = GameLoader.load();
        GalaxyMap galaxyMap = new GalaxyMap(galaxy);
        document.getBody().appendChild(galaxyMap.getCanvas());
    }

    // TODO: create a texture inside a different canvas:
    // Not worth it, just save it as an image...
    // HTMLCanvasElement.toDataURL
    
    // https://gero3.github.io/facetype.js/ to convert fonts

}
