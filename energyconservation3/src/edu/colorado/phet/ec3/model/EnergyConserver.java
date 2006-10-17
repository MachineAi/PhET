/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 11:38:49 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class EnergyConserver {
    public void fixEnergy( Body body, double desiredMechanicalEnergy ) {
        if( body.getThrust().getMagnitude() != 0 ) {
            return;
        }
//        EnergyDebugger.stepFinished( model, body );
        double speedThreshold = 1;//reduced from 20.
        for( int i = 0; i < 10; i++ ) {
            if( body.getSpeed() > speedThreshold ) {
                boolean done = conserveEnergyViaV( body, desiredMechanicalEnergy );
                if( done ) {
                    break;
                }
            }
            else {
            }
        }
        if( Math.abs( body.getGravity() ) > 1.0 ) {
            for( int i = 0; i < 3; i++ ) {
                boolean done = conserveEnergyViaH( body, desiredMechanicalEnergy );
                if( done ) {
                    break;
                }
            }
        }
//        double mechEnergy = model.getMechanicalEnergy( body );
//        System.out.println( "requested mechEnergy = " + desiredMechanicalEnergy + ", obtained me=" + mechEnergy );
    }

    private boolean conserveEnergyViaV( Body body, double desiredMechanicalEnergy ) {
        double dE = getDE( body, desiredMechanicalEnergy );
        if( dE == 0 ) {
            return true;
        }
        //alter the velocity to account for this difference.
//        double dv = dE / body.getMass() / body.getSpeed();
        double dv = dE / body.getMass() / body.getSpeed();
        AbstractVector2D dvVector = body.getVelocity().getInstanceOfMagnitude( -dv );
        body.setVelocity( dvVector.getAddedInstance( body.getVelocity() ) );
        return false;
    }

    private double getDE( Body body, double desiredMechanicalEnergy ) {
        return body.getMechanicalEnergy() - desiredMechanicalEnergy;
    }

    private boolean conserveEnergyViaH( Body body, double desiredMechEnergy ) {
        double dE = getDE( body, desiredMechEnergy );
        if( dE == 0 ) {
            return true;
        }
        double dh = dE / body.getMass() / body.getGravity();
        body.translate( 0, dh );
        return false;
//        System.out.println( "------->requested mechEnergy = " + desiredMechEnergy+ ", obtained me=" + model.getMechanicalEnergy( body ));
    }
}
