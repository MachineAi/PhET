/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/graphics/transforms/CompositeTransformListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:03 $
 */
package edu.colorado.phet.common.view.graphics.transforms;

import java.util.ArrayList;

/**
 * CompositeTransformListener
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class CompositeTransformListener implements TransformListener {
    ArrayList listeners = new ArrayList();

    public void transformChanged( ModelViewTransform2D mvt ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            TransformListener o = (TransformListener)listeners.get( i );
            o.transformChanged( mvt );
        }
    }

    public TransformListener transformListenerAt( int i ) {
        return (TransformListener)listeners.get( i );
    }

    public void removeTransformListener( TransformListener tl ) {
        listeners.remove( tl );
    }

    public int numTransformListeners() {
        return listeners.size();
    }

    public void addTransformListener( TransformListener tl ) {
        listeners.add( tl );
    }

    public TransformListener[] getTransformListeners() {
        return (TransformListener[])listeners.toArray( new TransformListener[0] );
    }
}