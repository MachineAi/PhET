// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WaterDrop {
    public final Property<ImmutableVector2D> position;
    public final Property<ImmutableVector2D> velocity;
    private final double mass = 1;
    private final double gravity = -9.8;
    private ArrayList<SimpleObserver> removalListeners = new ArrayList<SimpleObserver>();
    private final double volume;

    public WaterDrop( ImmutableVector2D position, ImmutableVector2D velocity, double volume ) {
        this.volume = volume;
        this.position = new Property<ImmutableVector2D>( position );
        this.velocity = new Property<ImmutableVector2D>( velocity );
    }

    public void stepInTime( double simulationTimeChange ) {
        ImmutableVector2D force = new ImmutableVector2D( 0, mass * gravity );
        ImmutableVector2D acceleration = force.getScaledInstance( 1.0 / mass );
        velocity.setValue( acceleration.getScaledInstance( simulationTimeChange ).getAddedInstance( velocity.getValue() ) );
        position.setValue( velocity.getValue().getScaledInstance( simulationTimeChange ).getAddedInstance( position.getValue() ) );
    }

    public void addRemovalListener( SimpleObserver simpleObserver ) {
        removalListeners.add( simpleObserver );
    }

    public double getVolume() {
        return volume;
    }

    public void notifyRemoved() {
        for ( SimpleObserver removalListener : removalListeners ) {
            removalListener.update();
        }
    }

    //v = 4/3 pi * r^3
    public double getRadius() {
        return Math.pow( getVolume() * 3.0 / 4.0 / Math.PI, 1.0 / 3.0 );
    }

    public boolean contains( double x, double y ) {
        double r = getRadius();
        final Ellipse2D.Double shape = new Ellipse2D.Double( position.getValue().getX() - r, position.getValue().getY() - r, r * 2, r * 2 );
        return shape.contains( x, y );
    }
}
