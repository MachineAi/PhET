/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.factory;

import edu.colorado.phet.hydrogenatom.factory.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.model.AlphaParticle;
import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.view.AlphaParticleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * AlphaParticleNodeFactory creates PNodes that display alpha particle model objects.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AlphaParticleNodeFactory extends NodeFactory {

    public AlphaParticleNodeFactory( PNode parent ) {
        super( AlphaParticle.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        PNode node = null;
        if ( modelObject instanceof AlphaParticle ) {
            node = new AlphaParticleNode( (AlphaParticle) modelObject );
        }
        return node;
    }
}
