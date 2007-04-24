package edu.colorado.phet.energyskatepark.serialization;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.SPoint2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 7, 2006
 * Time: 12:49:52 PM
 * Copyright (c) Nov 7, 2006 by Sam Reid
 */

public class EnergySkateParkModuleBean {
    private ArrayList bodies = new ArrayList();
    private ArrayList splines = new ArrayList();
    private double gravity = EnergySkateParkModel.G_EARTH;

    public EnergySkateParkModuleBean() {
    }

    public EnergySkateParkModuleBean( EnergySkateParkModule module ) {
        for( int i = 0; i < module.getEnergySkateParkModel().getNumBodies(); i++ ) {
            addBody( module.getEnergySkateParkModel().getBody( i ) );
        }
        for( int i = 0; i < module.getEnergySkateParkModel().getNumSplines(); i++ ) {
            EnergySkateParkSpline splineSurface = module.getEnergySkateParkModel().getSpline( i );
//            if( !( splineSurface instanceof FloorSpline ) ) {
            addSplineSurface( splineSurface );             //todo: handle floor
//            }
        }
        this.gravity = module.getEnergySkateParkModel().getGravity();
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
    }

    private void addSplineSurface( EnergySkateParkSpline splineSurface ) {
        splines.add( new SplineElement( splineSurface ) );
    }

    private void addBody( Body body ) {
        bodies.add( new BodyElement( body ) );
    }

    public ArrayList getSplines() {
        return splines;
    }

    public void setSplines( ArrayList splines ) {
        this.splines = splines;
    }

    public ArrayList getBodies() {
        return bodies;
    }

    public void setBodies( ArrayList bodies ) {
        this.bodies = bodies;
    }

    public void apply( EnergySkateParkModule module ) {
        module.getEnergySkateParkModel().removeAllBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = module.createBody();
            ( (BodyElement)bodies.get( i ) ).apply( body );
            module.getEnergySkateParkModel().addBody( body );
        }

        module.getEnergySkateParkModel().removeAllSplineSurfaces();
        for( int i = 0; i < splines.size(); i++ ) {
            SplineElement spline = (SplineElement)splines.get( i );
            module.getEnergySkateParkModel().addSplineSurface( spline.toEnergySkateParkSpline() );
        }
        module.getEnergySkateParkModel().updateFloorState();
        module.getEnergySkateParkModel().setGravity( gravity );
        module.setSkaterCharacter( module.getDefaultSkaterCharacter() );
    }


    public static class BodyElement {
        private Point2D.Double position;
        private Point2D.Double velocity;
        private Point2D.Double acceleration;

        //        private double cmRotation;
        private double angularVelocity;
        private double frictionCoefficient;
        private double mass;
        private double thermalEnergy;
        private double attachmentPointRotation;
        private boolean freeFrame;

        public BodyElement() {
        }

        public BodyElement( Body body ) {
            position = new Point2D.Double( body.getPosition().getX(), body.getPosition().getY() );
            velocity = new Point2D.Double( body.getVelocity().getX(), body.getVelocity().getY() );
//            acceleration = new Point2D.Double( body.getAcceleration().getX(), body.getAcceleration().getY() );
//            cmRotation = body.getCMRotation();
            angularVelocity = body.getAngularVelocity();
            frictionCoefficient = body.getFrictionCoefficient();
            mass = body.getMass();
            thermalEnergy = body.getThermalEnergy();
            freeFrame = body.isFreeFallMode();
            this.attachmentPointRotation = body.getAngle();
        }

        public void apply( Body body ) {
            body.setPosition( position.getX(), position.getY() );
            body.setVelocity( velocity.x, velocity.y );
//            body.setAcceleration( acceleration.x, acceleration.y );

            body.setAngularVelocity( angularVelocity );
            body.setFrictionCoefficient( frictionCoefficient );
            body.setMass( mass );
//            body.setThermalEnergy( thermalEnergy );
//            body.convertToFreefall( freeFrame );
//            body.setCMRotation( cmRotation );
//            body.setAttachmentPointRotation( attachmentPointRotation );
        }

        public boolean isFreeFrame() {
            return freeFrame;
        }

        public void setFreeFrame( boolean freeFrame ) {
            this.freeFrame = freeFrame;
        }

        public double getAttachmentPointRotation() {
            return attachmentPointRotation;
        }

        public void setAttachmentPointRotation( double attachmentPointRotation ) {
            this.attachmentPointRotation = attachmentPointRotation;
        }

//        public double getCmRotation() {
//            return cmRotation;
//        }
//
//        public void setCmRotation( double cmRotation ) {
//            this.cmRotation = cmRotation;
//        }

        public double getAngularVelocity() {
            return angularVelocity;
        }

        public void setAngularVelocity( double angularVelocity ) {
            this.angularVelocity = angularVelocity;
        }

        public double getFrictionCoefficient() {
            return frictionCoefficient;
        }

        public void setFrictionCoefficient( double frictionCoefficient ) {
            this.frictionCoefficient = frictionCoefficient;
        }

        public double getMass() {
            return mass;
        }

        public void setMass( double mass ) {
            this.mass = mass;
        }

        public double getThermalEnergy() {
            return thermalEnergy;
        }

        public void setThermalEnergy( double thermalEnergy ) {
            this.thermalEnergy = thermalEnergy;
        }

        public Point2D.Double getPosition() {
            return position;
        }

        public void setPosition( Point2D.Double position ) {
            this.position = position;
        }

        public Point2D.Double getVelocity() {
            return velocity;
        }

        public void setVelocity( Point2D.Double velocity ) {
            this.velocity = velocity;
        }

        public Point2D.Double getAcceleration() {
            return acceleration;
        }

        public void setAcceleration( Point2D.Double acceleration ) {
            this.acceleration = acceleration;
        }

    }

    public static class SplineElement {
        private Point2D[] controlPoints;
        private boolean rollerCoaster;

        public SplineElement() {
        }

        public SplineElement( EnergySkateParkSpline splineSurface ) {
            SPoint2D[] pts = splineSurface.getControlPoints();
            controlPoints = new Point2D[pts.length];
            for( int i = 0; i < pts.length; i++ ) {
                controlPoints[i] = new Point2D.Double( pts[i].getX(), pts[i].getY() );
            }
            rollerCoaster = splineSurface.isRollerCoasterMode();
        }

        public Point2D[] getControlPoints() {
            return controlPoints;
        }

        public void setControlPoints( Point2D[] controlPoints ) {
            this.controlPoints = controlPoints;
        }

        public EnergySkateParkSpline toEnergySkateParkSpline() {
            SPoint2D[] pt = new SPoint2D[controlPoints.length];
            for( int i = 0; i < pt.length; i++ ) {
                pt[i] = new SPoint2D( controlPoints[i] );
            }
            return new EnergySkateParkSpline( pt );
        }
    }
}
