package edu.colorado.phet.distanceladder;

import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.framesetup.MaxExtentFrameSetup;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.distanceladder.model.*;
import edu.colorado.phet.distanceladder.controller.CockpitModule;
import edu.colorado.phet.distanceladder.controller.StarMapModule;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Class: edu.colorado.phet.distanceladder.CockpitModuleTest
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 9:09:55 AM
 */

public class
        CockpitModuleTest {
    private static ApplicationDescriptor appDesc;
    private Color[] colors = new Color[] { Color.green, Color.magenta, Color.orange,
                                           Color.white, Color.yellow };

    public void test1() {

        StarField starField = new StarField();
        SwingTimerClock clock = new SwingTimerClock( 10, 10000, true ){
            public synchronized void start() {

            }
        };

//        AbstractClock clock = new ThreadedClock( 10, 20, true );

        UniverseModel model = new UniverseModel( starField, clock );
        model.getStarShip().setLocation( 0, 0 );

        CockpitModule cockpitModule = new CockpitModule( model );
        Module starMapModule = new StarMapModule( model );
        Module[] modules = new Module[]{cockpitModule, starMapModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );

        Star star = null;
        StarView starView = cockpitModule.getStarView();
        Point2D.Double p = null;

        Random random = new Random( );
        for( int i = 0; i < 200; i++ ) {
            double x = random.nextDouble() * Config.universeWidth - Config.universeWidth * 0.5;
            double y = random.nextDouble() * Config.universeWidth - Config.universeWidth * 0.5;
            int colorIdx = random.nextInt( colors.length );
            star = new NormalStar( colors[ colorIdx ], 50, new Point2D.Double( x, y ), random.nextDouble() * 500 - 250 );
            starField.addStar( star );
        }

////        star = new NormalStar( Color.magenta, 100, new Point2D.Double( Config.universeWidth * 1.2, 10 ), -50 );
////        starField.addStar( star );
        star = new NormalStar( Color.green, 1E6, new Point2D.Double( 100, 10 ), -25 );
        starField.addStar( star );
        star = new NormalStar( Color.green, 1E6, new Point2D.Double( 200, 10 ), -35 );
        starField.addStar( star );
        star = new NormalStar( Color.blue, 100, new Point2D.Double( 30, 10 ), -20 );
        starField.addStar( star );
        star = new NormalStar( Color.yellow, 100, new Point2D.Double( -100, -10 ), 50 );
        starField.addStar( star );
////        star = new NormalStar( Color.white, 100, new Point2D.Double( 0, 0 ), 50 );
////        starField.addStar( star );
//

        star = new FixedStar( Color.red, 1E9, 0, 0 );
        starField.addStar( star );
        star = new FixedStar( Color.red, 100, Math.PI / 2, 0 );
        starField.addStar( star );
        star = new FixedStar( Color.red, 100, -Math.PI / 2, 0 );
        starField.addStar( star );
        star = new FixedStar( Color.red, 100, Math.PI, 0 );
        starField.addStar( star );

//        star = new NormalStar( Color.yellow, 100, new Point2D.Double( 0, 0 ), 50 );
//        starField.addStar( star );

        /*
        star = new NormalStar( Color.yellow, 100, new Point2D.Double( 0, 100 ), 50 );
        starField.addStar( star );
        */

//        star = new NormalStar( Color.yellow, 100, new Point2D.Double( 150, -50 ), 50 );
//        starField.addStar( star );
//        star = new NormalStar( Color.yellow, 100, new Point2D.Double( 150, 50 ), 50 );
//        starField.addStar( star );

        model.getStarShip().setPov( new PointOfView( 0, 0, 0 ));
//        starView.setPointOfView( 0, 0, 0 );

//        cockpitModule.update();

    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to\nmeasure interstellar distances." );
        appDesc = new ApplicationDescriptor( "Lost In Space",
                                             desc,
                                             "0.1",
                                             new  MaxExtentFrameSetup( new FrameCenterer( 100, 100 ) ));
        CockpitModuleTest test = new CockpitModuleTest();

        test.test1();
    }
}
