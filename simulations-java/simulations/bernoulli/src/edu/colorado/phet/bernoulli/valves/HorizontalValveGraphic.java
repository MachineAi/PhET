package edu.colorado.phet.bernoulli.valves;

import edu.colorado.phet.bernoulli.BernoulliResources;
import edu.colorado.phet.bernoulli.common.RectangleImageGraphic2;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.TransformListener;
import edu.colorado.phet.common.bernoulli.bernoulli.simpleobserver.SimpleObserver;
import edu.colorado.phet.common.bernoulli.view.ApparatusPanel;
import edu.colorado.phet.common.bernoulli.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 1:09:19 AM
 */
public class HorizontalValveGraphic implements InteractiveGraphic, SimpleObserver, TransformListener {
    Color color = Color.red;
    boolean open;
    Valve model;
    private ModelViewTransform2d transform;
    private ApparatusPanel target;
    private boolean right;
    private Color backgroundColor;
    private Point location;
    int width = 40;
    int height = 30;
    int tankStrokeWidth = 4;
    private Rectangle rect;
    RectangleImageGraphic2 rig2;

    public HorizontalValveGraphic( Valve model, ModelViewTransform2d transform, ApparatusPanel target, boolean right, Color backgroundColor ) {
        rig2 = new RectangleImageGraphic2( BernoulliResources.getImage( "valvewrap.gif" ) );
        this.model = model;
        this.transform = transform;
        this.target = target;
        this.right = right;
        this.backgroundColor = backgroundColor;
        model.addObserver( this );
        transform.addTransformListener( this );
        update();
        this.height = transform.modelToViewDifferentialY( model.getHeight() );
        this.width = transform.modelToViewDifferentialX( model.getWidth() );
    }

    public void valveChanged() {
        this.open = model.isOpen();
        if( model.isOpen() ) {
            color = Color.red;
        }
        else {
            color = Color.red;
        }
        this.location = transform.modelToView( model.getX(), model.getY() );
        if( !right ) {
            this.rect = new Rectangle( location.x, location.y - tankStrokeWidth / 2, width, height + tankStrokeWidth / 2 );
        }
        else {
            this.rect = new Rectangle( location.x, location.y - height / 2, width, height );
        }
        target.repaint();
        rig2.setOutputRect( rect );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return rect.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        model.setOpen( !model.isOpen() );
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D g ) {
        if( rect != null ) {
            if( open ) {
                rig2.paint( g );
            }
            else {
                rig2.paint( g );
            }
        }
    }

    public void update() {
        valveChanged();
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        height = Math.abs( transform.modelToViewDifferentialY( model.getHeight() ) );
        this.width = Math.abs( transform.modelToViewDifferentialX( model.getWidth() ) );
        update();
    }

    public Rectangle getRectangle() {
        return rect;
    }
}
