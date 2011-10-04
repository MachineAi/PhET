// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.HANDLE_T;
import static edu.colorado.phet.fluidpressureandflow.flow.view.PipeFrontNode.EDGE_STROKE;

/**
 * Grab handle that lets the user translate the top or bottom of the pipe to deform the pipe.
 *
 * @author Sam Reid
 */
public class GrabHandle extends PNode {
    public GrabHandle( final ModelViewTransform transform, final PipeControlPoint controlPoint, final Function1<Point2D, Point2D> constraint, final boolean top ) {

        //Showing a handle image.
        //The original image is pointing up, flip it if this is a drag handle on the bottom of the pipe
        final BufferedImage sourceImage = BufferedImageUtils.multiScaleToWidth( HANDLE_T, 24 );
        BufferedImage image = top ? sourceImage : BufferedImageUtils.flipY( sourceImage );

        //Add the image
        addChild( new PImage( image ) {{
            controlPoint.point.addObserver( new SimpleObserver() {
                public void update() {

                    //REVIEW why does this use the pipe's EDGE_STROKE? seems like an odd coupling.
                    //Update the location on initialization and when the model changes
                    final ImmutableVector2D modelPoint = transform.modelToView( controlPoint.point.get() );
                    double dy = top ?
                                -getFullBounds().getHeight() - EDGE_STROKE.getLineWidth() :
                                EDGE_STROKE.getLineWidth();
                    setOffset( modelPoint.plus( -getFullBounds().getWidth() / 2, dy ).toPoint2D() );
                }
            } );

            //Make it draggable, but only up and down
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new RelativeDragHandler( this, transform, controlPoint.point, constraint ) );
        }} );
    }
}
