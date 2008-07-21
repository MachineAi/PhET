/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * GlaciersConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersConstants {

    /* Not intended for instantiation. */
    private GlaciersConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Flags
    //----------------------------------------------------------------------------
    
    public static final boolean UPDATE_WHILE_DRAGGING_SLIDERS = true;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final double CLOCK_DT = 1; // years
    public static final IntegerRange CLOCK_FRAME_RATE_RANGE = new IntegerRange( 1, 24, 12 ); // frames per second (years per second)
    public static final DecimalFormat CLOCK_DISPLAY_FORMAT = new DecimalFormat( "0" );
    public static final int CLOCK_DISPLAY_COLUMNS = 10;
    
    // Climate
    public static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 13, 20, 19 );  // temperature at sea level (degrees C)
    public static final DoubleRange SNOWFALL_RANGE = new DoubleRange( 0, 1.5, 0.95 ); // average snow accumulation (meters/year)
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // How y axis scaling is built into the background image
    public static final double Y_AXIS_SCALE_IN_IMAGE = 2.0;
    
    // model-view transform (MVT) parameters
    public static final double MVT_X_SCALE = 0.062; // scale x by this amount when going from model to view
    public static final double MVT_Y_SCALE = 0.1 * Y_AXIS_SCALE_IN_IMAGE; // scale y by this amount when going from model to view
    public static final double MVT_X_OFFSET = 0; // translate x by this amount when going from model to view
    public static final double MVT_Y_OFFSET = 0; // translate y by this amount when going from model to view
    public static final boolean MVT_FLIP_SIGN_X = false;
    public static final boolean MVT_FLIP_SIGN_Y = true;
    
    // maximum x coordinate for panning the zoomed view
    public static final double ZOOMED_VIEW_MAX_X = 80000; // meters
    
    // constant height of the birds-eye view, in pixels
    public static final double BIRDS_EYE_VIEW_HEIGHT = 75;
    
    // camera view scales
    public static final double BIRDS_EYE_CAMERA_VIEW_SCALE = 0.2 / Y_AXIS_SCALE_IN_IMAGE;
    public static final double ZOOMED_CAMERA_VIEW_SCALE = 1;
    
    // offset of upper-left corner of birds-eye viewport from highest point on the glacier
    public static final Point2D BIRDS_EYE_VIEWPORT_OFFSET = new Point2D.Double( -4400, +1000 ); // meters
    
    // where the glacier intersects the right edge of the zoomed viewport, percentage of zoomed viewport height
    public static final double ALIGNMENT_FACTOR_FOR_GLACIER_IN_ZOOMED_VIEWPORT = 0.25; // percent
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Colors
    //----------------------------------------------------------------------------
    
    // Generic transparent color
    public static final Color TRANSPARENT_COLOR = new Color( 0f, 0f, 0f, 0f );
    
    // Default color for module tabs
    public static final Color SELECTED_TAB_COLOR = Color.ORANGE;
    
    // color below ground level
    public static final Color UNDERGROUND_COLOR = new Color( 180, 158, 134 ); // tan
    
    // color of the Piccolo canvases
    public static final Color BIRDS_EYE_CANVAS_COLOR = new Color( 99, 173, 255 ); // sky blue
    public static final Color ZOOMED_CANVAS_COLOR = BIRDS_EYE_CANVAS_COLOR;
    
    // color of ice
    public static final Color ICE_CROSS_SECTION_COLOR = new Color( 232, 242, 252 ); // bluish white
    public static final Color ICE_SURFACE_COLOR = Color.WHITE;
    
    // main control panel color
    public static final Color CONTROL_PANEL_BACKGROUND_COLOR = new Color( 219, 255, 224 ); // pale green
    
    // colors for climate aspects
    public static final Color GLACIAL_BUDGET_COLOR = Color.RED;
    public static final Color ACCUMULATION_COLOR = Color.BLUE;
    public static final Color ABLATION_COLOR = Color.GREEN.darker();
    
    //----------------------------------------------------------------------------
    // Various components
    //----------------------------------------------------------------------------
    
    // Subpanels of the main control panel
    public static final Font SUBPANEL_TITLE_FONT = new PhetFont( Font.BOLD, 12 );
    public static final Font SUBPANEL_CONTROL_FONT = new PhetFont( Font.PLAIN, 12 );
    public static final Color SUBPANEL_BACKGROUND_COLOR = new Color( 82, 126, 90 ); // dark pastel green
    public static final Color SUBPANEL_TITLE_COLOR = Color.WHITE;
    public static final Color SUBPANEL_CONTROL_COLOR = Color.WHITE;
    
    // Coordinate axes
    public static final DoubleRange ELEVATION_AXIS_RANGE = new DoubleRange( 0, 7000 ); // meters
    public static final double ELEVATION_AXIS_TICK_SPACING = 500; // meters
    public static final double DISTANCE_AXIS_TICK_SPACING = 1000; // meters
}
