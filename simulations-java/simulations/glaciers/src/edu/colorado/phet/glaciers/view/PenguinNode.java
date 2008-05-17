/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PenguinNode is a node used to control the horizontal position of the zoomed viewport.
 * A penguin image is displayed, and can be dragged horizontally.
 * The location of the node is used to adjust the center of the zoomed viewport.
 * Horizontal dragging is limited such that the left and right edges of the zoomed viewport are
 * always within the birds-eye viewport.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PenguinNode extends PImage {
    
    private static final double X_UNDEFINED = -1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Viewport _birdsEyeViewport;
    private final Viewport _zoomedViewport;
    private final ModelViewTransform _mvt;
    private final double _zoomedViewportMaxCenterX;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor that allows the zoomed viewport to be dragged 
     * across the full width of the birds-eye viewport.
     */
    public PenguinNode( Viewport birdsEyeViewport, Viewport zoomedViewport, ModelViewTransform mvt ) {
        this( birdsEyeViewport, zoomedViewport, mvt, X_UNDEFINED );
    }
    
    /**
     * Constructor that constrains the center of the zoomed viewport
     * to be dragged up to some maximum x.
     * 
     * @param birdsEyeViewport
     * @param zoomedViewport
     * @param mvt
     * @param zoomedViewportMaxCenterX
     */
    public PenguinNode( Viewport birdsEyeViewport, Viewport zoomedViewport, ModelViewTransform mvt, double zoomedViewportMaxCenterX ) {
        super( GlaciersImages.PENGUIN );
        
        _birdsEyeViewport = birdsEyeViewport;
        _birdsEyeViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                updateScale();
                updateOffset();
            }
        });
        
        _zoomedViewport = zoomedViewport;
        _zoomedViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                updateOffset();
            }
        });
        
        _mvt = mvt;
        _zoomedViewportMaxCenterX = zoomedViewportMaxCenterX;
        
        addInputEventListener( new CursorHandler() );
        
        addInputEventListener( new PDragEventHandler() {

            private double _xOffset;

            protected void startDrag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                Rectangle2D rView = _mvt.modelToView( rModel  );
                _xOffset = event.getPosition().getX() - rView.getX();
                super.startDrag( event );
            }

            /*
             * Constrain dragging to horizontal, update the viewport, 
             * keep left and right edges of of zoomed viewport within the birds-eye viewport.
             */
            protected void drag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                Rectangle2D rView = _mvt.modelToView( rModel );
                double xView = event.getPosition().getX() - _xOffset;
                rView.setRect( xView, rView.getY(), rView.getWidth(), rView.getHeight() );
                rModel = _mvt.viewToModel( rView );
                Rectangle2D bb = _birdsEyeViewport.getBoundsReference();
                if ( rModel.getX() < bb.getX() ) {
                    /*
                     * Prevent dragging past left edge.
                     * The left edge is always the left edge of the of birds-eye viewport.
                     */
                    rModel.setRect( bb.getX(), rModel.getY(), rModel.getWidth(), rModel.getHeight() );
                }
                else {
                    /* 
                     * Prevent dragging past the right edge.
                     * the right edge may be either the right edge of the birds-eye viewport,
                     * or some arbitary maximum position for the center of the zoomed viewport.
                     */
                    double rightX = bb.getMaxX();
                    if ( _zoomedViewportMaxCenterX != X_UNDEFINED ) {
                        // we have an additional constraint on the right edge
                        rightX = Math.min( _zoomedViewportMaxCenterX + ( rModel.getWidth() / 2 ), rightX );
                    }
                    if ( rightX < rModel.getWidth() ) {
                        rModel.setRect( bb.getX(), rModel.getY(), rModel.getWidth(), rModel.getHeight() );
                    }
                    else if ( rModel.getMaxX() > rightX ) {
                        rModel.setRect( rightX - rModel.getWidth(), rModel.getY(), rModel.getWidth(), rModel.getHeight() );
                    }
                }
                _zoomedViewport.setBounds( rModel );
            }
        } );
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Centers the penguin at the bottom of the birds-eye viewport.
     */
    private void updateOffset() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double xOffset = rView.getCenterX() - ( getFullBoundsReference().getWidth() / 2 );
        double yOffset = _mvt.modelToView( _birdsEyeViewport.getBoundsReference() ).getMaxY() - getFullBoundsReference().getHeight();
        setOffset( xOffset, yOffset );
    }
    
    /*
     * Scales the penguin to fit into the birds-eye viewport.
     */
    private void updateScale() {
        setScale( 1 );
        final double portionOfViewportToFill = 0.75; // percent of birds-eye view height to be filled by the penguin
        double desiredHeight = portionOfViewportToFill * _mvt.modelToView( _birdsEyeViewport.getBoundsReference() ).getHeight();
        double penguinHeight = getFullBoundsReference().getHeight();
        double yScale = 1;
        if ( penguinHeight > desiredHeight ) {
            // scale the penguin down
            yScale = 1 - ( ( penguinHeight - desiredHeight ) / penguinHeight );
//            System.out.println( "PenguinNode.updateScale, scaling penguin down yScale=" + yScale + " ph=" + penguinHeight + " dh=" + desiredHeight );//XXX
        }
        else {
            // scale the penguin up
            yScale = 1 + ( ( desiredHeight - penguinHeight ) / desiredHeight );
//            System.out.println( "PenguinNode.updateScale, scaling penguin up yScale=" + yScale + " ph=" + penguinHeight + " dh=" + desiredHeight );//XXX
        }
        setScale( yScale );
    }
}
