/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * BilliardBall models the hydrogen atom as a billiard ball.
 * <p>
 * Physical representation:
 * The ball is spherical, with its local origin at its center.
 * <p>
 * Collision behavior:
 * When photons and alpha particles collide with the ball,
 * they bounce off as if the ball were a rigid body.
 * <p>
 * Absorption behavior:
 * Does not absorb photons or alpha particles.
 * <p>
 * Emission behavior:
 * Does not emit photons or alpha particles.
 */
public class BilliardBallModel extends AbstractHydrogenAtom {

    private static final double DEFAULT_RADIUS = 100;
    
    private static final double MIN_DEFLECTION_ANGLE = Math.toRadians( 120 );
    private static final double MAX_DEFLECTION_ANGLE = Math.toRadians( 170 );
    
    private double _radius;
    private Shape _shape;
    
    public BilliardBallModel( Point2D position ) {
        this( position, DEFAULT_RADIUS );
    }
    
    public BilliardBallModel( Point2D position, double radius ) {
        super( position, 0 /* orientation */ );
        _radius = radius;
        updateShape();
    }
    
    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }
    
    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
        updateShape();
    }
    
    public double getRadius() {
        return _radius;
    }
    
    public boolean contains( Point2D p ) {
        return _shape.contains( p );
    }
    
    private void updateShape() {
        _shape = new Ellipse2D.Double( getX() - _radius, getY() - _radius, 2 * _radius, 2 * _radius );
    }
    
    public void detectCollision( Photon photon ) {
        Point2D position = photon.getPosition();
        if ( _shape.contains( position ) ) {
            final int sign = ( position.getX() > getX() ) ? 1 : -1;
            final double deflection = sign * getRandomAngle( MIN_DEFLECTION_ANGLE, MAX_DEFLECTION_ANGLE );
            final double orientation = photon.getOrientation() + deflection;
            photon.setOrientation( orientation );
            photon.stepInTime( 1 ); //HACK to prevent particle from getting stuck inside atom
        }
    }
    
    public void detectCollision( AlphaParticle alphaParticle ) {
        Point2D position = alphaParticle.getPosition();
        if ( _shape.contains( position ) ) {
            final int sign = ( position.getX() > getX() ) ? 1 : -1;
            final double deflection = sign * getRandomAngle( MIN_DEFLECTION_ANGLE, MAX_DEFLECTION_ANGLE );
            final double orientation = alphaParticle.getOrientation() + deflection;
            alphaParticle.setOrientation( orientation );
            alphaParticle.stepInTime( 1 ); //HACK to prevent particle from getting stuck inside atom
        }
    }
    
    private double getRandomAngle( double min, double max ) {
        assert( max > min );
        return min + ( Math.random() * ( max - min ) );
    }
}
