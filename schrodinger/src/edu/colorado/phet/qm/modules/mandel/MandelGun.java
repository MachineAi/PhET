/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.ConnectorGraphic;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.qm.controls.IntensitySlider;
import edu.colorado.phet.qm.controls.SRRWavelengthSlider;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.GunControlPanel;
import edu.colorado.phet.qm.view.gun.OnOffCheckBox;
import edu.colorado.phet.qm.view.gun.OnOffItem;
import edu.colorado.phet.qm.view.gun.SRRWavelengthSliderComponent;
import edu.colorado.phet.qm.view.piccolo.BlueGunDetails;
import edu.colorado.phet.qm.view.piccolo.HorizontalWireConnector;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 9:33:23 PM
 * Copyright (c) Mar 2, 2006 by Sam Reid
 */

public class MandelGun extends PhetPNode {
    private PImage pimage;
    private GunControlPanel gunControlPanel;
    private ArrayList listeners = new ArrayList();
    private IntensitySlider intensitySlider;
    private SRRWavelengthSlider wavelengthSliderGraphic;
    private boolean on = false;
    private OnOffCheckBox onOffCheckBox;
    private PSwing onGunGraphic;

    public MandelGun( String image, SchrodingerPanel schrodingerPanel ) {
        pimage = PImageFactory.create( image );
        addChild( pimage );
        gunControlPanel = new GunControlPanel( schrodingerPanel );

        intensitySlider = new IntensitySlider( Color.blue, IntensitySlider.HORIZONTAL, new Dimension( 140, 30 ) );
        intensitySlider.setValue( 100 );
        VerticalLayoutPanel vlp = new VerticalLayoutPanel();
        vlp.addFullWidth( intensitySlider );
//        vlp.addFullWidth( new JLabel( "GunControl goes here" ) );

        wavelengthSliderGraphic = new SRRWavelengthSlider( schrodingerPanel );
        SRRWavelengthSliderComponent srrWavelengthSliderComponent = new SRRWavelengthSliderComponent( wavelengthSliderGraphic );
        vlp.addFullWidth( srrWavelengthSliderComponent );
        gunControlPanel.setGunControls( vlp );
        addChild( gunControlPanel.getPSwing() );

        wavelengthSliderGraphic.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSliderColor();
                fireColorChanged();
            }

        } );
        updateSliderColor();
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireIntensityChanged();
            }

        } );
        ConnectorGraphic connectorGraphic = new HorizontalWireConnector( pimage, gunControlPanel.getPSwing() );
        addChild( 0, connectorGraphic );

        onOffCheckBox = new OnOffCheckBox( new OnOffItem() {
            public boolean isOn() {
                return on;
            }

            public void setOn( boolean ison ) {
                on = ison;
            }
        } );
        onGunGraphic = new PSwing( schrodingerPanel, onOffCheckBox );
        addChild( onGunGraphic );
        onGunGraphic.setOffset( pimage.getFullBounds().getX() + pimage.getFullBounds().getWidth() / 2 - onGunGraphic.getFullBounds().getWidth() / 2 + BlueGunDetails.onGunControlDX, BlueGunDetails.gunControlAreaY + pimage.getFullBounds().getY() );
    }

    private void fireColorChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.wavelengthChanged();
        }
    }

    private void fireIntensityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.intensityChanged();
        }
    }

    private void updateSliderColor() {
        intensitySlider.setColor( wavelengthSliderGraphic.getVisibleColor() );
    }

    public void setControlsOffset( double x, double y ) {
        gunControlPanel.getPSwing().setOffset( x, y );
    }

    public PNode getGunControlPanelPSwing() {
        return gunControlPanel.getPSwing();
    }

    public PNode getGunImageGraphic() {
        return pimage;
    }

    public void setControlBackgroundColor( Color gunBackgroundColor ) {
        onOffCheckBox.setBackground( gunBackgroundColor );
    }

    public void translateOnGunControls( int dx, int dy ) {
        onGunGraphic.translate( dx, dy );
    }

    public boolean isOn() {
        return on;
    }

    public double getWavelength() {
        return wavelengthSliderGraphic.getWavelength();
    }

    public static interface Listener {
        void wavelengthChanged();

        void intensityChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
