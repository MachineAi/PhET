/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.piccolo.PImageFactory;
import edu.colorado.phet.solublesalts.model.Ion;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;

/**
 * IonGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonGraphic extends PNode implements SimpleObserver {

    private Ion ion;
    private PImage pImage;
    private PText pText;
    private PPath pDebugPath;

    public IonGraphic( Ion ion, String imageName ) {
        this.ion = ion;
        ion.addObserver( this );
        pImage = PImageFactory.create( imageName, new Dimension( (int)ion.getRadius() * 2,
                                                                 (int)ion.getRadius() * 10 ) );
        this.addChild( pImage );

        String text = ion.getCharge() < 0 ? "-" : ( ion.getCharge() > 0 ? "+" : "0" );
        pText = new PText( text );
        pText.setTextPaint( Color.white );
        Font font = pText.getFont();
        Font newFont = new Font( font.getName(), font.getStyle(), font.getSize() + 4);
        pText.setFont( newFont );
        pText.setX( pImage.getWidth() * 5 / 8 );
//        pText.setY( pImage.getHeight() * 3 / 8 );
        this.addChild( pText );
        update();
    }

    public void update() {
        this.setOffset( ion.getPosition().getX() - pImage.getWidth() / 2,
                        ion.getPosition().getY() - pImage.getHeight() / 2 );

        // Draws a mark on the ion if it's bound
        if( ion.isBound() && pDebugPath == null ) {
            pDebugPath = new PPath( new Ellipse2D.Double( ( pImage.getWidth() / 2 ) - 2,
                                                          ( pImage.getHeight() / 2 ) - 2,
                                                          4,
                                                          4 ) );
            pDebugPath.setPaint( Color.red );
            pDebugPath.setStrokePaint( Color.red );
            addChild( pDebugPath );
        }
        else if( !ion.isBound() && pDebugPath != null ) {
            removeChild( pDebugPath );
            pDebugPath = null;
        }
    }

    public void setColor( Color color ) {
        MakeDuotoneImageOp op = new MakeDuotoneImageOp( color );
        pImage.setImage( op.filter( (BufferedImage)pImage.getImage(), null ));
    }
}
