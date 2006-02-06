/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:41:14 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGPlotPanel extends PSwingCanvas {
    private XYDataset dataset;
    private int width = 700;
    private int height = 300;
    private DGModule dgModule;
    private XYSeries series;
    private JFreeChartNode jFreeChartNode;
    private int inset = 3;

    public DGPlotPanel( DGModule dgModule ) {
        this.dgModule = dgModule;
        series = new XYSeries( "series1" );
        dataset = new XYSeriesCollection( series );

        JFreeChart chart = ChartFactory.createScatterPlot( "Intensity Plot", "Angle (degrees)", "Intensity (units)", dataset, PlotOrientation.VERTICAL, false, false, false );
        chart.getXYPlot().getDomainAxis().setRange( 0, 90 );
        chart.getXYPlot().getRangeAxis().setRange( 0, 0.1 );
        jFreeChartNode = new JFreeChartNode( chart );
        jFreeChartNode.setBounds( 0, 0, width, height );
        setPreferredSize( new Dimension( width, height ) );
        getLayer().addChild( jFreeChartNode );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                replotAll();
            }
        } ).start();
    }

    public void replotAll() {
        series.clear();
        double dAngle = 1;
        for( double angle = 0; angle <= 90; angle += dAngle ) {
            double intensity = getIntensity( angle );
            series.add( angle, intensity );
        }
        jFreeChartNode.repaint();
    }

    protected Wavefunction getWavefunction() {
        return dgModule.getDiscreteModel().getWavefunction();
    }

    private double getIntensity( double angle ) {
        Point gridLocation = toGridLocation( angle );
        Complex value = getWavefunction().valueAt( gridLocation.x, gridLocation.y );
        return value.abs();
    }

    private Point toGridLocation( double degrees ) {
        if( degrees < 45 ) {
            return toGridLocationBottom( degrees );
        }
        else {
            return toGridLocationSide( degrees );
        }
    }

    private Point toGridLocationSide( double degrees ) {
        int yoffset = (int)( ( getWavefunction().getWidth() / 2 - inset ) * Math.tan( Math.toRadians( 90 - degrees ) ) );
        return new Point( getWavefunction().getWidth() - inset, getWavefunction().getHeight() / 2 + yoffset );
    }

    private Point toGridLocationBottom( double degrees ) {
        int xoffset = (int)( ( getWavefunction().getHeight() / 2 - inset ) * Math.tan( Math.toRadians( degrees ) ) );
        return new Point( xoffset + getWavefunction().getWidth() / 2, getWavefunction().getHeight() - inset );
    }

    public static void main( String[] args ) {
        DGPlotPanel dgPlotPanel = new DGPlotPanel( null ) {
            protected Wavefunction getWavefunction() {
                return new Wavefunction( 100, 100 );
            }
        };
        double dAngle = 1;
        for( double angle = 0; angle <= 90; angle += dAngle ) {
            Point loc = dgPlotPanel.toGridLocation( angle );
            System.out.println( "angle=" + angle + ", loc=" + loc );
        }
    }
}
