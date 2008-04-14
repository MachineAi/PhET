/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * Glacier is the model of the glacier.
 * The model was developed by Archie Paulson.
 * The model is a not physical; it is a "Hollywood" model that approximates published data.
 * This implementation is based on the model written in Python that can be found in glaciers/python/model/.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Glacier extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_X = 0; // x coordinate where the glacier starts (meters) CHANGING THIS IS UNTESTED!!
    private static final double DX = 80; // distance between x-axis sample points (meters)
    private static final double MAX_LENGTH = 80000; // maximum glacier length (meters)
    
    private static final double ELA_EQUALITY_THRESHOLD = 1; // ELAs are considered equal if they are at least this close (meters)
    private static final double U_SLIDE = 20; // downvalley ice speed (meters/year)
    private static final double U_DEFORM = 20; // contribution of vertical deformation to ice speed (meters/year)
    private static final double MIN_TIMESCALE = 20; // min value for climate change timescale
    private static final double MAX_TIMESCALE = 50; // max value for climate change timescale
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Valley _valley;
    private final Climate _climate;
    private final Clock _clock;
    
    private final ClimateListener _climateListener;
    private final ArrayList _listeners; // list of GlacierListener
    
    private double _climateChangedTime; // the time when the climate was last changed
    private double _previousELA; // the ELA the last time the climate was changed (meters)
    private double _currentELA; // current ELA at t=now (meters)
    private double[] _iceThicknessSamples; // ice thickness at t=now (meters)
    private double _averageIceThicknessSquares; // average of the squares of the ice thickness samples
    private boolean _steadyState; // is the glacier in the steady state?
    
    private final double _maxElevation; // the highest elevation on the glacier, at MIN_X (meters)
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Glacier( Valley valley, Climate climate, Clock clock ) {
        super();
        
        _valley = valley;
        
        _climate = climate;
        _climateListener = new ClimateListener() {

            public void temperatureChanged() {
                handleClimateChange();
            }
            
            public void snowfallChanged() {
                handleClimateChange();
            }

            public void snowfallReferenceElevationChanged() {
                handleClimateChange();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _clock = clock;
        
        _listeners = new ArrayList();
        
        _maxElevation = _valley.getElevation( MIN_X );

        _climateChangedTime = _clock.getSimulationTime();
        _previousELA = _currentELA = _climate.getEquilibriumLineAltitude();
        setSteadyState();
    }
    
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the valley that the glacier forms in.
     * 
     * @return Valley
     */
    public Valley getValley() {
        return _valley;
    }
    
    /**
     * Gets the climate.
     * 
     * @return Climate
     */
    public Climate getClimate() {
        return _climate;
    }
    
    /**
     * Gets the minimum x coordinate, used for the first x-axis sample point.
     * 
     * @return meters
     */
    public static double getMinX() {
        return MIN_X;
    }
    
    /**
     * Gets the maximum x coordinate.
     * Depending on how the model is parameterized, we may exceed this value.
     * This value is used mainly to tell the view how much of the Valley we need 
     * to be able to look at.
     * 
     * @return
     */
    public static double getMaxX() {
        return MIN_X + MAX_LENGTH;
    }
    
    /**
     * Gets the spacing used between x-axis sample points.
     * 
     * @return meters
     */
    public static double getDx() {
        return DX;
    }
    
    /**
     * Is the glacier currently in the steady state?
     * 
     * @return true or false
     */
    public boolean isSteadyState() {
        return _steadyState;
    }
    
    /**
     * Sets the glacier to the steady state.
     * 
     * @param steadyState
     */
    public void setSteadyState() {
        if ( !_steadyState ) {
            final double steadyStateELA = _climate.getEquilibriumLineAltitude();
            _previousELA = _currentELA = steadyStateELA;
            _iceThicknessSamples = computeIceThicknessSamples( steadyStateELA );
            _averageIceThicknessSquares = computeAverageIceThicknessSquares( _iceThicknessSamples );
            _steadyState = true;
            notifyIceThicknessChanged();
            notifySteadyStateChanged();
        }
    }
    
    /**
     * Gets the length of the glacier.
     * 
     * @return length in meters
     */
    public double getLength() {
        return ( _iceThicknessSamples.length - 1 ) * DX;
    }
    
    /**
     * Gets the x coordinate of the terminus.
     * 
     * @return meters
     */
    public double getTerminusX() {
        return MIN_X + getLength();
    }
    
    //----------------------------------------------------------------------------
    // Ice Thickness model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the ice thickness samples.
     * Samples were made from X0 to terminusX, at intervals of DX.
     * Use getX0, getTerminusX, and getDX to interpret these samples.
     * 
     * @return
     */
    public double[] getIceThicknessSamples() {
        return _iceThicknessSamples;
    }
    
    /**
     * Gets the approximate ice thickness at an x coordinate.
     * The x value specified will fall between 2 ice thickness samples.
     * Locate the 2 samples, and do a linear interpolation between them 
     * to determine the approximate ice thickness.
     * 
     * @param x
     * @return meters
     */
    public double getIceThickness( final double x ) {
        double iceThickness = 0;
        final double xTerminus = getTerminusX();
        if ( x >= MIN_X && x <= xTerminus ) {
            if ( x == xTerminus ) {
                iceThickness = _iceThicknessSamples[_iceThicknessSamples.length - 1];
            }
            else {
                int index = (int) ( ( x - MIN_X ) / DX );
                double x1 = MIN_X + ( index * DX );
                double t1 = _iceThicknessSamples[index];
                double t2 = _iceThicknessSamples[index + 1];
                iceThickness = t1 + ( ( ( x - x1 ) / DX ) * ( t2 - t1 ) ); // linear interpolation
            }
        }
        assert ( iceThickness >= 0 );
        return iceThickness;
    }
    
    /*
     * When the climate changes, note the time,
     * and update the ELA values used to compute ice thickness.
     */
    private void handleClimateChange() {
        _climateChangedTime = _clock.getSimulationTime();
        _previousELA = _currentELA;
        if ( _steadyState ) {
            _steadyState = false;
            notifySteadyStateChanged();
        }
    }
    
    /*
     * Computes the ice thickness samples for a give ELA.
     * 
     * @param ela
     * @return double[] ice thickness samples, meters
     */
    private double[] computeIceThicknessSamples( final double ela ) {
        
        double glacierLength = computeLength( ela ); // x_terminus in documentation, but this is really length
        double maxThickness = computeMaxThickness( ela ); // H_max in documentation
        
        final double xPeak = MIN_X + ( 0.5 * glacierLength );
        final int numberOfSamples = (int) ( glacierLength / DX ) + 1;

        double[] samples = new double[numberOfSamples];
        double x = MIN_X;
        final double p = 42 - ( 0.01 * ela );
        final double r = 1.5 * xPeak;
        final double xPeakPow = Math.pow( xPeak, p );
        for ( int i = 0; i < numberOfSamples; i++ ) {
            if ( x < xPeak ) {
                samples[i] = Math.sqrt( ( r * r ) - ( ( x - xPeak ) * ( x - xPeak ) ) ) * ( maxThickness / r );
                samples[i] *= ( xPeakPow - Math.pow( Math.abs( x - xPeak ), p ) ) / xPeakPow;
            }
            else {
                samples[i] = Math.sqrt( ( xPeak * xPeak ) - ( ( x - xPeak ) * ( x - xPeak ) ) ) * ( maxThickness / xPeak );
            }
            x += DX;
        }

        return samples;
    }
    
    /*
     * Computes the length of the glacier.
     * 
     * The length is used in the computation of ice thickness samples.
     * This is a "Hollywood" model, where were our algorithm was tweaked to fit a curve
     * that corresponded to published data.  This algorithm work only with one specific
     * Valley profile, so if you make any changes to the Valley model, this is sure 
     * to break.
     */
    private double computeLength( double ela ) {
        
        final double elaThreshold = 4000; // meters
        assert( elaThreshold < _maxElevation );
        
        double length = 0;
        if ( ela > _maxElevation ) {
            // above the top of the headwall, length is zero
            length = 0;
        }
        else if ( ela > elaThreshold ) {
            // above this threshold, use a different curve
            final double term0 = computeLength( elaThreshold );
            final double term1 = term0 / ( _maxElevation - elaThreshold );
            length = term0 - ( ( ela - elaThreshold ) * term1 );
        }
        else {
            // at all other elevations, the data fits this curve
            length = 170.5E3 - ( 41.8 *  ela );
        }
        
        assert( length >= 0 );
        return length;
    }
    
    /* 
     * Computes the maximum thickness of the glacier.
     * 
     * The maximum thickness is used in the computation of ice thickness samples.
     * This is a "Hollywood" model, where were our algorithm was tweaked to fit a curve
     * that corresponded to published data.  This algorithm work only with one specific
     * Valley profile, so if you make any changes to the Valley model, this is sure 
     * to break.
     */
    private double computeMaxThickness( double ela ) {
        double maxThickness = 0;
        if ( ela > _maxElevation ) {
            maxThickness = 0;
        }
        else {
            maxThickness = 400. - Math.pow( ( 1.04E-2 * ela ) - 23, 2 );
        }
        //XXX workaround, this must be fixed! get new model from Archie
        if ( maxThickness < 0 ) {
            maxThickness = 0;
        }
        assert( maxThickness >= 0 );
        return maxThickness;
    }
    
    /*
     * Gets the average of the square of the ice thickness over the complete length of the glaciers.
     * Only non-zero thickness value are included in the average.
     * (symbol: u0ave)
     * 
     * @return meters^2
     */
    private static double computeAverageIceThicknessSquares( double[] iceThicknessSamples ) {
        double sum = 0;
        double samples = 0;
        double h = 0;
        for ( int i = 0; i < iceThicknessSamples.length; i++ ) {
            h = iceThicknessSamples[i];
            if ( h > 0 ) {
                sum += ( h * h );
                samples++;
            }
        }
        return ( sum / samples );
    }
    
    //----------------------------------------------------------------------------
    // Ice Velocity model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the ice velocity at a point in the ice.
     * If the point is outside the ice, a zero vector is returned.
     * <p>
     * Magnitude of the velocity vector is determined by the ice speed model herein.
     * Direction corresponds to the slope of the valley floor.
     * 
     * @param x meters
     * @param elevation meters
     * @return Vector2D, components in meters/year
     */
    public Vector2D getIceVelocity( final double x, final double elevation ) {
        final double magnitude = getIceSpeed( x, elevation );
        final double direction = _valley.getDirection( x, x + DX );
        final double xComponent = PolarCartesianConverter.getX( magnitude, direction );
        final double yComponent = PolarCartesianConverter.getY( magnitude, direction );
        return new Vector2D.Double( xComponent, yComponent );
    }
    
    /*
     * Gets the steady-state ice speed at a point in the ice.
     * If the point is outside the ice, zero is returned.
     * 
     * @param x meters
     * @param elevation meters
     * @return meters/year
     */
    private double getIceSpeed( final double x, final double elevation ) {
        double iceSpeed = 0;
        final double iceThickness = getIceThickness( x );
        if ( iceThickness > 0 ) {
            final double valleyElevation = _valley.getElevation( x );
            final double iceSurfaceElevation = valleyElevation + iceThickness;
            // if the elevation is in the ice...
            if ( elevation >= valleyElevation && elevation <= iceSurfaceElevation ) {
                // zz varies linearly from 0 at the valley floor (rock-ice interface) to 1 at the ice surface (air-ice interface)
                final double zz = ( elevation - valleyElevation ) / iceThickness;
                final double u_deform_ave = computeVerticallyAveragedDeformationIceSpeed( iceThickness, _averageIceThicknessSquares );
                iceSpeed = U_SLIDE + ( u_deform_ave * 5. * ( zz - ( 1.5 * zz * zz ) + ( zz * zz * zz ) - ( 0.25 * zz * zz * zz * zz ) ) );
            }
        }
        return iceSpeed;
    }
    
    /*
     * Gets the vertically-averaged deformation ice speed.
     * Speed of ice moving downvalley is affected by vertical deformation,
     * and this method calculation the deformation contribution.
     * (symbol: u_deform_ave)
     * 
     * @param iceThickness ice thickness, meters
     * @param averageIceThicknessSquares average of ice thickness squares, meters^2
     * @return meters/year
     */
    private static double computeVerticallyAveragedDeformationIceSpeed( final double iceThickness, final double averageIceThicknessSquares ) {
        return ( ( iceThickness * iceThickness ) * U_DEFORM / averageIceThicknessSquares );
    }
    
    //----------------------------------------------------------------------------
    //  Timescale model
    //----------------------------------------------------------------------------
    
    /* 
     * Gets the climate change timescale for a specified ELA (equilibrium line altitude).
     * 
     * @param ela equilibrium line altitude (meters)
     * @return timescale (units?)
     */
    private static double getClimateChangeTimescale( double ela ) {
        double timescale = ( ( -37.5 / 300 ) * ela ) + 484.6;
        if ( timescale < MIN_TIMESCALE ) {
            timescale = MIN_TIMESCALE;
        }
        else if ( timescale > MAX_TIMESCALE ) {
            timescale = MAX_TIMESCALE;
        }
        return timescale;
    }
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent event ) {
        
        if ( !isSteadyState() ) {
            
            final double steadyStateELA = _climate.getEquilibriumLineAltitude();
            
            // evolve the ELA
            final double tElapsed = event.getSimulationTime() - _climateChangedTime;
            final double climateChangeTimescale = getClimateChangeTimescale( steadyStateELA );
            _currentELA = _previousELA + ( steadyStateELA - _previousELA ) * ( 1 - Math.exp( -tElapsed / climateChangeTimescale  ) );
            
            // are we close enough to steady state?
            if ( Math.abs( steadyStateELA - _currentELA ) <= ELA_EQUALITY_THRESHOLD ) {
                setSteadyState();
            }
            else {
                // compute the ice thickness for the new ELA
                _iceThicknessSamples = computeIceThicknessSamples( _currentELA );
                _averageIceThicknessSquares = computeAverageIceThicknessSquares( _iceThicknessSamples );
                notifyIceThicknessChanged();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface GlacierListener {
        public void iceThicknessChanged();
        public void steadyStateChanged();
    }
    
    public static class GlacierAdapter implements GlacierListener {
        public void iceThicknessChanged() {}
        public void steadyStateChanged() {}
    }
    
    public void addGlacierListener( GlacierListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeGlacierListener( GlacierListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyIceThicknessChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).iceThicknessChanged();
        }
    }
    
    private void notifySteadyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).steadyStateChanged();
        }
    }
}
