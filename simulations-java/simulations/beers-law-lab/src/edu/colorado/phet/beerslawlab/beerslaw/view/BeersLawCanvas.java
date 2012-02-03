// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawModel;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.common.view.SoluteChoiceNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawCanvas extends BLLCanvas {

    public enum WavelengthControlType {LAMBDA_MAX, VARIABLE}

    private final Property<WavelengthControlType> wavelengthControlType = new Property<WavelengthControlType>( WavelengthControlType.LAMBDA_MAX  ); //TODO probably a derived property of Light

    public BeersLawCanvas( final BeersLawModel model, Frame parentFrame ) {

        // Nodes
        PNode lightNode = new LightNode( model.light );
        PNode lightControlsNode = new LightControlsNode( model.light, wavelengthControlType );
        PNode soluteChoiceNode = new SoluteChoiceNode( model.getSolutes(), model.solute );
        PNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, BLLConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};
        PNode rulerNode = new BLLRulerNode( (int)model.getCuvetteWidthRange().getMax(), getStageSize(), model.mvt );

        // Rendering order
        {
            addChild( lightNode );
            addChild( lightControlsNode );
            addChild( soluteChoiceNode );
            addChild( resetAllButtonNode );
            addChild( rulerNode );
        }

        // layout
        {
            final double xMargin = 20;
            final double yMargin = 20;
            // left center
            lightNode.setOffset( xMargin,
                                 ( getStageSize().getHeight() - lightNode.getFullBoundsReference().getWidth() ) / 2 );
            // below the light
            lightControlsNode.setOffset( lightNode.getXOffset(),
                                         lightNode.getFullBoundsReference().getMaxY() + 20 );
            // solution combo box at top center
            soluteChoiceNode.setOffset( ( getStageSize().getWidth() - soluteChoiceNode.getFullBoundsReference().getWidth() ) / 2,
                                        yMargin );
             // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
            // bottom center
            rulerNode.setOffset( ( getStageSize().getWidth() - rulerNode.getFullBoundsReference().getWidth() ) / 2,
                                 ( getStageSize().getHeight() - rulerNode.getFullBoundsReference().getHeight() - yMargin ));
        }
    }
}
