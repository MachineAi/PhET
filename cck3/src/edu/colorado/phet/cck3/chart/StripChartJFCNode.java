/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.chart;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 20, 2005
 * Time: 1:18:43 AM
 * Copyright (c) Dec 20, 2005 by Sam Reid
 */

public class StripChartJFCNode extends PNode {
    private XYSeries series;
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private boolean enabled = true;//debugging
    private int maxItemCount = 100;

    public StripChartJFCNode( int width, int height, String xAxis, String yAxis ) {
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        series = new XYSeries( "Time Series" );
        xyDataset.addSeries( series );

        jFreeChart = createChart( xyDataset, xAxis, yAxis );
        jFreeChart.setBorderVisible( true );
        jFreeChart.setBorderStroke( new BasicStroke( 5 ) );

        jFreeChartNode = new JFreeChartNode( jFreeChart );

        addChild( jFreeChartNode );
        jFreeChartNode.setBounds( 0, 0, width, height );

        jFreeChart.setBorderPaint( new GradientPaint( 0, 0, new Color( 200, 200, 200, 255 ), (float)jFreeChartNode.getFullBounds().getWidth(), (float)jFreeChartNode.getFullBounds().getHeight(), Color.darkGray ) );
        try {
            jFreeChart.setBackgroundImage( ImageLoader.loadBufferedImage( "images/wood.jpg" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static JFreeChart createChart( XYDataset dataset, String xaxis, String yaxis ) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                "",  // title
                xaxis,             // x-axis label
                yaxis,   // y-axis label
                dataset,            // data
                PlotOrientation.VERTICAL,
                false,               // create legend?
                false,               // generate tooltips?
                false               // generate URLs?
        );

        XYPlot plot = (XYPlot)chart.getPlot();
//        plot.getRangeAxis().setTickLabelsVisible( true );
        plot.getRangeAxis().setAutoRange( false );
        plot.getRangeAxis().setRange( -3, 3 );
        plot.getDomainAxis().setRange( 0, 100 );
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( true, false );
        renderer.setStroke( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 ) );
        renderer.setPaint( Color.blue );
        plot.setRenderer( renderer );
        return chart;
    }

    public void setRangeRange( double minY, double maxY ) {
        jFreeChart.getXYPlot().getRangeAxis().setRange( minY, maxY );
    }

    public void setDomainRange( double minX, double maxX ) {
        jFreeChart.getXYPlot().getDomainAxis().setRange( minX, maxX );
    }

    public void addValue( double x, double y ) {
        if( enabled ) {
            //todo can we temporarily disable render, do both steps as batch?
            series.add( x, y );
            if( series.getItemCount() > maxItemCount && !jFreeChart.getXYPlot().getDomainAxis().isAutoRange() ) {
                jFreeChart.getXYPlot().getDomainAxis().setAutoRange( true );
            }
            if( series.getItemCount() > maxItemCount ) {
                series.remove( 0 );
            }
        }
    }
}
