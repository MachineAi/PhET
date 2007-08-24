/*  */
package edu.colorado.phet.waveinterference.test;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 10:51:19 PM
 */

public class TestIL8N {
    public static void main( String[] args ) {
        SimStrings.getInstance().init( args, "LabelsBundle" );
        String units = "cm";
        String hello = MessageFormat.format( SimStrings.getInstance().getString( "hello.0" ), new Object[]{units} );
        System.out.println( "hello = " + hello );
    }
}
