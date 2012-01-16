// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.molarity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;
import edu.colorado.phet.dilutions.DilutionsSimSharing.UserComponents;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode;
import edu.colorado.phet.dilutions.common.control.ShowValuesNode;
import edu.colorado.phet.dilutions.common.control.SoluteControlNode;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.ConcentrationDisplayNode;
import edu.colorado.phet.dilutions.common.view.PrecipitateNode;
import edu.colorado.phet.dilutions.common.view.SaturatedIndicatorNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityCanvas extends AbstractDilutionsCanvas {

    private final Property<Boolean> valuesVisible = new Property<Boolean>( false );


    public MolarityCanvas( final MolarityModel model, Frame parentFrame ) {

        // beaker, with solution and precipitate inside of it
        final BeakerNode beakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), 0.75, 0.75, model.solution.solute.get().formula, new PDimension( 180, 70 ), new PhetFont( Font.BOLD, 28 ) );
        final PDimension cylinderSize = beakerNode.getCylinderSize();
        SolutionNode solutionNode = new SolutionNode( cylinderSize, beakerNode.getCylinderEndHeight(), model.solution, model.getSolutionVolumeRange() );
        PrecipitateNode precipitateNode = new PrecipitateNode( model.solution, cylinderSize, beakerNode.getCylinderEndHeight() );
        SaturatedIndicatorNode saturatedIndicatorNode = new SaturatedIndicatorNode( model.solution );

        // control for selecting solute
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solution.solute );

        // slider for controlling amount of solute
        DilutionsSliderNode soluteAmountSliderNode = new DilutionsSliderNode( UserComponents.soluteAmountSlider, Strings.SOLUTE_AMOUNT, Strings.NONE, Strings.LOTS,
                                                                              new PDimension( 5, cylinderSize.getHeight() ),
                                                                              model.solution.soluteAmount, model.getSoluteAmountRange() );

        // slider for controlling volume of solution, sized to match tick marks on the beaker
        final double volumeSliderHeight = ( model.getSolutionVolumeRange().getLength() / model.getSolutionVolumeRange().getMax() ) * cylinderSize.getHeight();
        DilutionsSliderNode solutionVolumeSliderNode = new DilutionsSliderNode( UserComponents.volumeSlider, Strings.SOLUTION_VOLUME, Strings.LOW, Strings.FULL,
                                                                                new PDimension( 5, volumeSliderHeight ),
                                                                                model.solution.volume, model.getSolutionVolumeRange() );

        // concentration display
        PDimension concentrationBarSize = new PDimension( 40, cylinderSize.getHeight() + 50 );
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( Strings.SOLUTION_CONCENTRATION, concentrationBarSize,
                                                                                          model.solution, model.getConcentrationRange() );

        // Show Values checkbox
        ShowValuesNode showValuesNode = new ShowValuesNode( valuesVisible );

        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, new Color( 235, 235, 235 ) ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( solutionNode );
            addChild( beakerNode );
            addChild( precipitateNode );
            addChild( saturatedIndicatorNode );
            addChild( concentrationDisplayNode );
            addChild( soluteAmountSliderNode );
            addChild( solutionVolumeSliderNode );
            addChild( showValuesNode );
            addChild( resetAllButtonNode );
            addChild( soluteControlNode ); // combo box on top
        }

        // layout
        {
            // upper left
            soluteControlNode.setOffset( 0, 0 );
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
            // to the left of the Reset All button
            showValuesNode.setOffset( resetAllButtonNode.getFullBoundsReference().getMinX() - showValuesNode.getFullBoundsReference().getWidth() - 20,
                                      soluteControlNode.getYOffset() );
        }
        scaleRootNodeToFitStage();
        centerRootNodeOnStage();

        // manage dynamic beaker label
        SimpleObserver labelUpdater = new SimpleObserver() {
            public void update() {
                String labelText;
                if ( model.solution.volume.get() == 0 ) {
                    labelText = "";
                }
                else if ( model.solution.getConcentration() == 0 ) {
                    labelText = Symbols.WATER;
                }
                else {
                    labelText = model.solution.solute.get().formula;
                }
                beakerNode.setLabelText( labelText );
            }
        };
        model.solution.addConcentrationObserver( labelUpdater );
        model.solution.volume.addObserver( labelUpdater );
        model.solution.solute.addObserver( labelUpdater );
    }

    @Override public void reset() {
        super.reset();
        valuesVisible.reset();
    }
}

