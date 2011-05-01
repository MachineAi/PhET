// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.model.property2.Observer;
import edu.colorado.phet.common.phetcommon.model.property2.Property;
import edu.colorado.phet.common.phetcommon.model.property2.UpdateEvent;

/**
 * This provides a 2-way mapping between properties, but where one of the values is scaled by a specified factor.
 * This is used to make the fluid density control work flexibly with different units.
 *
 * @author Sam Reid
 */
public class ScaledDoubleProperty extends Property<Double> {
    public ScaledDoubleProperty( final Property<Double> property, final double scale ) {
        super( property.getValue() * scale );
        property.addObserver( new Observer<Double>() {
            @Override public void update( UpdateEvent<Double> event ) {
                setValue( event.value * scale );
            }
        } );
        addObserver( new Observer<Double>() {
            @Override public void update( UpdateEvent<Double> event ) {
                property.setValue( event.value / scale );
            }
        } );
    }
}
