/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.ec3.view.SplineMatch;
import edu.colorado.phet.piccolo.PanZoomWorldKeyHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:51 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Canvas extends PhetPCanvas {
    private EC3Module ec3Module;
    private EnergyConservationModel ec3Model;
    private HashMap pressedKeys = new HashMap();
    private EC3RootNode rootNode;

    private static final Object DUMMY_VALUE = new Object();
    public static final int NUM_CUBIC_SPLINE_SEGMENTS = 30;

    public EC3Canvas( EC3Module ec3Module ) {
        super( new Dimension( 942, 723 ) );
        this.ec3Module = ec3Module;
        this.ec3Model = ec3Module.getEnergyConservationModel();
        this.rootNode = new EC3RootNode( ec3Module, this );
        setPhetRootNode( rootNode );
        addFocusRequest();
        addKeyHandling();
        addKeyListener( new PanZoomWorldKeyHandler( this ) );
        addThrust();
//        addMeasuringTape();
        addGraphicsUpdate( ec3Module );
    }

    private void addGraphicsUpdate( EC3Module ec3Module ) {
        ec3Module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                updateWorldGraphics();
            }
        } );
    }

    private void addFocusRequest() {
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
    }

    private void addKeyHandling() {
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                EC3Canvas.this.keyPressed( e );
            }

            public void keyReleased( KeyEvent e ) {
                EC3Canvas.this.keyReleased( e );
            }

            public void keyTyped( KeyEvent e ) {
                EC3Canvas.this.keyTyped( e );
            }
        } );
    }

    private void addThrust() {
        ec3Model.addEnergyModelListener( new EnergyConservationModel.EnergyModelListener() {
            public void preStep( double dt ) {
                updateThrust();
            }
        } );
    }

    private void updateWorldGraphics() {
        redrawAllGraphics();
    }

    private void debugScreenSize() {
        System.out.println( "getSize( ) = " + getSize() );
    }

    public void clearBuses() {
        rootNode.clearBuses();
    }

    private void addBuses() {
        rootNode.addBuses();
    }

    private void updateThrust() {
        Body body = ec3Model.bodyAt( 0 );
        double xThrust = 0.0;
        double yThrust = 0.0;
        int thrustValue = 5000;
        if( pressedKeys.containsKey( new Integer( KeyEvent.VK_RIGHT ) ) ) {
            xThrust = thrustValue;
        }
        else if( pressedKeys.containsKey( new Integer( KeyEvent.VK_LEFT ) ) ) {
            xThrust = -thrustValue;
        }
        if( pressedKeys.containsKey( new Integer( KeyEvent.VK_UP ) ) ) {
            yThrust = -thrustValue;
        }
        else if( pressedKeys.containsKey( new Integer( KeyEvent.VK_DOWN ) ) ) {
            yThrust = thrustValue;
        }
        body.setThrust( xThrust, yThrust );
    }

    public void addSplineGraphic( SplineGraphic splineGraphic ) {
        rootNode.addSplineGraphic( splineGraphic );
    }

    private void addSkater() {
        Body body = new Body( Body.createDefaultBodyRect() );
        body.setPosition( 100, 0 );
        ec3Model.addBody( body );

        BodyGraphic bodyGraphic = new BodyGraphic( ec3Module, body );
        addBodyGraphic( bodyGraphic );
    }

    public void addBodyGraphic( BodyGraphic bodyGraphic ) {
        rootNode.addBodyGraphic( bodyGraphic );
    }

    private void toggleBox() {
        rootNode.toggleBox();
    }

    private void printControlPoints() {
        ec3Model.splineSurfaceAt( 0 ).printControlPointCode();
    }

    int threshold = 100;

    public SplineMatch proposeMatch( SplineGraphic splineGraphic, final Point2D toMatch ) {

        ArrayList matches = new ArrayList();

        for( int i = 0; i < numSplineGraphics(); i++ ) {
            SplineGraphic target = splineGraphicAt( i );
            PNode startNode = target.getControlPointGraphic( 0 );
            double dist = distance( toMatch, startNode );

            if( dist < threshold && ( splineGraphic != target ) ) {
                SplineMatch match = new SplineMatch( target, 0 );
                matches.add( match );
            }

            PNode endNode = target.getControlPointGraphic( target.numControlPointGraphics() - 1 );
            double distEnd = distance( toMatch, endNode );
            if( distEnd < threshold && splineGraphic != target ) {
                SplineMatch match = new SplineMatch( target, target.numControlPointGraphics() - 1 );
                matches.add( match );
            }
        }
        Collections.sort( matches, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                SplineMatch a = (SplineMatch)o1;
                SplineMatch b = (SplineMatch)o2;
                return Double.compare( distance( toMatch, a.getTarget() ), distance( toMatch, b.getTarget() ) );
            }
        } );
        if( matches.size() == 0 ) {
            return null;
        }
        return (SplineMatch)matches.get( 0 );
    }

    private double distance( Point2D toMatch, PNode startNode ) {
        double dist = startNode.getFullBounds().getCenter2D().distance( toMatch );
        return dist;
    }

    private SplineGraphic splineGraphicAt( int i ) {
        return rootNode.splineGraphicAt( i );
    }

    private int numSplineGraphics() {
        return rootNode.numSplineGraphics();
    }

    public void attach( SplineGraphic splineGraphic, int index, SplineMatch match ) {
        //delete both of those, add one new parent.
        removeSpline( splineGraphic );
        removeSpline( match.getSplineGraphic() );

        AbstractSpline spline = new CubicSpline( NUM_CUBIC_SPLINE_SEGMENTS );
        AbstractSpline a = splineGraphic.getSplineSurface().getTop();
        AbstractSpline b = match.getTopSplineMatch();
        if( index == 0 ) {
            for( int i = a.numControlPoints() - 1; i >= 0; i-- ) {
                spline.addControlPoint( a.controlPointAt( i ) );
            }
        }
        else {
            for( int i = 0; i < a.numControlPoints(); i++ ) {
                spline.addControlPoint( a.controlPointAt( i ) );
            }
        }
        if( match.matchesBeginning() ) {
            for( int i = 1; i < b.numControlPoints(); i++ ) {
                spline.addControlPoint( b.controlPointAt( i ) );
            }
        }
        else if( match.matchesEnd() ) {
            for( int i = b.numControlPoints() - 2; i >= 0; i-- ) {
                spline.addControlPoint( b.controlPointAt( i ) );
            }
        }
//        AbstractSpline reverse = spline.createReverseSpline();
        SplineSurface surface = new SplineSurface( spline );
        ec3Model.addSplineSurface( surface );
        addSplineGraphic( new SplineGraphic( this, surface ) );
    }

    private void removeSplineGraphic( SplineGraphic splineGraphic ) {
        rootNode.removeSplineGraphic( splineGraphic );
    }

    public EnergyConservationModel getEnergyConservationModel() {
        return ec3Model;
    }

    public void removeSpline( SplineGraphic splineGraphic ) {
        removeSplineGraphic( splineGraphic );
        ec3Model.removeSplineSurface( splineGraphic.getSplineSurface() );
//        ec3Model.removeSpline( splineGraphic.getSpline() );
//        ec3Model.removeSpline( splineGraphic.getReverseSpline() );
    }

    public EC3Module getEnergyConservationModule() {
        return ec3Module;
    }

    public void reset() {
        rootNode.reset();
        pressedKeys.clear();
    }

    public void keyPressed( KeyEvent e ) {
        pressedKeys.put( new Integer( e.getKeyCode() ), DUMMY_VALUE );
        if( e.getKeyCode() == KeyEvent.VK_P ) {
            System.out.println( "spline.getSegmentPath().getLength() = " + ec3Model.splineSurfaceAt( 0 ).getLength() );
            printControlPoints();
        }
        else if( e.getKeyCode() == KeyEvent.VK_B ) {
            toggleBox();
        }
        else if( e.getKeyCode() == KeyEvent.VK_A ) {
            addSkater();
        }
        else if( e.getKeyCode() == KeyEvent.VK_J ) {
            addBuses();
        }
        else if( e.getKeyCode() == KeyEvent.VK_D ) {
            debugScreenSize();
        }
    }

    public void keyReleased( KeyEvent e ) {
        pressedKeys.remove( new Integer( e.getKeyCode() ) );
    }

    public void keyTyped( KeyEvent e ) {
    }

    public void redrawAllGraphics() {
        rootNode.updateGraphics();
    }

    public boolean isMeasuringTapeVisible() {
        return rootNode.isMeasuringTapeVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        rootNode.setMeasuringTapeVisible( selected );
    }

    public void initPieGraphic() {
        rootNode.initPieChart();
    }

    public boolean isPieChartVisible() {
        return rootNode.isPieChartVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        rootNode.setPieChartVisible( selected );
    }
}
