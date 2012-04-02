// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.pressure.model.TrapezoidPool;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor.WATER_DENSITY;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor.getBottomColor;

/**
 * Shows the water flowing from the faucet as a solid blue rectangle
 *
 * @author Sam Reid
 */
public class FlowingWaterNode extends PNode {
    public FlowingWaterNode( final TrapezoidPool pool, final Property<Double> flowRatePercentage, final ModelViewTransform transform ) {
        final double distanceToStickUpAboveTheGroundToGoBehindFaucet = 0.5;
        addChild( new PhetPPath( getBottomColor( WATER_DENSITY ) ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    double width = 0.6 * flowRatePercentage.get();//meters wide of the fluid flow
                    final Rectangle2D.Double shape = new Rectangle2D.Double( pool.centerAtLeftChamberOpening - width / 2, -pool.height + pool.getWaterHeight(), width, pool.height + distanceToStickUpAboveTheGroundToGoBehindFaucet - pool.getWaterHeight() );
                    setPathTo( transform.modelToView( shape ) );
                }
            }.observe( flowRatePercentage, pool.waterVolume );
        }} );
    }
}