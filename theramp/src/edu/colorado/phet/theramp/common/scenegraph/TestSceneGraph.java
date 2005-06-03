/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:33:20 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class TestSceneGraph {

    public static void main( String[] args ) {
        final SceneGraphPanel sceneGraphPanel = new SceneGraphPanel();
        sceneGraphPanel.getGraphic().setName( "root" );

//        AbstractGraphic userRoot = new TextGraphic( "MYTEXT!" );
//        userRoot.setName( "MyText!" );
//        userRoot.setFontLucidaSansBold( 32 );
//        userRoot.setCursorHand();
//        userRoot.addMouseListener( new Translator() );
//        userRoot.addMouseListener( new Rotator() );
//        userRoot.addMouseListener( new Repaint() );

//        GraphicListNode m2 = createMainTree();
//        m2.scale( 2, 2 );
//        sceneGraphPanel.addGraphic( m2 );

        GraphicListNode m3 = createMainTree();
        m3.scale( .5, .5 );
        m3.setComposite( true );
        m3.setCursorHand();
        m3.addMouseListener( new Translator() );
        m3.addMouseListener( new Rotator() );
        m3.addMouseListener( new Repaint() );
        sceneGraphPanel.addGraphic( m3 );

        m3.setLocation( new Point2D.Double( 100, 100 ) );
        m3.setRegistrationPoint( new Point2D.Double( -75, -75 ) );

        sceneGraphPanel.addMouseMotionListener( new MouseMotionAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                if( e.isShiftDown() ) {
                    sceneGraphPanel.getGraphic().rotate( Math.PI / 32, sceneGraphPanel.getWidth() / 2, sceneGraphPanel.getHeight() / 2 );
                    sceneGraphPanel.repaint();
                }
            }
        } );

        JFrame frame = new JFrame( "Test" );

//        SceneGraphJComponent.init( frame );
//        JButton jButton = new JButton( "text" );
//        AbstractGraphic sceneGraphJComponent = SceneGraphJComponent.newInstance( sceneGraphPanel, jButton );
//        sceneGraphPanel.addGraphic( sceneGraphJComponent );
//        sceneGraphJComponent.addMouseListener( new Repaint() );
//        sceneGraphJComponent.translate( 400, 400 );
//        sceneGraphJComponent.setAntialias( true );
//        sceneGraphJComponent.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
//        sceneGraphJComponent.setCursorHand();
//        sceneGraphJComponent.addMouseListener( new Translator() );
//        sceneGraphJComponent.addMouseListener( new Rotator() );
//
//        TextGraphic textGraphic = new TextGraphic( "Top-Level text" );
//        sceneGraphPanel.addGraphic( textGraphic );
//        textGraphic.addMouseListener( new Rotator() );
//        textGraphic.addMouseListener( new Translator() );
//        textGraphic.addMouseListener( new Repaint());

        frame.setContentPane( sceneGraphPanel );
        frame.setSize( 800, 800 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private static GraphicListNode createSimpleTree() {
        GraphicListNode graphicListNode = new GraphicListNode();
        graphicListNode.setFont( new Font( "Lucida Sans", Font.PLAIN, 36 ) );
        graphicListNode.addGraphic( new TextGraphic( "Hello" ) );
        return graphicListNode;
    }

    private static GraphicListNode createMainTree() {
        GraphicListNode mainTree = new GraphicListNode();
        mainTree.setName( "Main Tree" );

        TextGraphic textGraphic = new TextGraphic( "Test" );
        textGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 28 ) );
        textGraphic.setAntialias( true );
        mainTree.addGraphic( textGraphic );
        textGraphic.translate( 100, 100 );

        GraphicListNode list = new GraphicListNode();
        list.setName( "Blocks" );
        for( int i = 0; i < 10; i++ ) {
//            FillGraphic graphic = new FillGraphic( new Ellipse2D.Double( 0, 0, 20, 20 ) );
            FillGraphic graphic = new FillGraphic( new Rectangle( 20, 20 ) );
            graphic.setCursorHand();
            graphic.addMouseListener( new Translator() );
            graphic.addMouseListener( new Rotator() );
            graphic.addMouseListener( new Repaint() );

            graphic.setColor( randomColor() );
            graphic.translate( 10 + i * 30, 50 );
            list.addGraphic( graphic );
        }
        list.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        mainTree.addGraphic( list );

        textGraphic.addMouseListener( new Translator() );
        textGraphic.addMouseListener( new Rotator() );
        textGraphic.setCursorHand();
        textGraphic.addMouseListener( new Repaint() );

//        mainTree.scale( 2, 2 );

        try {
            ImageGraphic imageGraphic = new ImageGraphic( ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ) );
//            imageGraphic.addMouseListener( new CursorHand() );
            mainTree.setCursorHand();
            mainTree.addGraphic( imageGraphic );
            imageGraphic.translate( 10, 10 );
            imageGraphic.scale( 2, 2 );

            imageGraphic.rotate( Math.PI / 16 );
            imageGraphic.addMouseListener( new Translator() );
            imageGraphic.addMouseListener( new Rotator() );
            imageGraphic.addMouseListener( new Repaint() );
            imageGraphic.setCursorHand();
            imageGraphic.addMouseListener( new SceneGraphMouseAdapter() {
                public void mousePressed( SceneGraphMouseEvent event ) {
                    System.out.println( "Pressed: event = " + event );
                }
            } );
            imageGraphic.addMouseListener( new SceneGraphMouseAdapter() {
                public void mouseReleased( SceneGraphMouseEvent event ) {
                    System.out.println( "Released: event = " + event );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
//        sceneGraphPanel.getRootGraphic().rotate( Math.PI / 16 );


        return mainTree;
    }

    private static Color randomColor() {
        return new Color( (int)( Math.random() * 255 ), (int)( Math.random() * 255 ), (int)( Math.random() * 255 ) );
    }
}
