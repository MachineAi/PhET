/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:46:29 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public abstract class LatticeScreenCoordinates {
    public abstract Point2D toScreenCoordinates( int i, int j );

    public abstract Point toLatticeCoordinates( double x, double y );

    public Rectangle2D toScreenRect( Rectangle rectangle ) {
        Point2D min = toScreenCoordinates( rectangle.x, rectangle.y );
        Point2D max = toScreenCoordinates( rectangle.x + rectangle.width, rectangle.y + rectangle.height );
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal( min, max );
        return rect;
    }
}
