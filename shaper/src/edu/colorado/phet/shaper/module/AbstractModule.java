/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.module;

import java.awt.Color;

import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.debug.BoundsDebugger;


/**
 * AbstractModule is the base class for all Optical Pulse Shaper modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractModule extends PhetGraphicsModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final double HELP_LAYER = ApparatusPanel.LAYER_TOP;
    public static final double DEBUG_LAYER = HELP_LAYER - 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BoundsDebugger _debuggerGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title the module title
     * @param clock the simulation clock
     */
    public AbstractModule( String title, IClock clock ) {
        super( title, clock );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Turns the wait cursor on and off.
     * 
     * @param enabled true or false
     */
    public void setWaitCursorEnabled( boolean enabled ) {
        ApparatusPanel apparatusPanel = getApparatusPanel();
        if ( enabled ) {
            apparatusPanel.setCursor( ShaperConstants.WAIT_CURSOR );
        }
        else {
            apparatusPanel.setCursor( ShaperConstants.DEFAULT_CURSOR );
        }
    }
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    /**
     * Causes the graphic's location and bounds to be rendered.
     * 
     * @param graphic the graphic
     */
    protected void drawBounds( PhetGraphic graphic ) {
        ApparatusPanel apparatusPanel = getApparatusPanel();
        if ( _debuggerGraphic == null ) {
            _debuggerGraphic = new BoundsDebugger( apparatusPanel );
            _debuggerGraphic.setLocationColor( Color.GREEN );
            _debuggerGraphic.setBoundsColor( Color.RED );
        }
        _debuggerGraphic.add( graphic );
        apparatusPanel.addGraphic( _debuggerGraphic, DEBUG_LAYER );
    }
}
