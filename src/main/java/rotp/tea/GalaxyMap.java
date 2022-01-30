package rotp.tea;

import eu.bebendorf.threejava.BufferGeometryUtils;
import eu.bebendorf.threejava.Three;
import eu.bebendorf.threejava.camera.Camera;
import eu.bebendorf.threejava.camera.PerspectiveCamera;
import eu.bebendorf.threejava.geometry.BoxGeometry;
import eu.bebendorf.threejava.geometry.BufferGeometry;
import eu.bebendorf.threejava.geometry.CircleGeometry;
import eu.bebendorf.threejava.geometry.EdgesGeometry;
import eu.bebendorf.threejava.geometry.PlaneGeometry;
import eu.bebendorf.threejava.geometry.TextGeometry;
import eu.bebendorf.threejava.material.LineBasicMaterial;
import eu.bebendorf.threejava.material.MeshBasicMaterial;
import eu.bebendorf.threejava.math.Box3;
import eu.bebendorf.threejava.math.Color;
import eu.bebendorf.threejava.math.Vector2;
import eu.bebendorf.threejava.objects.Line;
import eu.bebendorf.threejava.objects.Mesh;
import eu.bebendorf.threejava.path.Font;
import eu.bebendorf.threejava.path.Shape;
import eu.bebendorf.threejava.renderer.WebGLRenderer;
import eu.bebendorf.threejava.scene.Scene;
import eu.bebendorf.threejava.texture.Texture;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.events.TouchEvent;
import org.teavm.jso.dom.events.WheelEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.xml.Document;
import org.teavm.jso.json.JSON;

public class GalaxyMap implements AnimationFrameCallback {
    
    // we place all objects near Z coordinate 0, but order matters
    // This is Z coordinates for specific objects.
    
    // must be at the bottom as it cannot be transparent
    public static float Z_RANGE = 0.01f;
    public static float Z_NEBULA = 0.02f; // background
    public static float Z_STARS = 0.03f; // background
    // each starsystem
    public static float Z_STAR_OWNER = 0.04f;
    public static float Z_STAR_SHAPE = 0.05f;
    public static float Z_STAR_NAME = 0.06f;
    
    private Galaxy galaxy;

    private final Scene scene;
    private PerspectiveCamera camera;
    private Mesh cube = null;
    private WebGLRenderer renderer = null;
    private HTMLCanvasElement canvas;
    
    private Texture starTexture;
    private Font font;
            
    private boolean mouseButtonDown = false;
    private Vector2 touchPanStart = null;
    private Float touchZoomStart = null;
    
    int width, height;
    
    public GalaxyMap(Galaxy galaxy) throws IOException {
        this.galaxy = galaxy;
        this.width = Window.current().getInnerWidth();
        this.height = Window.current().getInnerHeight();
        
        Window.current().addEventListener("resize", (Event evt) -> {
            this.width = Window.current().getInnerWidth();
            this.height = Window.current().getInnerHeight();
            float aspect = (float)this.width / this.height;
            this.canvas.setWidth(this.width);
            this.canvas.setHeight(this.height);
            renderer.setSize(this.width, this.height);
            camera.setAspect(aspect);
            camera.updateProjectionMatrix();
        });
        
        scene = Three.Scene();
        
        initCamera();
        
        renderer = Three.WebGLRenderer();
        renderer.setSize(this.width, this.height);
        renderer.setAntialias(true);
        this.canvas = renderer.getDomElement();
        
        initMouseEvents(canvas);
        
        // helpers
        scene.add(Three.AxesHelper(5f));
        scene.add(Three.GridHelper(10, 10));
        
        // add spinning cube to show 3D
        initCube();
        
        initBackground();
        
        this.starTexture = loadTexture("assets/star-white-100.webp");
        // for some reason MyriadPro font fails, O letters become empty
//        this.font = loadFont("assets/MyriadPro-BoldCond.json");
        this.font = loadFont("assets/helvetiker_bold.typeface.json");
        
        JSArray<BufferGeometry> rangeArray = JSArray.create();
        for (StarSystem ss: galaxy.getStars()) {
            addStar(ss, rangeArray);
        }
        
        if (1 == 1) {
            BufferGeometry rangeGeometry = BufferGeometryUtils.mergeBufferGeometries(rangeArray, false);
            
            MeshBasicMaterialParameters p = JSObjects.create();
            p.setColor(galaxy.getPlayer().getEmpireRangeColor().dimColor());
            p.setTransparent(false);
            MeshBasicMaterial rangeMaterial = Three.MeshBasicMaterial(p);
            
            Mesh rangeMesh = Three.Mesh(rangeGeometry, rangeMaterial);
            rangeMesh.getPosition().setX(0);
            rangeMesh.getPosition().setY(0);
            rangeMesh.getPosition().setZ(Z_RANGE);
            scene.add(rangeMesh);
        }
//        // FAIL, draws entire circles
//        {
//            BufferGeometry rangeGeometry = BufferGeometryUtils.mergeBufferGeometries(rangeArray, false);
//            EdgesGeometry edges = Three.EdgesGeometry(rangeGeometry, 1);
//            
//            MeshBasicMaterialParameters p = JSObjects.create();
//            p.setColor(galaxy.getPlayer().getEmpireRangeColor());
//            p.setTransparent(false);
//            LineBasicMaterial rangeMaterial = Three.LineBasicMaterial(p);
//            
//            Line line = Three.Line(edges, rangeMaterial);
//            scene.add(line);
//        }
        System.out.println("Starting ROTP map");
        onAnimationFrame(0.0);
    }
    
    @Async
    public static native Texture loadTexture(String url) throws IOException;
    private static void loadTexture(String url, AsyncCallback<Texture> callback) {
        Three.TextureLoader().load(url, (Texture tx) -> {
            callback.complete(tx);
        }, (XMLHttpRequest xhr) -> {
        }, (String error) -> {
            callback.error(new IOException("ERROR " + error));
        });
    }

    @Async
    public static native Font loadFont(String url) throws IOException;
    private static void loadFont(String url, AsyncCallback<Font> callback) {
        Three.FontLoader().load(url, (Font f) -> {
            callback.complete(f);
            System.out.println("Font loaded");
        }, (XMLHttpRequest xhr) -> {
        }, (String error) -> {
            callback.error(new IOException("ERROR " + error));
        });
    }

    private void initCamera() {
        float aspect = (float)this.width / this.height;
        camera = Three.PerspectiveCamera(75, aspect, 0.1f, 1000);
        camera.getPosition().setX(3);
        camera.getPosition().setY(3);
        camera.getPosition().setZ(5);
    }
    
    private void initMouseEvents(HTMLCanvasElement canvas) {
        canvas.addEventListener("wheel", (WheelEvent event) -> {
//            event.preventDefault();
            float cameraMove = (float) Math.signum(event.getDeltaY());
            // minimum zoom level reached.
            if (camera.getPosition().getZ() < 3 && cameraMove < 0) {
                return;
            }
            float cameraTarget;
            if (cameraMove > 0) {
                cameraTarget = 1.1f * camera.getPosition().getZ();
            } else {
                cameraTarget = 0.9f * camera.getPosition().getZ();
            }
            camera.getPosition().setZ(cameraTarget);
            draw();
        });

        canvas.addEventListener("mousedown", (MouseEvent event) -> {
            mouseButtonDown = true;
        });
        canvas.addEventListener("mouseclick", (MouseEvent event) -> {
            System.out.println("MOUSE mouseclick getButton="+event.getButton());
            System.out.println("MOUSE mouseclick getClientX="+event.getClientX()+" getClientY="+event.getClientY());
            System.out.println("MOUSE mouseclick getMovementX="+event.getMovementX()+" getMovementY="+event.getMovementY());
            System.out.println("MOUSE mouseclick getScreenX="+event.getScreenX()+" getScreenY="+event.getScreenY());
        });
        canvas.addEventListener("mousemove", (MouseEvent event) -> {
            if (mouseButtonDown) {
                // make this zoom dependent
                float ratio = 0.0025f * camera.getPosition().getZ();
                camera.getPosition().setX(camera.getPosition().getX() - (float) event.getMovementX() * ratio);
                // invert Y axis
                camera.getPosition().setY(camera.getPosition().getY() + (float) event.getMovementY() * ratio);
                draw();
            }
        });
        canvas.addEventListener("mouseup", (MouseEvent event) -> {
            mouseButtonDown = false;
        });
        canvas.addEventListener("mouseleave", (MouseEvent event) -> {
            mouseButtonDown = false;
        });

        // Touch events
        System.out.println("Touch register start");
        canvas.addEventListener("touchstart", (TouchEvent event) -> {
            updateTouchState(event);
            System.out.println("Touch start "+touchDump());
        });
        canvas.addEventListener("touchmove", (TouchEvent event) -> {
            System.out.println("Touch move "+touchDump());
            applyTouchMove(event);
            updateTouchState(event);
        });
        canvas.addEventListener("touchend", (TouchEvent event) -> {
            updateTouchState(event);
            System.out.println("Touch end "+touchDump());
        });
        canvas.addEventListener("touchcancel", (TouchEvent event) -> {
            updateTouchState(event);
            System.out.println("Touch cancel "+touchDump());
        });
        System.out.println("Touch register end");
    }
    
    private void applyTouchMove(TouchEvent event) {
        if (touchPanStart != null) {
            float ratio = 0.004f * camera.getPosition().getZ();
            float x1 = touchPanStart.getX();
            float y1 = touchPanStart.getY();
            int x2 = event.getTouches().get(0).getClientX();
            int y2 = event.getTouches().get(0).getClientY();

            camera.getPosition().setX(camera.getPosition().getX() - (float) (x2-x1) * ratio);
            // invert Y
            camera.getPosition().setY(camera.getPosition().getY() + (float) (y2-y1) * ratio);
            System.out.println("Touch move camera");
            draw();
        } else if (touchZoomStart != null) {
            float touchZoomNow = touchDistance(event);
            float diff = touchZoomNow - touchZoomStart;
            if (camera.getPosition().getZ() < 3 && diff > 0) {
                return;
            }
            float cameraTarget = camera.getPosition().getZ() - diff*0.15f;
            camera.getPosition().setZ(cameraTarget);
            System.out.println("Touch zoom camera");
            draw();
        }
    }

    private void updateTouchState(TouchEvent event) {
        if (event.getTouches().getLength() == 1) {
            touchPanStart = Three.Vector2(event.getTouches().get(0).getClientX(),
                    event.getTouches().get(0).getClientY());
            touchZoomStart = null;
        } else if (event.getTouches().getLength() > 1) {
            touchPanStart = null;
            touchZoomStart = touchDistance(event);
        } else {
            touchPanStart = null;
            touchZoomStart = null;
        }
    }
    private String touchDump() {
        String s;
        if (touchPanStart != null) {
            s = "pan="+JSON.stringify(touchPanStart);
        } else {
            s = "pan=null";
        }
        if (touchZoomStart != null) {
            s += " zoom="+Float.toString(touchZoomStart);
        } else {
            s += " zoom=null";
        }
        return s;
    }
    private Float touchDistance(TouchEvent event) {
        if (event.getTouches().getLength() < 2) {
            System.out.println("Pinch zoom event has too few touches");
            return null;
        }
        // ignore other touch events, only take first two
        int x1 = event.getTouches().get(0).getClientX();
        int y1 = event.getTouches().get(0).getClientY();
        int x2 = event.getTouches().get(1).getClientX();
        int y2 = event.getTouches().get(1).getClientY();
//        System.out.println("Touch event x1="+x1+" y1="+y1+" x2="+x2+" y2="+y2);
        float sq = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
//        System.out.println("Touch event sq="+sq);
        float dist = (float)Math.sqrt(sq);
//        System.out.println("Touch event dist="+dist);
        return dist;
    }

    private void initCube() {
        BoxGeometry geometry = Three.BoxGeometry();

        Color c1 = Three.Color(0x00ff00);
        MeshBasicMaterialParameters p = JSObjects.create();
        p.setColor(c1);
        MeshBasicMaterial material = Three.MeshBasicMaterial(p);

        cube = Three.Mesh(geometry, material);
        cube.getPosition().setX(1);
        cube.getPosition().setY(2);

        scene.add(cube);
    }
    
    private void initBackground() {
        // assume galaxy start is 0,0 no matter what the real one is, so maxX and maxY is enough.
        float backgroundWidth = galaxy.getMaxX() * 1.1f;
        float backgroundHeight = galaxy.getMaxY() * 1.1f;
        
        Three.TextureLoader().load("assets/sharedStarBackground.webp", (Texture tx) -> {
            PlaneGeometry geometry = Three.PlaneGeometry(backgroundWidth, backgroundHeight);
            MeshBasicMaterialParameters p = JSObjects.create();
            p.setMap(tx);
            p.setTransparent(true);
            p.setOpacity(0.9f);
            MeshBasicMaterial material = Three.MeshBasicMaterial(p);

            Mesh plane = Three.Mesh(geometry, material);
            plane.getPosition().setX(galaxy.getMaxX()*1.05f / 2);
            plane.getPosition().setY(galaxy.getMaxY()*1.05f / 2);
            plane.getPosition().setZ(Z_STARS);
            scene.add(plane);
        }, (XMLHttpRequest xhr) -> {
        }, (String error) -> {
            System.out.println("ERROR " + error);
        });
        Three.TextureLoader().load("assets/sharedNebulaBackground.webp", (Texture tx) -> {
            PlaneGeometry geometry = Three.PlaneGeometry(backgroundWidth, backgroundHeight);
            MeshBasicMaterialParameters p = JSObjects.create();
            p.setMap(tx);
            p.setTransparent(true);
            p.setOpacity(0.9f);
            MeshBasicMaterial material = Three.MeshBasicMaterial(p);

            Mesh plane = Three.Mesh(geometry, material);
            plane.getPosition().setX(galaxy.getMaxX()*1.05f / 2);
            plane.getPosition().setY(galaxy.getMaxY()*1.05f / 2);
            plane.getPosition().setZ(Z_NEBULA);
            scene.add(plane);
        }, (XMLHttpRequest xhr) -> {
        }, (String error) -> {
            System.out.println("ERROR " + error);
        });
            
    }

    List<Mesh> planetNames = new ArrayList<>();
    
    private void addStar(StarSystem ss, JSArray<BufferGeometry> rangeArray) {
        CircleGeometry geometry = Three.CircleGeometry(0.5f, 32);
        MeshBasicMaterial material;
        {
            MeshBasicMaterialParameters p = JSObjects.create();
            p.setColor(ss.getStarType().color());
            p.setMap(starTexture);
            p.setOpacity(0.9f);
            p.setTransparent(true);
            material = Three.MeshBasicMaterial(p);
        }
        Mesh circle = Three.Mesh(geometry, material);
        circle.getPosition().setX(ss.getX());
        circle.getPosition().setY(ss.getY());
        circle.getPosition().setZ(Z_STAR_SHAPE);
        scene.add(circle);
        
        if (ss.getName() != null && camera.getPosition().getZ() < 20) {
            Empire ee = null;
            if (ss.getEmpireId() >= 0) {
                ee = galaxy.getEmpire(ss.getEmpireId());
            }
            JSMapLike parameters = JSObjects.create();
            
            parameters.set("font", font);
            parameters.set("size", JSNumber.valueOf(0.4f));
            parameters.set("height", JSNumber.valueOf(0.0f));
            parameters.set("curvesegments", JSNumber.valueOf(36));
            
            TextGeometry textGeometry = Three.TextGeometry( ss.getName(), parameters );         
            textGeometry.computeBoundingBox();
            Box3 box = textGeometry.getBoundingBox();
//            System.out.println("box.min x="+box.getMin().getX());
//            System.out.println("box.max x="+box.getMax().getX());
            MeshBasicMaterialParameters textMaterialParameters = JSObjects.create();
            textMaterialParameters.setSide(Three.DoubleSide);
            if (ee != null) {
                textMaterialParameters.setColor(ee.getColor().getColor());
            } else {
                textMaterialParameters.setColor(Three.Color("gray"));
            }
            MeshBasicMaterial textMaterial = Three.MeshBasicMaterial(textMaterialParameters);

            Mesh text = Three.Mesh( textGeometry, textMaterial );
            text.getPosition().setX(ss.getX()-box.getMax().getX()/2);
            text.getPosition().setY(ss.getY()-box.getMax().getY()-0.5f);
            text.getPosition().setZ(Z_STAR_NAME);
            planetNames.add(text);
            scene.add(text);
            
            // add owner shape
            if (ee != null) {
                ownerShape(ss, ee);
            }
            // Add range display
            if (ee != null && ee.isIsPlayer()) {
                CircleGeometry rangeCircleGeometry = Three.CircleGeometry(ee.getShipRange(), 32);
                rangeCircleGeometry.translate(ss.getX(),ss.getY(), 0);
                rangeArray.push(rangeCircleGeometry);
            }
        }
    }

    public HTMLCanvasElement getCanvas() {
        return canvas;
    }
    
    public void draw() {
        Window.requestAnimationFrame(this);
    }
    
    @Override
    public void onAnimationFrame(double timestamp) {
        if (camera.getPosition().getZ() > 50) {
            for (Mesh m: planetNames) {
                m.setVisible(false);
            }
        } else {
            for (Mesh m: planetNames) {
                m.setVisible(true);
            }
            
        }
        if (cube != null) {
            cube.getRotation().setX(cube.getRotation().getX() + 0.05f);
            cube.getRotation().setY(cube.getRotation().getY() + 0.05f);

            renderer.render(scene, camera);
        }
    }

    private void ownerShape(StarSystem ss, Empire ee) {
        BufferGeometry shapeGeometry;
        switch (ee.getShape()) {
            case Empire.SHAPE_SQUARE: {
                shapeGeometry = Three.BoxGeometry(2, 2, 0);
            } break;
            case Empire.SHAPE_DIAMOND: {
                shapeGeometry = Three.BoxGeometry(2, 2, 0);
                shapeGeometry.rotateZ((float)Math.PI / 4);
            } break;
            case Empire.SHAPE_TRIANGLE1: {
                Shape sh = Three.Shape();
                sh.moveTo(0, -1);
                sh.lineTo(-1, +1);
                sh.lineTo(+1, +1);
                sh.lineTo(0, -1);
                shapeGeometry = Three.BufferGeometry();
                shapeGeometry.setFromPoints(sh.getPoints(5));
            } break;
            case Empire.SHAPE_TRIANGLE2: {
                Shape sh = Three.Shape();
                sh.moveTo(0, +1);
                sh.lineTo(-1, -1);
                sh.lineTo(+1, -1);
                sh.lineTo(0, +1);
                shapeGeometry = Three.BufferGeometry();
                shapeGeometry.setFromPoints(sh.getPoints(5));
            } break;
            case Empire.SHAPE_CIRCLE: {
                shapeGeometry = Three.CircleGeometry(1, 32);
            } break;
            default:
                throw new IllegalArgumentException("No such shape "+ee.getShape());
        }

        MeshBasicMaterialParameters p = JSObjects.create();
        p.setColor(ee.getOwnershipColor().getColor());
        p.setOpacity(ee.getOwnershipColor().getAlpha());
        p.setTransparent(true);
        MeshBasicMaterial material = Three.MeshBasicMaterial(p);

        Mesh shapeMesh = Three.Mesh(shapeGeometry, material);
        shapeMesh.getPosition().setX(ss.getX());
        shapeMesh.getPosition().setY(ss.getY());
        shapeMesh.getPosition().setZ(Z_STAR_OWNER);
        scene.add(shapeMesh);
    }

}
