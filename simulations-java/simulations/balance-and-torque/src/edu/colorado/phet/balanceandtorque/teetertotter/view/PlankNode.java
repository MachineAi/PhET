// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for the plank.  Draws tick marks and provides handler for grabbing
 * the plank and setting its position.
 *
 * @author John Blanco
 */
public class PlankNode extends ModelObjectNode {
    private static final Stroke NORMAL_TICK_MARK_STROKE = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Stroke BOLD_TICK_MARK_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );

    public PlankNode( final ModelViewTransform mvt, final Plank plank ) {
        super( mvt, plank, new Color( 243, 203, 127 ) );

        // Create a layer for the tick marks and add the code to create and
        // update them.
        final PNode tickMarkLayer = new PNode();
        addChild( tickMarkLayer );
        plank.addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape rotatedPlankShape ) {
                // Update the tick marks by removing them and redrawing them.
                tickMarkLayer.removeAllChildren();
                // Add the tick marks.  The spacing should match that of the
                // plank's "snap to" locations.  The marks are created based
                // on the unrotated plank, and then rotated to match the
                // current orientation.
                for ( int i = 0; i < plank.getTickMarks().size(); i++ ) {
                    if ( i % 4 == 0 ) {
                        // Make some marks bold for easier placement of masses.
                        // The 'if' clause can be tweaked to put marks in
                        // different places.
                        tickMarkLayer.addChild( new PhetPPath( mvt.modelToView( plank.getTickMarks().get( i ) ), BOLD_TICK_MARK_STROKE, Color.BLACK ) );
                    }
                    else {
                        // Use the normal stroke.
                        tickMarkLayer.addChild( new PhetPPath( mvt.modelToView( plank.getTickMarks().get( i ) ), NORMAL_TICK_MARK_STROKE, Color.BLACK ) );
                    }
                }
            }
        } );

        // Create a layer for some grabbable "handles" that will allow the
        // user to manually change the angle of the plank.
        assert plank.getTiltAngle() == 0; // Tilt angle be zero for this to work.
        final PNode handleLayer = new PNode();
        addChild( handleLayer );
        // Only put the handles on the ends of the plank, otherwise things get
        // weird.  Note that the handles are invisible.
        Color handleColor = Color.pink;
        Rectangle2D plankBounds = plank.getShape().getBounds2D();
        final PNode rightHandle = new PhetPPath( new Rectangle2D.Double( mvt.modelToViewDeltaX( plankBounds.getWidth() / 4 ),
                                                                         -mvt.modelToViewDeltaY( plankBounds.getHeight() ),
                                                                         mvt.modelToViewDeltaX( plankBounds.getWidth() / 4 ),
                                                                         -mvt.modelToViewDeltaY( plankBounds.getHeight() ) ),
                                                 handleColor );
        rightHandle.setOffset( mvt.modelToView( plank.getPivotPoint() ) );
        rightHandle.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        handleLayer.addChild( rightHandle );

        final PNode leftHandle = new PhetPPath( new Rectangle2D.Double( -mvt.modelToViewDeltaX( plankBounds.getWidth() / 2 ),
                                                                        -mvt.modelToViewDeltaY( plankBounds.getHeight() ),
                                                                        mvt.modelToViewDeltaX( plankBounds.getWidth() / 4 ),
                                                                        -mvt.modelToViewDeltaY( plankBounds.getHeight() ) ),
                                                handleColor );
        leftHandle.setOffset( mvt.modelToView( plank.getPivotPoint() ) );
        leftHandle.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        handleLayer.addChild( leftHandle );

        plank.addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape rotatedPlankShape ) {
                // Rotate the handles to match the plank's angle.
                rightHandle.setRotation( -plank.getTiltAngle() );
                leftHandle.setRotation( -plank.getTiltAngle() );
            }
        } );
    }
}
