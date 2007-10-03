package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.RotationUtil;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:10:11 AM
 */
public class TorqueModel extends RotationModel {

    private DefaultTemporalVariable appliedForce = new DefaultTemporalVariable();//todo: sort out difference between appliedForce and appliedForceMagnitude
    private DefaultTemporalVariable appliedForceMagnitude = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedTorque = new DefaultTemporalVariable();

    private DefaultTemporalVariable brakeForceMagnitude = new DefaultTemporalVariable();
    private DefaultTemporalVariable brakeTorque = new DefaultTemporalVariable();

    private DefaultTemporalVariable netForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable netTorque = new DefaultTemporalVariable();

    private DefaultTemporalVariable momentOfInertia = new DefaultTemporalVariable();
    private DefaultTemporalVariable angularMomentum = new DefaultTemporalVariable();

    private AppliedForce appliedForceObject = new AppliedForce();
    private AppliedForce brakeForceObject = new AppliedForce();

    private UpdateStrategy forceDriven = new ForceDriven();
    private ArrayList listeners = new ArrayList();
    private boolean allowNonTangentialForces = false;
    private boolean showComponents = true;
    private boolean inited = false;
    private double brakePressure = 0;
    private boolean overwhelmingBrake = false;

    public TorqueModel( ConstantDtClock clock ) {
        super( clock );
        getRotationPlatform().setUpdateStrategy( forceDriven );
        inited = true;
        clear();
        appliedForceObject.setRadius( RotationPlatform.MAX_RADIUS );
        brakeForceObject.setRadius( RotationPlatform.MAX_RADIUS );
        getRotationPlatform().getVelocityVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                updateBrakeForce();
            }
        } );
        getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                if ( appliedForceObject.getRadius() > getRotationPlatform().getRadius() ) {
                    appliedForceObject.setRadius( getRotationPlatform().getRadius() );
                    updateAppliedForceFromRF();
                }

            }

            public void innerRadiusChanged() {
                if (appliedForceObject.getRadius()<getRotationPlatform().getInnerRadius()){
                    appliedForceObject.setRadius( getRotationPlatform().getInnerRadius() );
                    updateAppliedForceFromRF();
                }
            }
        } );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        momentOfInertia.addValue( getRotationPlatform().getMomentOfInertia(), getTime() );
        angularMomentum.addValue( getRotationPlatform().getMomentOfInertia() * getRotationPlatform().getVelocity(), getTime() );
        defaultUpdate( appliedTorque );
        defaultUpdate( appliedForce );
        defaultUpdate( appliedForceMagnitude );
        defaultUpdate( netForce );
        defaultUpdate( netTorque );
        defaultUpdate( brakeTorque );
        defaultUpdate( brakeForceMagnitude );

        brakeForceObject.stepInTime( dt, getTime() );
        appliedForceObject.stepInTime( dt, getTime() );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & clarity
    }

    private void defaultUpdate( ITemporalVariable variable ) {
        variable.addValue( variable.getValue(), getTime() );
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        appliedTorque.setPlaybackTime( time );
        appliedForce.setPlaybackTime( time );
        angularMomentum.setPlaybackTime( time );
        momentOfInertia.setPlaybackTime( time );
        appliedForceMagnitude.setPlaybackTime( time );
        netForce.setPlaybackTime( time );
        netTorque.setPlaybackTime( time );
        brakeTorque.setPlaybackTime( time );
        brakeForceMagnitude.setPlaybackTime( time );

        appliedForceObject.setPlaybackTime( time );
        brakeForceObject.setPlaybackTime( time );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & clarity
    }

    public void clear() {
        super.clear();
        if ( inited ) {
            appliedTorque.clear();
            appliedForce.clear();
            angularMomentum.clear();
            momentOfInertia.clear();

            appliedForceMagnitude.clear();

            appliedForceObject.clear();
            brakeForceObject.clear();
            netForce.clear();
            netTorque.clear();
            brakeTorque.clear();
            brakeForceMagnitude.clear();
            brakePressure = 0;
        }
    }

    public ITemporalVariable getBrakeForceMagnitudeVariable() {
        return brakeForceMagnitude;
    }

    public double getBrakeForceMagnitude() {
        return brakeForceObject.getForceMagnitude();
    }

    public double getBrakePressure() {
        return brakePressure;
    }

    public void setBrakePressure( double brakePressure ) {
        if ( brakePressure != this.brakePressure ) {
            this.brakePressure = brakePressure;
            updateBrakeForce();
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).brakePressureChanged();
            }
        }
    }

    private Line2D.Double computeBrakeForce() {
        Point2D.Double src = new Point2D.Double( getRotationPlatform().getRadius() * Math.sqrt( 2 ) / 2, -getRotationPlatform().getRadius() * Math.sqrt( 2 ) / 2 );
        AbstractVector2D vec = getBrakeForceVector();
        if ( vec == null ) {
            return new Line2D.Double( src, src );
        }
        Point2D dst = vec.getDestination( src );

        return new Line2D.Double( src, dst );
    }

    private AbstractVector2D getBrakeForceVector() {
        boolean clockwise = getRotationPlatform().getVelocity() > 0;
        if ( getRotationPlatform().getVelocity() == 0 ) {
            if ( Math.abs( getAppliedTorqueSignedValue() ) == 0 ) {
                return null;
            }
            clockwise = getAppliedTorqueSignedValue() > 0;
        }
        double magnitude = brakePressure;
        double requestedBrakeTorqueMagnitude = Math.abs( brakePressure * getRotationPlatform().getRadius() );
        double appliedTorqueMagnitude = Math.abs( appliedForceObject.getTorque( getPlatformCenter() ) );
        //todo: remove need for magic number
        double VELOCITY_THRESHOLD = 1;
        if ( requestedBrakeTorqueMagnitude > appliedTorqueMagnitude && Math.abs( getRotationPlatform().getVelocity() ) < VELOCITY_THRESHOLD ) {
            magnitude = appliedTorqueMagnitude / getRotationPlatform().getRadius();
            overwhelmingBrake = true;
        }
        else {
            overwhelmingBrake = false;
        }
        return Vector2D.Double.parseAngleAndMagnitude( magnitude, Math.PI / 4 + ( clockwise ? Math.PI : 0 ) );
    }

    private void updateBrakeForce() {
        brakeForceObject.setValue( computeBrakeForce() );
        brakeForceMagnitude.setValue( getBrakeForceObject().getSignedForce( getRotationPlatform().getCenter() ) );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).brakeForceChanged();
        }
        updateNetForce();
    }

    private void updateNetForce() {
        netForce.setValue( getSignedNetForceValue() );
    }

    private double getSignedNetForceValue() {
        return getAppliedForceObject().getSignedForce( getRotationPlatform().getCenter() ) +
               getBrakeForceObject().getSignedForce( getRotationPlatform().getCenter() );
    }

    public ITemporalVariable getAppliedTorqueTimeSeries() {
        return appliedTorque;
    }

    public ITemporalVariable getAppliedForceVariable() {
        return appliedForce;
    }

    public ITemporalVariable getNetForce() {
        return netForce;
    }

    public ITemporalVariable getNetTorque() {
        return netTorque;
    }

    public ITemporalVariable getBrakeTorque() {
        return brakeTorque;
    }

    public UpdateStrategy getForceDriven() {
        return forceDriven;
    }

    public ITemporalVariable getMomentOfInertiaTimeSeries() {
        return momentOfInertia;
    }

    public ITemporalVariable getAngularMomentumTimeSeries() {
        return angularMomentum;
    }

    public boolean isAllowNonTangentialForces() {
        return allowNonTangentialForces;
    }

    public void setAllowNonTangentialForces( boolean selected ) {
        this.allowNonTangentialForces = selected;
    }

    public double getAppliedForceMagnitude() {
        return getAppliedForce().getP1().distance( getAppliedForce().getP2() );
    }

    public boolean isShowComponents() {
        return showComponents;
    }

    public void setShowComponents( boolean selected ) {
        if ( selected != showComponents ) {
            this.showComponents = selected;
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).showComponentsChanged();
            }
        }
    }

    public ITemporalVariable getRadiusSeries() {
        return appliedForceObject.getRadiusSeries();
    }

    public void setAppliedForceMagnitude( double appliedForceMag ) {
        appliedForceMagnitude.setValue( appliedForceMag );
        updateAppliedForceFromRF();
    }

    private void updateAppliedForceFromRF() {
        setAppliedForce( new Line2D.Double( getRotationPlatform().getCenter().getX(),
                                            getRotationPlatform().getCenter().getY() - getRadiusSeries().getValue(),
                                            getRotationPlatform().getCenter().getX() + appliedForceMagnitude.getValue(),
                                            getRotationPlatform().getCenter().getY() - getRadiusSeries().getValue() ) );
    }

    public void setAppliedForceRadius( double r ) {
        appliedForceObject.setRadius( r );
        updateAppliedForceFromRF();
    }

    public Line2D.Double getBrakeForceValue() {
        return brakeForceObject.toLine2D();
    }

    public AppliedForce getBrakeForceObject() {
        return brakeForceObject;
    }

    public AppliedForce getAppliedForceObject() {
        return appliedForceObject;
    }

    public ITemporalVariable getBrakeRadiusSeries() {
        return brakeForceObject.getRadiusSeries();
    }

    private double getAppliedTorqueSignedValue() {
        return appliedForceObject.getTorque( getPlatformCenter() );
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            appliedTorque.setValue( getAppliedTorqueSignedValue() );
            double origAngVel = motionBody.getVelocity();
            brakeTorque.setValue( brakeForceObject.getTorque( getRotationPlatform().getCenter() ) );
//            System.out.println( "net torque value=" + ( appliedTorque.getValue() + brakeTorque.getValue() ) + ", applied=" + appliedTorque.getValue() + ", brake=" + brakeTorque.getValue() );
            TorqueModel.this.netTorque.setValue( appliedTorque.getValue() + brakeTorque.getValue() );//todo: should probably update even while paused

            //todo: better handling for zero moment?
            double acceleration = getMomentOfInertia() > 0 ? netTorque.getValue() / getMomentOfInertia() : 0;

            //if brake overwhelms applied force, do not change direction
            double proposedVelocity = motionBody.getVelocity() + acceleration * dt;
            if ( overwhelmingBrake ) {
//                proposedVelocity=origAngVel*0.99;
                proposedVelocity = 0.0;
                acceleration = 0.0;
            }
//            double proposedVelocity = 0.0;
//            if ( MathUtil.getSign( proposedVelocity ) != MathUtil.getSign( origAngVel ) && Math.abs( brakeTorque.getValue() ) >= Math.abs( appliedTorque.getValue() ) ) {
//                acceleration = 0.0;
////                System.out.println( "TorqueModel$ForceDriven.update" );
//            }
//            if ( overwhelmingBrake ) {
//                acceleration = 0.0;
//                proposedVelocity = 0.0;
//            }

            motionBody.addAccelerationData( acceleration, time );
            motionBody.addVelocityData( proposedVelocity, time );

            //if the friction causes the velocity to change sign, set the velocity to zero?
//            motionBody.addPositionData( motionBody.getPosition() + ( motionBody.getVelocity() + origAngVel ) / 2.0 * dt, time );
            motionBody.addPositionData( motionBody.getPosition() + proposedVelocity * dt, time );
        }


        private double getMomentOfInertia() {
            return getRotationPlatform().getMomentOfInertia();
        }
    }

    private Point2D getPlatformCenter() {
        return getRotationPlatform().getCenter();
    }

    public Line2D.Double getAppliedForce() {
        return appliedForceObject.toLine2D();
    }

    public Line2D.Double getTangentialAppliedForce() {
        return getTangentialAppliedForce( getAppliedForce() );
    }

    /*
     * Computes the allowed portion of the desired applied appliedForce, result depends on whether allowNonTangentialForces is true
     */
    public void setAllowedAppliedForce( Line2D.Double appliedForce ) {
        setAppliedForce( getAllowedAppliedForce( appliedForce ) );
    }

    public void setAppliedForce( Line2D.Double appliedForce ) {
        if ( !RotationUtil.lineEquals( getAppliedForce(), appliedForce ) ) {
            appliedForceObject.setValue( appliedForce );
            this.appliedForce.setValue( getAppliedForceObject().getSignedForce( getRotationPlatform().getCenter() ) );
            appliedTorque.setValue( appliedForceObject.getTorque( getRotationPlatform().getCenter() ) );

            updateNetForce();
            notifyAppliedForceChanged();
        }
    }

    private Line2D.Double getAllowedAppliedForce( Line2D.Double appliedForce ) {
        if ( !allowNonTangentialForces ) {
            appliedForce = getTangentialAppliedForce( appliedForce );
        }
        return appliedForce;
    }

    private Line2D.Double getTangentialAppliedForce( Line2D.Double appliedForce ) {
        Vector2D.Double v = new Vector2D.Double( appliedForce.getP1(), getRotationPlatform().getCenter() );
        v.rotate( Math.PI / 2 );
        if ( v.dot( new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ) ) < 0 ) {
            v.rotate( Math.PI );
        }

        AbstractVector2D x = v;
        if ( x.getMagnitude() == 0 ) {
            return new Line2D.Double( appliedForce.getP1(), appliedForce.getP1() );
        }
        double magnitude = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ).dot( x.getNormalizedInstance() );
        if ( magnitude != 0 ) {
            x = x.getInstanceOfMagnitude( magnitude );
        }
        else {
            x = new Vector2D.Double( 0, 0 );
        }
        return new Line2D.Double( appliedForce.getP1(), x.getDestination( appliedForce.getP1() ) );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyAppliedForceChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).appliedForceChanged();
        }
    }

    public static interface Listener {
        void appliedForceChanged();

        void showComponentsChanged();

        void brakeForceChanged();

        void brakePressureChanged();
    }

    public static class Adapter implements Listener {
        public void appliedForceChanged() {
        }

        public void showComponentsChanged() {
        }

        public void brakeForceChanged() {
        }

        public void brakePressureChanged() {
        }
    }

}
