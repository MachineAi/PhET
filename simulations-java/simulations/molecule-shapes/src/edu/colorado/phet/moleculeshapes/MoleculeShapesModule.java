// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.OverlayCamera;
import edu.colorado.phet.jmephet.PhetCamera;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.hud.HUDNode;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.BondTypeOverlayNode;
import edu.colorado.phet.moleculeshapes.control.GeometryNameNode;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.RealMoleculeOverlayNode;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.util.CanvasTransformedBounds;
import edu.colorado.phet.moleculeshapes.view.AtomNode;
import edu.colorado.phet.moleculeshapes.view.LonePairNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;
import edu.umd.cs.piccolo.util.PBounds;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.JmeCanvasContext;

import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.OUTSIDE_PADDING;

/**
 * Main module for Molecule Shapes
 */
public class MoleculeShapesModule extends JMEModule {

    private PhetJMEApplication app;

    /*---------------------------------------------------------------------------*
    * input mapping constants
    *----------------------------------------------------------------------------*/
    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_LMB = "CameraDrag";
    public static final String MAP_MMB = "RightMouseButton";

    private static final float ROTATION_MOUSE_SENSITIVITY = 5.0f;

    /*---------------------------------------------------------------------------*
    * model
    *----------------------------------------------------------------------------*/

    private MoleculeModel molecule = new MoleculeModel();

    public static final Property<Boolean> showLonePairs = new Property<Boolean>( true ); // TODO: convert to non-static?

    public final Property<Boolean> addSingleBondEnabled = new Property<Boolean>( true );
    public final Property<Boolean> addDoubleBondEnabled = new Property<Boolean>( true );
    public final Property<Boolean> addTripleBondEnabled = new Property<Boolean>( true );
    public final Property<Boolean> addLonePairEnabled = new Property<Boolean>( true );

    /*---------------------------------------------------------------------------*
    * dragging
    *----------------------------------------------------------------------------*/

    public static enum DragMode {
        MODEL_ROTATE, // rotate the VSEPR model molecule
        PAIR_FRESH_PLANAR, // drag an atom/lone pair on the z=0 plane
        PAIR_EXISTING_SPHERICAL, // drag an atom/lone pair across the surface of a sphere
        REAL_MOLECULE_ROTATE // rotate the "real" molecule in the display
    }

    private volatile boolean dragging = false; // keeps track of the drag state
    private volatile DragMode dragMode = DragMode.MODEL_ROTATE;
    private volatile PairGroup draggedParticle = null;
    private volatile boolean globalLeftMouseDown = false; // keep track of the LMB state, since we need to deal with a few synchronization issues

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    private volatile boolean resizeDirty = false;

    private Quaternion rotation = new Quaternion(); // The angle about which the molecule should be rotated, changes as a function of time

    private Property<Rectangle2D> realMoleculeOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, 1, 1 ) ); // initialized to technically valid state
    private Property<Rectangle2D> singleBondOverlayStageBounds;
    private Property<Rectangle2D> doubleBondOverlayStageBounds;
    private Property<Rectangle2D> tripleBondOverlayStageBounds;
    private Property<Rectangle2D> lonePairOverlayStageBounds;

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private CenteredStageCanvasTransform canvasTransform;
    private PiccoloJMENode controlPanel;
    private PiccoloJMENode namePanel;

    private JMEView guiView;

    private JMEView moleculeView;
    private Camera moleculeCamera;
    private MoleculeModelNode moleculeNode; // The molecule to display and rotate

    private MoleculeShapesControlPanel controlPanelNode;

    private RealMoleculeOverlayNode realMoleculeOverlayNode;

    private JMEInputHandler inputHandler;

    private static final Random random = new Random( System.currentTimeMillis() );


    public MoleculeShapesModule( Frame parentFrame, String name ) {
        super( parentFrame, name, new ConstantDtClock( 30.0 ) );
    }

    // should be called from stable positions in the JME and Swing EDT threads
    @Override public void initialize() {
        initializeResources();

        app = JMEUtils.getApplication();

        inputHandler = getInputHandler();

        // hook up mouse-move handlers
        inputHandler.addMapping( MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputHandler.addMapping( MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputHandler.addMapping( MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputHandler.addMapping( MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

        // hook up mouse-button handlers
        inputHandler.addMapping( MAP_LMB, new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );
        inputHandler.addMapping( MAP_MMB, new MouseButtonTrigger( MouseInput.BUTTON_MIDDLE ) );

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        inputHandler.addListener(
                new ActionListener() {
                    public void onAction( String name, boolean isMouseDown, float tpf ) {
                        // record whether the mouse button is down

                        // on left mouse button change
                        if ( name.equals( MAP_LMB ) ) {
                            globalLeftMouseDown = isMouseDown;

                            if ( isMouseDown ) {
                                onLeftMouseDown();
                            }
                            else {
                                onLeftMouseUp();
                            }
                        }

                        // kill any pair groups under the middle mouse button press
                        if ( isMouseDown && name.equals( MAP_MMB ) ) {
                            PairGroup pair = getElectronPairUnderPointer();
                            if ( pair != null ) {
                                molecule.removePair( pair );
                            }
                        }
                    }
                }, MAP_LMB, MAP_MMB );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        inputHandler.addListener(
                new AnalogListener() {
                    public void onAnalog( final String name, final float value, float tpf ) {

                        //By always updating the cursor at every mouse move, we can be sure it is always correct.
                        //Whenever there is a mouse move event, make sure the cursor is in the right state.
                        updateCursor();

                        if ( dragging ) {

                            // function that updates a quaternion in-place, by adding the necessary rotation in, multiplied by the scale
                            final VoidFunction2<Quaternion, Float> updateQuaternion = new VoidFunction2<Quaternion, Float>() {
                                public void apply( Quaternion quaternion, Float scale ) {
                                    // if our window is smaller, rotate more
                                    float correctedScale = scale / getApproximateScale();

                                    if ( name.equals( MAP_LEFT ) ) {
                                        quaternion.set( new Quaternion().fromAngles( 0, -value * correctedScale, 0 ).mult( quaternion ) );
                                    }
                                    if ( name.equals( MAP_RIGHT ) ) {
                                        quaternion.set( new Quaternion().fromAngles( 0, value * correctedScale, 0 ).mult( quaternion ) );
                                    }
                                    if ( name.equals( MAP_UP ) ) {
                                        quaternion.set( new Quaternion().fromAngles( -value * correctedScale, 0, 0 ).mult( quaternion ) );
                                    }
                                    if ( name.equals( MAP_DOWN ) ) {
                                        quaternion.set( new Quaternion().fromAngles( value * correctedScale, 0, 0 ).mult( quaternion ) );
                                    }
                                }
                            };

                            switch( dragMode ) {
                                case MODEL_ROTATE:
                                    updateQuaternion.apply( rotation, ROTATION_MOUSE_SENSITIVITY );
                                    break;
                                case REAL_MOLECULE_ROTATE:
                                    realMoleculeOverlayNode.dragRotation( updateQuaternion );
                                    break;
                                case PAIR_FRESH_PLANAR:
                                    // put the particle on the z=0 plane
                                    draggedParticle.dragToPosition( JMEUtils.convertVector( getPlanarMoleculeCursorPosition() ) );
                                    break;
                                case PAIR_EXISTING_SPHERICAL:
                                    draggedParticle.dragToPosition( JMEUtils.convertVector( getSphericalMoleculeCursorPosition( JMEUtils.convertVector( draggedParticle.position.get() ) ) ) );
                                    break;
                            }
                        }
                    }
                }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN, MAP_LMB );

        canvasTransform = new CenteredStageCanvasTransform( app );

        moleculeCamera = new PhetCamera( getStageSize(), canvasTransform.getCameraStrategy( 45, 1, 1000 ) );
        moleculeCamera.setLocation( new Vector3f( 0, 0, 40 ) );
        moleculeCamera.lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );

        moleculeView = createMainView( "Main", moleculeCamera );
        guiView = createBackGUIView( "Back GUI" );
        JMEView readoutView = createFrontGUIView( "Readout" );

        // add an offset to the left, since we have a control panel on the right
        // TODO: make the offset dependent on the control panel width?
        moleculeView.getScene().setLocalTranslation( new Vector3f( -4.5f, 1.5f, 0 ) );

        // add lighting to the main scene
        addLighting( moleculeView.getScene() );

        // when the molecule is made empty, make sure to show lone pairs again (will allow us to drag out new ones)
        molecule.onGroupChanged.addListener( new VoidFunction1<PairGroup>() {
            public void apply( PairGroup group ) {
                if ( molecule.getLonePairs().isEmpty() ) {
                    showLonePairs.set( true );
                }
            }
        } );

        moleculeNode = new MoleculeModelNode( molecule, inputHandler, readoutView, this, moleculeCamera );
        moleculeView.getScene().attachChild( moleculeNode );

        /*---------------------------------------------------------------------------*
        * molecule setup
        *----------------------------------------------------------------------------*/

        // start with two single bonds
        molecule.addPair( new PairGroup( new ImmutableVector3D( 8, 0, 3 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );
        molecule.addPair( new PairGroup( new ImmutableVector3D( 2, 8, -5 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );

        /*---------------------------------------------------------------------------*
        * real molecule overlay
        *----------------------------------------------------------------------------*/

        JMEView realMoleculeOverlayView = createMainView( "Overlay", new OverlayCamera( getStageSize(), getApp().canvasSize,
                                                                                        new CanvasTransformedBounds( canvasTransform, realMoleculeOverlayStageBounds ) ) {
            @Override public void positionMe() {
                setFrustumPerspective( 45f, 1, 1f, 1000f );
                setLocation( new Vector3f( 0, 0, 40 ) );
                lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );
            }
        } );

        realMoleculeOverlayNode = new RealMoleculeOverlayNode( this, realMoleculeOverlayView.getCamera() );
        realMoleculeOverlayView.getScene().attachChild( realMoleculeOverlayNode );

        addLighting( realMoleculeOverlayView.getScene() );

        /*---------------------------------------------------------------------------*
        * bond overlays
        *----------------------------------------------------------------------------*/

        singleBondOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );
        doubleBondOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );
        tripleBondOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );
        lonePairOverlayStageBounds = new Property<Rectangle2D>( new PBounds( 0, 0, getStageSize().width, getStageSize().height ) );

        Function2<String, Property<Rectangle2D>, JMEView> createBondOverlayView = new Function2<String, Property<Rectangle2D>, JMEView>() {
            public JMEView apply( String name, final Property<Rectangle2D> rectangle2DProperty ) {
                return createMainView( name + " Overlay", new OverlayCamera( getStageSize(), getApp().canvasSize,
                                                                             new CanvasTransformedBounds( canvasTransform,
                                                                                                          rectangle2DProperty ) ) {
                    @Override public void positionMe() {
                        setFrustumPerspective( 45f, (float) ( rectangle2DProperty.get().getWidth() / rectangle2DProperty.get().getHeight() ), 1f, 1000f );
                        setLocation( new Vector3f( 0, 0, 45 ) ); // slightly farther back, to avoid intersection with the main play area. yeah.
                        lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );
                    }
                } );
            }
        };

        // TODO: refactor

        JMEView singleBondOverlay = createBondOverlayView.apply( "Single Bond", singleBondOverlayStageBounds );
        singleBondOverlay.getScene().attachChild( new BondTypeOverlayNode( new MoleculeModel() {{
            addPair( new PairGroup( ImmutableVector3D.X_UNIT.times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );
        }}, singleBondOverlay, inputHandler, this, addSingleBondEnabled ) );
        addLighting( singleBondOverlay.getScene() );
        
        JMEView doubleBondOverlay = createBondOverlayView.apply( "Double Bond", doubleBondOverlayStageBounds );
        doubleBondOverlay.getScene().attachChild( new BondTypeOverlayNode( new MoleculeModel() {{
            addPair( new PairGroup( ImmutableVector3D.X_UNIT.times( PairGroup.BONDED_PAIR_DISTANCE ), 2, false ) );
        }}, doubleBondOverlay, inputHandler, this, addDoubleBondEnabled ) );
        addLighting( doubleBondOverlay.getScene() );
        
        JMEView tripleBondOverlay = createBondOverlayView.apply( "Triple Bond", tripleBondOverlayStageBounds );
        tripleBondOverlay.getScene().attachChild( new BondTypeOverlayNode( new MoleculeModel() {{
            addPair( new PairGroup( ImmutableVector3D.X_UNIT.times( PairGroup.BONDED_PAIR_DISTANCE ), 3, false ) );
        }}, tripleBondOverlay, inputHandler, this, addTripleBondEnabled ) );
        addLighting( tripleBondOverlay.getScene() );

        JMEView lonePairOverlay = createBondOverlayView.apply( "Lone Pair", lonePairOverlayStageBounds );
        lonePairOverlay.getScene().attachChild( new BondTypeOverlayNode( new MoleculeModel() {{
            addPair( new PairGroup( ImmutableVector3D.X_UNIT.times( PairGroup.LONE_PAIR_DISTANCE ), 0, false ) );
        }}, lonePairOverlay, inputHandler, this, addLonePairEnabled ) );
        addLighting( lonePairOverlay.getScene() );

        /*---------------------------------------------------------------------------*
        * main control panel
        *----------------------------------------------------------------------------*/
        Property<ImmutableVector2D> controlPanelPosition = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        controlPanelNode = new MoleculeShapesControlPanel( this, realMoleculeOverlayNode );
        controlPanel = new PiccoloJMENode( controlPanelNode, inputHandler, this, canvasTransform, controlPanelPosition );
        guiView.getScene().attachChild( controlPanel );
        controlPanel.onResize.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( controlPanel != null ) {
                            controlPanel.position.set( new ImmutableVector2D(
                                    getStageSize().width - controlPanel.getComponentWidth() - OUTSIDE_PADDING,
                                    getStageSize().height - controlPanel.getComponentHeight() - OUTSIDE_PADDING ) );
                        }
                        resizeDirty = true; // TODO: better way of getting this dependency?
                    }
                }, true );

        /*---------------------------------------------------------------------------*
        * "geometry name" panel
        *----------------------------------------------------------------------------*/
        namePanel = new PiccoloJMENode( new MoleculeShapesPanelNode( new GeometryNameNode( molecule ), Strings.CONTROL__GEOMETRY_NAME ) {{
            // TODO fix (temporary offset since PiccoloJMENode isn't checking the "origin")
            setOffset( 0, 10 );
        }}, inputHandler, this, canvasTransform );
        guiView.getScene().attachChild( namePanel );
        namePanel.position.set( new ImmutableVector2D( OUTSIDE_PADDING, OUTSIDE_PADDING ) );

//         guiView.getScene().attachChild( new Geometry( "DebugThing", new Quad( 2000, 2000, true ) ) {{
//            setMaterial( new Material( getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
//                setColor( "Color", new ColorRGBA( 1, 0, 0, 1 ) );
//            }} );
//        }} );
    }

    @Override public void updateState( final float tpf ) {
        super.updateState( tpf );
        molecule.update( tpf );
        moleculeNode.setLocalRotation( rotation );

        // update the overlay viewport
        if ( resizeDirty && controlPanel != null ) {
            // TODO: refactoring here into generic viewport handling? (just tell it to be at X/Y for stage and it sticks there?)
            resizeDirty = false;

            double bondScaledWidth = getStageSize().getWidth() / 2.3;
            double bondScaledHeight = getStageSize().getHeight() / 2.3;

            // bond overlays
            Rectangle2D singleBondTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getSingleBondTargetBounds() );
            Rectangle2D doubleBondTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getDoubleBondTargetBounds() );
            Rectangle2D tripleBondTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getTripleBondTargetBounds() );
            Rectangle2D lonePairTargetStageBounds = controlPanel.transformBoundsToStage( controlPanelNode.getLonePairTargetBounds() );
            // TODO: refactor
            singleBondOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            singleBondTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            singleBondTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
            doubleBondOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            doubleBondTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            doubleBondTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
            tripleBondOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            tripleBondTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            tripleBondTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );
            lonePairOverlayStageBounds.set(
                    new PBounds(
                            // position the center of these bounds at the middle-left edge of the target bounds
                            lonePairTargetStageBounds.getMinX() - bondScaledWidth / 2,
                            lonePairTargetStageBounds.getCenterY() - bondScaledHeight / 2,
                            bondScaledWidth,
                            bondScaledHeight ) );

            // handle the real molecule overlay
            boolean showOverlay = controlPanelNode.isOverlayVisible();
            realMoleculeOverlayNode.setCullHint( showOverlay ? CullHint.Never : CullHint.Always );
            if ( showOverlay ) {
                realMoleculeOverlayStageBounds.set( controlPanel.transformBoundsToStage( controlPanelNode.getRealMoleculeOverlayBounds() ) );
            }
        }
    }

    public PhetJMEApplication getApp() {
        return app;
    }

    @Override public PhetJMEApplication createApplication( Frame parentFrame ) {
        final PhetJMEApplication application = new PhetJMEApplication( parentFrame );
        MoleculeShapesColor.BACKGROUND.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
            public void apply( ColorRGBA colorRGBA ) {
                application.backgroundColor.set( colorRGBA );
            }
        } );
        return application;
    }

    public void startOverlayMoleculeDrag() {
        dragging = true;
        dragMode = DragMode.REAL_MOLECULE_ROTATE;
    }

    private void onLeftMouseDown() {
        // for dragging, ignore mouse presses over the HUD
        HUDNode.withComponentUnderPointer( guiView.getScene(), inputHandler, new VoidFunction1<Component>() {
            public void apply( final Component componentUnderPointer ) {
                boolean mouseOverInterface = componentUnderPointer != null;
                if ( !mouseOverInterface ) {
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
                            dragging = true;

                            PairGroup pair = getElectronPairUnderPointer();
                            if ( pair != null ) {
                                // we are over a pair group, so start the drag on it
                                dragMode = DragMode.PAIR_EXISTING_SPHERICAL;
                                draggedParticle = pair;
                                pair.userControlled.set( true );
                            }
                            else {
                                // set up default drag mode
                                dragMode = DragMode.MODEL_ROTATE;
                            }
                        }
                    } );
                }
            }
        } );
    }

    private void onLeftMouseUp() {
        // not dragging anymore
        dragging = false;

        // release an electron pair if we were dragging it
        if ( dragMode == DragMode.PAIR_FRESH_PLANAR || dragMode == DragMode.PAIR_EXISTING_SPHERICAL ) {
            draggedParticle.userControlled.set( false );
        }
    }

    public void startNewInstanceDrag( int bondOrder ) {
        // sanity check
        if ( !molecule.wouldAllowBondOrder( bondOrder ) ) {
            // don't add to the molecule if it is full
            return;
        }

        Vector3f localPosition = getPlanarMoleculeCursorPosition();

        PairGroup pair = new PairGroup( JMEUtils.convertVector( localPosition ), bondOrder, true );
        molecule.addPair( pair );

        // set up dragging information
        dragging = true;
        dragMode = DragMode.PAIR_FRESH_PLANAR;
        draggedParticle = pair;

        /*
         * If the left mouse button is not down, simulate a mouse-up. This is needed due to threading issues,
         * since if you do an "instant" mouse down/up, they both get processed before this is called.
         */
        if ( !globalLeftMouseDown ) {
            onLeftMouseUp();
        }
    }

    public static void addLighting( Node node ) {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( MoleculeShapesConstants.SUN_COLOR );
        node.addLight( sun );

        DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( MoleculeShapesConstants.MOON_COLOR );
        node.addLight( moon );
    }

    private void updateCursor() {
        //This solves a problem that we saw that: when there was no padding or other component on the side of the canvas, the mouse would become East-West resize cursor
        //And wouldn't change back.
        JmeCanvasContext context = (JmeCanvasContext) app.getContext();
        final Canvas canvas = context.getCanvas();

        //If the mouse is in front of a grabbable object, show a hand, otherwise show the default cursor
        final PairGroup pair = getElectronPairUnderPointer();

        HUDNode.withComponentUnderPointer( guiView.getScene(), inputHandler, new VoidFunction1<Component>() {
            public void apply( Component component ) {
                if ( dragging && ( dragMode == DragMode.MODEL_ROTATE || dragMode == DragMode.REAL_MOLECULE_ROTATE ) ) {
                    // rotating the molecule. for now, trying out the "move" cursor
                    canvas.setCursor( Cursor.getPredefinedCursor( MoleculeShapesProperties.useRotationCursor.get() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR ) );
                }
                else if ( pair != null || dragging ) {
                    // over a pair group, OR dragging one
                    canvas.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
                else if ( component != null ) {
                    // over a HUD node, so set the cursor to what the component would want
                    canvas.setCursor( component.getCursor() );
                }
                else {
                    // default to the default cursor
                    canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                }
            }
        } );
    }

    public Vector3f getPlanarMoleculeCursorPosition() {
        Vector2f click2d = inputHandler.getCursorPosition();
        Vector3f click3d = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        float t = -click3d.getZ() / dir.getZ(); // solve for below equation at z=0. assumes camera isn't z=0, which should be safe here

        Vector3f globalStartPosition = click3d.add( dir.mult( t ) );

        // transform to moleculeNode coordinates and return
        return moleculeNode.getWorldTransform().transformInverseVector( globalStartPosition, new Vector3f() );
    }

    public Vector3f getSphericalMoleculeCursorPosition( Vector3f currentLocalPosition ) {
        // decide whether to grab the closest or farthest point if possible. for now, we try to NOT move the pair at the start of the drag
        boolean returnCloseHit = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).mult( currentLocalPosition ).z >= 0;

        // override for dev option
        if ( !MoleculeShapesProperties.allowDraggingBehind.get() ) {
            returnCloseHit = true;
        }

        // set up intersection stuff
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputHandler.getCursorPosition();
        Vector3f click3d = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f transformedPosition = moleculeNode.getWorldTransform().transformInverseVector( click3d, new Vector3f() );
        Vector3f transformedDirection = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector
        Ray ray = new Ray( transformedPosition, transformedDirection );

        // how far we will end up from the center atom
        float finalDistance = (float) draggedParticle.getIdealDistanceFromCenter();

        // our sphere to cast our ray against
        BoundingSphere sphere = new BoundingSphere( finalDistance, new Vector3f( 0, 0, 0 ) );

        sphere.collideWithRay( ray, results );
        if ( results.size() == 0 ) {
            /*
             * Compute the point where the closest line through the camera and tangent to our bounding sphere intersects the sphere
             * ie, think 2d. we have a unit sphere centered at the origin, and a camera at (d,0). Our tangent point satisfies two
             * important conditions:
             * - it lies on the sphere. x^2 + y^2 == 1
             * - vector to the point (x,y) is tangent to the vector from (x,y) to our camera (d,0). thus (x,y) . (d-y, -y) == 0
             * Solve, and we get x = 1/d  plug back in for y (call that height), and we have our 2d solution.
             *
             * Now, back to 3D. Since camera is (0,0,d), our z == 1/d and our x^2 + y^2 == (our 2D y := height), then rescale them out of the unit sphere
             */

            float distanceFromCamera = transformedPosition.distance( new Vector3f() );

            // first, calculate it in unit-sphere, as noted above
            float d = distanceFromCamera / finalDistance; // scaled distance to the camera (from the origin)
            float z = 1 / d; // our result z (down-scaled)
            float height = FastMath.sqrt( d * d - 1 ) / d; // our result (down-scaled) magnitude of (x,y,0), which is the radius of the circle composed of all points that could be tangent

            /*
             * Since our camera isn't actually on the z-axis, we need to calculate two vectors. One is the direction towards
             * the camera (planeNormal, easy!), and the other is the direction perpendicular to the planeNormal that points towards
             * the mouse pointer (planeHitDirection).
             */

            // intersect our camera ray against our perpendicular plane (perpendicular to our camera position from the origin) to determine the orientations
            Vector3f planeNormal = transformedPosition.normalize();
            float t = -( transformedPosition.length() ) / ( planeNormal.dot( transformedDirection ) );
            Vector3f planeHitDirection = transformedPosition.add( transformedDirection.mult( t ) ).normalize();

            // use the above plane hit direction (perpendicular to the camera) and plane normal (collinear with the camera) to calculate the result
            Vector3f downscaledResult = planeHitDirection.mult( height ).add( planeNormal.mult( z ) );

            // scale it back to our sized sphere
            return downscaledResult.mult( finalDistance );
        }
        else {
            // pick our desired hitpoint (there are only 2), and return it
            CollisionResult result = returnCloseHit ? results.getClosestCollision() : results.getFarthestCollision();
            return result.getContactPoint();
        }
    }

    /**
     * @return The closest (hit) electron pair currently under the mouse pointer, or null if there is none
     */
    public PairGroup getElectronPairUnderPointer() {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputHandler.getCursorPosition();
        Vector3f click3d = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = moleculeCamera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );
        Ray ray = new Ray( click3d, dir );
        moleculeView.getScene().collideWith( ray, results );
        for ( CollisionResult result : results ) {
            PairGroup pair = getElectronPairForTarget( result.getGeometry() );
            if ( pair != null ) {
                return pair;
            }
        }
        return null;
    }

    /**
     * Given a spatial target, return any associated electron pair, or null if there is none
     *
     * @param target JME Spatial
     * @return Electron pair, or null
     */
    private PairGroup getElectronPairForTarget( Spatial target ) {
        boolean isAtom = target instanceof AtomNode;
        boolean isLonePair = target instanceof LonePairNode;

        if ( isAtom ) {
            return ( (AtomNode) target ).pair;
        }
        else if ( isLonePair ) {
            if ( !target.getCullHint().equals( CullHint.Always ) ) {
                return ( (LonePairNode) target ).pair;
            }
            else {
                return null; // lone pair invisible
            }
        }
        else {
            if ( target.getParent() != null ) {
                return getElectronPairForTarget( target.getParent() );
            }
            else {
                // failure
                return null;
            }
        }
    }

    @Override public void updateLayout( Dimension canvasSize ) {
        super.updateLayout( canvasSize );
        resizeDirty = true;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry( getAssetManager() );
    }

    public MoleculeModel getMolecule() {
        return molecule;
    }

    public boolean canAutoRotateRealMolecule() {
        return !( dragging && dragMode == DragMode.REAL_MOLECULE_ROTATE );
    }

    /**
     * @return Our relative screen display scale compared to the stage scale
     */
    public ImmutableVector2D getScale() {
        return new ImmutableVector2D( getCanvasSize().getWidth() / getStageSize().getWidth(),
                                      getCanvasSize().getHeight() / getStageSize().getHeight() );
    }

    public float getApproximateScale() {
        ImmutableVector2D scale = getScale();
        return (float) ( ( scale.getX() + scale.getY() ) / 2 );
    }
}
