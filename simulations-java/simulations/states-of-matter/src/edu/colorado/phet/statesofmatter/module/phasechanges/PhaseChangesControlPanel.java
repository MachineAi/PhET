/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.control.GravityControlPanel;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;


public class PhaseChangesControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final Font BUTTON_LABEL_FONT = new PhetFont(14);
    private static final int INTERACTION_POTENTIAL_DIAGRAM_WIDTH = 200;
    private static final int INTERACTION_POTENTIAL_DIAGRAM_HEIGHT = (int)(INTERACTION_POTENTIAL_DIAGRAM_WIDTH * 0.8);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private MultipleParticleModel m_model;
    private JPanel m_phaseDiagramPanel;
    private boolean m_phaseDiagramVisible;
    private JButton m_phaseDiagramCtrlButton;
    private JPanel m_interactionDiagramPanel;
    private boolean m_interactionDiagramVisible;
    private JButton m_interactionDiagramCtrlButton;
    private InteractionPotentialDiagramNode m_interactionPotentialDiagram;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public PhaseChangesControlPanel( PhaseChangesModule phaseChangesModule, Frame parentFrame ) {
        
        super();
        m_model = phaseChangesModule.getMultiParticleModel();
        m_phaseDiagramVisible = false;
        m_interactionDiagramVisible = false;
        
        // Register with the model for events that affect the diagrams on this panel.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void moleculeTypeChanged(){
                m_interactionPotentialDiagram.setLjPotentialParameters( m_model.getSigma(), m_model.getEpsilon() );
            }
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = StatesOfMatterResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the panel that allows the user to select molecule type.
        addControlFullWidth( new MoleculeSelectionPanel() );
        
        // Add the panel that allows the user to control the amount of gravity.
        addControlFullWidth( new GravityControlPanel( m_model ) );
        
        addVerticalSpace( 10 );
        
        // Add the button that allows the user to turn the phase diagram on/off.
        m_phaseDiagramCtrlButton = new JButton();
        m_phaseDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        addControlFullWidth( m_phaseDiagramCtrlButton );
        m_phaseDiagramCtrlButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                m_phaseDiagramVisible = !m_phaseDiagramVisible;
                m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
                updatePhaseDiagramButtonLabel();
            }
        });
        updatePhaseDiagramButtonLabel();
        
        // Add the phase diagram.
        m_phaseDiagramPanel = new JPanel();
        m_phaseDiagramPanel.add( new PhaseDiagram( m_model ) );
        addControlFullWidth( m_phaseDiagramPanel );
        m_phaseDiagramPanel.setVisible( m_phaseDiagramVisible );
        
        // Add the button that allows the user to turn the interaction diagram on/off.
        m_interactionDiagramCtrlButton = new JButton();
        m_interactionDiagramCtrlButton.setFont( BUTTON_LABEL_FONT );
        addControlFullWidth( m_interactionDiagramCtrlButton );
        m_interactionDiagramCtrlButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                m_interactionDiagramVisible = !m_interactionDiagramVisible;
                m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible );
                updateInteractionDiagramButtonLabel();
            }
        });
        updateInteractionDiagramButtonLabel();
        
        // Add the interaction potential diagram.
        m_interactionDiagramPanel = new JPanel();
        PhetPCanvas interactionDiagramCanvas = new PhetPCanvas();
        interactionDiagramCanvas.setPreferredSize( new Dimension(INTERACTION_POTENTIAL_DIAGRAM_WIDTH, 
                INTERACTION_POTENTIAL_DIAGRAM_HEIGHT) );
        interactionDiagramCanvas.setBackground( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        interactionDiagramCanvas.setBorder( null );
        m_interactionPotentialDiagram = new InteractionPotentialDiagramNode( m_model.getSigma(), m_model.getEpsilon(),
                false );
        interactionDiagramCanvas.addWorldChild( m_interactionPotentialDiagram );
        m_interactionDiagramPanel.add( interactionDiagramCanvas );
        addControlFullWidth( m_interactionDiagramPanel );
        m_interactionDiagramPanel.setVisible( m_interactionDiagramVisible );
        
        // Add the Reset All button.
        addSeparator();
        addVerticalSpace( 5 );
        addResetAllButton( phaseChangesModule );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes
    //----------------------------------------------------------------------------
    private class MoleculeSelectionPanel extends JPanel {
        
        private JRadioButton m_neonRadioButton;
        private JRadioButton m_argonRadioButton;
        private JRadioButton m_oxygenRadioButton;
        private JRadioButton m_waterRadioButton;
        
        MoleculeSelectionPanel(){
            
            setLayout( new GridLayout(0, 1) );
            
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                    StatesOfMatterStrings.MOLECULE_TYPE_SELECT_LABEL,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new PhetFont( Font.BOLD, 14 ),
                    Color.GRAY );
            
            setBorder( titledBorder );

            m_oxygenRadioButton = new JRadioButton( StatesOfMatterStrings.OXYGEN_SELECTION_LABEL );
            m_oxygenRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_oxygenRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.DIATOMIC_OXYGEN );
                }
            } );
            m_neonRadioButton = new JRadioButton( StatesOfMatterStrings.NEON_SELECTION_LABEL );
            m_neonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_neonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.NEON );
                }
            } );
            m_argonRadioButton = new JRadioButton( StatesOfMatterStrings.ARGON_SELECTION_LABEL );
            m_argonRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_argonRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.ARGON );
                }
            } );
            m_waterRadioButton = new JRadioButton( StatesOfMatterStrings.WATER_SELECTION_LABEL );
            m_waterRadioButton.setFont( new PhetFont( Font.PLAIN, 14 ) );
            m_waterRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m_model.setMoleculeType( StatesOfMatterConstants.WATER );
                }
            } );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( m_neonRadioButton );
            buttonGroup.add( m_argonRadioButton );
            buttonGroup.add( m_oxygenRadioButton );
            buttonGroup.add( m_waterRadioButton );
            m_neonRadioButton.setSelected( true );
            
            add( m_neonRadioButton );
            add( m_argonRadioButton );
            add( m_oxygenRadioButton );
            add( m_waterRadioButton );
        }
    }
    
    private void updatePhaseDiagramButtonLabel(){
        if (m_phaseDiagramVisible){
            m_phaseDiagramCtrlButton.setText( StatesOfMatterStrings.PHASE_DIAGRAM_BUTTON_LABEL + " <<" );
        }
        else{
            m_phaseDiagramCtrlButton.setText( StatesOfMatterStrings.PHASE_DIAGRAM_BUTTON_LABEL + " >>" );
        }
    }
    
    private void updateInteractionDiagramButtonLabel(){
        if (m_interactionDiagramVisible){
            m_interactionDiagramCtrlButton.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_BUTTON_LABEL + " <<" );
        }
        else{
            m_interactionDiagramCtrlButton.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_BUTTON_LABEL + " >>" );
        }
    }
}
