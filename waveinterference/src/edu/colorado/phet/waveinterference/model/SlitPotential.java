/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:29:42 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitPotential implements Potential {
    private WaveModel waveModel;
    private boolean enabled = true;
    private int slitWidth = 10;
    private int slitSeparation = 12;
    private int location = 50;
    private int thickness = 5;
    private double potentialValue = 100;
    private boolean oneSlit = true;
    private Potential potential;
    private ArrayList listeners = new ArrayList();

    public SlitPotential( WaveModel waveModel ) {
        this.waveModel = waveModel;
        update();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean isOneSlit() {
        return oneSlit;
    }

    public boolean isTwoSlits() {
        return !isOneSlit();
    }

    public static interface Listener {
        void slitsChanged();
    }

    private void update() {
        if( !enabled ) {
            potential = new ConstantPotential();
        }
        else if( oneSlit ) {
            this.potential = new VerticalSingleSlit( waveModel.getWidth(), waveModel.getHeight(), location, thickness, slitWidth, slitSeparation, potentialValue );
        }
        else {
            this.potential = new VerticalDoubleSlit( waveModel.getWidth(), waveModel.getHeight(), location, thickness, slitWidth, slitSeparation, potentialValue );
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.slitsChanged();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean selected ) {
        this.enabled = selected;
        update();
    }

    public void setOneSlit() {
        this.oneSlit = true;
        update();
    }

    public void setTwoSlits() {
        this.oneSlit = false;
        update();
    }

    public void setSlitWidth( int value ) {
        this.slitWidth = value;
        update();
    }

    public double getSlitWidth() {
        return slitWidth;
    }

    public int getSlitSeparation() {
        return slitSeparation;
    }

    public void setSlitSeparation( int slitSeparation ) {
        this.slitSeparation = slitSeparation;
        update();
    }

    public int getLocation() {
        return location;
    }

    public void setLocation( int location ) {
        this.location = location;
        update();
    }

    public double getPotential( int i, int j, int time ) {
        return potential.getPotential( i, j, time );
    }

    public Rectangle[] getBarrierRectangles() {
        if( potential instanceof ConstantPotential ) {
            return new Rectangle[0];
        }
        else if( potential instanceof VerticalBarrier ) {
            return ( (VerticalBarrier)potential ).getRectangleBarriers();
        }
        else {
            System.err.println( "Unknown type for barriers: " + potential.getClass() );
            return new Rectangle[0];
        }
    }
}
