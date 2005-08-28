/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Shows a Line connection between two PNodes.
 */

public class ConnectorGraphic extends PPath {

    private PNode source;
    private PNode destination;

    public ConnectorGraphic( PNode src, PNode dst ) {
        this.source = src;
        this.destination = dst;
        PropertyChangeListener changeHandler = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        };
        src.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeHandler );
        dst.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeHandler );
        update();
//        setPaint( Color.black );
        setStrokePaint( Color.black );
    }

    private void update() {
        connectRectsWithLine();
    }
    // This method connects the centers of two
// rectangle nodes with a line node. If you know that two nodes
// exist in the same coordinate system then you don't need to make
// all these conversions. This example assumes the most general case where
// they all exist in different coordinate systems.

    public void connectRectsWithLine() {

        // First get the center of each rectangle in the
        // local coordinate system of each rectangle.
        if( source == null || source.getFullBounds() == null ||
            destination == null || destination.getFullBounds() == null ||
            source.getParent() == null || destination.getParent() == null ) {
            return;
        }
        Point2D r1c = source.getFullBounds().getCenter2D();
        Point2D r2c = destination.getFullBounds().getCenter2D();

        // Next convert that center point for each rectangle
        // into global coordinate system.
        source.getParent().localToGlobal( r1c );
        destination.getParent().localToGlobal( r2c );

        // Now that the centers are in global coordinates they
        // can be converted into the local coordinate system
        // of the line node.
        globalToLocal( r1c );
        globalToLocal( r2c );

        // Finish by setting the endpoints of the line to
        // the center points of the rectangles, now that those
        // center points are in the local coordinate system of the line.
        updateShape( r1c, r2c );
        repaint();
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {
        setPathTo( new Line2D.Double( r1c, r2c ) );
    }

    public PNode getSource() {
        return source;
    }

    public PNode getDestination() {
        return destination;
    }
}
