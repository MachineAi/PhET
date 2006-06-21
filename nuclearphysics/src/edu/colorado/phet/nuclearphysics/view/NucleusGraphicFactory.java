/**
 * Class: NucleusGraphicFactory
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.*;

public class NucleusGraphicFactory {

    public NucleusGraphic create( Nucleus nucleus ) {

        if( nucleus instanceof Polonium210 ) {
            return new Polonium210Graphic( nucleus );
        }
        if( nucleus instanceof Lead206 ) {
            return new Lead206Graphic( nucleus );
        }
        if( nucleus instanceof Uranium235 ) {
            return new Uranium235Graphic( nucleus );
        }
        if( nucleus instanceof Uranium238 ) {
            return new Uranium238Graphic( nucleus );
        }
        if( nucleus instanceof Uranium239 ) {
            return new Uranium239Graphic( nucleus );
        }
        else {
            return new NucleusGraphic( nucleus );
        }
    }
}
