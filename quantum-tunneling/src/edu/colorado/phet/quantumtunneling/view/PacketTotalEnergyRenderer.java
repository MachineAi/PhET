/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * PacketTotalEnergyRenderer render the total energy of a wave packet 
 * as a gradient that represents the distribution of possible 
 * energy values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PacketTotalEnergyRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* The minumum transparency value, at the top & bottom edges of the band */
    private static final int MIN_ALPHA = 20;
    
    /* How far from the top (percentage wise) do we encounter the most transparent pixel? */
    private static final double FADE_POINT = 0.15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _bandHeight;  // height of the band, in model coordinates
    private int _series; // the series that we care about, ignore all others
    private BufferedImage _image; // the gradient image
    private Rectangle2D _previousDataArea; // plot's previous data area

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param bandHeight the height of the band, in model coordinates
     * @param series the series that contains the total energy data
     */
    public PacketTotalEnergyRenderer( double bandHeight, int series ) {
        super();
        if ( bandHeight <= 0 ) {
            throw new IllegalArgumentException( "bandWidth must be > 0: " + bandHeight );
        }
        _bandHeight = bandHeight;
        _previousDataArea = new Rectangle2D.Double();
    }

    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws an item.
     */
    public void drawItem( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item, 
            CrosshairState crosshairState, 
            int pass ) {
        
        /*
         * We only care about the first data point (item == 0) in 
         * the series that we're interested in, and only if that
         * series is visible.  Otherwise, do nothing.
         */
        if ( series != _series || !isSeriesVisible( series ) || item != 0 ) {
            return;
        }
        
        // If the plot's data area has changed, update the band.
        if ( !dataArea.equals( _previousDataArea ) || _image == null ) {
            updateBand( dataArea, plot, domainAxis, rangeAxis, dataset, series, item );
            _previousDataArea.setRect( dataArea );
        }
        
        // Draw the band...
        if ( _image != null ) {
            
            // Axis (model) coordinates
            double aMinX = domainAxis.getLowerBound();
            double aCenterY = dataset.getYValue( series, item ); // the total energy value

            // Java2D coordinates
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double jMinX = domainAxis.valueToJava2D( aMinX, dataArea, domainAxisLocation );
            double jCenterY = rangeAxis.valueToJava2D( aCenterY, dataArea, rangeAxisLocation );
            double jMinY = jCenterY - ( _image.getHeight() / 2 );

            // Draw the image
            AffineTransform transform = new AffineTransform();
            transform.translate( jMinX, jMinY );
            g2.drawRenderedImage( _image, transform );
        }
    }

    /*
     * Updates the band that represents the range of possible energy.
     * This band is implemented as two rectangle, each with its own
     * GradientPaint.  The rectangles and gradients are arranged such
     * that the darkest color at the total energy point, and the
     * color fades out above and below.
     */
    private void updateBand(
            Rectangle2D dataArea,             
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item ) {

        // Axis (model) coordinates
        double aMinX = domainAxis.getLowerBound();
        double aMaxX = domainAxis.getUpperBound();
        double aCenterY = dataset.getYValue( series, item ); // the total energy value
        double aMinY = aCenterY - ( _bandHeight / 2 );
        double aMaxY = aCenterY + ( _bandHeight / 2 );

        // Java2D coorinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        double jMinX = domainAxis.valueToJava2D( aMinX, dataArea, domainAxisLocation );
        double jMaxX = domainAxis.valueToJava2D( aMaxX, dataArea, domainAxisLocation );
        double jMinY = rangeAxis.valueToJava2D( aMaxY, dataArea, rangeAxisLocation ); // +y is down
        double jMaxY = rangeAxis.valueToJava2D( aMinY, dataArea, rangeAxisLocation ); // +y is down
        double jWidth = jMaxX - jMinX;
        double jHeight = jMaxY - jMinY;
       
        // Create the image of the band
        if ( jWidth > 0 && jHeight > 0 ) {
            
            final double overlap = .05 * jHeight;
            Shape shape1 = new Rectangle2D.Double( 0, 0, jWidth, ( jHeight / 2 ) + overlap );
            Shape shape2 = new Rectangle2D.Double( 0, ( jHeight / 2 ) - overlap, jWidth, (jHeight / 2 ) + overlap );

            Color color1 = QTConstants.TOTAL_ENERGY_COLOR;
            Color color2 = new Color( color1.getRed(), color1.getGreen(), color1.getBlue(), MIN_ALPHA );

            GradientPaint gradient1 = new GradientPaint( 0f, (float) ( FADE_POINT * jHeight ), color2, 0f, (float) jHeight / 2f, color1 );
            GradientPaint gradient2 = new GradientPaint( 0f, (float) jHeight / 2, color1, 0f, (float) ( jHeight - ( FADE_POINT * jHeight ) ), color2 );

            _image = new BufferedImage( (int)jWidth, (int)jHeight, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g2 = _image.createGraphics();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setPaint( gradient1 );
            g2.fill( shape1 );
            g2.setPaint( gradient2 );
            g2.fill( shape2 );
        }
    }
}
