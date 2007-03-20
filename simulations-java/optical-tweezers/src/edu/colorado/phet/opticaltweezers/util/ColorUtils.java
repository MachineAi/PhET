/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.util;

import java.awt.Color;

/**
 * ColorUtils is a collection of color utilities.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorUtils {

    private ColorUtils() {}
    
    public static Color addAlpha( Color c, int alpha ) {
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
    }
}
