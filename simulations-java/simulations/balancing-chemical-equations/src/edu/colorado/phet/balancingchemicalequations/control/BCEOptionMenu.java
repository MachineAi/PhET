// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;

/**
 * Options menu.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEOptionMenu extends OptionsMenu {

    public BCEOptionMenu( final Property<Boolean> moleculesVisibleProperty ) {

        // Show molecules (check box)
        final JCheckBoxMenuItem showMoleculesMenuItem = new JCheckBoxMenuItem( BCEStrings.SHOW_MOLECULES, moleculesVisibleProperty.getValue() );
        add( showMoleculesMenuItem );
        showMoleculesMenuItem.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                moleculesVisibleProperty.setValue( showMoleculesMenuItem.isSelected() );
            }
        } );
    }
}
