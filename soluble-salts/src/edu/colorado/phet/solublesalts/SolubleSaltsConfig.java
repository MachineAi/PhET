/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts;

import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SilverIodide;

import java.awt.*;
import java.awt.geom.Point2D;


/**
 * SolubleSaltsConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsConfig {

    // Descriptive information
    public static final String TITLE = "Slightly Soluble Salts";
    public static final String DESCRIPTION = "Soluble Salts";
    public static final String VERSION = "0.00.01";

    // Clock parameters
    public static final double DT = 1;
    public static final int FPS = 25;

    // Defaults
    public static final Salt DEFAULT_SALT = new SilverIodide();
    public static String DEFAULT_SALT_NAME = "Silver Iodide";

    // An ion will bind to another if it is within a distance of the open binding site that is less than or
    // equal to this many times the free ion's radius
    public static double BINDING_DISTANCE_FACTOR = 2;
    public static double DEFAULT_LATTICE_STICK_LIKELIHOOD = 0.9;
    public static double DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD = 0.001;
    public static double CONCENTRATION_CALIBRATION_FACTOR = 1 / 6.22E23;  // 2/27/06
    public static double VOLUME_CALIBRATION_FACTOR = 7.83E-16 / 500;    // 2/27/06
//    public static double VOLUME_CALIBRATION_FACTOR = 1.7342E-25;    // 2/27/06

    // Physical things
    public static final double AVAGADROS_NUMBER = 6.022E23;
//    public static final double SCALE = 500 / 580E-16;
    public static final double SCALE = 1;
    public static final Point2D VESSEL_ULC = new Point2D.Double( 150 / SCALE, 250 / SCALE ); // upper-left corner of vessel
    public static final Dimension VESSEL_SIZE = new Dimension( (int)( 700 / SCALE ), (int)( 500 / SCALE ) );
    public static final double VESSEL_WALL_THICKNESS = 20 / SCALE;
    public static double DEFAULT_WATER_LEVEL = 5E-16 / VOLUME_CALIBRATION_FACTOR;
    public static final double DEFAULT_LATTICE_SPEED = 3;
    // Acceleration of lattices when they come out of the shaker
    public static final double DEFAULT_LATTICE_ACCELERATION = .2;
    public static final double MAX_SPIGOT_FLOW = 1000;

    // Images
    public static final String IMAGE_PATH = "images/";
    public static final String SHAKER_IMAGE_NAME = IMAGE_PATH + "shaker.png";
    public static final String BLUE_ION_IMAGE_NAME = IMAGE_PATH + "molecule-big.gif";
    public static final String STOVE_IMAGE_FILE = IMAGE_PATH + "stove.png";
    public static final String FLAMES_IMAGE_FILE = IMAGE_PATH + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_PATH + "ice.gif";
    public static final String FAUCET_IMAGE = IMAGE_PATH + "faucet-gold-rt.gif";
    public static final Color WATER_COLOR = new Color( 200, 230, 255 );

    // Misc
    public static final String STRINGS_BUNDLE_NAME = "localization/SolubleSaltsStrings";

    // The time (real time in ms) after an ion is released from a lattice before it can
    // be bound to that lattice again
    public static final long RELEASE_ESCAPE_TIME = 2000;

    // Debug flags
    public static boolean RANDOM_WALK = false;
    public static boolean ONE_CRYSTAL_ONLY = false;
    public static boolean DEBUG = false;
    public static boolean SHOW_BONDS = false;
}
