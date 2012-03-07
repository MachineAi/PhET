// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawModel;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.common.view.DebugLocationNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawCanvas extends BLLCanvas implements Resettable {

    public static enum LightRepresentation {BEAM, PHOTONS}
    private final Property<LightRepresentation> lightRepresentation = new Property<LightRepresentation>( LightRepresentation.BEAM );

    public enum WavelengthType {LAMBDA_MAX, VARIABLE}
    private final Property<WavelengthType> wavelengthType = new Property<WavelengthType>( WavelengthType.LAMBDA_MAX );

    public BeersLawCanvas( final BeersLawModel model, Frame parentFrame ) {

        // Nodes
        PNode lightNode = new LightNode( model.light, model.mvt );
        PNode wavelengthNode = new WavelengthControlNode( model.light, wavelengthType );
        PNode solutionControlsNode = new SolutionControlsNode( model.getSolutions(), model.solution );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, BLLConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};
        PNode rulerNode = new BLLRulerNode( model.ruler, model.mvt );
        PNode cuvetteNode = new CuvetteNode( model.cuvette, model.solution, model.mvt, 0.1 /* snapInterval, cm */ );
        PNode detectorNode = new ATDetectorNode( model.detector, model.mvt );
        PNode debugLocationNode = new DebugLocationNode( model.mvt );
        PNode beamNode = new BeamNode( model.beam, lightRepresentation, model.mvt );

        // Rendering order
        {
            addChild( wavelengthNode );
            addChild( resetAllButtonNode );
            addChild( detectorNode );
            addChild( cuvetteNode );
            addChild( beamNode );
            addChild( lightNode );
            addChild( rulerNode );
            addChild( solutionControlsNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( debugLocationNode );
            }
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double xMargin = 20;
            final double yMargin = 20;
            // below the light
            wavelengthNode.setOffset( lightNode.getFullBoundsReference().getMinX(),
                                     lightNode.getFullBoundsReference().getMaxY() + 20 );
            // solution combo box at top center
            solutionControlsNode.setOffset( ( getStageSize().getWidth() - solutionControlsNode.getFullBoundsReference().getWidth() ) / 2,
                                            yMargin );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
            // location debugger left of Reset All button
            debugLocationNode.setOffset( resetAllButtonNode.getFullBoundsReference().getMinX() - 40,
                                         resetAllButtonNode.getFullBoundsReference().getCenterY() );
        }
    }

    public void reset() {
        lightRepresentation.reset();
    }
}
