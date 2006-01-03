/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.common.view.ModelSlider;
//import edu.colorado.phet.common.view.components.ModelSlider;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.text.DecimalFormat;

public class OptionsMenu extends JMenu {

    public OptionsMenu( final JFrame frame ) {
        super( "Options" );
        JMenu optionsMenu = this;
        optionsMenu.setMnemonic( 'O' );
        final JCheckBoxMenuItem showBondIndicatorMI = new JCheckBoxMenuItem( "Show bond indicators" );
        optionsMenu.add( showBondIndicatorMI );
        showBondIndicatorMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                IonGraphic.showBondIndicators( showBondIndicatorMI.isSelected() );
            }
        } );

        final JCheckBoxMenuItem randomWalkMI = new JCheckBoxMenuItem( "Random walk" );
        optionsMenu.add( randomWalkMI );
        randomWalkMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsConfig.RANDOM_WALK = randomWalkMI.isSelected();
            }
        } );

        final JCheckBoxMenuItem oneCrystalMI = new JCheckBoxMenuItem( "One crystal only" );
        optionsMenu.add(  oneCrystalMI );
        oneCrystalMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SolubleSaltsConfig.ONE_CRYSTAL_ONLY = oneCrystalMI.isSelected();
            }
        } );

        final JMenuItem randomWaltkThetaMI = new JMenuItem( "Adjust random walk...");
        randomWaltkThetaMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, "Random Walk Adjusment", false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final JSlider sldr = new JSlider( 0, 360, (int)Ion.randomWalkTheta );
                sldr.setMajorTickSpacing( 45 );
                sldr.setMinorTickSpacing( 15 );
                sldr.setPaintTicks( true );
                sldr.setPaintLabels( true );
                sldr.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        Ion.randomWalkTheta = sldr.getValue();
                    }
                } );
                dlg.getContentPane().add( sldr );
                JButton btn = new JButton( "Close" );
                btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        dlg.setVisible( false );
                    }
                } );
                JPanel btnPnl = new JPanel( );
                btnPnl.add( btn );
                dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
                dlg.pack();
                dlg.setVisible( true );
            }
        } );
        optionsMenu.add( randomWaltkThetaMI );

        final JMenuItem bindingDistanceMI = new JMenuItem( "Adjust binding distance...");
        bindingDistanceMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JDialog dlg = new JDialog( frame, "Set Binding Distance", false );
                dlg.getContentPane().setLayout( new BorderLayout() );
                final ModelSlider sldr = new ModelSlider( "Binding distance",
                                                          "",
                                                          0,
                                                          4,
                                                          SolubleSaltsConfig.BINDING_DISTANCE_FACTOR,
                                                          new DecimalFormat( "0.0"));
                sldr.setMajorTickSpacing( 0.5 );
                sldr.setPaintTicks( true );
                sldr.setPaintLabels( true );
                sldr.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        SolubleSaltsConfig.BINDING_DISTANCE_FACTOR = sldr.getValue();
                    }
                } );
                dlg.getContentPane().add( sldr );
                JButton btn = new JButton( "Close" );
                btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        dlg.setVisible( false );
                    }
                } );
                JPanel btnPnl = new JPanel( );
                btnPnl.add( btn );
                dlg.getContentPane().add( btnPnl, BorderLayout.SOUTH );
                dlg.pack();
                dlg.setVisible( true );
            }
        } );
        optionsMenu.add( bindingDistanceMI );

    }
}
