/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:24:07 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltagePanel extends PhetPCanvas {
    private TravoltageRootNode travoltageRootNode;
    private boolean createTrajectories = false;
    private MotionHelpBalloon motionHelpBalloon;

    public TravoltagePanel( TravoltageModule travoltageModule ) {
        travoltageRootNode = new TravoltageRootNode( travoltageModule, this, travoltageModule.getTravoltageModel() );
        addScreenChild( travoltageRootNode );

//        setCreateTrajectories();
        motionHelpBalloon = new MotionHelpBalloon( this, "<html>Rub the foot<br>on the carpet.</html> " );
        motionHelpBalloon.setBalloonVisible( true );
        motionHelpBalloon.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        getLayer().addChild( motionHelpBalloon );
    }

    private void setCreateTrajectories() {
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                System.out.print( e.getX() + ", " + e.getY() + ", " );
            }

            public void mouseReleased( MouseEvent e ) {
                System.out.println( e.getX() + ", " + e.getY() );
            }
        } );
        getTravoltageRootNode().getTravoltageBodyNode().getArmNode().setAngle( 0.0 );
        getTravoltageRootNode().getTravoltageBodyNode().getLegNode().setAngle( 0.0 );
        this.createTrajectories = true;
    }

    protected void sendInputEventToInputManager( InputEvent e, int type ) {
        if( createTrajectories ) {
            return;
        }
        super.sendInputEventToInputManager( e, type );
    }

    public TravoltageRootNode getTravoltageRootNode() {
        return travoltageRootNode;
    }

    public ElectronSetNode getElectronSetNode() {
        return travoltageRootNode.getElectronSetNode();
    }

    public void setSparkVisible( boolean b ) {
        travoltageRootNode.setSparkVisible( b );
    }

    public void showHelpBalloon() {
        motionHelpBalloon.animateTo( getTravoltageRootNode().getTravoltageBodyNode().getLegNode() );
    }
}
