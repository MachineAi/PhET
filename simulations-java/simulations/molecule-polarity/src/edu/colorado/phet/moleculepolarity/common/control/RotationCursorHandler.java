// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Point;
import java.awt.Toolkit;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.MPImages;

/**
 * Cursor handler that shows a rotation cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RotationCursorHandler extends CursorHandler {
    public RotationCursorHandler() {
        super( Toolkit.getDefaultToolkit().createCustomCursor( MPImages.ROTATION_CURSOR, new Point(), "rotate" ) );
    }
}
