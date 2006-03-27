/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 9:58:29 PM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

/**
 * Decorates with buttons and controls.
 */
public class IntensityReaderDecorator extends PNode {
    private ArrayList listeners = new ArrayList();
    private IntensityReader intensityReader;
    private PSwing buttonPSwing;
    private Point lastMovePoint = null;

    public IntensityReaderDecorator( final PSwingCanvas pSwingCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.intensityReader = new IntensityReader( waveModel, latticeScreenCoordinates );
        JButton options = new JButton( "Options" );
        final JPopupMenu jPopupMenu = new JPopupMenu( "Popup Menu" );
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem( "Display Readout", intensityReader.isReadoutVisible() );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityReader.setReadoutVisible( menuItem.isSelected() );
            }
        } );
        jPopupMenu.add( menuItem );
        jPopupMenu.addSeparator();
        JMenuItem deleteItem = new JMenuItem( "Delete" );
        deleteItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doDelete();
            }

        } );
        jPopupMenu.add( deleteItem );
        pSwingCanvas.addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
                lastMovePoint = e.getPoint();
            }

            public void mouseMoved( MouseEvent e ) {
                lastMovePoint = e.getPoint();
            }
        } );
        options.addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
            }

            public void mouseReleased( MouseEvent e ) {
                if( lastMovePoint != null ) {
                    jPopupMenu.show( pSwingCanvas, lastMovePoint.x, lastMovePoint.y );
                }
            }
        } );
        buttonPSwing = new PSwing( pSwingCanvas, options );
        addChild( intensityReader );
        addChild( buttonPSwing );
        intensityReader.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
    }

    private void doDelete() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.deleted();
        }
    }

    public static interface Listener {
        void deleted();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void updateLocation() {
        buttonPSwing.setOffset( intensityReader.getFullBounds().getX(), intensityReader.getFullBounds().getMaxY() );
    }

    public void update() {
        intensityReader.update();
    }
}
