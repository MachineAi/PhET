/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PushpinNode represents the pushpin used to "pin" the DNA strand or enzyme in place.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PushpinNode extends PImage {

    public PushpinNode() {
        super( OTResources.getImage( OTConstants.IMAGE_PUSHPIN ) );
    }
}
