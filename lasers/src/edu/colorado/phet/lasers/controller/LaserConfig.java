/**
 * Class: LaserConfig
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.util.VisibleColor;

import java.awt.*;
import java.awt.geom.Point2D;

public class LaserConfig {

    private static final LaserConfig instance = new LaserConfig();

    //    public static LaserConfig instance() {
    //        return instance;
    //    }
    //
    //    public String getTitle() {
    //        return "Lasers";
    //    }
    //
    //    public float getTimeStep() {
    //        return 0.01f;
    //    }
    //
    //    public int getWaitTime() {
    //        return 20;
    //    }

    // Localization
    public static final String localizedStringsPath = "localization/LasersStrings";

    // Physical things
    public static Point2D.Double ORIGIN = new Point2D.Double( 150, 200 );

    public static final int DEFAULT_SEED_PHOTON_RATE = 20;
    public static final int MINIMUM_SEED_PHOTON_RATE = 0;
    public static final int MAXIMUM_SEED_PHOTON_RATE = 50;
    //    public static final int MAXIMUM_SEED_PHOTON_RATE = 10;

    public static final int DEFAULT_PUMPING_PHOTON_RATE = 0;
    public static final int MINIMUM_PUMPING_PHOTON_RATE = 0;
    public static final int MAXIMUM_PUMPING_PHOTON_RATE = 200;
    //    public static final int MAXIMUM_PUMPING_PHOTON_RATE = 100;

    // Spontaneous emission times, in milliseconds
    public static final int MAXIMUM_STATE_LIFETIME = 200;
    public static final int DEFAULT_SPONTANEOUS_EMISSION_TIME = 50;
    //    public static final int DEFAULT_SPONTANEOUS_EMISSION_TIME = ( MINIMUM_SPONTANEOUS_EMISSION_TIME + MAXIMUM_STATE_LIFETIME ) / 2;

    // Graphics things
    public static final int CONTROL_FONT_SIZE = 12;
    public static final int CONTROL_FONT_STYLE = Font.BOLD;
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String ATOM_IMAGE_FILE = IMAGE_DIRECTORY + "particle-gray-med.gif";
    public static final String PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "photon-comet.png";
    //        public static final String PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-red-med.gif";
    public static final String MID_HIGH_ENERGY_PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-deep-red-xsml.gif";
    public static final String HIGH_ENERGY_PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-blue-xsml.gif";
    public static final String LOW_ENERGY_PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-red-xsml.gif";
    public static final String RAY_GUN_IMAGE_FILE = IMAGE_DIRECTORY + "flashlight.png";
    //    public static final String RAY_GUN_IMAGE_FILE = IMAGE_DIRECTORY + "ray-gun-1A.png";

    // Graphics layers
    public static final double CAVITY_LAYER = 12;
    public static final double ATOM_LAYER = 11;
    public static final double PHOTON_LAYER = 11.5;

    public static final double MIN_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    public static final double MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH;
}
