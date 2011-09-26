// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.fluidpressureandflow.watertower.model.Hose;

/**
 * @author Sam Reid
 */
public class HoseGeometry {

    public final ImmutableVector2D startPoint;
    public final ImmutableVector2D nozzleInput;
    public final ImmutableVector2D delta;
    public final ImmutableVector2D prePoint;
    public final ImmutableVector2D rightOfTower;
    public final ImmutableVector2D bottomLeft;

    public HoseGeometry( Hose hose ) {

        startPoint = new ImmutableVector2D( hose.attachmentPoint.get().getX(), hose.attachmentPoint.get().getY() + hose.holeSize / 2 );
        nozzleInput = hose.getNozzleInputPoint();
        delta = hose.getNozzleInputPoint().minus( hose.outputPoint.get() );
        prePoint = hose.getNozzleInputPoint().plus( delta );

        rightOfTower = new ImmutableVector2D( startPoint.getX() + 2, startPoint.getY() );
        bottomLeft = new ImmutableVector2D( rightOfTower.getX(), prePoint.getY() );
    }

    public ImmutableVector2D getHandlePoint() {
        return bottomLeft.plus( prePoint ).times( 0.5 );
    }
}
