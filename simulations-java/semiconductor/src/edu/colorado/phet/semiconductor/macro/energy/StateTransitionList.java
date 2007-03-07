/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common_semiconductor.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 16, 2004
 * Time: 9:57:07 PM
 * Copyright (c) Mar 16, 2004 by Sam Reid
 */
public class StateTransitionList implements ModelElement {
    ArrayList t = new ArrayList();
    private EnergySection energySection;

    public StateTransitionList( EnergySection energySection ) {
        this.energySection = energySection;
    }

    public void addTransition( StateTransition transition ) {
        t.add( transition );
    }

    /**
     * Returns true if the particle gets a state set.  Could be the same state...
     */
    public boolean apply( BandParticle particle, EnergySection section ) {
        for( int i = 0; i < t.size(); i++ ) {
            StateTransition stateTransition = (StateTransition)t.get( i );
            boolean ok = stateTransition.apply( particle, section );
            if( ok ) {
                return true;
            }
        }
        return false;
    }

    public void apply( EnergySection energySection ) {
        for( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            apply( bp, energySection );
        }
    }

    public void stepInTime( double dt ) {
        apply( energySection );
    }

}
