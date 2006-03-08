/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.boundstates.BSConstants;


/**
 * BSSquareWell
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWell extends BSAbstractWell {

    public BSSquareWell() {
        this( 1 );
    }
    
    public BSSquareWell( int numberOfWells ) {
        super( numberOfWells,
                BSConstants.DEFAULT_SQUARE_WELL_SPACING, 
                BSConstants.DEFAULT_SQUARE_WELL_WIDTH, 
                BSConstants.DEFAULT_SQUARE_WELL_DEPTH, 
                BSConstants.DEFAULT_WELL_OFFSET,
                BSConstants.DEFAULT_WELL_CENTER);
    }
    
    public BSSquareWell( double width, double depth, double offset, double center ) {
        super( width, depth, offset, center );
    }
    
    public BSSquareWell( int numberOfWells, double spacing, double width, double depth, double offset, double center ) {
        super( numberOfWells, spacing, width, depth, offset, center );
    }
    
    //XXX Hack -- creates 10 eigenstates between offset and depth
    public BSEigenstate[] getEigenstates() {
        final int numberOfEigenstates = 10;
        BSEigenstate[] eigenstates = new BSEigenstate[ numberOfEigenstates ];
        final double maxEnergy = getOffset();
        final double minEnergy = getOffset() + getDepth();
        final double deltaEnergy = ( maxEnergy - minEnergy ) / numberOfEigenstates;
        for ( int i = 0; i < eigenstates.length; i++ ) {
            eigenstates[i] = new BSEigenstate( minEnergy + ( i * deltaEnergy ) );
        }
        return eigenstates;
    }
    
    /**
     * Gets the points that approximate this well.
     * For a square well, we don't need many points.
     * So we ignore dx and piece together and calculate the points
     * that define each straight line segment.
     * 
     * @param minX
     * @param maxX
     * @param dx
     */
    public Point2D[] getPoints( double minX, double maxX, double dx ) {
        
        final int numberOfWells = getNumberOfWells();
        final double spacing = getSpacing();
        final double offset = getOffset();
        final double width = getWidth();
        final double depth = getDepth();
        final double center = getCenter();
        
        ArrayList points = new ArrayList(); // array of Point2D
        
        // Calculate points the left edge of first well at x=0...
        final int numberOfPoints = ( numberOfWells * 4 ) + 2;
        points.add( new Point2D.Double( -width, getOffset() ) );
        for ( int i = 0; i < numberOfWells; i++ ) {
            Point2D p1 = new Point2D.Double( i * spacing, offset );
            Point2D p2 = new Point2D.Double( i * spacing, offset + depth );
            Point2D p3 = new Point2D.Double( p1.getX() + width, offset + depth );
            Point2D p4 = new Point2D.Double( p1.getX() + width, offset );
            points.add( p1 );
            points.add( p2 );
            points.add( p3 );
            points.add( p4 );
        }
        Point2D pLast = (Point2D) points.get( points.size() - 1 );
        points.add( new Point2D.Double( pLast.getX() + width, getOffset() ) );
        
        // Adjust all points to account for the center...
        double adjustX = 0;
        if ( numberOfWells == 1 ) {
            adjustX = width / 2;
        }
        else if ( numberOfWells % 2 == 0 ) {
            // Even number of wells has center between wells
            adjustX = ( ( numberOfWells / 2 ) * spacing ) - ( (spacing - width) / 2 );
        }
        else {
            // Odd number of wells has center in the middle well
            adjustX = ( (int)(numberOfWells / 2 ) * spacing ) + ( width / 2 );
        }
        adjustX -= center;
        Iterator i = points.iterator();
        while ( i.hasNext() ) {
            Point2D p = (Point2D) i.next();
            p.setLocation( p.getX() - adjustX, p.getY() );
        }
        
        // Adjust x of the first and last points to fit our range...
        Point2D pMin = (Point2D) points.get(0);
        Point2D pMax = (Point2D) points.get( points.size() - 1 );
        if ( pMin.getX() > minX ) {
            pMin.setLocation( minX, pMin.getY() );
        }
        if ( pMax.getX() < maxX ) {
            pMax.setLocation( maxX, pMax.getY() );
        }
        
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[ points.size() ] );
    }
}
