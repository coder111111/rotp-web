/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp.model.galaxy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// TODO: Attempt to reuse original ROTP classes. Fail so far.

/*
import rotp.model.colony.Colony;
import rotp.model.empires.Empire;
import rotp.model.empires.SystemView;
import rotp.model.events.StarSystemEvent;
import rotp.model.planet.Planet;
import rotp.model.planet.PlanetFactory;
import rotp.model.ships.Design;
import rotp.model.ships.ShipDesign;
import rotp.model.ships.ShipLibrary;
*/
public class StarSystem implements Serializable {
    private static final long serialVersionUID = 1L;
	// modnar: change shield colors to color-coded loot rarity
	// shield-5 --> shield-10 --> shield-15 --> shield-20
	//    green -->      blue -->    purple --> orange
    private static final Color shield5C = new Color(32,255,0); 
    private static final Color shield10C = new Color(0,112,224); 
    private static final Color shield15C = new Color(160,48,240);
    private static final Color shield20C = new Color(255,128,0);
    private static final Color selectionC = new Color(160,160,0);
    public static final Color systemNameBackC = new Color(40,40,40);
    public static final Color systemDataBackC = new Color(160,160,160);
    public static final int NULL_ID = -1;

    private String name = "";
    private float x, y;
//    private Planet planet;
    private final String starTypeKey;
    public final int id;

    private boolean abandoned = false;
    private boolean piracy = false;
    private boolean inNebula = false;
//    private final List<Transport> orbitingTransports = new ArrayList<>();
    private int[] nearbySystems;
    private String notes;
    private String eventKey;
//    private SpaceMonster monster;
//    private final List<StarSystemEvent> events = new ArrayList<>();

    public int transportDestId;
    public int transportAmt;
    public float transportTravelTime;
    
    // public so we can access without lazy inits from accessors
//    public transient SystemTransportSprite transportSprite;
//    public transient ShipRelocationSprite rallySprite;
    private transient StarType starType;
    private transient Rectangle nameBox;
    private transient boolean hovering;
    private transient int twinkleCycle, twinkleOffset, drawRadius;
    private transient boolean displayed = false;

    public StarSystem(String starTypeKey, int id) {
        this.starTypeKey = starTypeKey;
        this.id = id;
    }

    
}
