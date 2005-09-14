/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.sliders.WavePacketKWidthSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketXWidthSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketCenterSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketSpacingSlider;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.view.d2c.D2CAmplitudesView;
import edu.colorado.phet.fourier.view.d2c.D2CHarmonicsView;
import edu.colorado.phet.fourier.view.d2c.D2CSumView;
import edu.colorado.phet.fourier.view.tools.WavePacketKWidthTool;
import edu.colorado.phet.fourier.view.tools.WavePacketXWidthTool;
import edu.colorado.phet.fourier.view.tools.WavePacketPeriodTool;
import edu.colorado.phet.fourier.view.tools.WavePacketSpacingTool;



/**
 * D2CControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TITLED_BORDER_WIDTH = 1;
    
    private static final int SPACE_BETWEEN_SUBPANELS = 10;
    
    private static final int MIN_SPACING = 0;
    private static final int MAX_SPACING = 100;
    private static final int MIN_X_WIDTH = 0;
    private static final int MAX_X_WIDTH = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesView _amplitudesGraph;
    private D2CHarmonicsView _harmonicsGraph;
    private D2CSumView _sumGraph;
    private WavePacketSpacingTool _spacingTool;
    private WavePacketKWidthTool _kWidthTool;
    private WavePacketXWidthTool _xWidthTool;
    private WavePacketPeriodTool _periodTool;

    // UI components
    private FourierComboBox _domainComboBox;
    private WavePacketSpacingSlider _spacingSlider;
    private WavePacketCenterSlider _centerSlider;
    private JCheckBox _amplitudesEnvelopeCheckBox;
    private JCheckBox _sumEnvelopeCheckBox;
    private WavePacketKWidthSlider _kWidthSlider;
    private WavePacketXWidthSlider _xWidthSlider;
    private FourierComboBox _waveTypeComboBox;
    private JCheckBox _showWidthsCheckBox;
    
    // Choices
    private ArrayList _domainChoices;
    private ArrayList _waveTypeChoices;
    
    private EventListener _listener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     * @param wavePacket
     * @param amplitudesGraph
     * @param harmonicsGraph
     */
    public D2CControlPanel( FourierModule module, 
            GaussianWavePacket wavePacket, 
            D2CAmplitudesView amplitudesGraph,
            D2CHarmonicsView harmonicsGraph,
            D2CSumView sumGraph,
            WavePacketSpacingTool spacingTool,
            WavePacketKWidthTool kWidthTool,
            WavePacketXWidthTool xWidthTool,
            WavePacketPeriodTool periodTool ) {
        
        super( module );
        
        assert( wavePacket != null );
        assert( amplitudesGraph != null );
        assert( harmonicsGraph != null );
        assert( sumGraph != null );
        assert( spacingTool != null );
        assert( kWidthTool != null );
        assert( xWidthTool != null );
        assert( periodTool != null );
        
        _wavePacket = wavePacket;
        _amplitudesGraph = amplitudesGraph;
        _harmonicsGraph = harmonicsGraph;
        _sumGraph = sumGraph;
        _spacingTool = spacingTool;
        _kWidthTool = kWidthTool;
        _xWidthTool = xWidthTool;
        _periodTool = periodTool;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "D2CControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );

        JPanel graphControlsPanel = new JPanel();
        {
            String title = SimStrings.get( "D2CControlPanel.graphControls" );
            TitledBorder titledBorder = new TitledBorder( title );
            Font font = titledBorder.getTitleFont();
            titledBorder.setTitleFont( new Font( font.getName(), Font.BOLD, font.getSize() ) );
            graphControlsPanel.setBorder( titledBorder );
            
            // Domain
            {
                // Label
                String label = SimStrings.get( "D2CControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
 
                // Function combo box
                _domainComboBox = new FourierComboBox( label, _domainChoices );
            }

            // Wave Type
            {
                // Label
                String label = SimStrings.get( "D2CControlPanel.waveType" );

                // Choices
                _waveTypeChoices = new ArrayList();
                _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_SINE, SimStrings.get( "waveType.sines" ) ) );
                _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_COSINE, SimStrings.get( "waveType.cosines" ) ) );

                // Wave Type combo box
                _waveTypeComboBox = new FourierComboBox( label, _waveTypeChoices );
            }
            
            // Envelope checkboxes
            _amplitudesEnvelopeCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.kEnvelope" ) );
            _sumEnvelopeCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.xEnvelope" ) );
            
            // Show widths checkbox
            _showWidthsCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.showWidths" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( graphControlsPanel );
            graphControlsPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _domainComboBox, row++, 0 );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
            layout.addComponent( _amplitudesEnvelopeCheckBox, row++, 0 );
            layout.addComponent( _sumEnvelopeCheckBox, row++, 0 );
            layout.addComponent( _showWidthsCheckBox, row++, 0 );
        }
       
        // Packet width panel
        JPanel wavePacketControlsPanel = new JPanel();
        {
            String title = SimStrings.get( "D2CControlPanel.gaussianWavePacket" );
            TitledBorder titledBorder = new TitledBorder( title );
            Font font = titledBorder.getTitleFont();
            titledBorder.setTitleFont( new Font( font.getName(), Font.BOLD, font.getSize() ) );
            wavePacketControlsPanel.setBorder( titledBorder );
            
            // spacing (k1)
            _spacingSlider = new WavePacketSpacingSlider();
            
            // center point (k0)
            _centerSlider = new WavePacketCenterSlider();
            
            // k-space width
            _kWidthSlider = new WavePacketKWidthSlider();

            // x-space width
            _xWidthSlider = new WavePacketXWidthSlider();
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( wavePacketControlsPanel );
            wavePacketControlsPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _spacingSlider, row++, 0 );
            layout.addComponent( _centerSlider, row++, 0 );
            layout.addComponent( _kWidthSlider, row++, 0 );
            layout.addComponent( _xWidthSlider, row++, 0 );
        }

        // Layout
        addFullWidth( wavePacketControlsPanel );
        addVerticalSpace( SPACE_BETWEEN_SUBPANELS );
        addFullWidth( graphControlsPanel );
        
        // Set the state of the controls.
        reset();
        
        // Wire up event handling (after setting state with reset).
        {
            _listener = new EventListener();
            _domainComboBox.addItemListener( _listener );
            _spacingSlider.addChangeListener( _listener );
            _amplitudesEnvelopeCheckBox.addActionListener( _listener );
            _sumEnvelopeCheckBox.addActionListener( _listener );
            _showWidthsCheckBox.addActionListener( _listener );
            _centerSlider.addChangeListener( _listener );
            _kWidthSlider.addChangeListener( _listener );
            _xWidthSlider.addChangeListener( _listener );
            _waveTypeComboBox.addItemListener( _listener );
        }
    }
    
    public void reset() {
        
        _amplitudesEnvelopeCheckBox.setSelected( _amplitudesGraph.isEnvelopeEnabled() );
        _sumEnvelopeCheckBox.setSelected( _sumGraph.isEnvelopeEnabled() );
        _showWidthsCheckBox.setSelected( true );
        
        _domainComboBox.setSelectedKey( FourierConstants.DOMAIN_SPACE );
        handleDomain();
        
        _spacingSlider.setValue( _wavePacket.getK1() );
        _centerSlider.setValue( _wavePacket.getK0() );
        _kWidthSlider.setValue( _wavePacket.getDeltaK() );
        _xWidthSlider.setValue( _wavePacket.getDeltaX() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _amplitudesEnvelopeCheckBox ) {
                handleAmplitudeEnvelope();
            }
            else if ( event.getSource() == _sumEnvelopeCheckBox ) {
                handleSumEnvelope();
            }
            else if ( event.getSource() == _showWidthsCheckBox ) {
                handleShowWidths();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {

            if ( event.getSource() == _spacingSlider ) {
                handleSpacing();
            }
            else if ( event.getSource() == _centerSlider ) {
                handleCenter();
            }
            else if ( event.getSource() == _kWidthSlider ) {
                handleKWidth();
            }
            else if ( event.getSource() == _xWidthSlider ) {
                handleXWidth();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _domainComboBox.getComboBox() ) {
                    handleDomain();
                }
                else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                    handleWaveType();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        } 
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Handles changes to the domain combo box.
     */
    private void handleDomain() {
        int domain = _domainComboBox.getSelectedKey();
        
        _amplitudesGraph.setDomain( domain );
        _harmonicsGraph.setDomain( domain );
        _sumGraph.setDomain( domain );
        _spacingTool.setDomain( domain );
        _kWidthTool.setDomain( domain );
        _xWidthTool.setDomain( domain );
        _periodTool.setDomain( domain );
        
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _spacingSlider.setFormat( SimStrings.get( "WavePacketSpacingSlider.format.space" ) );
            _centerSlider.setFormat( SimStrings.get( "WavePacketCenterSlider.format.space" ) );
            _kWidthSlider.setFormat( SimStrings.get( "WavePacketKWidthSlider.format.space" ) );
            _xWidthSlider.setFormat( SimStrings.get( "WavePacketXWidthSlider.format.space" ) );
            _amplitudesEnvelopeCheckBox.setText( SimStrings.get( "D2CControlPanel.kEnvelope" ) );
            _sumEnvelopeCheckBox.setText( SimStrings.get( "D2CControlPanel.xEnvelope" ) );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _spacingSlider.setFormat( SimStrings.get( "WavePacketSpacingSlider.format.time" ) );
            _centerSlider.setFormat( SimStrings.get( "WavePacketCenterSlider.format.time" ) );
            _kWidthSlider.setFormat( SimStrings.get( "WavePacketKWidthSlider.format.time" ) );
            _xWidthSlider.setFormat( SimStrings.get( "WavePacketXWidthSlider.format.time" ) );
            _amplitudesEnvelopeCheckBox.setText( SimStrings.get( "D2CControlPanel.wEnvelope" ) );
            _sumEnvelopeCheckBox.setText( SimStrings.get( "D2CControlPanel.tEnvelope" ) );
        }
    }
    
    /*
     * Handles changes to the wave type combo box.
     */
    private void handleWaveType() {
        setWaitCursorEnabled( true );
        int waveType = _waveTypeComboBox.getSelectedKey();
        _harmonicsGraph.setWaveType( waveType );
        _sumGraph.setWaveType( waveType );
        setWaitCursorEnabled( false );
    }
    
    /*
     * Handles changes to the k-space envelope check box.
     */
    private void handleAmplitudeEnvelope() {
        setWaitCursorEnabled( true );
        _amplitudesGraph.setEnvelopeEnabled( _amplitudesEnvelopeCheckBox.isSelected() );
        setWaitCursorEnabled( false );
    }
    
    /*
     * Handles changes to the x-space envelope check box.
     */
    private void handleSumEnvelope() {
        setWaitCursorEnabled( true );
        _sumGraph.setEnvelopeEnabled( _sumEnvelopeCheckBox.isSelected() );
        setWaitCursorEnabled( false );
    }
    
    /*
     * Handles changes to the spacing (k1) slider.
     */
    private void handleSpacing() {
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_spacingSlider.isAdjusting() ) {
            setWaitCursorEnabled( true );
            double k1 = _spacingSlider.getValue();
            _wavePacket.setK1( k1 );
            setWaitCursorEnabled( false );
        }
    }
    
    /*
     * Handles changes to the center-point (k0) slider.
     */
    private void handleCenter() {
        // Update the wave packet if the user is done dragging the slider.
        if ( !_centerSlider.isAdjusting() ) {
            setWaitCursorEnabled( true );
            double k0 = _centerSlider.getValue();
            _wavePacket.setK0( k0 );
            setWaitCursorEnabled( false );
        }
    }
    
    /*
     * Handles changes to the k-space width slider.
     */
    private void handleKWidth() {
        
        setWaitCursorEnabled( true );
        
        double deltaK = _kWidthSlider.getValue();
        
        // Update the slider.
        _xWidthSlider.removeChangeListener( _listener );
        _xWidthSlider.setValue( 1 / deltaK );
        _xWidthSlider.addChangeListener( _listener );
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_kWidthSlider.isAdjusting() ) {
            _wavePacket.setDeltaK( deltaK );
        }
        
        setWaitCursorEnabled( false );
    }
   
    /*
     * Handles changes to the x-space width slider.
     */
    private void handleXWidth() {
        
        setWaitCursorEnabled( true );
        
        double deltaX = _xWidthSlider.getValue();
        
        // Update the slider.
        _kWidthSlider.removeChangeListener( _listener );
        _kWidthSlider.setValue( 1 / deltaX );
        _kWidthSlider.addChangeListener( _listener );
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_xWidthSlider.isAdjusting() ) {
            _wavePacket.setDeltaX( deltaX );
        }
        
        setWaitCursorEnabled( false );
    }
    
    /*
     * Handles changes to the "Show widths" checkbox.
     */
    private void handleShowWidths() {
        //XXX
    }
}