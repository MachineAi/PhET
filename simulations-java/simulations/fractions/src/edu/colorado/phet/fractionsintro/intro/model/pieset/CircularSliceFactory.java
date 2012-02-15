// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public class CircularSliceFactory extends AbstractSliceFactory {

    public static final CircularSliceFactory CircularSliceFactory = new CircularSliceFactory();

    //Private, require users to use singleton
    private CircularSliceFactory() {}

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final Function1<Slice, Shape> toShape = new Function1<Slice, Shape>() {
        @Override public Shape apply( Slice slice ) {
            double epsilon = 1E-6;
            ImmutableVector2D tip = slice.tip;
            double radius = slice.radius;
            double angle = slice.angle;
            double extent = slice.extent;
            return extent >= Math.PI * 2 - epsilon ?
                   new Ellipse2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2 ) :
                   new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
        }
    };

    public final double pieDiameter = 155;
    public final double pieRadius = pieDiameter / 2;
    public final double pieSpacing = 10;

    //Put the pieces right in the center of the bucket hole.
    //They are pointing up so that when they rotate to align with the closest targets (the bottom ones) they don't have far to rotate, since the bottom targets are also pointing up
    public Slice createBucketSlice( int denominator ) {
        final double x = bucket.getHoleShape().getBounds2D().getCenterX() + bucket.getPosition().getX();
        final double y = -bucket.getHoleShape().getBounds2D().getCenterY() - bucket.getPosition().getY();

        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( x + ( random.nextDouble() * 2 - 1 ) * pieRadius, y - pieRadius / 2 ), 3 * Math.PI / 2 - anglePerSlice / 2, anglePerSlice, pieRadius, false, null, toShape );
    }

    public Slice createPieCell( int pie, int cell, int denominator ) {
        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( pieDiameter * ( pie + 1 ) + pieSpacing * ( pie + 1 ) - 80, 250 ), anglePerSlice * cell, anglePerSlice, pieDiameter / 2, false, null, toShape );
    }
}