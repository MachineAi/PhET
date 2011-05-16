// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Sugar crystal
 *
 * @author Sam Reid
 */
public class MacroSugar extends MacroCrystal {
    //Create a sugar crystal with 1E-6 moles of sugar
    public MacroSugar( ImmutableVector2D position ) {
        this( position, 1E-6 );
    }

    public MacroSugar( ImmutableVector2D position, double moles ) {
        super( position, moles );
    }
}
