/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;
import java.util.Arrays;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 1:46:56 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Wavefunction {
    private Complex[][] wavefunction;
    private double magnitude = 0.0;
    private boolean magnitudeDirty = true;

    public Wavefunction( Wavefunction wavefunction ) {
        this( wavefunction.wavefunction );
    }

    public void clearRect( Rectangle rect ) {
        for( int i = rect.x; i < rect.x + rect.width; i++ ) {
            for( int j = rect.y; j < rect.y + rect.height; j++ ) {
                //todo intersect rectangles to avoid contains call each time.
                if( containsLocation( i, j ) ) {
                    setValue( i, j, new Complex() );
                }
            }
        }
    }

    public void splitWave( Rectangle region, Wavefunction a, Wavefunction b ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int j = region.y; j < region.y + region.height; j++ ) {
                Complex v = valueAt( i, j );
                a.setValue( i, j, v.times( 0.5 ) );
                b.setValue( i, j, v.times( 0.5 ) );
            }
        }
    }

    public void combineWaves( Rectangle region, Wavefunction a, Wavefunction b ) {
        for( int i = region.x; i < region.x + region.width; i++ ) {
            for( int j = region.y; j < region.y + region.height; j++ ) {
                double sum = SplitModel.sumMagnitudes( a.valueAt( i, j ), b.valueAt( i, j ) );
                setValue( i, j, new Complex( sum, 0 ) );
            }
        }
    }

    public static interface Listener {
        void cleared();

        void scaled( double scale );
    }

    public Wavefunction( int width, int height ) {
        wavefunction = new Complex[width][height];
        clear();
    }

    public Wavefunction( Complex[][] values ) {
        this.wavefunction = values;
        setMagnitudeDirty();
    }

    public void setMagnitude( double newMagnitude ) {
        setMagnitudeDirty();
        double origMagnitude = getMagnitude();
        scale( Math.sqrt( newMagnitude ) / Math.sqrt( origMagnitude ) );
        double m = getMagnitude();//todo remove this after we're sure its working
        if( Math.abs( m - newMagnitude ) > 10E-6 ) {
            throw new RuntimeException( "Normalization failed: requested new magnitude=" + newMagnitude + ", received=" + m );
        }
    }

    public void scale( double scale ) {
        if( scale != 1.0 ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int j = 0; j < getHeight(); j++ ) {
                    Complex complex = wavefunction[i][j];
                    complex.scale( scale );
                }
            }
        }
        setMagnitudeDirty();
    }

    public boolean containsLocation( int i, int k ) {
        return getBounds().contains( i, k );
    }

    public static String toString( Complex[] complexes ) {
        return Arrays.asList( complexes ).toString();
    }

    public static Complex[][] newInstance( int w, int h ) {
        Complex[][] copy = new Complex[w][h];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = new Complex();
            }
        }
        return copy;
    }

    public static Complex[][] copy( Complex[][] w ) {
        Complex[][] copy = new Complex[w.length][w[0].length];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = new Complex( w[i][j] );
            }
        }
        return copy;
    }

    public void copyTo( Wavefunction dest ) {
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                dest.setValue( i, j, valueAt( i, j ) );
//                dest.setValue( i, j, new Complex( valueAt( i, j ) ) );
            }
        }
        dest.setMagnitudeDirty();
    }

    public int getWidth() {
        return wavefunction.length;
    }

    public int getHeight() {
        return wavefunction[0].length;
    }

    public void setValue( int i, int j, double re, double im ) {
        wavefunction[i][j].setValue( re, im );
    }

    public void setValue( int i, int j, Complex complex ) {
        setValue( i, j, complex.getReal(), complex.getImaginary() );
    }

    public Complex valueAt( int i, int j ) {
        return wavefunction[i][j];
    }

    public void normalize() {
        double totalProbability = getMagnitude();
//        System.out.println( "totalProbability = " + totalProbability );
        double scale = 1.0 / Math.sqrt( totalProbability );
        scale( scale );
        double postProb = getMagnitude();
//        System.out.println( "postProb = " + postProb );

        double diff = 1.0 - postProb;
        if( !( Math.abs( diff ) < 0.0001 ) ) {
            System.out.println( "Error in probability normalization, norm=" + postProb );
//            throw new RuntimeException( "Error in probability normalization." );
        }
        setMagnitudeDirty();
    }

    public double getMagnitude() {
        if( magnitudeDirty ) {
//            System.out.println( "Wavefunction.getMagnitude" );
            this.magnitude = recomputeMagnitude();
            magnitudeDirty = false;
        }
        return magnitude;
    }

    private double recomputeMagnitude() {//        System.out.println( "Get Magnitude @ t="+System.currentTimeMillis() );
        Complex runningSum = new Complex();
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                Complex psiStar = wavefunction[i][j].complexConjugate();
                Complex psi = wavefunction[i][j];
                Complex term = psiStar.times( psi );
                runningSum = runningSum.plus( term );
            }
        }
        return runningSum.abs();
    }

    public void clear() {

        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                if( valueAt( i, j ) == null ) {
                    wavefunction[i][j] = new Complex();
                }
                else {
                    valueAt( i, j ).zero();
                }
            }
        }
        setMagnitudeDirty();
//        notifyCleared();
    }

    public void setMagnitudeDirty() {
        magnitudeDirty = true;
    }

    public void setSize( int width, int height ) {
        wavefunction = new Complex[width][height];
        clear();

    }

    public Wavefunction copy() {
        Complex[][] copy = new Complex[getWidth()][getHeight()];
        for( int i = 0; i < getWidth(); i++ ) {
            for( int j = 0; j < getHeight(); j++ ) {
                copy[i][j] = valueAt( i, j ).copy();
            }
        }
        return new Wavefunction( copy );
    }

    public Rectangle getBounds() {
        return new Rectangle( getWidth(), getHeight() );
    }

    public Wavefunction copyRegion( int x, int y, int width, int height ) {
        Wavefunction sub = new Wavefunction( width, height );
        for( int i = x; i < x + width; i++ ) {
            for( int j = y; j < y + height; j++ ) {
                sub.wavefunction[i - x][j - y] = new Complex( valueAt( i, j ) );
            }
        }
        return sub;
    }

    public void add( Wavefunction w ) {
        if( w.getWidth() == getWidth() && w.getHeight() == getHeight() ) {
            for( int i = 0; i < getWidth(); i++ ) {
                for( int k = 0; k < getHeight(); k++ ) {
                    valueAt( i, k ).add( w.valueAt( i, k ) );
                }
            }
        }
        else {
            throw new RuntimeException( "illegal arg dim" );
        }
        setMagnitudeDirty();
    }

    public void setWavefunction( Wavefunction wavefunction ) {
        clear();
        add( wavefunction );
    }

    public Wavefunction createEmptyWavefunction() {
        return new Wavefunction( getWidth(), getHeight() );
    }
}
