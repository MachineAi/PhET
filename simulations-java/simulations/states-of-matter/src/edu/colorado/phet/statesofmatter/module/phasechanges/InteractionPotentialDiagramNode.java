/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class displays a phase diagram suitable for inclusion on the control
 * panel of a PhET simulation.
 *
 * @author John Blanco
 */
public class InteractionPotentialDiagramNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Constants that control the appearance of the diagram.
    private static final double WIDTH = 200;
    private static final float AXIS_LINE_WIDTH = 1;
    private static final Stroke AXIS_LINE_STROKE = new BasicStroke(AXIS_LINE_WIDTH);
    private static final Color AXIS_LINE_COLOR = Color.LIGHT_GRAY;
    private static final double ARROW_LINE_WIDTH = 0.50;
    private static final double ARROW_HEAD_WIDTH = 8 * ARROW_LINE_WIDTH;
    private static final double ARROW_HEAD_HEIGHT = 10 * ARROW_LINE_WIDTH;
    private static final float POTENTIAL_ENERGY_LINE_WIDTH = 1.5f;
    private static final Stroke POTENTIAL_ENERGY_LINE_STROKE = new BasicStroke(POTENTIAL_ENERGY_LINE_WIDTH);
    private static final Color POTENTIAL_ENERGY_LINE_COLOR = Color.red;
    private static final int NUM_HORIZ_TICK_MARKS = 4;
    private static final int NUM_VERT_TICK_MARKS = 3;
    private static final double TICK_MARK_LENGTH = 2;
    private static final float TICK_MARK_WIDTH = 1;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke(TICK_MARK_WIDTH);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    
    // Constants used for the Lennard-Jones potential calculation.
    private static final double SIGMA = 3.3;
    private static final double EPSILON = 120;
    private static final double HORIZONTAL_INDEX_MULTIPLIER = 0.05;  // Empirically determined so curve will look reasonable.
    private static final double VERTICAL_SCALING_FACTOR = 0.5;       // Empirically determined so curve will fit graph.
    
    // Constants that control the location and size of the graph.
    private static final double HORIZ_AXIS_SIZE_PROPORTION = 0.80;
    private static final double VERT_AXIS_SIZE_PROPORTION = 0.85;
    
    // Font for the labels used on the axes and within the graph.
    private static final int AXIS_LABEL_FONT_SIZE = 13;
    private static final Font AXIS_LABEL_FONT = new PhetFont(AXIS_LABEL_FONT_SIZE);
    private static final int GREEK_LETTER_FONT_SIZE = 16;
    private static final Font GREEK_LETTER_FONT = new PhetFont(GREEK_LETTER_FONT_SIZE);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private double m_width;
    private double m_height;
    private double m_graphOffsetX;
    private double m_graphOffsetY;
    private double m_graphWidth;
    private double m_graphHeight;
    
    /**
     * Constructor.
     * 
     * @param wide - True if the widescreen version of the graph is needed,
     * false if not.
     */
    public InteractionPotentialDiagramNode(boolean wide){

        // Set up for the normal or wide version of the graph.
        if (wide){
            m_width = 1.5 * WIDTH;
            m_height = m_width * 0.6;
        }
        else{
            m_width = WIDTH;
            m_height = m_width * 0.8;
        }
        m_graphOffsetX = 0.10 * (double)m_width;
        m_graphOffsetY = 0;
        m_graphWidth = m_width * HORIZ_AXIS_SIZE_PROPORTION;
        m_graphHeight = m_height * VERT_AXIS_SIZE_PROPORTION;
        
        // Create a background that will sit behind everything.
        PPath graphBackground = new PPath(new Rectangle2D.Double( 0, 0, m_width, m_height ));
        graphBackground.setPaint( BACKGROUND_COLOR );
        addChild( graphBackground );

        // Create and add the node that will contain the graph.
        PPath ljPotentialGraph = new PPath(new Rectangle2D.Double(0, 0, m_graphWidth, m_graphHeight));
        ljPotentialGraph.setOffset( m_graphOffsetX, 0 );
        ljPotentialGraph.setPaint( Color.WHITE );
        addChild( ljPotentialGraph );
        
        // Create and add the axis line for the graph.
        PPath horizontalAxis = new PPath(new Line2D.Double(new Point2D.Double(0, 0), 
                new Point2D.Double(m_graphWidth, 0)));
        horizontalAxis.setStroke( AXIS_LINE_STROKE );
        horizontalAxis.setStrokePaint( AXIS_LINE_COLOR );
        ljPotentialGraph.addChild( horizontalAxis );
        horizontalAxis.setOffset( 0, m_graphHeight / 2 );
        
        // Create and add the tick marks for the graph.
        double horizTickMarkSpacing = m_graphWidth / (NUM_HORIZ_TICK_MARKS + 1);
        Line2D tickMarkShape = new Line2D.Double();
        Point2D endpoint1 = new Point2D.Double();
        Point2D endpoint2 = new Point2D.Double();
        for (int i = 0; i < NUM_HORIZ_TICK_MARKS; i++){
            // Top tick mark
            endpoint1.setLocation( horizTickMarkSpacing * (i + 1), 0 );
            endpoint2.setLocation( horizTickMarkSpacing * (i + 1), TICK_MARK_LENGTH );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath topTickMark = new PPath(tickMarkShape);
            topTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( topTickMark );

            // Bottom tick mark
            endpoint1.setLocation( horizTickMarkSpacing * (i + 1), m_graphHeight );
            endpoint2.setLocation( horizTickMarkSpacing * (i + 1), m_graphHeight - TICK_MARK_LENGTH );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath bottomTickMark = new PPath(tickMarkShape);
            bottomTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( bottomTickMark );
        }
        double vertTickMarkSpacing = m_graphHeight / (NUM_VERT_TICK_MARKS + 1);
        for (int i = 0; i < NUM_VERT_TICK_MARKS; i++){
            // Left tick mark
            endpoint1.setLocation( 0, vertTickMarkSpacing * (i + 1) );
            endpoint2.setLocation( TICK_MARK_LENGTH, vertTickMarkSpacing * (i + 1) );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath leftTickMark = new PPath(tickMarkShape);
            leftTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( leftTickMark );

            // Right tick mark
            endpoint1.setLocation( m_graphWidth, vertTickMarkSpacing * (i + 1) );
            endpoint2.setLocation( m_graphWidth - TICK_MARK_LENGTH, vertTickMarkSpacing * (i + 1) );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath rightTickMark = new PPath(tickMarkShape);
            rightTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( rightTickMark );
        }
        
        // Create and add the potential energy line to the graph.
        PPath potentialEnergyLine = new PPath();
        potentialEnergyLine.setStroke( POTENTIAL_ENERGY_LINE_STROKE );
        potentialEnergyLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        GeneralPath potentialEnergyLineShape = new GeneralPath();
        potentialEnergyLineShape.moveTo( 0, 0);
        Point2D graphMin = new Point2D.Double(0, 0);
        Point2D zeroCrossingPoint = new Point2D.Double(0, 0);
        for (int i = 1; i < (int)m_graphWidth; i++){
            double potential = calculateLennardJonesPotential( i * HORIZONTAL_INDEX_MULTIPLIER);
            double yPos = ((m_graphHeight / 2) - (potential * VERTICAL_SCALING_FACTOR)) * (1/getScale());
            if ((yPos > 0) && (yPos < m_graphHeight)){
                potentialEnergyLineShape.lineTo( (float)i, (float)(yPos * (1/getScale())));
                if (yPos > graphMin.getY()){
                    // A new minimum has been found.  If you're wondering why
                    // the test is for greater than rather than less than, it
                    // is because positive Y is down rather than up within a
                    // PNode.
                    graphMin.setLocation( i, yPos );
                }
                if (potential > 0){
                    // The potential hasn't become negative yet, so update the
                    // zero crossing point.
                    zeroCrossingPoint.setLocation( i, m_graphHeight / 2 );
                }
            }
            else{
                // Move to a good location from which to start graphing.
                potentialEnergyLineShape.moveTo( i, 0);
            }
        }
        potentialEnergyLine.setPathTo( potentialEnergyLineShape );
        
        // Put in the arrows that depict sigma and epsilon.
        DoubleArrowNode epsilonArrow = new DoubleArrowNode(graphMin, 
                new Point2D.Double( graphMin.getX(), m_graphHeight / 2 ), ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        epsilonArrow.setPaint( Color.BLACK );
        ljPotentialGraph.addChild( epsilonArrow );
        
        PText epsilon = new PText("\u03B5");
        epsilon.setFont( GREEK_LETTER_FONT );
        epsilon.setOffset( graphMin.getX() + epsilon.getFullBoundsReference().width * 0.5, 
                m_graphHeight / 2 + epsilon.getFullBoundsReference().height * 0.5 );
        ljPotentialGraph.addChild( epsilon );

        PText sigma = new PText("\u03C3");
        sigma.setFont( GREEK_LETTER_FONT );
        sigma.setOffset( zeroCrossingPoint.getX() / 2 - sigma.getFullBoundsReference().width / 2, 
                m_graphHeight / 2 );
        ljPotentialGraph.addChild( sigma );

        DoubleArrowNode sigmaArrow = new DoubleArrowNode(new Point2D.Double(0, m_graphHeight / 2), zeroCrossingPoint, 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        sigmaArrow.setPaint( Color.BLACK );
        ljPotentialGraph.addChild( sigmaArrow );

        // Add the potential energy line here so that it is above the arrows
        // in the layering.
        ljPotentialGraph.addChild( potentialEnergyLine );

        // Create and add the labels for the axes.
        PText horizontalAxisLabel = new PText(StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL);
        horizontalAxisLabel.setFont( AXIS_LABEL_FONT );
        horizontalAxisLabel.setOffset( m_graphOffsetX + (m_graphWidth / 2) - 
                (horizontalAxisLabel.getFullBoundsReference().width / 2), 
                m_graphOffsetY + m_graphHeight + (horizontalAxisLabel.getFullBoundsReference().height * 0.3));
        addChild( horizontalAxisLabel );
        
        PText verticalAxisLabel = new PText(StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_Y_AXIS_LABEL);
        verticalAxisLabel.setFont( AXIS_LABEL_FONT );
        verticalAxisLabel.setOffset( 0, 
                (m_graphOffsetY + m_graphHeight) / 2 + (verticalAxisLabel.getFullBoundsReference().width / 2) );
        verticalAxisLabel.rotate( 3 * Math.PI / 2 );
        addChild( verticalAxisLabel );
        
        // Set the overall background color.
        setPaint( Color.LIGHT_GRAY );
    }
    
    /**
     * Calculate the normalized Lennard-Jones potential, meaning that the 
     * sigma and epsilon values are assumed to be equal to 1.
     * 
     * @param radius
     * @return
     */
    private double calculateLennardJonesPotential(double radius){
        
        return (4 * EPSILON * (Math.pow( SIGMA / radius, 12 ) - Math.pow( SIGMA / radius, 6 )));
        
    }
}
