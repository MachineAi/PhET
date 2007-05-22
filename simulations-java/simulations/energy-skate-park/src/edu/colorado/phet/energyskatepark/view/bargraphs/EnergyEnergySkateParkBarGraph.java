/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:25 PM
 */

public class EnergyEnergySkateParkBarGraph extends EnergySkateParkBarGraph {
    public EnergyEnergySkateParkBarGraph( EnergySkateParkSimulationPanel canvas, final EnergySkateParkModel energySkateParkModel, double scale) {
        super( canvas, energySkateParkModel, EnergySkateParkStrings.getString( "properties.energy" ), scale);
        final ValueAccessor[] energyAccess = new ValueAccessor[]{
                new ValueAccessor.KineticEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.PotentialEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.ThermalEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() ),
                new ValueAccessor.TotalEnergy( canvas.getEnergyConservationModule().getEnergyLookAndFeel() )
        };
        Variable[] v = toVariableArray( energyAccess, energySkateParkModel );
        setVariables( v );
    }


}
