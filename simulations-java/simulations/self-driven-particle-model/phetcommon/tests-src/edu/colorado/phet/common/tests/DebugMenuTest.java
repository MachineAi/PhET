/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/tests-src/edu/colorado/phet/common/tests/DebugMenuTest.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:06 $
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;

/**
 * DebugMenuTest
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class DebugMenuTest {

    public static void main( String[] args ) {
        SwingTimerClock clock = new SwingTimerClock( 1, 25, AbstractClock.FRAMES_PER_SECOND );
        ApplicationModel am = new ApplicationModel( "Debug Test", "", "" );
        am.setClock( clock );
        DebugMenuTestModule debugMenuTestModule = new DebugMenuTestModule( am.getClock() );
        am.setModules( new Module[]{debugMenuTestModule} );
        am.setInitialModule( debugMenuTestModule );

//        PhetApplication app = new PhetApplication( args, clock, "Debug Test", "", "" );
//        DebugMenuTestModule debugMenuTestModule = new DebugMenuTestModule( clock );
//        app.addModule( debugMenuTestModule );
//        app.setInitialModule( debugMenuTestModule );
//        app.startApplication();
    }

    static class DebugMenuTestModule extends Module {
        protected DebugMenuTestModule( AbstractClock clock ) {
            super( "Debug Menu Test", clock );

            BaseModel model = new BaseModel();
            setModel( model );
            ApparatusPanel2 ap = new ApparatusPanel2( clock );
//            ap.setUseOffscreenBuffer( true );
            ap.setBackground( Color.white );
            setApparatusPanel( ap );
        }
    }
}
