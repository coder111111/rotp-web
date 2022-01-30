package rotp.tea;

import eu.bebendorf.threejava.Three;
import eu.bebendorf.threejava.math.Color;
import java.io.IOException;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.ajax.XMLHttpRequest;
import rotp.model.galaxy.StarType;

public class GameLoader {

    @Async
    public static native String get(String url) throws IOException;

    private static void get(String url, AsyncCallback<String> callback) {
        XMLHttpRequest xhr = XMLHttpRequest.create();
        xhr.open("get", url);
        xhr.setOnReadyStateChange(() -> {
            if (xhr.getReadyState() != XMLHttpRequest.DONE) {
                return;
            }

            int statusGroup = xhr.getStatus() / 100;
            if (statusGroup != 2 && statusGroup != 3) {
                callback.error(new IOException("HTTP status: "
                        + xhr.getStatus() + " " + xhr.getStatusText()));
            } else {
                callback.complete(xhr.getResponseText());
            }
        });
        xhr.send();
    }

    private static void loadStarSystems(Galaxy galaxy) throws IOException {
        String text = get("stars.txt");
        String lines[] = text.split("\n");
        boolean header = true;
        for (String line : lines) {
            if (header) {
                header = false;
                continue;
            }
            String values[] = line.split(",");
            int i = 0;
            int id = Integer.parseInt(values[i++]);
            float x = Float.parseFloat(values[i++]);
            float y = Float.parseFloat(values[i++]);
            int empireId = Integer.parseInt(values[i++]);
            String type = values[i++];
            String name;
            if (values.length == 6) {
                name = values[5];
            } else {
                name = null;
            }
            StarSystem ss = new StarSystem(id);
            ss.setX(x);
            ss.setY(y);
            ss.setEmpireId(empireId);
            ss.setStarType(StarType.valueOf(type));
            ss.setName(name);
            galaxy.addStarSystem(ss);
        }
    }
    private static void loadEmpires(Galaxy galaxy) throws IOException {
        String text = get("empires.txt");
        String lines[] = text.split("\n");
        boolean header = true;
        for (String line : lines) {
            if (header) {
                header = false;
                continue;
            }
            String values[] = line.split(",");
            
            int i = 0;
            int id = Integer.parseInt(values[i++]);
            boolean isPlayer = Boolean.parseBoolean(values[i++]);
            String name = values[i++];
            int shape = Integer.parseInt(values[i++]);
            float shipRange = Float.parseFloat(values[i++]);
            float scoutRange = Float.parseFloat(values[i++]);
            
            // XXX
            AlphaColor color = AlphaColor.parse(values[i++]);
            AlphaColor ownershipColor = AlphaColor.parse(values[i++]);
            AlphaColor empireRangeColor = AlphaColor.parse(values[i++]);
            AlphaColor shipBorderColor = AlphaColor.parse(values[i++]);
            AlphaColor scoutBorderColor = AlphaColor.parse(values[i++]);

            Empire ee = new Empire(id);
            ee.setIsPlayer(isPlayer);
            ee.setName(name);
            ee.setShape(shape);
            ee.setShipRange(shipRange);
            ee.setScoutRange(scoutRange);
            
            ee.setColor(color);
            ee.setOwnershipColor(ownershipColor);
            ee.setEmpireRangeColor(empireRangeColor);
            ee.setShipBorderColor(shipBorderColor);
            ee.setScoutBorderColor(scoutBorderColor);
            
            galaxy.addEmpire(ee);
        }
    }
    public static Galaxy load() throws IOException {
        Galaxy galaxy = new Galaxy();
        loadEmpires(galaxy);
        loadStarSystems(galaxy);
        return galaxy;
    }
}
