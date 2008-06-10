/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class OnOffSlider extends JSlider {
    
    private static final int OFF_VALUE = 0;
    private static final int ON_VALUE = 100;
    
    private boolean _on;
    private final ArrayList _listeners;
    
    public OnOffSlider() {
        this( false /* on */ );
    }
    
    public OnOffSlider( boolean on ) {
        super();
        _listeners = new ArrayList();
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSliderChange();
            }
        } );
        addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                setOn( false );
            }
        } );
        setValue( false );
    }
    
    private void handleSliderChange() {
        final boolean on = ( getValue() != OFF_VALUE );
        if ( on != _on ) {
            _on = on;
            notifyStateChanged();
        }
    }
    
    public boolean isOn() {
        return _on;
    }
    
    public void setOn( boolean on ) {
        if ( on != _on ) {
            _on = on;
            setValue( on );
            notifyStateChanged();
        }
    }
    
    private void setValue( boolean on ) {
        setValue( on ? ON_VALUE : OFF_VALUE );
    }
    
    public interface OnOffSliderListener {
        public void stateChanged( boolean on );
    }
    
    public void addOnOffSliderListener( OnOffSliderListener listener ) {
        _listeners.add( listener );
    }
    
    
    public void removeOnOffSliderListener( OnOffSliderListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyStateChanged() {
        final boolean on = isOn();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (OnOffSliderListener) i.next() ).stateChanged( on );
        }
    }

}
