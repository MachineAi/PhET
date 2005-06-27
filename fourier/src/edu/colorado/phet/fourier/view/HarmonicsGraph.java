/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.LabelTable;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.charts.HarmonicPlot;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.event.ZoomEvent;
import edu.colorado.phet.fourier.event.ZoomListener;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * HarmonicsGraph
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsGraph extends GraphicLayerSet 
    implements SimpleObserver, ZoomListener, HarmonicFocusListener, ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;
    private static final double MATH_LAYER = 5;

    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 200 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin

    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    private static final Font AXIS_TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;
    
    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );
    
    // Tick Mark parameter
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 12 );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;
    private static final Color MINOR_TICK_COLOR = MAJOR_TICK_COLOR;
    
    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    
    // X Axis parameters
    private static final double L = FourierConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

    // Y Axis parameters
    private static final double Y_RANGE_START = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAJOR_TICK_SPACING = 0.5;

    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 580, 130 );

    // Wave parameters
    private static final Stroke WAVE_NORMAL_STROKE = new BasicStroke( 1f );
    private static final Stroke WAVE_FOCUS_STROKE = new BasicStroke( 2f );
    private static final Stroke WAVE_DIMMED_STROKE = new BasicStroke( 0.5f );
    private static final Color WAVE_DIMMED_COLOR = Color.GRAY;
    private static final int NUMBER_OF_DATA_POINTS = 1000;
    private static final int MAX_FUNDAMENTAL_CYCLES = 4;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _fourierSeries;
    private Chart _chartGraphic;
    private HarmonicsEquation _mathGraphic;
    private PhetTextGraphic _xAxisTitleGraphic;
    private String _xAxisTitleTime, _xAxisTitleSpace;
    private ArrayList _harmonicPlots; // array of HarmonicPlot
    private ZoomControl _horizontalZoomControl;
    private int _xZoomLevel;
    private int _domain;
    private int _mathForm;
    private LabelTable _spaceLabels1, _spaceLabels2;
    private LabelTable _timeLabels1, _timeLabels2;
    private int _previousNumberOfHarmonics;
    private int _previousPreset;
    private int _previousWaveType;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component the parent Component
     * @param fourierSeries the Fourier series that this view displays
     */
    public HarmonicsGraph( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        backgroundGraphic.setStroke( BACKGROUND_STROKE );
        backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        backgroundGraphic.setLocation( -100, -115 );
        
        // Model
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        // Title
        String title = SimStrings.get( "HarmonicsGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );

        // Chart
        {
            _chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );

            _chartGraphic.setLocation( 0, -( CHART_SIZE.height / 2 ) );

            // X axis
            {
                _chartGraphic.getXAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getXAxis().setColor( AXIS_COLOR );
                
                // Title
                _xAxisTitleTime = "" + MathStrings.C_TIME;
                _xAxisTitleSpace = "" + MathStrings.C_SPACE;
                _xAxisTitleGraphic = new PhetTextGraphic( component, AXIS_TITLE_FONT, _xAxisTitleSpace, AXIS_TITLE_COLOR );
                _chartGraphic.setXAxisTitle( _xAxisTitleGraphic );

                // No ticks or labels on the axis
                _chartGraphic.getXAxis().setMajorTicksVisible( false );
                _chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getXAxis().setMinorTicksVisible( false );
                _chartGraphic.getXAxis().setMinorTickLabelsVisible( false );

                // Major ticks with labels below the chart
                _chartGraphic.getHorizontalTicks().setMajorTicksVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizontalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                _chartGraphic.getHorizontalTicks().setMajorTickFont( MAJOR_TICK_FONT );
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );

                // Vertical gridlines for major ticks.
                _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );
                
                // Vertical gridlines for minor ticks.
                _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMinorTickSpacing( X_MINOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
            }

            // Y axis
            {
                _chartGraphic.getYAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getYAxis().setColor( AXIS_COLOR );

                // No ticks or labels on the axis
                _chartGraphic.getYAxis().setMajorTicksVisible( false );
                _chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getYAxis().setMinorTicksVisible( false );
                _chartGraphic.getYAxis().setMinorTickLabelsVisible( false );

                // Range labels
                _chartGraphic.getVerticalTicks().setRangeLabelsVisible( RANGE_LABELS_VISIBLE );
                _chartGraphic.getVerticalTicks().setRangeLabelsNumberFormat( RANGE_LABELS_FORMAT );
                
                // Major ticks with labels to the left of the chart
                _chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                _chartGraphic.getVerticalTicks().setMajorTickFont( MAJOR_TICK_FONT );

                // Horizontal gridlines for major ticks
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );
            }
        }

        // Math
        {
            _mathGraphic = new HarmonicsEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.setLocation( CHART_SIZE.width / 2, -( CHART_SIZE.height / 2  ) - 6 ); // above center of chart
        }
        
        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( CHART_SIZE.width + 20, -50 );  // to the right of the chart
        }

        // Interactivity
        {
            titleGraphic.setIgnoreMouse( true );
            _horizontalZoomControl.addZoomListener( this );
        }

        // Misc initialization
        {
            _harmonicPlots = new ArrayList();
        }
        
        reset();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
        _horizontalZoomControl.removeAllZoomListeners();
    }

    /**
     * Resets to the initial state.
     */
    public void reset() {

        // Domain
        _domain = FourierConstants.DOMAIN_SPACE;
        
        // Chart
        {
            _xZoomLevel = 0;
            _chartGraphic.setRange( CHART_RANGE );
            updateLabelsAndLines();
            updateZoomButtons();
        }
        
        // Math Mode
        _mathForm = FourierConstants.MATH_FORM_WAVE_NUMBER;
        _mathGraphic.setVisible( false );
        updateMath();
        
        // Synchronize with model
        _previousNumberOfHarmonics = 0; // force an update
        _previousPreset = -1;
        _previousWaveType = -1;
        update();
    }
    
    //----------------------------------------------------------------------------
    // Chart Labels
    //----------------------------------------------------------------------------
    
    /*
     * Lazy initialization of the X axis "space" labels.
     */
    private LabelTable getSpaceLabels1() {
        if ( _spaceLabels1 == null ) {
            Component component = getComponent();
            _spaceLabels1 = new LabelTable();
            _spaceLabels1.put( -1.00 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( -0.75 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3L/4", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( -0.50 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/2", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( -0.25 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/4", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put(     0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( +0.25 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/4", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( +0.50 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/2", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( +0.75 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3L/4", MAJOR_TICK_COLOR ) );
            _spaceLabels1.put( +1.00 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L", MAJOR_TICK_COLOR ) );
        }
        return _spaceLabels1;
    }
    
    /*
     * Lazy initialization of the X axis "space" labels.
     */
    private LabelTable getSpaceLabels2() {
        if ( _spaceLabels2 == null ) {
            _spaceLabels2 = new LabelTable();
            Component component = getComponent();
            _spaceLabels2.put( -2.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-2L", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( -1.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3L/2", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( -1.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( -0.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/2", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put(    0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( +0.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/2", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( +1.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( +1.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3L/2", MAJOR_TICK_COLOR ) );
            _spaceLabels2.put( +2.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "2L", MAJOR_TICK_COLOR ) );
        }
        return _spaceLabels2;
    }
    
    /*
     * Lazy initialization of the X axis "time" labels.
     */
    private LabelTable getTimeLabels1() {
        if ( _timeLabels1 == null ) {
            double T = L; // use the same quantity for wavelength and period
            Component component = getComponent();
            _timeLabels1 = new LabelTable();
            _timeLabels1.put( -1.00 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-T", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( -0.75 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3T/4", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( -0.50 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-T/2", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( -0.25 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-T/4", MAJOR_TICK_COLOR ) );
            _timeLabels1.put(     0 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( +0.25 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "T/4", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( +0.50 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "T/2", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( +0.75 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3T/4", MAJOR_TICK_COLOR ) );
            _timeLabels1.put( +1.00 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "T", MAJOR_TICK_COLOR ) );
        }
        return _timeLabels1;
    }
    
    /*
     * Lazy initialization of the X axis "time" labels.
     */
    private LabelTable getTimeLabels2() {   
        if ( _timeLabels2 == null ) {
            double T = L; // use the same quantity for wavelength and period
            Component component = getComponent();
            _timeLabels2 = new LabelTable();
            _timeLabels2.put( -2.0 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-2T", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( -1.5 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3T/2", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( -1.0 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-T", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( -0.5 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-T/2", MAJOR_TICK_COLOR ) );
            _timeLabels2.put(    0 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( +0.5 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "T/2", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( +1.0 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "T", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( +1.5 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3T/2", MAJOR_TICK_COLOR ) );
            _timeLabels2.put( +2.0 * T, new PhetTextGraphic( component, MAJOR_TICK_FONT, "2T", MAJOR_TICK_COLOR ) );
        }
        return _timeLabels2;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the horizontal zoom control.
     * 
     * @return the horizontal zoom control
     */
    public ZoomControl getHorizontalZoomControl() {
        return _horizontalZoomControl;
    }
    
    /**
     * Enables things that are related to "math mode".
     * 
     * @param enabled true or false
     */
    public void setMathEnabled( boolean enabled ) {
        _mathGraphic.setVisible( enabled );
    }
    
    /**
     * Gets the chart associated with this graphic.
     * 
     * @return the chart
     */
    public Chart getChart() {
        return _chartGraphic;
    }
    
    /**
     * Sets the wave type for each data sets.
     * 
     * @param waveType
     */
    public void setWaveType( int waveType ) {
        assert( FourierConstants.isValidWaveType( waveType ) );
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            ((HarmonicPlot) _harmonicPlots.get( i )).setWaveType( waveType );
        }
    }
    
    /**
     * Sets the domain and math form.
     * Together, these values determines how the chart is 
     * labeled, and the format of the equation shown above the chart.
     * 
     * @param domain
     * @param mathForm
     */
    public void setDomainAndMathForm( int domain, int mathForm ) {
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        _domain = domain;
        _mathForm = mathForm;
        updateLabelsAndLines();
        updateMath();
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
            harmonicPlot.setStartX( 0 );
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();
        
        if ( numberOfHarmonics != _previousNumberOfHarmonics ) {
            
            //FourierLog.trace( "HarmonicsGraph.update" );

            // Clear the chart.
            _chartGraphic.removeAllDataSetGraphics();

            // Re-populate the chart.
            for ( int i = 0; i < numberOfHarmonics; i++ ) {

                HarmonicPlot dataSetGraphic = null;
                if ( i < _harmonicPlots.size() ) {
                    dataSetGraphic = (HarmonicPlot) _harmonicPlots.get( i );
                }
                else {
                    // Allocate new data sets graphic.
                    dataSetGraphic = new HarmonicPlot( getComponent(), _chartGraphic );
                    _harmonicPlots.add( dataSetGraphic );
                }
                
                dataSetGraphic.setHarmonic( _fourierSeries.getHarmonic( i ) );
                dataSetGraphic.setPeriod( L / ( i + 1 ) );
                dataSetGraphic.setStroke( WAVE_NORMAL_STROKE );
                dataSetGraphic.setBorderColor( HarmonicColors.getInstance().getColor( i ) );

                _chartGraphic.addDataSetGraphic( dataSetGraphic );
            }
            
            _previousNumberOfHarmonics = numberOfHarmonics;
        }
        
        int preset = _fourierSeries.getPreset();
        int waveType = _fourierSeries.getWaveType();
        if ( _previousPreset != preset || _previousWaveType != waveType ) {
            for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
                HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                harmonicPlot.setStartX( 0 );
            }
        }
    }

    //----------------------------------------------------------------------------
    // ZoomListener implementation
    //----------------------------------------------------------------------------

    /**
     * Invokes when a zoom of the chart has been performed.
     * 
     * @param event
     */
    public void zoomPerformed( ZoomEvent event ) {
        int zoomType = event.getZoomType();
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN || zoomType == ZoomEvent.HORIZONTAL_ZOOM_OUT ) {
            handleHorizontalZoom( zoomType );
        }
        else {
            throw new IllegalArgumentException( "unexpected event: " + event );
        }
    }
    
    //----------------------------------------------------------------------------
    // HarmonicFocusListener implementation
    //----------------------------------------------------------------------------

    /**
     * When a harmonic gains focus, grays out all harmonics except for the one with focus.
     */
    public void focusGained( HarmonicFocusEvent event ) {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicGraphic = (HarmonicPlot) _harmonicPlots.get( i );
            if ( harmonicGraphic.getHarmonic() != event.getHarmonic() ) {
                harmonicGraphic.setBorderColor( WAVE_DIMMED_COLOR );
                harmonicGraphic.setStroke( WAVE_DIMMED_STROKE );
            }
            else {
                Color harmonicColor = HarmonicColors.getInstance().getColor( harmonicGraphic.getHarmonic() );
                harmonicGraphic.setBorderColor( harmonicColor );
                harmonicGraphic.setStroke( WAVE_FOCUS_STROKE );
            }
        }
    }
    
    /**
     * When a harmonic loses focus, sets all harmonics to their assigned color.
     */
    public void focusLost( HarmonicFocusEvent event ) {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicGraphic = (HarmonicPlot) _harmonicPlots.get( i );
            Color harmonicColor = HarmonicColors.getInstance().getColor( harmonicGraphic.getHarmonic() );
            harmonicGraphic.setBorderColor( harmonicColor );
            harmonicGraphic.setStroke( WAVE_NORMAL_STROKE );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    /*
     * Handles horizontal zooming.
     * 
     * @param zoomType indicates the type of zoom
     */
    private void handleHorizontalZoom( int zoomType ) {

        // Adjust the zoom level.
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN ) {
            _xZoomLevel++;
        }
        else {
            _xZoomLevel--;
        }
        
        // Obtuse sqrt(2) zoom factor, immune to numeric precision errors 
        double zoomFactor = Math.pow( 2, Math.abs( _xZoomLevel ) / 2.0 );
        
        // Adjust the chart's horizontal range.
        Range2D range = _chartGraphic.getRange();
        double xRange;
        if ( _xZoomLevel == 0 ) {
            xRange = ( L / 2 );
        }
        else if ( _xZoomLevel > 0 ) {
            xRange = ( L / 2 ) / zoomFactor; 
        }
        else {
            xRange = ( L / 2 ) * zoomFactor;
        }
        range.setMaxX( xRange );
        range.setMinX( -xRange );
        _chartGraphic.setRange( range );

        updateLabelsAndLines();
        updateZoomButtons();
    }
    
    /*
     * Adjusts labels, ticks and gridlines to match the chart range.
     */
    private void updateLabelsAndLines() {
        
        // X axis
        if ( _domain == FourierConstants.DOMAIN_TIME ) {
            _xAxisTitleGraphic.setText( _xAxisTitleTime );
            if ( _xZoomLevel > -3 ) {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getTimeLabels1() );
            }
            else {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getTimeLabels2() );
            }   
        }
        else { /* DOMAIN_SPACE or DOMAIN_SPACE_AND_TIME */
            _xAxisTitleGraphic.setText( _xAxisTitleSpace );
            if ( _xZoomLevel > -3 ) {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getSpaceLabels1() );
            }
            else {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getSpaceLabels2() );
            }
        }
    }
    
    /*
     * Enables and disables zoom buttons based on the current
     * zoom levels and range of the chart.
     */
    private void updateZoomButtons() {
        
        Range2D range = _chartGraphic.getRange();
        
        // Horizontal buttons
        if ( range.getMaxX() >= X_RANGE_MAX ) {
            _horizontalZoomControl.setZoomOutEnabled( false );
            _horizontalZoomControl.setZoomInEnabled( true );
        }
        else if ( range.getMaxX() <= X_RANGE_MIN ) {
            _horizontalZoomControl.setZoomOutEnabled( true );
            _horizontalZoomControl.setZoomInEnabled( false );
        }
        else {
            _horizontalZoomControl.setZoomOutEnabled( true );
            _horizontalZoomControl.setZoomInEnabled( true );
        }
    }
    
    private void updateMath() {
        _mathGraphic.setForm( _domain, _mathForm );
        _mathGraphic.setRegistrationPoint( _mathGraphic.getWidth() / 2, _mathGraphic.getHeight() ); // bottom center
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves the waveforms in space by shifting their start location.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        if ( isVisible() && _domain == FourierConstants.DOMAIN_SPACE_AND_TIME && FourierConfig.ANIMATION_ENABLED ) {
            for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
                HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                double startX = harmonicPlot.getStartX();
                harmonicPlot.setStartX( startX + ( dt * L / FourierConfig.ANIMATION_STEPS_PER_CYCLE ) );
            }
        }
    }
}
