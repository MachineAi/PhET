/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import JSci.maths.Mapping;
import JSci.maths.NumericalMath;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 28, 2005
 * Time: 10:47:11 PM
 * Copyright (c) Sep 28, 2005 by Sam Reid
 */

public class ForceMode implements UpdateMode, Derivable {
    private Vector2D.Double netForce;
//    private double t = 0.0;

    public ForceMode() {
        this.netForce = new Vector2D.Double();
    }

    public void setNetForce( AbstractVector2D netForce ) {
        this.netForce = new Vector2D.Double( netForce );
//        System.out.println( "netForce = " + netForce );
    }

    protected Vector2D.Double getNetForce() {
        return netForce;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
//        updateTechRK( model, body, dt );
//        updateFlanRK( model, body, dt );
//        updateJSCiRK4( model, body, dt );
//        updateJSCILeapfrog( model, body, dt );
//        System.out.println( "model.getTotalEnergy( body ) = " + model.getTotalEnergy( body ) );

//        updateRK4( model, body, dt );
        int num=4;
        for( int i = 0; i < num; i++ ) {
            updateEuler( model, body, dt/num);
        }
    }


    double Tmin = 0.0;
    double Tmax = 10.0;         //endpoints
    int Ntimes = 100;
    double h = ( Tmax - Tmin ) / Ntimes;
    double t = Tmin;

    private void updateRK4( final EnergyConservationModel model, final Body body, double dt ) {
        double y[] = new double[]{body.getY(),body.getVelocity().getY()};
//        for( t = Tmin; t <= Tmax; t += h ) {
//        System.out.println( "RK4 t=" + t + " , x= " + y[0] + ", v= " + y[1] );//printout
        RK4.Diff diff = new RK4.Diff() {
            public void f( double t, double y[], double F[] ) {
//                F[0] = y[1];  // RHS of first equation
                F[0] = body.getVelocity().getY();
                F[1] = model.getGravity();
            }
        };
//            w.println( t + " " + y[0] + " " + y[1] );//output to file
        RK4.rk4( t, y, h, 2, diff );
        body.setPosition( body.getX(), y[0] );
        body.setVelocity( body.getVelocity().getX(), y[1] );
//        }
    }

//    double oldY;
//    double oldV;
//
//    private void updateJSCILeapfrog( EnergyConservationModel model, final Body body, double dt ) {
//
//        double[]v = new double[3];
//        v[0] = oldV;
//        v[1] = body.getVelocity().getY();
//        NumericalMath.leapFrog( v, new Mapping() {
//            public double map( double v ) {
//                return 9.8 * 8;
//            }
//        }, dt );
//
//        body.setVelocity( 0, v[2] );
//
//        double[]y = new double[3];
//        y[0] = oldY;
//        y[1] = body.getY();
//        NumericalMath.leapFrog( y, new Mapping() {
//            public double map( double x ) {
//                return body.getVelocity().getY();
//            }
//        }, dt );
//        body.setPosition( body.getX(), y[2] );
//        this.oldY = body.getY();
//        this.oldV = body.getVelocity().getY();
//    }

    private void updateJSCiRK4( EnergyConservationModel model, final Body body, double dt ) {
        double[]v = new double[2];
        v[0] = body.getVelocity().getY();
        NumericalMath.rungeKutta4( v, new Mapping() {
            public double map( double v ) {
                return getNetForce().getY();
            }
        }, dt );

        body.setVelocity( 0, v[1] );

        double[]y = new double[2];
        y[0] = body.getY();
        NumericalMath.rungeKutta4( y, new Mapping() {
            public double map( double x ) {
                return body.getVelocity().getY();
            }
        }, dt );
        body.setPosition( body.getX(), y[1] );
    }

    private void updateFlanRK( EnergyConservationModel model, Body body, double dt ) {
        double y = body.getY();
        double t = 0;
        double tFinal = t + dt;
        DerivFunction derivFunction = new DerivFunction() {
            public double deriv( double x, double y ) {
                double dydt = 9.8;//
//                double dydx = a * x * Math.sqrt( 1.0 - y * y );
                return dydt;
            }
        };
        double yFinal = FlanaganRK4th.fourthOrder( derivFunction, t, y, tFinal, dt );
        body.setVelocity( body.getVelocity().getX(), yFinal );
    }

    static interface Diff {
        double diff( double val );
    }

    private void updateTechRK( EnergyConservationModel model, Body body, double dt ) {
        double xNew = integrate( body.getX(), body.getVelocity().getX(), dt, new Diff() {
            public double diff( double val ) {
                return getNetForce().getX();
            }
        } );
        double[]pos = new double[]{body.getX(), body.getY()};
        double[]vel = new double[]{body.getVelocity().getX(), body.getVelocity().getY()};
        System.out.println( "ORIGbody.getVelocity() = " + body.getVelocity() );
        JavaTechRK4th.step( 0, dt, pos, vel, this );
        AbstractVector2D acceleration = getNetForce().getScaledInstance( 1.0 / body.getMass() );

//        double []velocity = new double[]{body.getVelocity().getX(), body.getVelocity().getY()};
//        double []acc = new double[]{body.getAcceleration().getX(), body.getAcceleration().getY()};
//        JavaTechRK4th.step( 0, dt, velocity, acc, new Derivable() {
//            public double deriv( int i, double pos, double v, double t ) {
//                return 0;
//            }
//        } );
        double vx = ( pos[0] - body.getX() ) / dt;
        double vy = ( pos[1] - body.getY() ) / dt;

        body.setState( acceleration, new Vector2D.Double( vx, vy ), new Point2D.Double( pos[0], pos[1] ) );
        System.out.println( "FINabody.getVelocity() = " + body.getVelocity() );
//        t += dt;
    }

    private double integrate( double x, double v, double dt, Diff diff ) {
        double[]rkX = new double[]{x};
        double[]rkV = new double[]{v};
        JavaTechRK4th.step( 0, dt, rkX, rkV, new Derivable() {
            public double deriv( int i, double pos, double v, double t ) {
                return 0;
            }
        } );
        return 0;
    }

    public void updateEuler( EnergyConservationModel model, Body body, double dt ) {
        AbstractVector2D acceleration = getNetForce().getScaledInstance( 1.0 / body.getMass() );
        AbstractVector2D velocity = body.getVelocity().getAddedInstance( acceleration.getScaledInstance( dt ) );
        Point2D newPosition = new Point2D.Double( body.getX() + velocity.getX() * dt, body.getY() + velocity.getY() * dt );
        body.setState( acceleration, velocity, newPosition );
//        t += dt;
    }

    public double deriv( int i, double var, double vel, double t ) {
        if( i == 0 ) { // x variable
            return netForce.getX();
        }
        else {// y variable
//            System.out.println( "netForce.getY() = " + netForce.getY() );
//            return netForce.getY()/100;
//            return 9.8;
            return netForce.getY();
        }
    }
}
