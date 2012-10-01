// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.DEFAULT_FONT;
import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.INSET;

/**
 * Draws a force arrow node, including an arrow and text for the label and optionally the value (such as 50N).
 * This class is immutable and is regenerated if any dependencies change.
 * It is non-interactive, just a display.
 *
 * @author Sam Reid
 */
public class ForceArrowNode extends PNode {
    public ForceArrowNode( final boolean transparent, final Vector2D tail, final double forceInNewtons, final String name, Color color, final TextLocation textLocation, final Boolean showValues ) {

        //Choose a single scale factor that works in all of the tabs.
        final double value = forceInNewtons * 3.625;
        if ( value == 0 && textLocation == TextLocation.SIDE ) { return; }
        else if ( value == 0 && textLocation == TextLocation.TOP ) {
            showTextOnly( tail );
            return;
        }
        final double arrowScale = 1.2;
        final Point2D.Double tipLocation = tail.plus( value, 0 ).toPoint2D();
        final ArrowNode arrowNode = new ArrowNode( tail.toPoint2D(), tipLocation, 30 * arrowScale, 40 * arrowScale, 20 * arrowScale, 0.5, false );
        arrowNode.setPaint( transparent ? new Color( color.getRed(), color.getGreen(), color.getBlue(), 175 ) : color );
        arrowNode.setStroke( transparent ? new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 6, 4 }, 0 ) : new BasicStroke( 1 ) );
        addChild( arrowNode );
        final PhetPText nameNode = new PhetPText( name, DEFAULT_FONT ) {{
            if ( textLocation == TextLocation.SIDE ) {
                if ( value > 0 ) {
                    setOffset( arrowNode.getFullBounds().getMaxX() + INSET, arrowNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
                else {
                    setOffset( arrowNode.getFullBounds().getMinX() - getFullBounds().getWidth() - INSET, arrowNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
            }
            else {
                setOffset( arrowNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, arrowNode.getFullBounds().getY() - getFullBounds().getHeight() - INSET - 2 );
            }
        }};
        addChild( nameNode );

        if ( showValues ) {
            final String text = new DecimalFormat( "0" ).format( Math.abs( forceInNewtons ) );
            addChild( new PhetPText( text + "N", new PhetFont( 16, true ) ) {{
                centerFullBoundsOnPoint( arrowNode.getFullBounds().getCenter2D() );
                double dx = 2;
                translate( forceInNewtons < 0 ? dx :
                           forceInNewtons > 0 ? -dx :
                           0, 0 );

                //For single character text (9N and lower), show below the name
                if ( text.length() <= 1 ) {
                    setOffset( nameNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, nameNode.getFullBounds().getMaxY() - 3 );
                }
            }} );
        }
    }

    private void showTextOnly( final Vector2D tail ) {
        addChild( new PhetPText( "Sum of Forces = 0", DEFAULT_FONT ) {{
            centerBoundsOnPoint( tail.x, tail.y - 46 );//Fine tune location so that it shows at the same y location as the text would if there were an arrow
        }} );
    }
}
