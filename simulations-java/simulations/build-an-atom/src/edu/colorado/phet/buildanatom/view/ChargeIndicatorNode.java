package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class ChargeIndicatorNode extends PNode {
    final int BOX_DIMENSION = 80;
    private final Color purple = new Color( 112, 48, 160 );

    public ChargeIndicatorNode( final Atom atom ) {
        final PhetPPath boxNode = new PhetPPath( new Rectangle2D.Double( 0, 0, BOX_DIMENSION, BOX_DIMENSION ), BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( boxNode );
        int arcOffsetY = 10;
        int arcInsetDX = 2;
        final PhetPPath pieNode = new PhetPPath( new Arc2D.Double( arcInsetDX, arcOffsetY, BOX_DIMENSION - arcInsetDX * 2, BOX_DIMENSION, 0, 180, Arc2D.PIE ), Color.black );
        addChild( pieNode );
        final PText textNode = new PText( atom.getCharge() + "" ) {{setFont( BuildAnAtomConstants.READOUT_FONT );}};
        //center text below pie
        SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                textNode.setOffset( pieNode.getFullBounds().getCenterX() - textNode.getFullBounds().getWidth() / 2, ( pieNode.getFullBounds().getMaxY() + boxNode.getFullBounds().getMaxY() ) / 2 - textNode.getFullBounds().getHeight() / 2 );
                textNode.setTextPaint( getTextPaint( atom ) );
                textNode.setText( atom.getCharge() + "" );
            }
        };
        atom.addObserver( updateText );
        updateText.update();
        addChild( textNode );

        //+ and - labels on the pie part of the indicator
        addChild( new PText( "+" ) {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setOffset( pieNode.getFullBounds().getWidth() * 3.0 / 4.0 - getFullBounds().getWidth() / 2, pieNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            setTextPaint( Color.red );
        }} );

        addChild( new PText( "-" ) {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setOffset( pieNode.getFullBounds().getWidth() * 1.0 / 4.0 - getFullBounds().getWidth() / 2, pieNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            setTextPaint( new Color( 69, 94, 255 ) );//Blue that shows up against black
        }} );

        final PhetPPath arrowNode = new PhetPPath( BuildAnAtomConstants.READOUT_BACKGROUND_COLOR );
        final SimpleObserver updateArrow = new SimpleObserver() {
            public void update() {
                Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 12, -Math.PI / 2, 0 );//can only have 11 electrons, but map 12 to theta=0 so 11 looks maxed out
                double angle = linearFunction.evaluate( atom.getCharge() );
                arrowNode.setPathTo( new Arrow( new Point2D.Double( pieNode.getFullBounds().getCenterX(), pieNode.getFullBounds().getMaxY() ),
                                                ImmutableVector2D.parseAngleAndMagnitude( pieNode.getFullBounds().getHeight() * 0.98, angle ), 8, 8, 4, 4, false ).getShape() );
            }
        };
        atom.addObserver( updateArrow );
        updateArrow.update();
        addChild( arrowNode );

        final PText atomText = new PText( "Atom" ) {{setFont( new PhetFont( 14, true ) );}};
        final PText ionText = new PText( "Ion" ) {{setFont( new PhetFont( 14, true ) );}};

        final double w1 = ionText.getFullBounds().getWidth();
        final double w2 = atomText.getFullBounds().getWidth();
        double offsetDX = Math.max( w1, w2 );//align texts
        ionText.translate( -offsetDX - 20, boxNode.getFullBounds().getHeight() - ionText.getFullBounds().getHeight() );
        atomText.setOffset( ionText.getOffset().getX(), ionText.getOffset().getY() - atomText.getFullBounds().getHeight() );

        addChild( atomText );
        addChild( ionText );

        final SimpleObserver updateIconText = new SimpleObserver() {
            public void update() {
                if ( atom.getCharge() == 0 ) {
                    atomText.setTextPaint( getTextPaint( atom ) );
                    ionText.setTextPaint( Color.darkGray );
                }
                else {
                    atomText.setTextPaint( Color.darkGray );
                    ionText.setTextPaint( getTextPaint( atom ) );
                }
            }
        };
        atom.addObserver( updateIconText );
        updateIconText.update();

        //Add the check mark
        int width=5;
        int tailLength=20;
        int headLength=10;
        DoubleGeneralPath path = new DoubleGeneralPath( 0,0);
        path.lineToRelative( headLength,headLength);
        path.lineToRelative( tailLength,-tailLength);
        path.lineToRelative( -width,-width );
        path.lineToRelative( -(tailLength-width),tailLength-width );
        path.lineToRelative( -(headLength-width),-(headLength-width) );
        path.lineTo( 0,0 );
        path.closePath();
        final PhetPPath atomCheckMark = new PhetPPath( path.getGeneralPath(),purple, new BasicStroke( 2),Color.black );
        atomCheckMark.scale( 0.7 );
        addChild( atomCheckMark );

        final SimpleObserver updateCheckMarkVisible = new SimpleObserver() {
            public void update() {
                atomCheckMark.setVisible( atom.getCharge() == 0 );
            }
        };
        atom.addObserver( updateCheckMarkVisible );
        updateCheckMarkVisible.update();

        atomCheckMark.setOffset( atomText.getFullBounds().getX()-atomCheckMark.getFullBounds().getWidth(),atomText.getFullBounds().getCenterY() );
    }

    private Paint getTextPaint( Atom atom ) {
        if ( atom.getCharge() == 0 ) {
            return purple;
        }
        else if ( atom.getCharge() > 0 ) {
            return Color.red;
        }
        else {
            return Color.blue;
        }
    }

    public double getBoxWidth() {
        return BOX_DIMENSION;
    }
}
