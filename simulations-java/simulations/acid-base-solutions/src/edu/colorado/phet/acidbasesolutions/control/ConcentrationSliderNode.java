
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.control.IScalarTransform.LogLinearTransform;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Log slider for concentration.
 * Drag the thumb or click in the track.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationSliderNode extends PhetPNode {
    
    // Thumb
    private static final PDimension THUMB_SIZE = new PDimension( 13, 18 );
    private static final Color THUMB_FILL_COLOR = Color.WHITE;
    private static final Color THUMB_STROKE_COLOR = Color.BLACK;
    private static final float THUMB_STROKE_WIDTH = 1f;
    private static final Stroke THUMB_STROKE = new BasicStroke( THUMB_STROKE_WIDTH );

    // Track
    private static final PDimension TRACK_SIZE = new PDimension( 250, 5 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final float TRACK_STROKE_WIDTH = 1f;
    private static final Stroke TRACK_STROKE = new BasicStroke( TRACK_STROKE_WIDTH );

    // Major ticks
    private static final double MAJOR_TICK_LENGTH = 10;
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final float MAJOR_TICK_STROKE_WIDTH = 1f;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( MAJOR_TICK_STROKE_WIDTH );
    private static final double MAJOR_TICK_LABEL_Y_SPACING = 2;

    // Major tick labels
    private static final Font MAJOR_TICK_LABEL_FONT = new PhetFont( 12 );
    private static final Color MAJOR_TICK_LABEL_COLOR = Color.BLACK;
    
    // Minor ticks
    private static final double MINOR_TICK_LENGTH = 0.6 * MAJOR_TICK_LENGTH;
    private static final Color MINOR_TICK_COLOR = Color.GRAY;
    private static final float MINOR_TICK_STROKE_WIDTH = 1f;
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( MINOR_TICK_STROKE_WIDTH );
    private static final double MINOR_TICKS_CLOSEST_X_SPACING = 2;
    private static final double MINOR_TICK_X_SPACING_MULTIPLIER = 1.5;

    private final ArrayList changeListeners;
    private final ThumbNode thumbNode;
    private final TrackNode trackNode;
    private final double min, max;
    private double value;
    private final IScalarTransform transform;

    public ConcentrationSliderNode( double min, double max ) {
        assert ( min < max );
        assert ( value >= min && value <= max );
        
        this.min = min;
        this.max = max;
        this.value = min;
        transform = new LogLinearTransform( min, max, 0, TRACK_SIZE.getWidth() );
        changeListeners = new ArrayList();
        
        // track
        trackNode = new TrackNode();
        addChild( trackNode );
        trackNode.addInputEventListener( new TrackClickHandler( this ) );
        
        // ticks
        createTicks( min, max );
        
        // thumb
        thumbNode = new ThumbNode();
        addChild( thumbNode );
        thumbNode.addInputEventListener( new CursorHandler() );
        thumbNode.addInputEventListener( new ThumbDragHandler( this ) );
        
        // initial state
        updateThumb();
    }

    /*
     * Create major and minor ticks.
     * Major ticks are labeled with 10^N.
     */
    private void createTicks( double min, double max ) {
        
        int minExponent = (int) Math.floor( MathUtil.log10( min ) );
        int maxExponent = (int) Math.ceil( MathUtil.log10( max ) );
        
        // this algorithm assumes that we're dealing powers of 10
        assert( Math.pow( 10, minExponent ) == min );
        assert( Math.pow( 10, maxExponent ) == max );
        
        final double dx = TRACK_SIZE.getWidth() / ( maxExponent - minExponent );
        double xOffset = 0;
        final double yOffset = trackNode.getFullBoundsReference().getMaxY();
        for ( int i = minExponent; i <= maxExponent; i++ ) {
            String label = "10<sup>" + i + "</sup>";
            if ( i == 0 ) {
                label = "1";
            }
            MajorTick majorTick = new MajorTick( label );
            addChild( majorTick );
            majorTick.setOffset( xOffset, yOffset );
            if ( xOffset != 0 ) {
                createMinorTicks( xOffset - dx, xOffset );
            }
            xOffset += dx;
        }
    }
    
    /*
     * Creates minor ticks between 2 major ticks, moving right-to-left, log spacing.
     */
    private void createMinorTicks( double majorTickLeft, double majorTickRight ) {
        double dx = MINOR_TICKS_CLOSEST_X_SPACING;
        double xOffset = majorTickRight - dx;
        final double yOffset = trackNode.getFullBoundsReference().getMaxY();
        while ( xOffset >= majorTickLeft + dx ) {
            MinorTick minorTick = new MinorTick();
            addChild( minorTick );
            minorTick.setOffset( xOffset, yOffset );
            dx *= MINOR_TICK_X_SPACING_MULTIPLIER;
            xOffset = xOffset - dx;
        }
    }

    public void setValue( double value ) {
        if ( !( value >= min && value <= max ) ) {
            throw new IllegalArgumentException( "value out of range: " + value );
        }
        if ( value != getValue() ) {
//            System.out.println( "ConcentrationSliderNode.setValue value=" + value );//XXX
            this.value = value;
            updateThumb();
            fireStateChanged();
        }
    }

    public double getValue() {
        return value;
    }
    
    public double getMin() {
        return min;
    }
    
    public double getMax() {
        return max;
    }
    
    protected TrackNode getTrackNode() {
       return trackNode; 
    }
    
    protected ThumbNode getThumbNode() {
        return thumbNode;
    }
    
    protected IScalarTransform getMVTransform() {
        return transform;
    }
    
    private void updateThumb() {
        double xOffset = transform.modelToView( value );
        double yOffset = trackNode.getFullBoundsReference().getCenterY();
        thumbNode.setOffset( xOffset, yOffset );
    }

    public void addChangeListener( ChangeListener listener ) {
        changeListeners.add( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeListeners.remove( listener );
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator i = changeListeners.iterator();
        while ( i.hasNext() ) {
            ( (ChangeListener) i.next() ).stateChanged( event );
        }
    }

    /*
     * The thumb has an arrow tip that points down.
     * Origin is at the geometric center.
     * The thumb is interactive.
     */
    private static class ThumbNode extends PNode {

        public ThumbNode() {
            super();

            float w = (float) THUMB_SIZE.getWidth();
            float h = (float) THUMB_SIZE.getHeight();
            float hTip = 0.35f * h;

            // start at the tip, move clockwise
            GeneralPath knobPath = new GeneralPath();
            knobPath.moveTo( 0f, h / 2f );
            knobPath.lineTo( -w / 2f, ( h / 2f ) - hTip );
            knobPath.lineTo( -w / 2f, -h / 2f );
            knobPath.lineTo( w / 2, -h / 2f );
            knobPath.lineTo( w / 2, ( h / 2f ) - hTip );
            knobPath.closePath();

            PPath pathNode = new PPath();
            pathNode.setPathTo( knobPath );
            pathNode.setPaint( THUMB_FILL_COLOR );
            pathNode.setStroke( THUMB_STROKE );
            pathNode.setStrokePaint( THUMB_STROKE_COLOR );
            addChild( pathNode );
        }
    }

    /*
     * The track is a rectangular region, oriented horizontally.
     * Origin is at the upper-left corner.
     * The track is interactive.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, TRACK_SIZE.getWidth(), TRACK_SIZE.getHeight() ) );
            setPaint( TRACK_FILL_COLOR );
            setStroke( TRACK_STROKE );
            setStrokePaint( TRACK_STROKE_COLOR );
        }
    }

    /*
     * Minor ticks are vertical lines that appear below the track.
     * Origin is at the top center.
     * They have no label, and are not interactive.
     */
    private static class MinorTick extends PPath {

        public MinorTick() {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            // properties
            setPathTo( new Line2D.Double( 0, 0, 0, MINOR_TICK_LENGTH ) );
            setStroke( MINOR_TICK_STROKE );
            setStrokePaint( MINOR_TICK_COLOR );
        }
    }

    /*
     * Major ticks are vertical lines that appear below the track.
     * Origin is at the top center.
     * They are labeled, and the label is centered below the tick line.
     * They are not interactive.
     */
    private static class MajorTick extends PComposite {

        public MajorTick( String label ) {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            // tick
            PPath tickNode = new PPath();
            tickNode.setPathTo( new Line2D.Double( 0, 0, 0, MAJOR_TICK_LENGTH ) );
            tickNode.setStroke( MAJOR_TICK_STROKE );
            tickNode.setStrokePaint( MAJOR_TICK_COLOR );
            addChild( tickNode );
            // label
            PNode labelNode = new MajorTickLabel( label );
            addChild( labelNode );
            // layout, label centered below tick
            tickNode.setOffset( 0, 0 );
            double xOffset = -labelNode.getFullBoundsReference().getWidth() / 2;
            double yOffset = tickNode.getFullBoundsReference().getMaxY() + MAJOR_TICK_LABEL_Y_SPACING;
            labelNode.setOffset( xOffset, yOffset );
        }
    }

    /*
     * Major tick labels are in HTML or plain text format.
     * They are not interactive.
     * Origin is at upper-left corner.
     */
    private static class MajorTickLabel extends HTMLNode {

        public MajorTickLabel( String label ) {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            // properties
            setHTML( HTMLUtils.toHTMLString( label ) );
            setFont( MAJOR_TICK_LABEL_FONT );
            setHTMLColor( MAJOR_TICK_LABEL_COLOR );
        }
    }

    /*
     * Dragging the thumb changes the slider value.
     */
    private static class ThumbDragHandler extends PDragEventHandler {

        private final ConcentrationSliderNode sliderNode;
        private double _globalClickXOffset; // X offset of mouse click from knob's origin, in global coordinates

        public ThumbDragHandler( ConcentrationSliderNode sliderNode ) {
            super();
            this.sliderNode = sliderNode;
        }

        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            // note the offset between the mouse click and the knob's origin
            Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
            Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
            Point2D pThumbGlobal = sliderNode.localToGlobal( sliderNode.getThumbNode().getOffset() );
            _globalClickXOffset = pMouseGlobal.getX() - pThumbGlobal.getX();
        }

        protected void drag( PInputEvent event ) {

            // determine the thumb's new offset
            Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
            Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
            Point2D pThumbGlobal = new Point2D.Double( pMouseGlobal.getX() - _globalClickXOffset, pMouseGlobal.getY() );
            Point2D pThumbLocal = sliderNode.globalToLocal( pThumbGlobal );

            // transform offset to a slider value
            double value = sliderNode.getMVTransform().viewToModel( pThumbLocal.getX() );
            if ( value < sliderNode.getMin() ) {
                value = sliderNode.getMin();
            }
            else if ( value > sliderNode.getMax() ) {
                value = sliderNode.getMax();
            }
            
            sliderNode.setValue( value );
        }
    }
    
    /*
     * Clicking in the slider track changes the slider value.
     */
    private static class TrackClickHandler extends PBasicInputEventHandler {
        
        private final ConcentrationSliderNode sliderNode;
        
        public TrackClickHandler( ConcentrationSliderNode sliderNode ) {
            super();
            this.sliderNode = sliderNode;
        }
        
        public void mousePressed( PInputEvent event ) {
            
            // determine the offset of the mouse click
            Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
            Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
            Point2D pTrackLocal = sliderNode.globalToLocal( pMouseGlobal );

            // transform offset to a slider value
            double value = sliderNode.getMVTransform().viewToModel( pTrackLocal.getX() );
            if ( value < sliderNode.getMin() ) {
                value = sliderNode.getMin();
            }
            else if ( value > sliderNode.getMax() ) {
                value = sliderNode.getMax();
            }
            
            sliderNode.setValue( value );
        }
    }
    
    // test
    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 600, 300 );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( canvasSize );
        
        ConcentrationSliderNode sliderNode = new ConcentrationSliderNode( 1E-3, 1 );
        canvas.getLayer().addChild( sliderNode );
        sliderNode.setOffset( 100, 100 );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
