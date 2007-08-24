package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 31, 2006
 * Time: 9:28:16 AM
 */

public class SingleParticleClockControlPanel extends ClockControlPanel {
    public SingleParticleClockControlPanel( final SingleParticleModule singleParticleModule, IClock clock ) {
        super( clock );
        final JCheckBox rapid = new JCheckBox( "Rapid", false );
        rapid.setBorder( BorderFactory.createLineBorder( Color.blue ) );
        rapid.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                singleParticleModule.setRapid( rapid.isSelected() );
            }
        } );
        addControl( rapid );
    }
}
