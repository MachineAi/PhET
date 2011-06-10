// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * A specialized wire, found in all of our circuits.
 * It connects the bottom of a battery (B) to the bottoms of N parallel capacitors (C1,C2,...,Cn).
 * <code>
 * B     C1    C2       Cn
 * |     |      |       |
 * |-----|------|--...--|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBatteryBottomToCapacitorBottoms extends Wire {

    private final Battery battery;
    private final ArrayList<Capacitor> capacitors;

    /**
     * Constructor
     *
     * @param mvt        model-view transform
     * @param thickness  thickness of the wire, in meters
     * @param wireExtent how far the wire extends below the capacitors, in meters
     * @param battery
     * @param capacitors
     */
    public WireBatteryBottomToCapacitorBottoms( CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, Capacitor... capacitors ) {
        this( mvt, thickness, wireExtent, battery, new ArrayList<Capacitor>( Arrays.asList( capacitors ) ) );
    }

    // Same as above constructor, but with list of capacitors instead of varargs.
    public WireBatteryBottomToCapacitorBottoms( CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, ArrayList<Capacitor> capacitors ) {
        super( mvt, thickness );

        this.battery = battery;
        this.capacitors = capacitors;

        // determine max Y
        double maxY = capacitors.get( 0 ).getY();
        for ( Capacitor capacitor : capacitors ) {
            maxY = Math.max( maxY, capacitor.getLocation().getY() + wireExtent );
        }

        // horizontal segment connecting battery (B) to the rightmost capacitor (Cn)
        final Capacitor rightmostCapacitor = capacitors.get( capacitors.size() - 1 );
        final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), maxY );
        final Point2D.Double rightCorner = new Point2D.Double( rightmostCapacitor.getX(), leftCorner.getY() );
        final double t = getCornerOffset(); // for proper connection at corners with wire stroke end style
        addSegment( new BatteryBottomWireSegment( battery, getEndOffset(), leftCorner ) );
        addSegment( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
        addSegment( new CapacitorBottomWireSegment( rightmostCapacitor, rightCorner ) );

        // add vertical segments for all capacitors (C1...Cn-1) in between the battery (B) and rightmost capacitor (Cn)
        for ( int i = 0; i < capacitors.size() - 1; i++ ) {
            Capacitor capacitor = capacitors.get( i );
            Point2D.Double startPoint = new Point2D.Double( capacitor.getX(), maxY );
            addSegment( new CapacitorBottomWireSegment( capacitor, startPoint ) );
        }

        setShape( createShape() );
    }

    // Subtract any part of the wire that is occluded by the battery or one of the bottom plates.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // Null checks required because createShape is called in the superclass constructor.
        if ( battery != null && capacitors != null ) {
            wireShape = ShapeUtils.subtract( wireShape, battery.getShapeCreator().createBodyShape() );
            for ( Capacitor capacitor : capacitors ) {
                wireShape = ShapeUtils.subtract( wireShape, capacitor.getShapeCreator().createBottomPlateShape() );
            }
        }
        return wireShape;
    }
}