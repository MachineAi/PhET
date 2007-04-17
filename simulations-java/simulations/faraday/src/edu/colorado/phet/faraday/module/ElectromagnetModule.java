/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.control.FaradayControlPanel;
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.Vector2D;
import edu.colorado.phet.faraday.view.*;


/**
 * ElectromagnetModule is the "Electromagnet" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double ELECTROMAGNET_BACK_LAYER = 1;
    private static final double COMPASS_GRID_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double ELECTROMAGNET_FRONT_LAYER = 4;
    private static final double FIELD_METER_LAYER = 5;

    // Locations
    private static final Point ELECTROMAGNET_LOCATION = new Point( 400, 400 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 500, 150 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Battery
    private static final double BATTERY_AMPLITUDE = 1.0;
    
    // AC Power Supply
    private static final double AC_MAX_AMPLITUDE = 0.5;
    private static final double AC_FREQUENCY = 0.5;
    
    // Source Coil
    private static final int ELECTROMAGNET_NUMBER_OF_LOOPS = FaradayConstants.ELECTROMAGNET_LOOPS_MAX;
    private static final double ELECTROMAGNET_LOOP_RADIUS = 50.0;  // Fixed loop radius
    private static final double ELECTROMAGNET_DIRECTION = 0.0; // radians
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    private ACPowerSupply _acPowerSupplyModel;
    private SourceCoil _sourceCoilModel;
    private Electromagnet _electromagnetModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private ElectromagnetGraphic _electromagnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private ElectromagnetPanel _electromagnetPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public ElectromagnetModule() {

        super( FaradayResources.getString( "ElectromagnetModule.title" ) );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
     
        // Battery
        _batteryModel = new Battery();
        _batteryModel.setMaxVoltage( FaradayConstants.BATTERY_VOLTAGE_MAX  );
        _batteryModel.setAmplitude( BATTERY_AMPLITUDE );
        _batteryModel.setEnabled( true );
        
        // AC Power Supply
        _acPowerSupplyModel = new ACPowerSupply();
        _acPowerSupplyModel.setMaxVoltage( FaradayConstants.AC_VOLTAGE_MAX );
        _acPowerSupplyModel.setMaxAmplitude( AC_MAX_AMPLITUDE );
        _acPowerSupplyModel.setFrequency( AC_FREQUENCY );
        _acPowerSupplyModel.setEnabled( false );
        model.addModelElement( _acPowerSupplyModel );
        
        // Source Coil
        _sourceCoilModel = new SourceCoil();
        _sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        _sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        _sourceCoilModel.setDirection( ELECTROMAGNET_DIRECTION );
        
        // Electromagnet
        AbstractVoltageSource voltageSource = null;
        if ( _batteryModel.isEnabled() ) {
            voltageSource = _batteryModel;
        }
        else if ( _acPowerSupplyModel.isEnabled() ) {
            voltageSource = _acPowerSupplyModel;
        }
        _electromagnetModel = new Electromagnet( _sourceCoilModel, voltageSource );
        _electromagnetModel.setMaxStrength( FaradayConstants.ELECTROMAGNET_STRENGTH_MAX );
        _electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        _electromagnetModel.setDirection( ELECTROMAGNET_DIRECTION );
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil appearance.
        _electromagnetModel.update();
        
        // Compass model
        _compassModel = new Compass( _electromagnetModel );
        _compassModel.setBehavior( Compass.INCREMENTAL_BEHAVIOR );
        _compassModel.setLocation( COMPASS_LOCATION );
        model.addModelElement( _compassModel );
        
        // Field Meter
        _fieldMeterModel = new FieldMeter( _electromagnetModel );
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( getClock() );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Electromagnet
        _electromagnetGraphic = new ElectromagnetGraphic( apparatusPanel, model, 
                _electromagnetModel, _sourceCoilModel, _batteryModel, _acPowerSupplyModel );
        apparatusPanel.addChangeListener( _electromagnetGraphic );
        apparatusPanel.addGraphic( _electromagnetGraphic.getForeground(), ELECTROMAGNET_FRONT_LAYER );
        apparatusPanel.addGraphic( _electromagnetGraphic.getBackground(), ELECTROMAGNET_BACK_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, 
                _electromagnetModel, FaradayConstants.GRID_SPACING, FaradayConstants.GRID_SPACING );
        _gridGraphic.setRescalingEnabled( true );
        _gridGraphic.setNeedleSize( FaradayConstants.GRID_NEEDLE_SIZE );
        _gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
        apparatusPanel.addChangeListener( _gridGraphic );
        apparatusPanel.addGraphic( _gridGraphic, COMPASS_GRID_LAYER );
        super.setCompassGridGraphic( _gridGraphic );
        
        // Compass
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, _fieldMeterModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, FIELD_METER_LAYER );

        // Collision detection
        _electromagnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( _electromagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            FaradayControlPanel controlPanel = new FaradayControlPanel( this );
            setControlPanel( controlPanel );
            
            // Electromagnet controls
            _electromagnetPanel = new ElectromagnetPanel( _electromagnetModel,
                    _sourceCoilModel, _batteryModel, _acPowerSupplyModel, _compassModel, _fieldMeterModel,
                    _electromagnetGraphic, _gridGraphic );
            controlPanel.addControlFullWidth( _electromagnetPanel );
            
            // Scaling calibration
            if ( FaradayConstants.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addVerticalSpace();
                
                ScalePanel scalePanel = new ScalePanel( null, null, null, _electromagnetGraphic );
                controlPanel.addControlFullWidth( scalePanel );
            }
            
            // Reset button
            controlPanel.addResetButton();
        }
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _batteryModel, _electromagnetModel );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        wiggleMe.setEnabled( false ); // per 4/27/2005 status meeting
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    //----------------------------------------------------------------------------
    // FaradayModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        // Battery model
        _batteryModel.setAmplitude( BATTERY_AMPLITUDE );
        _batteryModel.setEnabled( true );
        
        // AC Power Supply model
        _acPowerSupplyModel.setMaxAmplitude( AC_MAX_AMPLITUDE );
        _acPowerSupplyModel.setFrequency( AC_FREQUENCY );
        _acPowerSupplyModel.setEnabled( false );
        
        // Source Coil model
        _sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        _sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        _sourceCoilModel.setDirection( ELECTROMAGNET_DIRECTION );
        
        // Electromagnet model
        _electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        _electromagnetModel.setDirection( ELECTROMAGNET_DIRECTION );
        if ( _batteryModel.isEnabled() ) {
            _electromagnetModel.setVoltageSource( _batteryModel );
        }
        else {
            _electromagnetModel.setVoltageSource( _acPowerSupplyModel );
        }
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil appearance.
        _electromagnetModel.update();
        
        // Compass model
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( true );
        
        // Electromagnet view
        _electromagnetGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        
        // Compass Grid view
        _gridGraphic.setVisible( true );
        
        // Field Meter view
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Control panel
        _electromagnetPanel.update();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the electromagnet battery's voltage is changed, 
     * or the battery is disabled, or the electromagnet is moved.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private Battery _batteryModel;
        private double _batteryVoltage;
        private Electromagnet _electromagnetModel;
        private Point2D _electromagnetLocation;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param batteryModel
         * @param electromagnetModel
         */
        public ThisWiggleMeGraphic( Component component, BaseModel model, 
                Battery batteryModel, Electromagnet electromagnetModel ) {
            super( component, model );

            _batteryModel = batteryModel;
            _batteryVoltage = _batteryModel.getVoltage();
            _batteryModel.addObserver( this );
            _electromagnetModel = electromagnetModel;
            _electromagnetLocation = _electromagnetModel.getLocation();
            _electromagnetModel.addObserver( this );
            
            setText( FaradayResources.getString( "ElectromagnetModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.BOTTOM_LEFT, new Vector2D( -50, 50 ) );
            setRange( 25, 0 );
            setCycleDuration( 5 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the battery voltage changes or the battery is disabled, disable and unwire the wiggle me.
         */
        public void update() {
            if ( _batteryVoltage != _batteryModel.getVoltage() || 
                    ! _batteryModel.isEnabled() ||
                    ! _electromagnetLocation.equals( _electromagnetModel.getLocation() )  ) {
                // Disable
                setEnabled( false );
                // Unwire
                _batteryModel.removeObserver( this );
                _electromagnetModel.removeObserver( this );
            }
        }
    }
}