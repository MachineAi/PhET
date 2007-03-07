/** Sam Reid*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common_semiconductor.math.PhetVector;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 26, 2004
 * Time: 1:23:42 AM
 * Copyright (c) Mar 26, 2004 by Sam Reid
 */
public class EnergySpaceRegion {
    double minX;
    double minEnergy;
    double spatialWidth;
    double energyRange;

    public EnergySpaceRegion( double minX, double minEnergy, double spatialWidth, double energyRange ) {
        this.minX = minX;
        this.minEnergy = minEnergy;
        this.spatialWidth = spatialWidth;
        this.energyRange = energyRange;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinEnergy() {
        return minEnergy;
    }

    public double getSpatialWidth() {
        return spatialWidth;
    }

    public double getEnergyRange() {
        return energyRange;
    }

    public Rectangle2D.Double toRectangle() {
        return new Rectangle2D.Double( getMinX(), getMinEnergy(), getSpatialWidth(), getEnergyRange() );
    }

    public boolean contains( PhetVector particlePosition ) {
        return toRectangle().contains( particlePosition.toPoint2D() );
    }
}
