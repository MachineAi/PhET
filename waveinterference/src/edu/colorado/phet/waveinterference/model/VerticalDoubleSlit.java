/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:49:48 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class VerticalDoubleSlit extends VerticalBarrier {
    private Rectangle topSlit;
    private Rectangle bottomSlit;

    private Rectangle topBar;
    private Rectangle midBar;
    private Rectangle bottomBar;


    public VerticalDoubleSlit( int gridWidth, int gridHeight, int x, int thickness, int slitSize, int slitSeparation, double potential ) {
        super( gridWidth, gridHeight, x, thickness, slitSize, slitSeparation, potential );
    }

    protected void update() {
        int gridWidth = getGridWidth();
        int slitSeparation = getSlitSeparation();
        int slitSize = getSlitSize();
        int thickness = getThickness();
        int x = getX();
        double potential = getPotential();

        int topSlitCenter = round( gridWidth / 2.0 - slitSeparation / 2.0 );
        int bottomSlitCenter = round( gridWidth / 2.0 + slitSeparation / 2.0 );
        int midBarSize = round( gridWidth / 2.0 - slitSeparation / 2.0 - slitSize / 2.0 );

        topBar = new Rectangle( x, 0, thickness, round( topSlitCenter - slitSize / 2.0 ) );
        midBar = new Rectangle( x, round( topSlitCenter + slitSize / 2.0 ), thickness, slitSeparation - slitSize );
        bottomBar = new Rectangle( x, round( bottomSlitCenter + slitSize / 2.0 ), thickness, midBarSize + 1 );

        this.topSlit = new Rectangle( x, topBar.x + topBar.width, thickness, slitSize );
        this.bottomSlit = new Rectangle( x, midBar.x + midBar.width, thickness, slitSize );

        CompositePotential compositePotential = new CompositePotential();
        if( super.getInverse() ) {
            compositePotential.addPotential( new BarrierPotential( topSlit, potential ) );
            compositePotential.addPotential( new BarrierPotential( bottomSlit, potential ) );
        }
        else {
            compositePotential.addPotential( new BarrierPotential( topBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( midBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( bottomBar, potential ) );
        }
        setPotentialDelegate( new PrecomputedPotential( compositePotential, gridWidth, getGridHeight() ) );
        notifyListeners();

    }

    public Rectangle[] getSlitAreas() {
        return new Rectangle[]{new Rectangle( topSlit ), new Rectangle( bottomSlit )};
    }

    public Rectangle[]getRectangleBarriers() {
        ArrayList r = new ArrayList();
        if( getInverse() ) {
            r.add( topSlit );
            r.add( bottomSlit );
        }
        else {
            r.add( topBar );
            r.add( midBar );
            r.add( bottomBar );
        }
        return (Rectangle[])r.toArray( new Rectangle[0] );
    }
}
