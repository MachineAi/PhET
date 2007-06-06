/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/persistence/AffineTransformPersistenceDelegate.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.geom.AffineTransform;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * AffineTransformPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class AffineTransformPersistenceDelegate extends PersistenceDelegate {

    protected Expression instantiate( Object oldInstance, Encoder out ) {
        AffineTransform tx = (AffineTransform)oldInstance;
        double[] coeffs = new double[6];
        tx.getMatrix( coeffs );
        return new Expression( oldInstance,
                               oldInstance.getClass(),
                               "new",
                               new Object[]{coeffs} );
    }
}
