// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.SlowMotionNormalTimeControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.colorado.phet.energyformsandchanges.intro.model.Thermometer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.normalMotionRadioButton;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.slowMotionRadioButton;

/**
 * Piccolo canvas for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EFACIntroCanvas extends PhetPCanvas implements Resettable {

    public static Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;
    private static double EDGE_INSET = 10;
    private static final Color CONTROL_PANEL_COLOR = new Color( 255, 255, 224 );

    private final BooleanProperty showEnergyOfObjects = new BooleanProperty( false );

    private final EFACIntroModel model;
    private ThermometerToolBox thermometerToolBox;

    /**
     * Constructor.
     *
     * @param model
     */
    public EFACIntroCanvas( final EFACIntroModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.82 ) ),
                2200 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( new Color( 245, 246, 247 ) );

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Create some PNodes that will act as layers in order to create the
        // needed Z-order behavior.
        PNode backLayer = new PNode();
        rootNode.addChild( backLayer );
        PNode blockLayer = new PNode();
        rootNode.addChild( blockLayer );
        PNode frontLayer = new PNode();
        rootNode.addChild( frontLayer );

        // Add the control for showing/hiding object energy. TODO: i18n
        {
            PropertyCheckBox showEnergyCheckBox = new PropertyCheckBox( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                        "Show energy of objects",
                                                                        showEnergyOfObjects );
            showEnergyCheckBox.setFont( new PhetFont( 20 ) );
            backLayer.addChild( new ControlPanelNode( new PSwing( showEnergyCheckBox ), CONTROL_PANEL_COLOR ) {{
                setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - EDGE_INSET, EDGE_INSET );
            }} );
        }

        // Add a node that will act as the background below the lab bench
        // surface, basically like the side of the bench.
        {
            double width = model.getLabBenchSurface().getWidth() * 0.95;
            double height = 1000; // Arbitrary large number, user should never see the bottom of this.
            Shape benchSupportShape = new Rectangle2D.Double( -width / 2, -height, width, height );
            backLayer.addChild( new PhetPPath( mvt.modelToView( benchSupportShape ), new Color( 120, 120, 120 ) ) );
        }

        // Add the clock controls. TODO: i18n
        {
            BooleanProperty slowMotionEnabled = new BooleanProperty( false );
            PNode clockControl = new SlowMotionNormalTimeControlPanel( slowMotionRadioButton, "Slow Motion", "Normal",
                                                                       normalMotionRadioButton, slowMotionEnabled, model.getClock() );
            clockControl.setOffset( STAGE_SIZE.getWidth() / 2 - clockControl.getFullBoundsReference().width / 2,
                                    STAGE_SIZE.getHeight() - clockControl.getFullBoundsReference().height );
            backLayer.addChild( clockControl );
        }

        // Add the reset button.
        {
            ResetAllButtonNode resetButton = new ResetAllButtonNode( this, this, 20, Color.black, new Color( 255, 153, 0 ) );
            resetButton.setConfirmationEnabled( false );
            resetButton.setOffset( STAGE_SIZE.getWidth() - resetButton.getFullBoundsReference().width - 20,
                                   STAGE_SIZE.getHeight() - resetButton.getFullBoundsReference().height - 20 );
            backLayer.addChild( resetButton );
        }

        // Add the lab bench surface.
        backLayer.addChild( new ShelfNode( model.getLabBenchSurface(), mvt ) );

        // Add the burners.
        backLayer.addChild( new BurnerNode( model.getLeftBurner(), mvt ) );
        backLayer.addChild( new BurnerNode( model.getRightBurner(), mvt ) );

        // Add the movable objects.
        final PNode brickNode = new BlockNode( model, model.getBrick(), mvt );
        blockLayer.addChild( brickNode );
        final PNode leadNode = new BlockNode( model, model.getLeadBlock(), mvt );
        blockLayer.addChild( leadNode );
        BeakerView beakerView = new BeakerView( model, this, mvt );
        frontLayer.addChild( beakerView.getFrontNode() );
        backLayer.addChild( beakerView.getBackNode() );

        // Add the tool box for the thermometers.
        thermometerToolBox = new ThermometerToolBox( model, mvt, CONTROL_PANEL_COLOR );
        thermometerToolBox.setOffset( EDGE_INSET, EDGE_INSET );
        backLayer.addChild( thermometerToolBox );

        // Add the thermometers.
        for ( Thermometer thermometer : model.getThermometers() ) {
            ThermometerNode thermometerNode = new ThermometerNode( thermometer, mvt ) {{
                addInputEventListener( new PBasicInputEventHandler() {
                    // Put the thermometer into the tool box if dropped over it.
                    @Override public void mouseReleased( PInputEvent event ) {
                        if ( getFullBoundsReference().intersects( thermometerToolBox.getFullBoundsReference() ) ) {
                            thermometerToolBox.putThermometerInOpenSpot( getThermometer() );
                        }
                    }
                } );
            }};
            frontLayer.addChild( thermometerNode );
            thermometerToolBox.putThermometerInOpenSpot( thermometer );
        }

        /*
        {
            // TODO: i18n
            PNode title = new PhetPText( "Tool Box", new PhetFont( 20, false ) );
            double toolBoxHeight = 

        }

        ControlPanelNode thermometerToolBox = new ControlPanelNode( new PhetPPath( mvt.modelToView( model.getThermometerToolBox() ), new BasicStroke( 0 ), new Color( 0, 0, 0, 0 ) ),
                                                                    Color.LIGHT_GRAY,
                                                                    new BasicStroke( 2 ),
                                                                    Color.BLACK,
                                                                    0 ) {{
            PNode title = new PhetPText( "Tool Box", new PhetFont( 20, false ) );
            if ( title.getFullBoundsReference().width > getFullBoundsReference().width ) {
                title.setScale( getFullBoundsReference().width / title.getFullBoundsReference().width * 0.9 );
            }
            title.centerFullBoundsOnPoint( getCenterX(), title.getFullBoundsReference().height / 2 );
            addChild( title );
            setOffset( mvt.modelToView( model.getThermometerToolBox().getMinX(), model.getThermometerToolBox().getMaxY() ) );
        }};
        backLayer.addChild( thermometerToolBox );
        */


        // Create an observer that updates the Z-order of the blocks when the
        // user controlled state changes.
        SimpleObserver blockChangeObserver = new SimpleObserver() {
            public void update() {
                if ( model.getLeadBlock().isStackedUpon( model.getBrick() ) ) {
                    brickNode.moveToBack();
                }
                else if ( model.getBrick().isStackedUpon( model.getLeadBlock() ) ) {
                    leadNode.moveToBack();
                }
                else if ( model.getLeadBlock().getRect().getMinX() >= model.getBrick().getRect().getMaxX() ||
                          model.getLeadBlock().getRect().getMinY() >= model.getBrick().getRect().getMaxY() ) {
                    leadNode.moveToFront();
                }
                else if ( model.getBrick().getRect().getMinX() >= model.getLeadBlock().getRect().getMaxX() ||
                          model.getBrick().getRect().getMinY() >= model.getLeadBlock().getRect().getMaxY() ) {
                    brickNode.moveToFront();
                }
            }
        };

        // Update the Z-order of the blocks whenever the "userControlled" state
        // of either changes.
        model.getBrick().position.addObserver( blockChangeObserver );
        model.getLeadBlock().position.addObserver( blockChangeObserver );
    }

    public void reset() {
        model.reset();
        showEnergyOfObjects.reset();
        // Put the thermometers in the tool box.
        for ( Thermometer thermometer : model.getThermometers() ) {
            thermometerToolBox.putThermometerInOpenSpot( thermometer );
        }
    }

    // Class that defines the thermometer tool box.
    private static class ThermometerToolBox extends PNode {

        private static Font TITLE_FONT = new PhetFont( 20, false );
        private static int NUM_THERMOMETERS_SUPPORTED = 2;

        private final EFACIntroModel model;
        private final ModelViewTransform mvt;

        private ThermometerToolBox( EFACIntroModel model, ModelViewTransform mvt, Color backgroundColor ) {
            this.model = model;
            this.mvt = mvt;
            PNode title = new PhetPText( "Tool Box", TITLE_FONT );
            addChild( title );
            double thermometerHeight = EnergyFormsAndChangesResources.Images.THERMOMETER_BACK.getHeight( null );
            double thermometerWidth = EnergyFormsAndChangesResources.Images.THERMOMETER_BACK.getWidth( null );
            PhetPPath thermometerRegion = new PhetPPath( new Rectangle2D.Double( 0, 0, thermometerHeight * 1.1, thermometerWidth * 3 ), new Color( 0, 0, 0, 0 ) );
            addChild( new ControlPanelNode( new VBox( 0, title, thermometerRegion ), backgroundColor ) );
        }

        public void putThermometerInOpenSpot( Thermometer thermometer ) {
            // This is a little tweaky due to the relationship between the
            // thermometer in the model and the view representation.
            double xPos = 30;
            double yPos = getFullBoundsReference().getMaxY() - 30;
            boolean openLocationFound = false;
            for ( int i = 0; i < NUM_THERMOMETERS_SUPPORTED && !openLocationFound; i++ ) {
                xPos = getFullBoundsReference().width / NUM_THERMOMETERS_SUPPORTED * i + 20;
                openLocationFound = true;
                for ( Thermometer modelThermometer : model.getThermometers() ) {
                    if ( modelThermometer.position.get().distance( new ImmutableVector2D( mvt.viewToModel( xPos, yPos ) ) ) < 1E-3 ) {
                        openLocationFound = false;
                        break;
                    }
                }
            }
            thermometer.position.set( new ImmutableVector2D( mvt.viewToModel( xPos, yPos ) ) );
        }
    }

    // Event handler that returns thermometer to tool box if released above it.
    private static class ThermometerReturner extends PBasicInputEventHandler {

        private final ThermometerToolBox toolBox;
        private final ThermometerNode thermometerNode;

        private ThermometerReturner( ThermometerToolBox toolBox, ThermometerNode thermometerNode ) {
            this.thermometerNode = thermometerNode;
            this.toolBox = toolBox;
        }

        @Override public void mouseReleased( PInputEvent event ) {
            if ( thermometerNode.getFullBoundsReference().intersects( toolBox.getFullBoundsReference() ) ) {
                toolBox.putThermometerInOpenSpot( thermometerNode.getThermometer() );
            }
        }
    }
}
