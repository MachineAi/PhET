// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.PaintHighlightHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler.Orientation;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Vertical sliders in the Dilutions simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionsSliderNode extends PhetPNode {

    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PDimension THUMB_SIZE = new PDimension( 45, 15 );
    private static final PhetFont MIN_MAX_FONT = new PhetFont( 14 );

    private final LinearFunction function; // maps model value to a track position
    private final PNode trackNode;
    private final ThumbNode thumbNode;

    // Slider with a default track fill and background color.
    public DilutionsSliderNode( String title, String minLabel, String maxLabel, final PDimension trackSize,
                                final Property<Double> modelValue, DoubleRange range ) {
        this( title, minLabel, maxLabel, trackSize, Color.BLACK, new Color( 200, 200, 200, 140 ), modelValue, range );
    }

    public DilutionsSliderNode( String title, String minLabel, String maxLabel,
                                final PDimension trackSize, final Paint trackPaint, final Paint trackBackgroundPaint,
                                final Property<Double> modelValue, DoubleRange range ) {

        this.function = new LinearFunction( range.getMin(), range.getMax(), trackSize.getHeight(), 0 );

        // title
        PNode titleNode = new HTMLNode( title, Color.BLACK, TITLE_FONT );

        // track that the thumb moves in, origin at upper left
        trackNode = new PPath() {{
            setPathTo( new Rectangle2D.Double( 0, 0, trackSize.getWidth(), trackSize.getHeight() ) );
            setPaint( trackPaint );
        }};

        // background that surrounds the track
        PNode backgroundNode = new PPath() {{
            final double xMargin = 7;
            final double yMargin = 7;
            setPathTo( new RoundRectangle2D.Double( -xMargin, -yMargin, trackSize.getWidth() + ( 2 * xMargin ), trackSize.getHeight() + ( 2 * yMargin ), 10, 10 ) );
            setPaint( trackBackgroundPaint );
            setStroke( null );

        }};

        // thumb that moves in the track
        thumbNode = new ThumbNode( title, THUMB_SIZE, this, trackNode, range, modelValue );

        // min and max labels
        final PNode minNode = new PText( minLabel ) {{
            setFont( MIN_MAX_FONT );
        }};
        final PNode maxNode = new PText( maxLabel ) {{
            setFont( MIN_MAX_FONT );
        }};

        // rendering order
        {
            addChild( titleNode );
            addChild( maxNode );
            addChild( minNode );
            addChild( backgroundNode );
            addChild( trackNode );
            addChild( thumbNode );
        }

        // layout
        {
            // max label centered above the bar
            maxNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( maxNode.getFullBoundsReference().getWidth() / 2 ),
                               trackNode.getFullBoundsReference().getMinY() - ( thumbNode.getFullBoundsReference().getHeight() / 2 ) - maxNode.getFullBoundsReference().getHeight() - 1 );
            // min label centered below the bar
            minNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( minNode.getFullBoundsReference().getWidth() / 2 ),
                               trackNode.getFullBoundsReference().getMaxY() + ( thumbNode.getFullBoundsReference().getHeight() / 2 ) + 1 );
            // title centered above max label
            titleNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 maxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 8 );
            // thumb centered in track
            thumbNode.setOffset( trackNode.getFullBoundsReference().getCenterX(),
                                 trackNode.getFullBoundsReference().getCenterY() );
        }

        // adjust the slider to reflect the model value
        modelValue.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                updateNode( value );
            }
        } );
    }

    private void updateNode( double value ) {
        // knob location
        thumbNode.setOffset( thumbNode.getXOffset(), function.evaluate( value ) );
    }

    // The slider thumb, rounded rectangle with a horizontal line through the center. Origin is at the thumb's geometric center.
    private static class ThumbNode extends PComposite {

        private static final Stroke THUMB_STROKE = new BasicStroke( 1f );
        private static final Color THUMB_NORMAL_COLOR = new Color( 89, 156, 212 );
        private static final Color THUMB_HIGHLIGHT_COLOR = THUMB_NORMAL_COLOR.brighter();
        private static final Color THUMB_STROKE_COLOR = Color.BLACK;
        private static final Color THUMB_CENTER_LINE_COLOR = Color.WHITE;

        public ThumbNode( final String name, final PDimension size, PNode relativeNode, PNode trackNode, DoubleRange range, final Property<Double> modelValue ) {

            PPath bodyNode = new PPath() {{
                final double arcWidth = 0.25 * size.getWidth();
                setPathTo( new RoundRectangle2D.Double( -size.getWidth() / 2, -size.getHeight() / 2,
                                                        size.getWidth(), size.getHeight(),
                                                        arcWidth, arcWidth ) );
                setPaint( THUMB_NORMAL_COLOR );
                setStroke( THUMB_STROKE );
                setStrokePaint( THUMB_STROKE_COLOR );
            }};
            addChild( bodyNode );

            PPath centerLineNode = new PPath() {{
                setPathTo( new Line2D.Double( -( size.getWidth() / 2 ) + 3, 0, ( size.getWidth() / 2 ) - 3, 0 ) );
                setStrokePaint( THUMB_CENTER_LINE_COLOR );
            }};
            addChild( centerLineNode );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PaintHighlightHandler( bodyNode, THUMB_NORMAL_COLOR, THUMB_HIGHLIGHT_COLOR ) );
            addInputEventListener( new SliderThumbDragHandler( Orientation.VERTICAL, relativeNode, trackNode, this, range,
                                                               new VoidFunction1<Double>() {
                                                                   public void apply( Double value ) {
                                                                       modelValue.set( value );
                                                                   }
                                                               } ) );
        }
    }
}
