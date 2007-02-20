/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


public class Laser extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POWER = "power";
    public static final String PROPERTY_RUNNING = "running";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width; // nm
    private double _wavelength; // nm
    private double _power; // mW
    private boolean _running;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position of the laser's objective (nm)
     * @param orientation orientation of the beam (radians)
     * @param width width of the laser beam, as it enters the objective (nm)
     * @param wavelength wavelenght (nm)
     * @param power power (mW)
     */
    public Laser( Point2D position, double orientation, double width, double wavelength, double power ) {
        super( position, orientation, 0 /* speed */ );
        _width = width;
        _wavelength = wavelength;
        _power = power;
        _running = false;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setRunning( boolean running ) {
        if ( running != _running ) {
            _running = running;
            notifyObservers( PROPERTY_RUNNING );
        }
    }
    
    public boolean isRunning() {
        return _running;
    }
    
    public double getWidth() {
        return _width;
    }
    
    private void setWidth( double width ) {
        throw new UnsupportedOperationException( "width is immutable" );
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    private void setWavelength( double wavelength ) {
        throw new UnsupportedOperationException( "wavelength is immutable" );
    }
    
    public double getPower() {
        return _power;
    }
    
    public void setPower( double power ) {
        if ( power < 0 ) {
            throw new IllegalArgumentException( "power < 0: " + power );
        }
        if ( power != _power ) {
            _power = power;
            notifyObservers( PROPERTY_POWER );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
