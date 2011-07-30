// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Model dispenser that shakes sucrose into the model.
 *
 * @author Sam Reid
 */
public class MicroSugarDispenser extends SugarDispenser<MicroModel> {

    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    private final Property<Integer> stepsOfAddingSugar = new Property<Integer>( 0 );

    public MicroSugarDispenser( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, final String sugarDispenserName, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MicroModel model ) {
        super( x, y, beaker, moreAllowed, sugarDispenserName, distanceScale, selectedType, type, model );
    }

    @Override protected void addSugarToModel( ImmutableVector2D outputPoint ) {

        //Only add a crystal every N steps, otherwise there are too many
        stepsOfAddingSugar.set( stepsOfAddingSugar.get() + 1 );
        if ( stepsOfAddingSugar.get() % 30 == 0 ) {
            //TODO: Make getPosition abstract so the particle can query the lattice?

            //Create a random crystal
            final SucroseCrystal crystal = new SucroseCrystal( outputPoint, randomAngle() );
            crystal.grow();
            model.addSucroseCrystal( crystal );
        }
    }
}
