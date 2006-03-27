/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.view.FaucetGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 4:48:25 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class FaucetOnOffControlPanel extends HorizontalLayoutPanel {
    private FaucetGraphic faucetGraphic;

    public FaucetOnOffControlPanel( final FaucetGraphic faucetGraphic ) {
        this.faucetGraphic = faucetGraphic;
        ButtonGroup buttonGroup = new ButtonGroup();
        final JCheckBox on = new JCheckBox( "On", faucetGraphic.isEnabled() );
        on.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                faucetGraphic.setEnabled( on.isSelected() );
            }
        } );
        buttonGroup.add( on );
        add( on );

        final JCheckBox off = new JCheckBox( "Off", !faucetGraphic.isEnabled() );
        off.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                faucetGraphic.setEnabled( !off.isSelected() );
            }
        } );
        buttonGroup.add( off );
        add( off );

        setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.blue, 2 ), "Faucet" ) );
    }
}
