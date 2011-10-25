// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.molarity.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode.SoluteAmountSliderNode;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode.SolutionVolumeSliderNode;
import edu.colorado.phet.dilutions.common.control.SoluteControlNode;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.ConcentrationDisplayNode;
import edu.colorado.phet.dilutions.common.view.PrecipitateNode;
import edu.colorado.phet.dilutions.common.view.SaturatedIndicatorNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.colorado.phet.dilutions.molarity.model.MolarityModel;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityCanvas extends AbstractDilutionsCanvas implements Resettable {

    public MolarityCanvas( MolarityModel model, Frame parentFrame ) {

        // nodes
        BeakerNode beakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.75, model.solution );
        final PDimension cylinderSize = beakerNode.getCylinderSize();
        final double cylinderEndHeight = beakerNode.getCylinderEndHeight();
        SolutionNode solutionNode = new SolutionNode( cylinderSize, cylinderEndHeight, model.solution, model.getSolutionVolumeRange() );
        PrecipitateNode precipitateNode = new PrecipitateNode( model.solution, cylinderSize, cylinderEndHeight );
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solution.solute );
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, new Color( 235, 235, 235 ) ) {{
            setConfirmationEnabled( false );
        }};
        PDimension concentrationBarSize = new PDimension( 40, cylinderSize.getHeight() + 50 );
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( Strings.SOLUTION_CONCENTRATION, concentrationBarSize,
                                                                                          model.solution, model.getConcentrationRange() );
        SoluteAmountSliderNode soluteAmountSliderNode = new SoluteAmountSliderNode( Strings.SOLUTE_AMOUNT, Strings.NONE, Strings.LOTS,
                                                                                    new PDimension( 5, cylinderSize.getHeight() ),
                                                                                    model.solution.soluteAmount, model.getSoluteAmountRange() );
        SolutionVolumeSliderNode solutionVolumeSliderNode = new SolutionVolumeSliderNode( Strings.SOLUTION_VOLUME, Strings.LOW, Strings.FULL,
                                                                                          new PDimension( 5, 0.8 * cylinderSize.getHeight() ), //TODO 0.8 is based on specific volume range
                                                                                          model.solution.volume, model.getSolutionVolumeRange() );
        SaturatedIndicatorNode saturatedIndicatorNode = new SaturatedIndicatorNode( model.solution );

        // rendering order
        {
            addChild( solutionNode );
            addChild( beakerNode );
            addChild( precipitateNode );
            addChild( saturatedIndicatorNode );
            addChild( concentrationDisplayNode );
            addChild( soluteAmountSliderNode );
            addChild( solutionVolumeSliderNode );
            addChild( resetAllButtonNode );
            addChild( soluteControlNode ); // combo box on top
        }

        // layout
        {
            // upper left
            soluteControlNode.setOffset( 30, 30 );
            // below the Solute selector
            soluteAmountSliderNode.setOffset( soluteControlNode.getXOffset() - PNodeLayoutUtils.getOriginXOffset( soluteAmountSliderNode ),
                                              soluteControlNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beakerNode ) + 40 );
            // to the right of the Solute Amount control
            solutionVolumeSliderNode.setOffset( soluteAmountSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionVolumeSliderNode ) + 40,
                                                soluteAmountSliderNode.getYOffset() );
            // to the right of the Solution Volume control
            beakerNode.setOffset( solutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( beakerNode ) + 20,
                                  solutionVolumeSliderNode.getYOffset() );
            // in the same coordinate frame as the beaker
            solutionNode.setOffset( beakerNode.getOffset() );
            // in the same coordinate frame as the beaker
            precipitateNode.setOffset( beakerNode.getOffset() );
            // below the beaker
            saturatedIndicatorNode.setOffset( beakerNode.getXOffset() + ( cylinderSize.getWidth() / 2 ) - ( saturatedIndicatorNode.getFullBoundsReference().getWidth() / 2 ),
                                              beakerNode.getYOffset() + ( 0.9 * cylinderSize.getHeight() ) - saturatedIndicatorNode.getFullBoundsReference().getHeight() );
            // to the right of the beaker, vertically aligned with beaker's bottom
            concentrationDisplayNode.setOffset( beakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( concentrationDisplayNode ) + 50,
                                                beakerNode.getFullBoundsReference().getMaxY() - concentrationDisplayNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( concentrationDisplayNode ) );
            // centered above concentration bar
            resetAllButtonNode.setOffset( concentrationDisplayNode.getXOffset() + ( concentrationBarSize.getWidth() / 2 ) - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          soluteControlNode.getYOffset() );
        }
    }

    public void reset() {
        // do nothing
    }
}
