/**
 * Class: EmfApplication
 * Package: edu.colorado.phet.emf
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_1200.application.ApplicationModel;
import edu.colorado.phet.common_1200.application.PhetApplication;
import edu.colorado.phet.common_1200.model.clock.SwingTimerClock;
import edu.colorado.phet.common_1200.view.PhetFrame;
import edu.colorado.phet.common_1200.view.util.FrameSetup;
import edu.colorado.phet.coreadditions.emf.LecturePhetLookAndFeel;
import edu.colorado.phet.coreadditions.emf.PhetLookAndFeel;
import edu.colorado.phet.waves.view.WaveMediumGraphic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class RadioWavesApplication {

    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 6;

    public static void main( String[] args ) {

        // Get a logger; the logger is automatically created if
        // it doesn't already exist
        Logger logger = Logger.getLogger( "edu.colorado.phet.PhetLogger" );

        // Initialize simulation strings using resource bundle for the locale.
        SimStrings.setStrings( EmfConfig.localizedStringsPath );//todo: add String[] args to this
        SimStrings.getInstance().addStrings( "emf-localization/phetcommon-strings" );//todo: add String[] args to this

        // Log a few message at different severity levels
        PhetLookAndFeel lookAndFeel = new edu.colorado.phet.coreadditions.emf.ClientPhetLookAndFeel();
        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-p" ) ) {
                    lookAndFeel = new LecturePhetLookAndFeel();
                }
            }
        }

        SwingTimerClock clock = new SwingTimerClock( 0.5, 40, true );
//        SwingTimerClock clock = new SwingTimerClock( 1, 40, false  );
        final EmfModule antennaModule = new EmfModule( clock );
        FrameSetup fs = new FrameSetup.CenteredWithSize( 1024, 768 );
        ApplicationModel appDescriptor = new ApplicationModel(
                new String( SimStrings.get( "EmfApplication.title" )
                            + " ("
                            + EmfConfig.VERSION
                            + ")" ),
                SimStrings.get( "EmfApplication.description" ),
                EmfConfig.VERSION, fs );
        appDescriptor.setModule( antennaModule );
        appDescriptor.setInitialModule( antennaModule );
        appDescriptor.setClock( clock );
        appDescriptor.setName( "radiowaves" );

        PhetApplication application = new PhetApplication( appDescriptor );
        PhetFrame frame = application.getApplicationView().getPhetFrame();
        PhetFrame.setDefaultLookAndFeelDecorated( true );
        frame.setIconImage( lookAndFeel.getSmallIconImage() );

        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                if( args[i].toLowerCase().equals( "-gi" ) ) {
                    try {
                        String giUrl = args[i + 1];
                        logger.info( "Loading gi: " + giUrl );
                    }
                    catch( Exception e ) {
                        logger.severe( "Error loading and instantiating gi" );
                    }
                }
            }
        }

        // Add an options menu
        JMenu optionsMenu = new JMenu( "Options" );
//        final JCheckBoxMenuItem scalarRepCB = new JCheckBoxMenuItem( "Scalar representation" );
//        scalarRepCB.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                antennaModule.setScalarRepEnabled( scalarRepCB.isSelected() );
//            }
//        } );
//        optionsMenu.add( scalarRepCB );
        final JCheckBoxMenuItem fadeScalarRepCB = new JCheckBoxMenuItem( "Fade scalar representation" );
        fadeScalarRepCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                WaveMediumGraphic.Y_GRADIENT = fadeScalarRepCB.isSelected();
                antennaModule.setFieldSense( antennaModule.getFieldSense() );
            }
        } );
        optionsMenu.add( fadeScalarRepCB );

//        final JCheckBoxMenuItem centerSingleVectorRowOffsetMI = new JCheckBoxMenuItem( "Center vectors on x axis");
//        centerSingleVectorRowOffsetMI.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                EmfConfig.SINGLE_VECTOR_ROW_OFFSET = centerSingleVectorRowOffsetMI.isSelected() ?
//                                                     0.5 : 0;
//            }
//        } );
//        optionsMenu.add( centerSingleVectorRowOffsetMI );
//        centerSingleVectorRowOffsetMI.setSelected( true );

//        frame.addMenu( optionsMenu );

        // Prevent the frame from being resized
        frame.setResizable( false );

        Runtime.getRuntime().gc();
        application.startApplication();
    }

}
