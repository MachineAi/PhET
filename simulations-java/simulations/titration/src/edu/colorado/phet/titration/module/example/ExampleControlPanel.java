/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.example;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.titration.TitrationResources;
import edu.colorado.phet.titration.control.ExampleSubPanel;

/**
 * ExampleControlPanel is the control panel for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleSubPanel _exampleSubPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public ExampleControlPanel( ExampleModule module, Frame parentFrame, ExampleModel model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = TitrationResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _exampleSubPanel = new ExampleSubPanel( model.getExampleModelElement() );
        
        // Layout
        {
            addControlFullWidth( _exampleSubPanel );
            addSeparator();
            addResetAllButton( module );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
    public ExampleSubPanel getExampleSubPanel() {
        return _exampleSubPanel;
    }

}
