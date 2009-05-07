/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.45; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control relative sizes and placements of major items on\
    // the canvas.
    private final double BACKGROUND_HEIGHT_PROPORTION = 0.7;     // Vertical fraction of canvas for background.
    private final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.5; // Fraction of canvas for proportions chart.
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private RadioactiveDatingGameModel _model;
    private PNode _backgroundImageLayer;
    private PNode _backgroundImage;
    private NuclearDecayProportionChart _proportionsChart;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel) {
        
        _model = radioactiveDatingGameModel;
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Create the layer where the background will be placed.
        _backgroundImageLayer = new PNode();
        addScreenChild(_backgroundImageLayer);
        
        // Load the background image.
        BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "dating-game-background.png" );
        _backgroundImage = new PImage( bufferedImage );
        _backgroundImageLayer.addChild( _backgroundImage );
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart.Builder(Carbon14Nucleus.HALF_LIFE * 3.2, 
        		Carbon14Nucleus.HALF_LIFE, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL, 
        		NuclearPhysicsConstants.CARBON_COLOR).
        		postDecayElementLabel(NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL).
        		postDecayLabelColor(NuclearPhysicsConstants.NITROGEN_COLOR).
        		pieChartEnabled(false).
        		showPostDecayCurve(false).
        		timeMarkerLabelEnabled(true).
        		build();
        addScreenChild(_proportionsChart);
        
        // Add decay curve to chart.
        drawDecayCurveOnChart();
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    protected void updateLayout(){

    	if ( getWidth() > 0 && getHeight() > 0){
    		resizeAndPositionNodes( getWidth(), getHeight() );
    	}
    }
    
    private void resizeAndPositionNodes( double newWidth, double newHeight ){
    	
    	// TODO: Really crude workaround for a resizing issue.  Get rid of this soon.
    	if (newWidth == 919){
    		return;
    	}
    	
        // Reload and scale the background image.  This is necessary (I think)
    	// because PNodes can't be scaled differently in the x and y
    	// dimensions, and we want to be able to handle the case where the
    	// user changes the aspect ratio.
    	_backgroundImageLayer.removeChild( _backgroundImage );
    	BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "dating-game-background.png" );
        double xScale = newWidth / (double)bufferedImage.getWidth();
        double yScale = BACKGROUND_HEIGHT_PROPORTION * newHeight / (double)bufferedImage.getHeight();
        bufferedImage = BufferedImageUtils.rescaleFractional(bufferedImage, xScale, yScale);
        _backgroundImage = new PImage( bufferedImage );
        _backgroundImageLayer.addChild( _backgroundImage );
        
        // Size and locate the proportions chart.
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, newWidth * PROPORTIONS_CHART_WIDTH_FRACTION,
        		( newHeight - _backgroundImage.getFullBoundsReference().height ) * 0.95 ) );
        
        _proportionsChart.setOffset(newWidth / 2 - _proportionsChart.getFullBoundsReference().width / 2,
        		_backgroundImage.getFullBoundsReference().height + 3);
    }
    
    /**
     * Set up the chart to show the appropriate decay curve.
     */
    private void drawDecayCurveOnChart(){
    	_proportionsChart.clear();
    	double timeSpan = Carbon14Nucleus.HALF_LIFE * 3;
    	double timeIncrement = timeSpan / 500;
    	double lambda = Math.log(2)/Carbon14Nucleus.HALF_LIFE;
    	for ( double time = 0; time < timeSpan; time += timeIncrement ){
    		// Calculate the proportion of carbon that should be decayed at this point in time.
    		double percentageDecayed = 100 - (100 * Math.exp(-time*lambda));
    		_proportionsChart.addDecayEvent(time, percentageDecayed);
    	}
    }
}