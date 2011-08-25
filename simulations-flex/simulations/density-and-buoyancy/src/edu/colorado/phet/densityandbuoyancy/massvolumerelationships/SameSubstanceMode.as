//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.massvolumerelationships {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;
import edu.colorado.phet.densityandbuoyancy.view.modes.*;

/**
 * Requested modifications for Abraham, Gelder and Greenbowe, made by copying and modifying the version from the density sim.
 * Density simulation mode which shows 4 blocks of the same density (though different colors)
 */
public class SameSubstanceMode extends Mode {

    public function SameSubstanceMode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super( canvas );
    }

    private var _block1: Block;
    private var _block2: Block;
    private var _block3: Block;
    private var _block4: Block;

    override public function init(): void {
        super.init();
        const model: DensityAndBuoyancyModel = canvas.model;
        var density: Number = 800; //Showing the blocks as partially floating allows easier visualization of densities

        //The masses below were selected so that calculations with 2 decimal points come up exactly equal
        _block1 = Block.newBlockDensityMass( density, 4, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.CUSTOM );
        model.addDensityObject( _block1 );

        _block2 = Block.newBlockDensityMass( density, 3, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.CUSTOM );
        model.addDensityObject( _block2 );

        _block3 = Block.newBlockDensityMass( density, 2, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.CUSTOM );
        model.addDensityObject( _block3 );

        _block4 = Block.newBlockDensityMass( density, 1, 0, 0, DensityAndBuoyancyConstants.YELLOW, model, Material.CUSTOM );
        model.addDensityObject( _block4 );

        canvas.model.addDensityObject( new Scale( Scale.GROUND_SCALE_X_LEFT, Scale.GROUND_SCALE_Y, canvas.model ) );

        resetBlockPositions();
    }

    override public function reset(): void {
        super.reset();
        resetBlockPositions();
    }

    private function resetBlockPositions(): void {
        _block1.setPosition( -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, _block1.getHeight() / 2 );
        _block2.setPosition( -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 - _block1.getWidth(), _block2.getHeight() / 2 );
        _block3.setPosition( DensityAndBuoyancyConstants.POOL_WIDTH_X / 2, _block3.getHeight() / 2 );
        _block4.setPosition( DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 + _block3.getWidth(), _block4.getHeight() / 2 );
    }
}
}