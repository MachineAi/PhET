/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.common.Legend;
import edu.colorado.phet.ec3.common.MeasuringTape;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.view.*;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class EC3RootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    private PNode buses;
    private EC3Module ec3Module;
    private EC3Canvas ec3Canvas;
    private PNode historyGraphics = new PNode();
    private MeasuringTape measuringTape;
    private static final boolean DEFAULT_TAPE_VISIBLE = false;
    private static final boolean DEFAULT_PIE_CHART_VISIBLE = false;
    private PNode pieCharts = new PNode();
    private OffscreenManIndicator offscreenManIndicator;
    private boolean ignoreThermal = true;
    private PauseIndicator pauseIndicator;
    private Legend legend;
    private PNode screenBackground;
    private Image backgroundImage;
    private SplineToolbox splineToolbox;
    private PNode toolboxPlaceholder;
//    private Planet lastPlanet = null;
    private FloorGraphic floorGraphic;
    private ZeroPointPotentialGraphic zeroPointPotentialGraphic;

    public EC3RootNode( EC3Module ec3Module, EC3Canvas ec3Canvas ) {
        this.ec3Module = ec3Module;
        this.ec3Canvas = ec3Canvas;
        EnergyConservationModel ec3Model = getModel();
        Floor floor = ec3Model.floorAt( 0 );

        ec3Canvas.setBackground( new Color( 170, 200, 220 ) );
        toolboxPlaceholder = new PNode();
        screenBackground = new PNode();
//        screenBackground.addChild( new PPath( new Ellipse2D.Double( 50, 50, 300, 300 ) ) );
        splineToolbox = new SplineToolbox( ec3Canvas, this );

        double coordScale = 1.0 / 1.0;
        measuringTape = new MeasuringTape( coordScale, new Point2D.Double( 100, 100 ), bodyGraphics );//any world node should do here, no?
        pauseIndicator = new PauseIndicator( ec3Module, ec3Canvas, this );
        legend = new EC3Legend( ec3Module );
        floorGraphic = new FloorGraphic( floor );
        zeroPointPotentialGraphic = new ZeroPointPotentialGraphic( ec3Canvas );
        offscreenManIndicator = new OffscreenManIndicator( ec3Canvas, ec3Module, numBodyGraphics() > 0 ? bodyGraphicAt( 0 ) : null );

        addScreenChild( screenBackground );
        addScreenChild( splineToolbox );
        addWorldChild( floorGraphic );
        addWorldChild( splineGraphics );
        addWorldChild( bodyGraphics );
        addWorldChild( historyGraphics );
        addScreenChild( measuringTape );
        addScreenChild( pieCharts );
        addScreenChild( pauseIndicator );
        addScreenChild( legend );
        addWorldChild( toolboxPlaceholder );
        addScreenChild( zeroPointPotentialGraphic );
        addScreenChild( offscreenManIndicator );

        resetDefaults();
        ec3Canvas.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                updateBackgroundImage();
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );
        setZeroPointVisible( false );
    }

    public PNode getToolboxPlaceholder() {
        return toolboxPlaceholder;
    }


    public PNode getBackground() {
        return screenBackground;
    }

    public void setBackground( Image image ) {
        if( this.backgroundImage != image ) {
            this.backgroundImage = image;
            updateBackgroundImage();
        }
    }

    private void updateBackgroundImage() {
        if( backgroundImage == null ) {
            screenBackground.removeAllChildren();
        }
        else {
//            System.out.println( "EC3RootNode.updateBackgroundImage@" + System.currentTimeMillis() );
            BufferedImage i2 = BufferedImageUtils.toBufferedImage( backgroundImage );
            if( ec3Canvas.getHeight() > 0 && ec3Canvas.getWidth() > 0 ) {
                i2 = BufferedImageUtils.rescaleYMaintainAspectRatio( i2, ec3Canvas.getHeight() );
//                System.out.println( "i2.getHeight( ) = " + i2.getHeight() + ", canvasHeight=" + ec3Canvas.getHeight() );
            }
            screenBackground.removeAllChildren();
            PImage child = new PImage( i2 );
//        double overshootY = ec3Canvas.getHeight() - child.getFullBounds().getHeight();
            double maxY = floorGraphic.getGlobalFullBounds().getMinY();
            Point2D.Double loc = new Point2D.Double( 0, maxY );
            screenBackground.globalToLocal( loc );
//        child.translate( 0, -loc.y + getEC3Panel().getHeight() );
            double dy = child.getFullBounds().getHeight() - maxY;
            child.translate( 0, -dy );
            screenBackground.addChild( child );
        }
    }

    private void resetDefaults() {
        setPieChartVisible( DEFAULT_PIE_CHART_VISIBLE );
        setMeasuringTapeVisible( DEFAULT_TAPE_VISIBLE );
    }

    public void initPieChart() {
        PieChartIndicator pieChartIndicator = new PieChartIndicator( ec3Module, bodyGraphicAt( 0 ) );
        pieChartIndicator.setIgnoreThermal( ignoreThermal );
        pieCharts.addChild( pieChartIndicator );
    }

    private EnergyConservationModel getModel() {
        return ec3Module.getEnergyConservationModel();
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
    }

    public void clearBuses() {
        if( buses != null ) {
            buses.removeAllChildren();
            removeChild( buses );
            buses = null;
        }
    }

    public void addBuses() {
        if( buses == null ) {
            try {
                buses = new PNode();
                Floor floor = getModel().floorAt( 0 );
                BufferedImage newImage = ImageLoader.loadBufferedImage( "images/schoolbus200.gif" );
                PImage schoolBus = new PImage( newImage );
                double y = floor.getY() - schoolBus.getFullBounds().getHeight() + 10;
                schoolBus.setOffset( 0, y );
                double busStart = 500;
                for( int i = 0; i < 10; i++ ) {
                    PImage bus = new PImage( newImage );
                    double dbus = 2;
                    bus.setOffset( busStart + i * ( bus.getFullBounds().getWidth() + dbus ), y );
                    buses.addChild( bus );
                }
                addWorldChild( buses );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void addSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.addChild( splineGraphic );
    }

    public void reset() {
        bodyGraphics.removeAllChildren();
        splineGraphics.removeAllChildren();
        clearBuses();
        pieCharts.removeAllChildren();
        setZeroPointVisible( false );
//        resetDefaults();//needs MVC update before this will work.
    }

    public void addBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.addChild( bodyGraphic );
        offscreenManIndicator.setBodyGraphic( bodyGraphic );
    }

    public void toggleBox() {
        if( bodyGraphics.getChildrenReference().size() > 0 ) {
            boolean state = ( (BodyGraphic)bodyGraphics.getChildrenReference().get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.getChildrenReference().size(); i++ ) {
                BodyGraphic bodyGraphic = (BodyGraphic)bodyGraphics.getChildrenReference().get( i );
                bodyGraphic.setBoxVisible( !state );
            }
        }
    }

    public SplineGraphic splineGraphicAt( int i ) {
        return (SplineGraphic)splineGraphics.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineGraphics.getChildrenReference().size();
    }

    public void removeSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.removeChild( splineGraphic );
    }

    public void updateGraphics() {
        updateSplines();
        updateBodies();
        updateHistory();
        updatePieChart();
        updateZeroPotential();
        offscreenManIndicator.update();
    }

    private void updateZeroPotential() {
        zeroPointPotentialGraphic.setZeroPointPotential( getModel().getZeroPointPotentialY() );
    }

    private void updatePieChart() {
        for( int i = 0; i < pieCharts.getChildrenCount(); i++ ) {
            PieChartIndicator pieChartIndicator = (PieChartIndicator)pieCharts.getChild( i );
            pieChartIndicator.update();
        }
    }

    private void updateHistory() {
//        System.out.println( "numHistoryGraphics() = " + numHistoryGraphics() );
//        System.out.println( "getModel().numHistoryPoints() = " + getModel().numHistoryPoints() );
        while( numHistoryGraphics() < getModel().numHistoryPoints() ) {
            addHistoryGraphic( new HistoryPointGraphic( getModel().historyPointAt( 0 ) ) );
        }
        while( numHistoryGraphics() > getModel().numHistoryPoints() ) {
            removeHistoryPointGraphic( historyGraphicAt( numHistoryGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numHistoryPoints(); i++ ) {
            historyGraphicAt( i ).setHistoryPoint( getModel().historyPointAt( i ) );
        }
    }

    private HistoryPointGraphic historyGraphicAt( int i ) {
        return (HistoryPointGraphic)historyGraphics.getChild( i );
    }

    private void removeHistoryPointGraphic( HistoryPointGraphic graphic ) {
        historyGraphics.removeChild( graphic );
    }

    private void addHistoryGraphic( HistoryPointGraphic historyPointGraphic ) {
        historyGraphics.addChild( historyPointGraphic );
    }

    private int numHistoryGraphics() {
        return historyGraphics.getChildrenCount();
    }

    private void updateBodies() {
        while( numBodyGraphics() < getModel().numBodies() ) {
            addBodyGraphic( new BodyGraphic( ec3Module, getModel().bodyAt( 0 ) ) );
        }
        while( numBodyGraphics() > getModel().numBodies() ) {
            removeBodyGraphic( bodyGraphicAt( numBodyGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numBodies(); i++ ) {
            bodyGraphicAt( i ).setBody( getModel().bodyAt( i ) );
        }
    }

    private void updateSplines() {
        while( numSplineGraphics() < getModel().numSplineSurfaces() ) {
            addSplineGraphic( new SplineGraphic( ec3Canvas, getModel().splineSurfaceAt( 0 ) ) );
        }
        while( numSplineGraphics() > getModel().numSplineSurfaces() ) {
            removeSplineGraphic( splineGraphicAt( numSplineGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numSplineSurfaces(); i++ ) {
            splineGraphicAt( i ).setSplineSurface( getModel().splineSurfaceAt( i ) );
        }
    }

    private void removeBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.removeChild( bodyGraphic );
    }

    public int numBodyGraphics() {
        return bodyGraphics.getChildrenCount();
    }

    public BodyGraphic bodyGraphicAt( int i ) {
        return (BodyGraphic)bodyGraphics.getChild( i );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
        measuringTape.setPickable( selected );
        measuringTape.setChildrenPickable( selected );
    }

    public boolean isPieChartVisible() {
        return pieCharts.getVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        pieCharts.setVisible( selected );
        legend.setVisible( selected );
    }

    public boolean getIgnoreThermal() {
        return ignoreThermal;
    }

    public void setIgnoreThermal( boolean selected ) {
        this.ignoreThermal = selected;
        for( int i = 0; i < pieCharts.getChildrenCount(); i++ ) {
            PieChartIndicator pieChartIndicator = (PieChartIndicator)pieCharts.getChild( i );
            pieChartIndicator.setIgnoreThermal( ignoreThermal );
        }
    }

    public void clearBackground() {
        BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( new Color( 0, 0, 0, 255 ) );
        g2.fillRect( 0, 0, 1, 1 );
        setBackground( null );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        pauseIndicator.relayout();
        double insetX = 10;
        double insetY = 10;
        legend.setOffset( getEC3Panel().getWidth() - legend.getFullBounds().getWidth() - insetX, insetY );
    }

    private EC3Canvas getEC3Panel() {
        return ec3Canvas;
    }

    public void setZeroPointVisible( boolean selected ) {
        zeroPointPotentialGraphic.setVisible( selected );
    }

    public boolean isZeroPointVisible() {
        return zeroPointPotentialGraphic.getVisible();
    }
}
