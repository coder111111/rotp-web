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

import eu.bebendorf.threejava.Three;
import eu.bebendorf.threejava.math.Color;

public enum StarType {
    RED(Three.Color("red"), "RED_STAR_DESCRIPTION"),
    YELLOW(Three.Color("yellow"), "YELLOW_STAR_DESCRIPTION"),
    ORANGE(Three.Color("rgb(255,128,0)"), "ORANGE_STAR_DESCRIPTION"),
    GREEN(Three.Color("rgb(255,128,0)"), "ORANGE_STAR_DESCRIPTION"),
    WHITE(Three.Color("white"), "WHITE_STAR_DESCRIPTION"),
    BLUE(Three.Color("blue"), "BLUE_STAR_DESCRIPTION"),
    PURPLE(Three.Color("magenta"), "PURPLE_STAR_DESCRIPTION");
	
    private final Color color;
    private final String description;

    private StarType(Color color, String description) {
        this.color = color;
        this.description = description;
    }
    

    public Color color()              { return color; }
    public String description()       { return description; }
}
