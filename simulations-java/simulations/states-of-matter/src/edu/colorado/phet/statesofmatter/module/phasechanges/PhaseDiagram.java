/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

// TODO: JPB TBD - Make the labels into translatable strings.

/**
 * This class displays a phase diagram suitable for inclusion on the control
 * panel of a PhET simulation.
 *
 * @author John Blanco
 */
public class PhaseDiagram extends PhetPCanvas {

    // Constants that control the size of the canvas.
    public static final int WIDTH = 200;
    public static final int HEIGHT = WIDTH;
    
    // Constants that control the look of the axes.
    public static final double AXES_LINE_WIDTH = 1;
    public static final double AXES_ARROW_HEAD_WIDTH = 5 * AXES_LINE_WIDTH;
    public static final double AXES_ARROW_HEAD_HEIGHT = 8 * AXES_LINE_WIDTH;
    public static final double HORIZ_AXIS_SIZE_PROPORTION = 0.8;
    public static final double VERT_AXIS_SIZE_PROPORTION = 0.8;
    
    // Constants that control the location of the origin for the graph.
    public static final double xOriginOffset = 0.10 * (double)WIDTH;
    public static final double yOriginOffset = 0.90 * (double)HEIGHT;
    public static final double xUsableRange = WIDTH * HORIZ_AXIS_SIZE_PROPORTION - AXES_ARROW_HEAD_HEIGHT;
    public static final double yUsableRange = HEIGHT * VERT_AXIS_SIZE_PROPORTION - AXES_ARROW_HEAD_HEIGHT;
    
    // Font for the labels used on the axes.
    public static final int AXIS_LABEL_FONT_SIZE = 14;
    public static final Font axisLabelFont = new PhetFont(AXIS_LABEL_FONT_SIZE);
    
    // Constants that control the appearance of the phase diagram for the
    // various substances.  Note that all points are controlled as proportions
    // of the overall graph size and not as absolute values.
    public static final double POINT_MARKER_DIAMETER = 4;
    public static final Point2D DEFAULT_TOP_OF_SOLID_LIQUID_CURVE = new Point2D.Double(xUsableRange/2 + xOriginOffset, 
            yOriginOffset - yUsableRange);
    public static final Point2D DEFAULT_TRIPLE_POINT = new Point2D.Double(xOriginOffset + (xUsableRange * 0.32), 
            yOriginOffset - (yUsableRange * 0.2));
    public static final Point2D DEFAULT_CRITICAL_POINT = new Point2D.Double(xOriginOffset + (xUsableRange * 0.8), 
            yOriginOffset - (yUsableRange * 0.45));
    
    // Variables that define the appearance of the phase diagram.
    private PPath m_triplePoint;
    private PPath m_criticalPoint;
    private PPath m_solidLiquidLine;
    private PPath m_liquidGasLine;
    
    /**
     * Constructor.
     */
    public PhaseDiagram(){

        setPreferredSize( new Dimension(WIDTH, HEIGHT) );

        // Create and add the axes for the graph.
        
        ArrowNode horizontalAxis = new ArrowNode( new Point2D.Double(xOriginOffset, yOriginOffset), 
                new Point2D.Double(xOriginOffset + (HORIZ_AXIS_SIZE_PROPORTION * WIDTH), yOriginOffset), 
                AXES_ARROW_HEAD_HEIGHT, AXES_ARROW_HEAD_WIDTH, AXES_LINE_WIDTH );
        horizontalAxis.setPaint( Color.BLACK );
        addWorldChild( horizontalAxis );
        
        ArrowNode verticalAxis = new ArrowNode( new Point2D.Double(xOriginOffset, yOriginOffset), 
                new Point2D.Double(xOriginOffset, yOriginOffset - VERT_AXIS_SIZE_PROPORTION * HEIGHT), 
                AXES_ARROW_HEAD_HEIGHT, AXES_ARROW_HEAD_WIDTH, AXES_LINE_WIDTH );
        verticalAxis.setPaint( Color.BLACK );
        addWorldChild( verticalAxis );
        
        // Create and add the labels for the axes.
        // TODO: JPB TBD - Make these into translatable strings if kept.
        PText horizontalAxisLabel = new PText("Temperature");
        horizontalAxisLabel.setOffset( WIDTH - (horizontalAxisLabel.getFullBoundsReference().width * 1.1), 
                yOriginOffset + horizontalAxisLabel.getFullBoundsReference().height * 0.3);
        addWorldChild( horizontalAxisLabel );
        
        PText horizontalOriginLabel = new PText("0 K");
        horizontalOriginLabel.setOffset( xOriginOffset - horizontalOriginLabel.getFullBoundsReference().width * 0.3, 
                yOriginOffset + horizontalOriginLabel.getFullBoundsReference().height * 0.3);
        addWorldChild( horizontalOriginLabel );
        
        PText verticalAxisLabel = new PText("Pressure");
        verticalAxisLabel.setOffset( xOriginOffset - (verticalAxisLabel.getFullBoundsReference().height * 1.1),
                verticalAxisLabel.getFullBoundsReference().width * 1.3);
        verticalAxisLabel.rotate( 3 * Math.PI / 2 );
        addWorldChild( verticalAxisLabel );
        
        PText verticalAxisOriginLabel = new PText("0");
        verticalAxisOriginLabel.setOffset( 
                xOriginOffset - (verticalAxisOriginLabel.getFullBoundsReference().height * 1.1), yOriginOffset);
        verticalAxisOriginLabel.rotate( 3 * Math.PI / 2 );
        addWorldChild( verticalAxisOriginLabel );
        
        // Create the variables that will define the look of the phase diagram.
        m_solidLiquidLine = new PPath();
        addWorldChild( m_solidLiquidLine );
        m_liquidGasLine = new PPath();
        addWorldChild( m_liquidGasLine );
        m_triplePoint = new PPath(new Ellipse2D.Double(0, 0, POINT_MARKER_DIAMETER, POINT_MARKER_DIAMETER));
        m_triplePoint.setPaint( Color.BLACK );
        addWorldChild( m_triplePoint );
        m_criticalPoint = new PPath(new Ellipse2D.Double(0, 0, POINT_MARKER_DIAMETER, POINT_MARKER_DIAMETER));
        m_criticalPoint.setPaint( Color.BLACK );
        addWorldChild( m_criticalPoint );
        
        // Draw the initial phase diagram.
        drawPhaseDiagram( 0 );
    }
    
    private void drawPhaseDiagram(int substance){
        
        // Locate the triple point marker.
        m_triplePoint.setOffset( DEFAULT_TRIPLE_POINT.getX() - POINT_MARKER_DIAMETER / 2, 
                DEFAULT_TRIPLE_POINT.getY() - POINT_MARKER_DIAMETER / 2 );
        
        // Add the curve that separates solid and liquid.
        QuadCurve2D solidLiquidCurve = new QuadCurve2D.Double(xOriginOffset, yOriginOffset, 
                (xOriginOffset + xUsableRange) * 0.5, yOriginOffset, DEFAULT_TOP_OF_SOLID_LIQUID_CURVE.getX(),
                DEFAULT_TOP_OF_SOLID_LIQUID_CURVE.getY() );
        
        m_solidLiquidLine.setPathTo( solidLiquidCurve );

        // Locate the critical point marker.
        m_criticalPoint.setOffset( DEFAULT_CRITICAL_POINT.getX() - POINT_MARKER_DIAMETER / 2, 
                DEFAULT_CRITICAL_POINT.getY() - POINT_MARKER_DIAMETER / 2 );

        // Add the curve that separates liquid and gas.
        double controlCurveXPos = DEFAULT_TRIPLE_POINT.getX() + 
            ((DEFAULT_CRITICAL_POINT.getX() - DEFAULT_TRIPLE_POINT.getX()) / 2);
        double controlCurveYPos = DEFAULT_TRIPLE_POINT.getY();
        QuadCurve2D liquidGasCurve = new QuadCurve2D.Double( DEFAULT_TRIPLE_POINT.getX(), DEFAULT_TRIPLE_POINT.getY(),
                controlCurveXPos, controlCurveYPos, DEFAULT_CRITICAL_POINT.getX(), DEFAULT_CRITICAL_POINT.getY() );

        m_liquidGasLine.setPathTo( liquidGasCurve );
        
    }
}
