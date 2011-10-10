// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a node that looks like a large finger, which can be
 * used to push down on things.
 */
public class PointingHandNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Width of the finger node as a proportion of the width of the particle
    // container.
    private static final double HAND_NODE_WIDTH_PROPORTION = 0.55;

    // Horizontal position of the node as function of the container width.
    private static final double NODE_X_POS_PROPORTION = 0.30;

    // File name of the primary image.
    public static final String PRIMARY_IMAGE = "finger-4.png";

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private PImage m_fingerImageNode;
    private MultipleParticleModel m_model;
    private double m_scale;
    private double m_mouseMovementAmount;
    private double m_containerSizeAtDragStart;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public PointingHandNode( MultipleParticleModel model ) {

        m_model = model;
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        double desiredHandWidth = containerRect.getWidth() * HAND_NODE_WIDTH_PROPORTION;

        // Listen to the model for notifications of changes to the container size.
        m_model.addListener( new MultipleParticleModel.Adapter() {
            public void containerSizeChanged() {
                handleContainerSizeChanged();
            }
        } );

        // Load and scale the image.
        m_fingerImageNode = StatesOfMatterResources.getImageNode( PRIMARY_IMAGE );
        m_scale = desiredHandWidth / m_fingerImageNode.getFullBoundsReference().width;
        m_fingerImageNode.scale( m_scale );

        // Set up a cursor handler so that the user will get an indication
        // that the node can be moved.
        m_fingerImageNode.setPickable( true );
        m_fingerImageNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );

        // Add the "hint" node that indicates to the user that this node can
        // be dragged up and down.  This is only shown when the user has
        // placed the mouse over the finger.
        final PNode hintNode = new MovementHintNode() {{
            // Position this off to the side of the finger node.  The hard-
            // coded values were empirically determined.
            setOffset( m_fingerImageNode.getFullBoundsReference().getMaxX() + 150,
                       m_fingerImageNode.getFullBoundsReference().getMaxY() - getFullBoundsReference().getHeight() - 1000 );
            setVisible( false );
        }};
        addChild( hintNode );

        // Create a listener that shows and hides the hint based on mouse over
        // events for the finger node.
        m_fingerImageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( PInputEvent event ) {
                hintNode.setVisible( true );
            }

            @Override public void mouseExited( PInputEvent event ) {
                hintNode.setVisible( false );
            }
        } );

        // Set ourself up to listen for and handle mouse dragging events.
        m_fingerImageNode.addInputEventListener( new PDragEventHandler() {

            public void startDrag( PInputEvent event ) {
                super.startDrag( event );
                handleMouseStartDragEvent( event );
            }

            public void drag( PInputEvent event ) {
                handleMouseDragEvent( event );
            }

            public void endDrag( PInputEvent event ) {
                super.endDrag( event );
                handleMouseEndDragEvent( event );
            }
        } );

        // Add the finger node as a child.
        addChild( m_fingerImageNode );

        // Set our initial offset.
        setOffset( containerRect.getX() + containerRect.getWidth() * NODE_X_POS_PROPORTION,
                   -getFullBoundsReference().height );
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void handleMouseDragEvent( PInputEvent event ) {

        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo( draggedNode );
        draggedNode.localToParent( d );
        m_mouseMovementAmount += d.getHeight();

        // Resize the container based on the amount that the node has moved.
        m_model.setTargetParticleContainerHeight( m_containerSizeAtDragStart - m_mouseMovementAmount );
    }

    private void handleMouseStartDragEvent( PInputEvent event ) {
        m_mouseMovementAmount = 0;
        m_containerSizeAtDragStart = m_model.getParticleContainerHeight();
    }

    private void handleMouseEndDragEvent( PInputEvent event ) {
        // Set the target size to the current size, which will stop any change
        // in size that is currently underway.
        m_model.setTargetParticleContainerHeight( m_model.getParticleContainerHeight() );
    }

    private void handleContainerSizeChanged() {
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        if ( !m_model.getContainerExploded() ) {
            setOffset( getFullBoundsReference().x,
                       StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT - containerRect.getHeight() -
                       getFullBoundsReference().height );
        }
        else {
            // If the container is exploding that hand is retracted more
            // quickly.
            setOffset( getFullBoundsReference().x,
                       StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT - ( containerRect.getHeight() * 2 ) -
                       getFullBoundsReference().height );
        }
    }

    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------

    /**
     * This class represents a node that is meant to make it clear to the user
     * that the hand can be moved up and down.
     */
    private static class MovementHintNode extends PNode {
        private static Color ARROW_COLOR = Color.GREEN;
        private static double ARROW_LENGTH = 1000;
        private static double ARROW_HEAD_WIDTH = 1000;
        private static double ARROW_HEAD_HEIGHT = 500;
        private static double ARROW_TAIL_WIDTH = 500;
        private static double DISTANCE_BETWEEN_ARROWS = 250;

        private MovementHintNode() {
            // Add the up arrow.
            addChild( new ArrowNode( new Point2D.Double( ARROW_HEAD_WIDTH / 2, ARROW_LENGTH ),
                                     new Point2D.Double( ARROW_HEAD_WIDTH / 2, 0 ),
                                     ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ) {{
                setPaint( ARROW_COLOR );
            }} );
            // Add the down arrow.
            addChild( new ArrowNode( new Point2D.Double( ARROW_HEAD_WIDTH / 2, ARROW_LENGTH + DISTANCE_BETWEEN_ARROWS ),
                                     new Point2D.Double( ARROW_HEAD_WIDTH / 2, ARROW_LENGTH * 2 + DISTANCE_BETWEEN_ARROWS ),
                                     ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ) {{
                setPaint( ARROW_COLOR );
            }} );
            // Make sure this node doesn't intercept mouse events.
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
