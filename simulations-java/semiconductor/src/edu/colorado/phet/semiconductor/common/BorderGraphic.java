package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common_semiconductor.view.graphics.Graphic;

import javax.swing.border.Border;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 1:59:44 AM
 * Copyright (c) Jan 27, 2004 by Sam Reid
 */
public class BorderGraphic implements Graphic {
    private Border border;
    private Component target;
    private Rectangle rect;

    public BorderGraphic( Border border, Component target, Rectangle rect ) {
        this.border = border;
        this.target = target;
        this.rect = rect;
    }

    public void paint( Graphics2D graphics2D ) {
        border.paintBorder( target, graphics2D, rect.x, rect.y, rect.width, rect.height );
    }

    public void setRectangle( Rectangle rect ) {
        this.rect = rect;
    }
}
