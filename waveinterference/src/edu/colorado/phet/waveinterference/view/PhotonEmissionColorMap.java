/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:58:36 PM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class PhotonEmissionColorMap implements ColorMap {
    private WaveModel lattice;
    private boolean[][] inited;
    private Color color;

    public PhotonEmissionColorMap( WaveModel lattice ) {
        this( lattice, Color.blue );
    }

    public PhotonEmissionColorMap( final WaveModel lattice, Color color ) {
        this.color = color;
        this.lattice = lattice;
        inited = new boolean[lattice.getWidth()][lattice.getHeight()];
        lattice.addListener( 0, new WaveModel.Listener() {//todo fix this workaround: has to get in front of WaveModelGraphic for notifications.

            public void sizeChanged() {
                System.out.println( "PhotonEmissionColorMap.sizeChanged" );
                inited = new boolean[lattice.getWidth()][lattice.getHeight()];
                debug();
            }
        } );
    }

    private void debug() {
        System.out.println( "lattice.getWidth() = " + lattice.getWidth() + ", h=" + lattice.getHeight() );
        System.out.println( "inited.length = " + inited.length + ", inited[0].length=" + inited[0].length );
    }

    public Color getColor( int i, int k ) {
        //todo ensure wavefunction is the correct size (could have been resized).
        float value = lattice.getLattice().getValue( i, k );
        float epsilon = 0.025f;
        if( Math.abs( value ) < epsilon && !inited[i][k] ) {
            return Color.black;
        }
        else {
            inited[i][k] = true;
            return new BasicTestColorMap( lattice.getLattice(), color ).getColor( i, k );
        }
    }

}
