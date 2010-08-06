/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for pH paper.
 * Origin is at top center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaper extends SolutionRepresentation {
    
    private static final boolean ANIMATE_COLOR = false;
    
    /*
     * When solution is changed, we animate the dipped color.
     * This constant determines how much the pH value changes per unit of clock time.
     */
    private static final double PH_DELTA_PER_TIME_UNIT = 0.1;
    
    private final PDimension size;
    private final Beaker beaker;
    private double dippedHeight;
    private double pHValueShown; // current pH value shown by the paper, from dipping in the solution
    private final EventListenerList listeners;
    private final PHColorStrategy colorStrategy;

    public PHPaper( AqueousSolution solution, Point2D location, boolean visible, PDimension size, Beaker beaker ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
        this.beaker = beaker;
        this.listeners = new EventListenerList();
        this.colorStrategy = new DiscreteColorStrategy();
        dippedHeight = getSubmergedHeight();
        pHValueShown = solution.getPH();
    }
    
    public PDimension getSizeReference() {
        return size;
    }

    public double getWidth() {
        return size.getWidth();
    }

    public double getHeight() {
        return size.getHeight();
    }
    
    @Override
    public void setSolution( AqueousSolution solution ) {
        super.setSolution( solution );
        setDippedHeight( getSubmergedHeight() ); // Clear any dipped color on the paper above the solution.
        if ( !ANIMATE_COLOR ) {
            pHValueShown = solution.getPH();
            fireDippedColorChanged();
        }
    }

    @Override
    public void setLocation( double x, double y ) {
        super.setLocation( constrainX( x ), constrainY( y ) );
        setDippedHeight( Math.max( dippedHeight, getSubmergedHeight() ) ); // dipped height can only increase
    }

    /*
     * Constrains an x coordinate to be between the walls of the beaker.
     */
    private double constrainX( double requestedX ) {
        double min = beaker.getLocationReference().getX() - ( beaker.getWidth() / 2 ) + ( this.getWidth() / 2 );
        double max = beaker.getLocationReference().getX() + ( beaker.getWidth() / 2 ) - ( this.getWidth() / 2 );
        double x = requestedX;
        if ( x < min ) {
            x = min;
        }
        else if ( x > max ) {
            x = max;
        }
        return x;
    }
    
    /*
     * Constraints a y coordinate to be in or slightly above the solution.
     */
    private double constrainY( double requestedY ) {
        double min = beaker.getLocationReference().getY() - beaker.getHeight() - this.getHeight() - 20;
        double max = beaker.getLocationReference().getY() - this.getHeight() - 20;
        double y = requestedY;
        if ( y < min ) {
            y = min;
        }
        else if ( y > max ) {
            y = max;
        }
        return y;
    }
    
    /**
     * Gets the original color of the paper, before it's dipped in solution.
     * @return
     */
    public Color getPaperColor() {
        return ABSColors.PH_PAPER_COLOR;
    }
    
    /**
     * Gets the color of the paper when it's dipped in solution.
     * @return
     */
    public Color getDippedColor() {
        return createColor( pHValueShown );
    }
    
    /**
     * Creates a color that corresponds to a specific pH.
     * @param pH
     * @return
     */
    public Color createColor( double pH ) {
        return colorStrategy.createColor( pH );
    }
    
    private void setDippedHeight( double dippedHeight ) {
        if ( dippedHeight != this.dippedHeight ) {
            this.dippedHeight = dippedHeight;
            if ( getDippedHeight() == 0 ) {
                pHValueShown = getSolution().getPH(); // If the paper isn't dipped, no need to animate, go directly to final pH.
            }
            fireDippedHeightChanged();
        }
    }
    
    /**
     * Gets the height of the portion of the paper that has been dipped in the solution.
     */
    public double getDippedHeight() {
        return dippedHeight;
    }

    /*
     * Gets the height of the portion of the paper that's submerged in the solution.
     */
    private double getSubmergedHeight() {
        double by = beaker.getY();
        double py = getY();
        double bh = beaker.getHeight();
        double ph = getHeight();
        double h = -Math.abs( by - py ) + bh + ph;
        if ( h < 0 ) {
            h = 0;
        }
        else if ( h > ph ) {
            h = ph;
        }
        return h;
    }

    /**
     * Our magic pH paper animates its color when the solution is changed.
     * This animation happens gradually over time, changing incrementally each time the clock ticks.
     * @param simulationTimeChange
     */
    public void clockTicked( double simulationTimeChange ) {
        if ( pHValueShown != getSolution().getPH() ) {
            double sign = ( getSolution().getPH() - pHValueShown ) > 0 ? +1 : -1; // unit step towards target
            if ( Math.abs( pHValueShown - getSolution().getPH() ) < sign ) { // close enough, go directly to the target value
                pHValueShown = getSolution().getPH();
            }
            else {
                pHValueShown = pHValueShown + ( sign * simulationTimeChange * PH_DELTA_PER_TIME_UNIT );
            }
            fireDippedColorChanged();
        }
    }
    
    public interface PHPaperChangeListener extends EventListener {
        public void dippedColorChanged();
        public void dippedHeightChanged();
    }
    
    public void addPHPaperChangeListener( PHPaperChangeListener listener ) {
        listeners.add( PHPaperChangeListener.class, listener );
    }
    
    public void removePHPaperChangeListener( PHPaperChangeListener listener ) {
        listeners.remove( PHPaperChangeListener.class, listener );
    }
    
    private void fireDippedColorChanged() {
        for ( PHPaperChangeListener listener : listeners.getListeners( PHPaperChangeListener.class ) ) {
            listener.dippedColorChanged();
        }
    }
    
    private void fireDippedHeightChanged() {
        for ( PHPaperChangeListener listener : listeners.getListeners( PHPaperChangeListener.class ) ) {
            listener.dippedHeightChanged();
        }
    }
    
    private interface PHColorStrategy {
        public Color createColor( double pH );
    }
    
    /*
     * Maps a pH value to a color in the visible light spectrum.
     * We use visible wavelength as an intermediate representation,
     * then map the wavelength to a color using VisibleColor from phetcommon.
     */
    private static class VisibleSpectrumStrategy implements PHColorStrategy {
        
        private static final LinearFunction MAPPING_FUNCTION = new Function.LinearFunction( ABSConstants.MIN_PH, ABSConstants.MAX_PH, VisibleColor.MAX_WAVELENGTH, VisibleColor.MIN_WAVELENGTH );
        
        public Color createColor( double pH ) {
            double wavelength = MAPPING_FUNCTION.evaluate( pH );
            return new VisibleColor( wavelength ); 
        }
    }
    
    /*
     * Maps a pH value to a specific discrete color.
     * There is a limited number of colors that correspond to integer pH values.
     * When creating a color, the actual pH is truncated to an integer value.
     */
    private static class DiscreteColorStrategy implements PHColorStrategy {
        
        // colors as shown in acid-base-solutions/doc/pH-colors.png
        private static final Color[] COLORS = { 
            new Color( 182, 70, 72 ),  // 0
            new Color( 196, 80, 86 ),  // 1
            new Color( 213, 83, 71 ),  // 2
            new Color( 237, 123, 83 ), // 3
            new Color( 246, 152, 86 ), // 4
            new Color( 244, 158, 79 ), // 5 
            new Color( 243, 160, 78 ), // 6
            new Color( 244, 182, 67 ), // 7
            new Color( 231, 201, 75 ), // 8
            new Color( 93, 118, 88),   // 9
            new Color( 30, 92, 89 ),   // 10
            new Color( 34, 90, 105 ),  // 11
            new Color( 39, 87, 111 ),  // 12
            new Color( 27, 67, 90 ),   // 13
            new Color( 0, 34, 52 ) };  // 14
        
        public Color createColor( double pH ) {
            assert( COLORS.length == ( ABSConstants.MAX_PH - ABSConstants.MIN_PH + 1 ) );
            return COLORS[ (int)pH ];
        }
    }
}
