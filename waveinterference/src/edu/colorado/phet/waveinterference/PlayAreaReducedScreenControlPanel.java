/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.ScreenNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 12:50:43 AM
 * Copyright (c) May 22, 2006 by Sam Reid
 */

public class PlayAreaReducedScreenControlPanel extends PhetPNode {
    private LightSimulationPanel lightSimulationPanel;
    private ScreenNode screenNode;
    private PhetPNode showButton;
    private PhetPNode closeButtonNode;

    public PlayAreaReducedScreenControlPanel( LightSimulationPanel lightSimulationPanel, final ScreenNode screenNode ) {
        this.lightSimulationPanel = lightSimulationPanel;
        this.screenNode = screenNode;
        JButton button = new JButton( "Show Screen" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setScreenEnabled( true );
            }
        } );
        showButton = new PhetPNode( new PSwing( lightSimulationPanel, button ) );
        addChild( showButton );

        lightSimulationPanel.getLatticeScreenCoordinates().addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        screenNode.addListener( new ScreenNode.Listener() {
            public void enabledStateChanged() {
                updateExpanded();
            }

        } );

        JButton closeButton = null;
        try {
//            closeButton = new JButton( "Hide Screen", new ImageIcon( ImageLoader.loadBufferedImage( "images/x-20.png" ) ) );
            closeButton = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/x-20.png" ) ) );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    screenNode.setScreenEnabled( false );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        closeButtonNode = new PhetPNode( new PSwing( lightSimulationPanel, closeButton ) );
        addChild( closeButtonNode );
        update();
        updateExpanded();
    }

    private void update() {
        showButton.setOffset( lightSimulationPanel.getLatticeScreenCoordinates().getScreenRect().getMaxX(), lightSimulationPanel.getLatticeScreenCoordinates().getScreenRect().getCenterY() );
//        closeButtonNode.setOffset( screenNode.getBrightnessScreenGraphic().getFullBounds().getCenterX(), screenNode.getBrightnessScreenGraphic().getFullBounds().getMaxY() - closeButtonNode.getFullBounds().getHeight() );
        closeButtonNode.setOffset( screenNode.getBrightnessScreenGraphic().getFullBounds().getCenterX(), screenNode.getBrightnessScreenGraphic().getFullBounds().getY() + 50 );
    }

    private void updateExpanded() {
        showButton.setVisible( !screenNode.isScreenEnabled() );
        closeButtonNode.setVisible( screenNode.isScreenEnabled() );
    }
}
