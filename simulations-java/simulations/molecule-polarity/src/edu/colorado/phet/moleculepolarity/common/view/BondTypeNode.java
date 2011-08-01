// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the bond type, by placing a marker on a continuum whose
 * extremes are "more covalent" and "more ionic".
 * Range is absolute value of the difference in electronegativity between the 2 atoms,
 * from 0.0 (non-polar covalent) to 3.3 (mostly iconic)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondTypeNode extends PComposite {

    private static final double ARROW_LENGTH = 350;
    private static final double ARROW_HEAD_HEIGHT = 15;
    private static final double ARROW_HEAD_WIDTH = 15;
    private static final double ARROW_TAIL_WIDTH = 5;
    private static final Font LABEL_FONT = new PhetFont( 12 );
    private static final double LABEL_Y_SPACING = 4;
    private static final double THUMB_WIDTH = 20;
    private static final double THUMB_HEIGHT = 20;

    private static final LinearFunction X_OFFSET_FUNCTION = new LinearFunction( 0, 3.3, ARROW_HEAD_HEIGHT, ARROW_LENGTH - ARROW_HEAD_HEIGHT );

    public BondTypeNode( TwoAtomMolecule molecule ) {

        DoubleArrowNode trackNode = new DoubleArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( ARROW_LENGTH, 0 ), ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ) {{
            setPaint( Color.BLACK );
        }};
        PNode maxLabelNode = new PText( MPStrings.MORE_IONIC ) {{
            setFont( LABEL_FONT );
        }};
        PNode minLabelNode = new PText( MPStrings.MORE_COVALENT ) {{
            setFont( LABEL_FONT );
        }};
        final PPath thumbNode = new PPath( new DoubleGeneralPath() {{
            moveTo( 0, 0 );
            lineTo( -0.5 * THUMB_WIDTH, -THUMB_HEIGHT );
            lineTo( 0.5 * THUMB_WIDTH, -THUMB_HEIGHT );
            closePath();
        }}.getGeneralPath() );
        thumbNode.setPaint( Color.WHITE );

        // rendering order
        addChild( maxLabelNode );
        addChild( minLabelNode );
        addChild( trackNode );
        addChild( thumbNode );

        // layout
        minLabelNode.setOffset( trackNode.getFullBoundsReference().getMinX(),
                                trackNode.getFullBoundsReference().getMaxY() + LABEL_Y_SPACING );
        maxLabelNode.setOffset( trackNode.getFullBoundsReference().getMaxX() - maxLabelNode.getFullBoundsReference().getWidth(),
                                trackNode.getFullBoundsReference().getMaxY() + LABEL_Y_SPACING );

        molecule.bond.dipoleMagnitude.addObserver( new VoidFunction1<Double>() {
            public void apply( Double magnitude ) {
                thumbNode.setOffset( X_OFFSET_FUNCTION.evaluate( Math.abs( magnitude ) ), thumbNode.getYOffset() );
            }
        } );
    }
}
