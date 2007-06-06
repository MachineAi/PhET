/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/graphics/mousecontrols/TranslationHandler.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * TranslationHandler
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class TranslationHandler implements MouseInputListener {

    TranslationListener translationListener;
    private Point last;

    public TranslationHandler( TranslationListener translationListener ) {
        this.translationListener = translationListener;
    }

    public void mouseDragged( MouseEvent event ) {
        if( last == null ) {
            mousePressed( event );
            return;
        }
        Point modelLoc = event.getPoint();
        Point dx = new Point( modelLoc.x - last.x, modelLoc.y - last.y );
        TranslationEvent trEvent = new TranslationEvent( this, event, event.getX(), event.getY(), dx.x, dx.y );
        translationListener.translationOccurred( trEvent );
        last = modelLoc;
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent event ) {
        last = event.getPoint();
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    //////////////////////////////////////////////////////////
    // Persistence support
    //
    public TranslationHandler() {
    }

    public TranslationListener getTranslationListener() {
        return translationListener;
    }

    public void setTranslationListener( TranslationListener translationListener ) {
        this.translationListener = translationListener;
    }

    public Point getLast() {
        return last;
    }

    public void setLast( Point last ) {
        this.last = last;
    }
}
