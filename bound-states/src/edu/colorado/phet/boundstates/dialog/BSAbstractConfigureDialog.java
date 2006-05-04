/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSAbstractConfigureDialog is the base class for all dialogs that 
 * are used to configure potential energy types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractConfigureDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    protected static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    protected static final boolean NOTIFY_WHILE_DRAGGING = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractPotential _potential;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param frame
     * @param title
     * @param potential
     */
    public BSAbstractConfigureDialog( Frame frame, String title, BSAbstractPotential potential ) {
        super( frame, title );
        setModal( false );
        setResizable( false );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent event ) {
                dispose();
            } 
        } );
        _potential = potential;
        _potential.addObserver( this );
    }
    
    /*
     * Gets the potential that this dialog will configure.
     * For use by subclasses.
     */
    protected BSAbstractPotential getPotential() {
        return _potential;
    }
    
    //----------------------------------------------------------------------------
    // UI initializers
    //----------------------------------------------------------------------------
    
    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    protected void createUI( JPanel inputPanel ) {
        
        JPanel actionsPanel = createActionsPanel();

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }
    
    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    protected JPanel createActionsPanel() {

        JButton closeButton = new JButton( SimStrings.get( "button.close" ) );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 1 ) );
        buttonPanel.add( closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _potential ) {
            updateControls();
        }
    }
    
    /*
     * Updates the controls to match the model.
     */
    protected abstract void updateControls();
    
    //----------------------------------------------------------------------------
    // JDialog overrides
    //----------------------------------------------------------------------------
    
    public void dispose() {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
            _potential = null;
        }
        super.dispose();
    }
}
