package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.AndProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;
import edu.colorado.phet.fluidpressureandflow.view.*;

/**
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas {
    private static final double modelHeight = 10;
    private static final double scale = STAGE_SIZE.getHeight() / modelHeight;

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( module, new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width / 2, STAGE_SIZE.height * 0.65 ), scale, true ) );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
        addChild( new PhetPPath(transform.createTransformedShape( new Ellipse2D.Double(-2,-2,4,4) )) );

        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, null, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) );
        }

        for ( VelocitySensor velocitySensor : module.getFluidPressureAndFlowModel().getVelocitySensors() ) {
            addChild( new VelocitySensorNode( transform, velocitySensor ) );
        }

        //TODO: this is duplicated in FluidFlowCanvas
        // Control Panel
        final ControlPanel controlPanelNode = new ControlPanel( new FluidFlowControlPanel<WaterTowerModel>( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetButton( module ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        addChild( new FluidDensityControl<WaterTowerModel>( module ) {{
            setOffset( 0, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        addChild( new MeterStick( transform, module.getMeterStickVisibleProperty(), rulerModelOrigin ) );
        addChild( new EnglishRuler( transform, module.getYardStickVisibleProperty(), rulerModelOrigin ) );

        Property<Boolean> moduleActive = new Property<Boolean>( false ) {{
            module.addListener( new Module.Listener() {
                public void activated() {
                    setValue( true );
                }

                public void deactivated() {
                    setValue( false );
                }
            } );
        }};

        Property<Boolean> clockRunning = new Property<Boolean>( true );
        //wire up the clock to be running if the module is active and if the clock control button has been pressed
        new AndProperty( clockRunning, moduleActive ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    module.getFluidPressureAndFlowModel().getClock().setRunning( getValue() );
                }
            } );
        }};
        addChild( new FloatingClockControlNode( clockRunning, new Function1<Double, String>() {
            public String apply( Double time ) {
                return (int) ( time / 1.00 ) + " sec";
            }
        }, module.getClock() ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
    }
}
