package edu.colorado.phet.common.batteryvoltage.phys2d.propagators;

import edu.colorado.phet.common.batteryvoltage.phys2d.DoublePoint;
import edu.colorado.phet.common.batteryvoltage.phys2d.Particle;
import edu.colorado.phet.common.batteryvoltage.phys2d.Propagator;

public class ResetAcceleration implements Propagator {
    public void propagate( double time, Particle p ) {
        DoublePoint zero = new DoublePoint();
        p.setAcceleration( zero );
    }
}
