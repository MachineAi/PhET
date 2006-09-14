/** Sam Reid*/
package edu.colorado.phet.cck3.model.components;

import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;
import net.n3.nanoxml.IXMLElement;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class Resistor extends CircuitComponent {
    public Resistor( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        setResistance( 10 );
        setKirkhoffEnabled( true );
    }

    public Resistor( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
    }

    public Resistor( double resistance ) {
        this( new Point2D.Double(), new Vector2D.Double(), 1, 1, new CircuitChangeListener() {
            public void circuitChanged() {
            }
        } );
        setKirkhoffEnabled( false );
        setResistance( resistance );
        setKirkhoffEnabled( true );
    }

    public void addAttributes( IXMLElement xml ) {
        xml.setAttribute( "resistance", getResistance() + "" );
    }

//    public static Resistor parseXML( IXMLElement xml, Junction startJunction, Junction endJunction, KirkhoffListener kl ) {
//        Resistor r = new Resistor( kl, startJunction, endJunction );
//        String bulb = xml.getAttribute( "resistance", "0" );
//        double rx = Double.parseDouble( bulb );
//        r.setResistance( rx );
//        return r;
//    }

}
