/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.control.ElectronProductionControl;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.view.CollisionEnergyIndicator;
import edu.colorado.phet.dischargelamps.view.DischargeLampEnergyMonitorPanel2;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.Electron;
import edu.colorado.phet.quantum.model.ElectronSource;
import edu.colorado.phet.quantum.model.Tube;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

/**
 * SingleAtomModule
 * <p/>
 * Provides a lamp with a single atom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SingleAtomModule extends DischargeLampModule {
    private DischargeLampAtom atom;
    private double maxCurrent = 3;
    private CollisionEnergyIndicator collisionEnergyIndicatorGraphic;
    private JPanel logoPanel;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    /**
     * Constructor
     *
     * @param clock
     */
    protected SingleAtomModule( String name, IClock clock ) {
        super( name, clock );
        addAtom( getTube() );

        // Set model parameters
        getDischargeLampModel().setElectronProductionMode( ElectronSource.SINGLE_SHOT_MODE );
        getDischargeLampModel().setMaxCurrent( maxCurrent / getCurrentDisplayFactor() );

        // Make the area from which the cathode emits electrons very small, so they will always
        // come out of the middle of the plate
        getDischargeLampModel().getLeftHandPlate().setEmittingLength( 1 );
        getDischargeLampModel().getRightHandPlate().setEmittingLength( 1 );

        // Add module-specific controls
        addControls();

        setSquigglesEnabled( true );
    }


    /**
     * todo: clean this up
     */
    private void addControls() {
        final DischargeLampEnergyMonitorPanel2 elmp = super.getEneregyLevelsMonitorPanel();

        // Add the indicator for what energy an electron will have when it hits the atom. At first, it
        // isn't enabled. We add a listener that will enable the collision energy indicator when the first
        // collision occurs
        collisionEnergyIndicatorGraphic = new CollisionEnergyIndicator( elmp.getElmp(), this );
        elmp.getElmp().addGraphic( collisionEnergyIndicatorGraphic, -1 );
        collisionEnergyIndicatorGraphic.setEnabled( false );
        atom.addElectronCollisionListener( new DischargeLampAtom.ElectronCollisionListener() {
            public void collisionOccurred( DischargeLampAtom.ElectronCollisionEvent event ) {
                collisionEnergyIndicatorGraphic.setEnabled( true );
                event.getAtom().removeElectronCollisionListener( this );
            }
        } );

        // Add text that labels the ground state
        PhetTextGraphic2 groundStateTextGraphic = new PhetTextGraphic2( elmp.getElmp(),
                                                                        DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                                        SimStrings.get("Misc.groundState" ),
                                                                        Color.black,
                                                                        110,
                                                                        270 );
        elmp.getElmp().addGroundStateLabel( groundStateTextGraphic, -1);

        // Put the current slider in a set of controls with the Fire button
        getElectronProductionControl().setMaxCurrent( maxCurrent );
        getElectronProductionControl().setHeaterValue( 25 );
//        final CurrentSlider currentSlider = getHeaterControlSlider();
//        currentSlider.setMaxCurrent( maxCurrent );
//        getHeaterControlSlider().setValue( 25 );

        // Add a button for firing a single electron. This also tells the energy level panel that if an
        // electron has been produced
        {
            final JButton singleShotBtn = new JButton( SimStrings.get( "Controls.FireElectron" ) );
            singleShotBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    Electron electron = null;
                    if( getDischargeLampModel().getVoltage() > 0 ) {
                        electron = getDischargeLampModel().getLeftHandPlate().produceElectron();
                    }
                    else if( getDischargeLampModel().getVoltage() < 0 ) {
                        electron = getDischargeLampModel().getRightHandPlate().produceElectron();
                    }
                    if( electron != null ) {
                        elmp.addElectron( electron );
                    }
                }
            } );
            JRadioButton continuousRB = new JRadioButton( new AbstractAction( SimStrings.get( "Controls.Continuous" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    getElectronProductionControl().setProductionType( ElectronProductionControl.CONTINUOUS);
//                    setContinuousElectronProduction( getElectronProductionControl(), singleShotBtn );
                }
            } );
            JRadioButton singleShotRB = new JRadioButton( new AbstractAction( SimStrings.get( "Controls.Single" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    getElectronProductionControl().setProductionType( ElectronProductionControl.SINGLE_SHOT );
//                    setSingleShotElectronProduction( getElectronProductionControl(), singleShotBtn );
                }
            } );
            ButtonGroup electronProductionBtnGrp = new ButtonGroup();
            electronProductionBtnGrp.add( continuousRB );
            electronProductionBtnGrp.add( singleShotRB );
            singleShotRB.setSelected( true );
            setSingleShotElectronProduction( getElectronProductionControl(), singleShotBtn );

            {
                JPanel electronProductionControlPanel = getElectronProductionControl();
                electronProductionControlPanel.setLayout( new GridBagLayout() );
                GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( -5, 0, -1, 0 ), 0, 0 );
                JPanel rbPanel = new JPanel();
                rbPanel.add( singleShotRB );
                rbPanel.add( continuousRB );
                electronProductionControlPanel.add( rbPanel, gbc );
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.gridy = 1;
                gbc.gridx = 0;
                gbc.insets = new Insets( 0, 0, -1, 0 );
                electronProductionControlPanel.add( singleShotBtn, gbc );
//                electronProductionControlPanel.add( currentSlider, gbc );

                singleShotRB.setSelected( true );
//                currentSlider.setVisible( false );

                getElectronProductionControl().setProductionType( ElectronProductionControl.SINGLE_SHOT );
                getControlPanel().add( elmp );
            }
        }

        // Set the size of the panel
        elmp.getElmp().setPreferredSize( new Dimension( 200, 300 ) );
    }

    /**
     * Sets the simulation to fire single electrons when the "Fire" button is pressed
     *
     * @param singleShotBtn
     */
    private void setSingleShotElectronProduction( ElectronProductionControl electronProductionControl, JButton singleShotBtn ) {

        if( getLogoPanel() == null ) {
            setLogoPanel( logoPanel );
            PhetUtilities.getPhetFrame().repaint();
        }

        electronProductionControl.setProductionType( ElectronProductionControl.SINGLE_SHOT );
//        currentSlider.setVisible( false );
//        getDischargeLampModel().getLeftHandPlate().setCurrent( 0 );
//        singleShotBtn.setVisible( true );
//        getDischargeLampModel().setElectronProductionMode( ElectronSource.SINGLE_SHOT_MODE );
//        super.setHeatingElementsVisible( false );
    }

    /**
     * Sets the simulation to fire electrons continuously at a rate determined by the
     * "Electron Production Rate" slider
     *
     */
//    private void setContinuousElectronProduction( ElectronProductionControl electronProductionControl, JButton singleShotBtn ) {
//        electronProductionControl.setProductionType( ElectronProductionControl.CONTINUOUS );
//        currentSlider.setVisible( true );
//        getDischargeLampModel().getLeftHandPlate().setCurrent( currentSlider.getValue() / currentDisplayFactor );
//        singleShotBtn.setVisible( false );
////        collisionEnergyIndicatorGraphic.setVisible( false );
//        getDischargeLampModel().setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
//        super.setHeatingElementsVisible( true );
//
//        if( logoPanel == null ) {
//            logoPanel = getLogoPanel();
//        }
//        setLogoPanel( null );
//        PhetUtilities.getPhetFrame().repaint();
//        getControlPanel().revalidate();
//    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     */
    private void addAtom( Tube tube ) {
        Rectangle2D tubeBounds = tube.getBounds();

        atom = new DischargeLampAtom( (LaserModel)getModel(), getDischargeLampModel().getElementProperties() );
        atom.setPosition( tubeBounds.getX() + tubeBounds.getWidth() / 2,
                          tubeBounds.getY() + tubeBounds.getHeight() / 2 );
        AtomGraphic atomGraphic = addAtom( atom );
        // The graphic may have been put behind the circuit graphic (it is randomly put in front of or behind
        // the circuit in DischargeLampModule.addAtom(). We need to make sure it is above the circuit graphic
        // so that we can get at it with the mouse
        getApparatusPanel().removeGraphic( atomGraphic );
        getApparatusPanel().addGraphic( atomGraphic, DischargeLampsConfig.CIRCUIT_LAYER + 1 );

        // Make the atom movable with the mouse within the bounds of the tube
//        Rectangle2D atomBounds = new Rectangle2D.Double( tubeBounds.getMinX() + atom.getRadius(),
//                                                         atom.getPosition().getY(),
//                                                         tubeBounds.getWidth() - atom.getRadius() * 2,
//                                                         1 );
        Rectangle2D atomBounds = new Rectangle2D.Double( tubeBounds.getMinX() + atom.getRadius(),
                                                         tubeBounds.getMinY() + atom.getRadius(),
                                                         tubeBounds.getWidth() - atom.getRadius() * 2,
                                                         tubeBounds.getHeight() - atom.getRadius() * 2 );
        atomGraphic.setIsMouseable( true, atomBounds );
        atomGraphic.setCursorHand();

        getEneregyLevelsMonitorPanel().reset();
    }

    /**
     * @return
     */
    public Atom getAtom() {
        return atom;
    }
}
