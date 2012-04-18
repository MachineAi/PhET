package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.Color;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Buttons used in the matching game for "check answer", etc.
 *
 * @author Sam Reid
 */
public class Button extends TextButtonNode {
    public Button( IUserComponent component, String text, Color color, Vector2D location, ActionListener listener ) {
        super( text, new PhetFont( 18, true ) );
        setUserComponent( component );
        setBackground( color );
        centerFullBoundsOnPoint( location.toPoint2D() );
        addActionListener( listener );
    }
}