/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.phetcomponents.PhetJPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Electrode;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.dischargelamps.model.ElectronSink;
import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.view.ElectronGraphic;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.lasers.view.BeamCurtainGraphic;
import edu.colorado.phet.lasers.view.LampGraphic;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.photoelectric.PhotoelectricApplication;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.controller.AmmeterDataCollector;
import edu.colorado.phet.photoelectric.controller.PhotoelectricControlPanel;
import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;
import edu.colorado.phet.photoelectric.view.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * PhotoelectricModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModule extends BaseLaserModule {

    //----------------------------------------------------------------
    // Class data 
    //----------------------------------------------------------------

    static private final int BEAM_VIEW = 1;
    static private final int PHOTON_VIEW = 2;

    static private final double TUBE_LAYER = 2000;
    static private final double CIRCUIT_LAYER = TUBE_LAYER - 1;
    static private final double SLIDER_LAYER = CIRCUIT_LAYER + 1;
    static private final double LAMP_LAYER = 1000;
    static private final double BEAM_LAYER = 900;
    static private final double ELECTRON_LAYER = 900;

    static private HashMap TARGET_COLORS = new HashMap();

    static {
        TARGET_COLORS.put( PhotoelectricTarget.COPPER, new Color( 210, 130, 30 ) );
        TARGET_COLORS.put( PhotoelectricTarget.MAGNESIUM, new Color( 130, 150, 170 ) );
        TARGET_COLORS.put( PhotoelectricTarget.SODIUM, new Color( 160, 180, 160 ) );
        TARGET_COLORS.put( PhotoelectricTarget.ZINC, new Color( 200, 200, 200 ) );
        TARGET_COLORS.put( PhotoelectricTarget.PLATINUM, new Color( 203, 230, 230 ) );
    }

//    public static boolean DEBUG = true;
    public static boolean DEBUG = false;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private ElectronSink anode;
    private PhotoelectricTarget targetPlate;

    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    // An ElectronSink that absorbs electrons if they come back and hit the target
    // TODO: get this into the PhotoelectronTarget class
    private ElectronSink targetSink;
    private BeamCurtainGraphic beamGraphic;
    // Flag for type of beam view: either photon or solid beam
    private int viewType = BEAM_VIEW;
    private CurrentVsVoltageGraph2 currentVsVoltageGraph;
//    private CurrentVsVoltageGraph currentVsVoltageGraph;
    private PhetImageGraphic circuitGraphic;


    /**
     * Constructor
     *
     * @param application
     */
    public PhotoelectricModule( PhotoelectricApplication application ) {
        super( "Photoelectric Effect", application.getClock() );

        // Set up the basic stuff
        AbstractClock clock = application.getClock();
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setPaintStrategy( ApparatusPanel2.OFFSCREEN_BUFFER_STRATEGY );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        //----------------------------------------------------------------
        // Model
        //----------------------------------------------------------------

        // Set up the model
        PhotoelectricModel model = new PhotoelectricModel( clock );
        setModel( model );
        setControlPanel( new ControlPanel( this ) );
//        model.getTarget().addListener( new CathodeListener() );

        // Add an electron sink in the same place as the target plate, to absorb electrons
        // TODO: refactor this into the model. Prefereably into the PhotoelectricPlate or Electrode class
        {
            PhotoelectricTarget target = model.getTarget();
            targetSink = new ElectronSink( model, target.getEndpoints()[0], target.getEndpoints()[1] );
            model.addModelElement( targetSink );
        }

        // Set the default work function for the target
        model.getTarget().setWorkFunction( PhotoelectricTarget.WORK_FUNCTIONS.get( PhotoelectricTarget.SODIUM ) );

        //----------------------------------------------------------------
        // View
        //----------------------------------------------------------------
        CollimatedBeam beam = model.getBeam();

        // Add a graphic for the tube
        addTubeGraphic( model, getApparatusPanel() );

        // Add a graphic for the beam
        addBeamGraphic( beam );

        // Add a listener that will place photons right next to the plate when we are in beam view mode
        beam.addPhotonEmittedListener( new PhotonPlacementManager() );

        // Add a listener that will produce photon graphics for the beam and take them away when the photons
        // leave the system
        beam.addPhotonEmittedListener( new PhotonGraphicManager() );

        // Add a listener to the target plate that will create electron graphics when electrons
        // are produced, and remove them when they the electrons leave the system.
        PhotoelectricTarget target = model.getTarget();
        target.addListener( new ElectronGraphicManager() );

        // Add the battery and wire graphic
        addCircuitGraphic( apparatusPanel );

        // Add a graphic for the target plate
        addTargetGraphic( model, apparatusPanel );

        // Add a graphic for the anode
        addAnodeGraphic( model, apparatusPanel );

        // Set the targetPlate to listen for potential changes relative to the anode
        hookCathodeToAnode();

        // Put a mask over the part of the light beam that is to the left of the target
        Rectangle mask = new Rectangle( 0, 0, DischargeLampsConfig.CATHODE_LOCATION.x, 2000 );
        PhetShapeGraphic maskGraphic = new PhetShapeGraphic( getApparatusPanel(),
                                                             mask,
                                                             getApparatusPanel().getBackground() );
        getApparatusPanel().addGraphic( maskGraphic, BEAM_LAYER + 1 );

        // Create current vs voltage graph
        GraphWindow graphWindow = new GraphWindow( application.getPhetFrame(),
                                                   clock,
                                                   getPhotoelectricModel() );
        graphWindow.setVisible( false );


        //----------------------------------------------------------------
        // Controls
        //----------------------------------------------------------------

        // Set up the control panel
        new PhotoelectricControlPanel( this, graphWindow );

        // Add a slider for the battery
//        GraphicSlider batterySlider = new GraphicSlider( getApparatusPanel() );
//        batterySlider = new PhotoelectricSlider( getApparatusPanel(), 100 /* track length */ );
//        addGraphic( batterySlider, SLIDER_LAYER );

//        batterySlider.setMinimum( (int)-( PhotoelectricModel.MIN_VOLTAGE ) );
//        batterySlider.setMaximum( (int)( PhotoelectricModel.MAX_VOLTAGE ) );
//        batterySlider.setValue( (int)( getPhotoelectricModel().getAnodePotential() * PhotoelectricModel.MAX_VOLTAGE ) );
//        batterySlider.addTick( batterySlider.getMinimum() );
//        batterySlider.addTick( batterySlider.getMaximum() );
//        batterySlider.addTick( 0 );

//        batterySlider.centerRegistrationPoint();
        // TODO: locate the slider symbolically
//        batterySlider.setLocation( 400, 400 );
//        final GraphicSlider batterySlider1 = batterySlider;
//        batterySlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                int voltage = batterySlider1.getValue();
//                getPhotoelectricModel().getRightHandPlate().setPotential( voltage / PhotoelectricModel.MAX_VOLTAGE );
//            }
//        } );


        //----------------------------------------------------------------
        // Total hack to get beam to look right when things come up. This should
        // be removed when BeamCurtainGraphic.update() is fixed
        //----------------------------------------------------------------
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                CollimatedBeam beam = getPhotoelectricModel().getBeam();
                beam.setPhotonsPerSecond( beam.getPhotonsPerSecond() );
            }
        } );

        //----------------------------------------------------------------
        // Debug
        //----------------------------------------------------------------

        // Add options menu item that will show current
        JMenu optionsMenu = application.getOptionsMenu();
        final JCheckBoxMenuItem currentDisplayMI = new JCheckBoxMenuItem( "Show meters" );
        optionsMenu.add( currentDisplayMI );

        final JDialog meterDlg = new JDialog( PhetApplication.instance().getPhetFrame(), false );

        final AmmeterView ammeterView = new AmmeterView( getPhotoelectricModel().getAmmeter() );
        final IntensityView intensityView = new IntensityView( getPhotoelectricModel().getBeamIntensityMeter() );
        JPanel meterPanel = new JPanel( new GridLayout( 2, 1 ) );
        meterDlg.setContentPane( meterPanel );
        meterPanel.add( ammeterView );
        meterPanel.add( intensityView );
        meterDlg.pack();
        currentDisplayMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                meterDlg.setVisible( currentDisplayMI.isSelected() );
            }
        } );

//        final AmmeterView ammeterView2 = new AmmeterView( getPhotoelectricModel().getAmmeter(), false );
//        ammeterView2.setBorder( (LineBorder)BorderFactory.createLineBorder(Color.black) );
//        PhetJPanel pjp = new PhetJPanel( getApparatusPanel(), ammeterView2 );
//        pjp.setLocation( DischargeLampsConfig.ANODE_LOCATION.x + 50, DischargeLampsConfig.ANODE_LOCATION.y );
//        getApparatusPanel().addGraphic( pjp, CIRCUIT_LAYER + 1 );

//        JPanel wrapperPanel = new JPanel();
//        wrapperPanel.setBounds( DischargeLampsConfig.ANODE_LOCATION.x + 50, DischargeLampsConfig.ANODE_LOCATION.y,
//                                60, 20 );
//        wrapperPanel.add( ammeterView2 );
//        getApparatusPanel().add( wrapperPanel );

        // Slap an ammeter on the circuit, near the anode
        AmmeterViewGraphic avg = new AmmeterViewGraphic( getApparatusPanel(),
                                                         getPhotoelectricModel().getAmmeter(),
                                                         getPhotoelectricModel() );
        avg.setLocation( DischargeLampsConfig.ANODE_LOCATION.x- 80, DischargeLampsConfig.ANODE_LOCATION.y + 218 );
        getApparatusPanel().addGraphic( avg, CIRCUIT_LAYER + 1);



        // Add an option to randomize the electron velocities
        final JRadioButtonMenuItem uniformSpeedOption = new JRadioButtonMenuItem( "Uniform electron speeds" );
        optionsMenu.addSeparator();
        optionsMenu.add( uniformSpeedOption );
        uniformSpeedOption.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( uniformSpeedOption.isSelected() ) {
                    getPhotoelectricModel().getTarget().setUniformInitialElectronSpeedStrategy();
                }
            }
        } );
        final JRadioButtonMenuItem randomizedSpeedOption = new JRadioButtonMenuItem( "Randomized electron speeds" );
        optionsMenu.add( randomizedSpeedOption );
        randomizedSpeedOption.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( randomizedSpeedOption.isSelected() ) {
                    getPhotoelectricModel().getTarget().setRandomizedInitialElectronSpeedStrategy();
                }
            }
        } );
        optionsMenu.addSeparator();
        ButtonGroup speedOptionBtnGrp = new ButtonGroup();
        speedOptionBtnGrp.add( uniformSpeedOption );
        speedOptionBtnGrp.add( randomizedSpeedOption );
        uniformSpeedOption.setSelected( true );



        // Draw red dots on the beam source location and the middle of the target plate
        if( DEBUG ) {
            PhetShapeGraphic beamIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                                   new Ellipse2D.Double( beam.getPosition().getX(),
                                                                                         beam.getPosition().getY(),
                                                                                         10, 10 ),
                                                                   Color.red );
            getApparatusPanel().addGraphic( beamIndicator, 10000 );
            PhetShapeGraphic cathodIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                                     new Ellipse2D.Double( targetPlate.getPosition().getX(),
                                                                                           targetPlate.getPosition().getY(),
                                                                                           10, 10 ),
                                                                     Color.red );
            getApparatusPanel().addGraphic( cathodIndicator, 10000 );
        }
    }

    /**
     * @param beam
     */
    private void addBeamGraphic( CollimatedBeam beam ) {
        beamGraphic = new BeamCurtainGraphic( getApparatusPanel(), beam );
        getApparatusPanel().addGraphic( beamGraphic, BEAM_LAYER );
        try {
            BufferedImage lampImg = ImageLoader.loadBufferedImage( PhotoelectricConfig.LAMP_IMAGE_FILE );
            // Make the lens on the lamp the same size as the beam
            AffineTransform scaleTx = AffineTransform.getScaleInstance( 100.0 / lampImg.getWidth(),
                                                                        beam.getWidth() / lampImg.getHeight() );
            AffineTransformOp scaleTxOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
            lampImg = scaleTxOp.filter( lampImg, null );

            Point2D rp = new Point2D.Double( lampImg.getWidth(), lampImg.getHeight() / 2 );
            AffineTransform atx = AffineTransform.getRotateInstance( beam.getAngle(), rp.getX(), rp.getY() );

            LampGraphic lampGraphic = new LampGraphic( beam, getApparatusPanel(), lampImg, atx );
            // todo: this is positioned with hard numbers. Fix it
            lampGraphic.setLocation( (int)beam.getPosition().getX() - 90, (int)beam.getPosition().getY() );
            getApparatusPanel().addGraphic( lampGraphic, LAMP_LAYER );

            // Put a mask behind the lamp graphic to hide the beam or photons that start behind it
            Rectangle mask = new Rectangle( 0, 0, lampImg.getWidth(), lampImg.getHeight() );
            PhetShapeGraphic maskGraphic = new PhetShapeGraphic( getApparatusPanel(),
                                                                 mask,
                                                                 getApparatusPanel().getBackground() );
            maskGraphic.setTransform( atx );
            maskGraphic.setLocation( lampGraphic.getLocation() );
            getApparatusPanel().addGraphic( maskGraphic, LAMP_LAYER - 1 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a typed reference to the model
     */
    private PhotoelectricModel getPhotoelectricModel() {
        return (PhotoelectricModel)getModel();
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        circuitGraphic = new PhetImageGraphic( getApparatusPanel(), "images/battery-w-wires-2.png" );
        AffineTransform flipVertical = AffineTransform.getScaleInstance( 1, -1 );
        flipVertical.translate( 0, -circuitGraphic.getImage().getHeight() );
        AffineTransformOp flipVerticalOp = new AffineTransformOp( flipVertical, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage flippedImg = flipVerticalOp.filter( circuitGraphic.getImage(), null );
        circuitGraphic.setImage( flippedImg );

        scaleImageGraphic( circuitGraphic );
        circuitGraphic.setRegistrationPoint( (int)( 124 * externalGraphicsScale ),
                                             (int)( 110 * externalGraphicsScale ) );
        circuitGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, CIRCUIT_LAYER );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     *
     * @param model
     * @param apparatusPanel
     */
    private void addTubeGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        ResonatingCavity tube = model.getTube();
        ResonatingCavityGraphic tubeGraphic = new ResonatingCavityGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, TUBE_LAYER );
    }

    /**
     * Creates a listener that manages the production rate of the targetPlate based on its potential
     * relative to the anode
     */
    private void hookCathodeToAnode() {
        anode.addStateChangeListener( new Electrode.StateChangeListener() {
            public void stateChanged( Electrode.StateChangeEvent event ) {
                double anodePotential = event.getElectrode().getPotential();
                targetPlate.setSinkPotential( anodePotential );
            }
        } );
    }

    /**
     * @param model
     * @param apparatusPanel
     */
    private void addAnodeGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        this.anode = model.getRightHandPlate();
        PhetImageGraphic anodeGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

        // Make the graphic the right size
        double scaleX = 1;
        double scaleY = DischargeLampsConfig.CATHODE_LENGTH / anodeGraphic.getImage().getHeight();
        AffineTransformOp scaleOp = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ),
                                                           AffineTransformOp.TYPE_BILINEAR );
        anodeGraphic.setImage( scaleOp.filter( anodeGraphic.getImage(), null ) );
        anodeGraphic.setRegistrationPoint( (int)anodeGraphic.getBounds().getWidth(),
                                           (int)anodeGraphic.getBounds().getHeight() / 2 );

        anodeGraphic.setRegistrationPoint( 0, (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( DischargeLampsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, CIRCUIT_LAYER );
    }

    /**
     * @param model
     * @param apparatusPanel
     */
    private void addTargetGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        targetPlate = model.getTarget();
        PhetImageGraphic targetGraphic = new PhetImageGraphic( getApparatusPanel(), "images/electrode-2.png" );

        // Make the graphic the right size
        double scaleX = 1;
        double scaleY = DischargeLampsConfig.CATHODE_LENGTH / targetGraphic.getImage().getHeight();
        AffineTransformOp scaleOp = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ),
                                                           AffineTransformOp.TYPE_BILINEAR );
        targetGraphic.setImage( scaleOp.filter( targetGraphic.getImage(), null ) );
        targetGraphic.setRegistrationPoint( (int)targetGraphic.getBounds().getWidth(),
                                            (int)targetGraphic.getBounds().getHeight() / 2 );

        targetGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( targetGraphic, CIRCUIT_LAYER );

        // Add a layer on top of the electrode to represent the target material
        double materialOffsetY = 5;
        double materialThickness = 7;
        Rectangle2D material = new Rectangle2D.Double( targetGraphic.getBounds().getMaxX(),
                                                       targetGraphic.getBounds().getMinY() + materialOffsetY,
                                                       materialThickness,
                                                       targetGraphic.getBounds().getHeight() - 2 * materialOffsetY );
        Color color = (Color)TARGET_COLORS.get( targetPlate.getMaterial() );
        final PhetShapeGraphic targetMaterialGraphic = new PhetShapeGraphic( getApparatusPanel(), material, color );
        getApparatusPanel().addGraphic( targetMaterialGraphic, CIRCUIT_LAYER );

        // Add a listener to the target that will set the proper color if the material changes
        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter()  {
            public void targetMaterialChanged( PhotoelectricModel.ChangeEvent event ) {
                targetMaterialGraphic.setPaint( (Paint)TARGET_COLORS.get( targetPlate.getMaterial() ) );
            }
        } );
    }

    //----------------------------------------------------------------
    // Utility methods
    //----------------------------------------------------------------

    /**
     * Scales an image graphic so it appears properly on the screen. This method depends on the image used by the
     * graphic to have been created at the same scale as the battery-wires graphic. The scale is based on the
     * distance between the electrodes in that image and the screen distance between the electrodes specified
     * in the configuration file.
     *
     * @param imageGraphic
     */
    private void scaleImageGraphic( PhetImageGraphic imageGraphic ) {
        if( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( DischargeLampsConfig.ANODE_LOCATION,
                                           DischargeLampsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        imageGraphic.setImage( externalGraphicScaleOp.filter( imageGraphic.getImage(), null ) );
    }

    /**
     * Computes the scale to be applied to externally created graphics.
     * <p/>
     * Scale is determined by specifying a distance in the external graphics that should
     * be the same as the distance between two point on the screen.
     *
     * @param p1
     * @param p2
     * @param externalGraphicDist
     */
    private void determineExternalGraphicScale( Point p1, Point p2, int externalGraphicDist ) {
        externalGraphicsScale = p1.distance( p2 ) / externalGraphicDist;
    }

    //----------------------------------------------------------------
    // State/mode setters
    //----------------------------------------------------------------

    /**
     * Toggles the view of the light between beam view and photon view
     *
     * @param isEnabled
     */
    public void setPhotonViewEnabled( boolean isEnabled ) {
        viewType = isEnabled ? PHOTON_VIEW : BEAM_VIEW;
        beamGraphic.setVisible( !isEnabled );
    }

    //----------------------------------------------------------------
    // Inner classes for event handling
    //----------------------------------------------------------------

    /**
     * Modifies the initial placement of photons to be very near the target when we're in
     * beam view. This prevents the delay in response of the target when the wavelength or
     * intensity of the beam is changed.
     */
    private class PhotonPlacementManager implements PhotonEmittedListener {
        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            if( viewType == BEAM_VIEW ) {
                Photon photon = event.getPhoton();
                Line2D photonPath = new Line2D.Double( photon.getPosition().getX(),
                                                       photon.getPosition().getY(),
                                                       photon.getPosition().getX() + photon.getVelocity().getX(),
                                                       photon.getPosition().getY() + photon.getVelocity().getY() );
                Point2D p = MathUtil.getLinesIntersection( photonPath.getP1(), photonPath.getP2(),
                                                           targetPlate.getEndpoints()[0], targetPlate.getEndpoints()[1] );
                photon.setPosition( p.getX() - photon.getVelocity().getX(),
                                    p.getY() - photon.getVelocity().getY() );
            }
        }
    }

    /**
     * Creates, adds and removes graphics for electrons
     */
    private class ElectronGraphicManager implements ElectronSource.ElectronProductionListener {
        public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
            Electron electron = event.getElectron();
            final ElectronGraphic eg = new ElectronGraphic( getApparatusPanel(), electron );
            getApparatusPanel().addGraphic( eg, ELECTRON_LAYER );

            electron.addChangeListener( new Electron.ChangeListener() {
                public void leftSystem( Electron.ChangeEvent changeEvent ) {
                    getApparatusPanel().removeGraphic( eg );
                }

                public void energyChanged( Electron.ChangeEvent changeEvent ) {
                    // noop
                }
            } );
        }
    }

    /**
     * Creates, adds and removes graphics for photons
     */
    private class PhotonGraphicManager implements PhotonEmittedListener {

        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            if( viewType == PHOTON_VIEW ) {
                Photon photon = event.getPhoton();
                final PhotonGraphic pg = PhotonGraphic.getInstance( getApparatusPanel(), photon );
                getApparatusPanel().addGraphic( pg, BEAM_LAYER );

                photon.addLeftSystemListener( new Photon.LeftSystemEventListener() {
                    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
                        getApparatusPanel().removeGraphic( pg );
                    }
                } );
            }
        }
    }
}
