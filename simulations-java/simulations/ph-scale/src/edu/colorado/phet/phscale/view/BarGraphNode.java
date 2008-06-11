/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.phscale.PHScaleImages;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class BarGraphNode extends PNode {

    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
    private static final Color OUTLINE_FILL_COLOR = Color.WHITE;

    private static final Font LEGEND_FONT = new PhetFont( 18 );
    private static final double LEGEND_ITEM_Y_SPACING = 5;
    private static final double LEGEND_X_SPACING = 25;
    private static final double LEGEND_Y_SPACING = 5;
    
    private PPath _graphOutlineNode;
    
    public BarGraphNode( double width, double height ) {
        super();
        
        Rectangle2D r = new Rectangle2D.Double( 0, 0, width, height );
        _graphOutlineNode = new PPath( r );
        _graphOutlineNode.setStroke( OUTLINE_STROKE );
        _graphOutlineNode.setStrokePaint( OUTLINE_STROKE_COLOR );
        _graphOutlineNode.setPaint( OUTLINE_FILL_COLOR );
        addChild( _graphOutlineNode );
        
        LegendItemNode h3oLegendItemNode = new LegendItemNode( PHScaleImages.H3O, PHScaleStrings.LABEL_H3O );
        LegendItemNode ohLegendItemNode = new LegendItemNode( PHScaleImages.OH, PHScaleStrings.LABEL_OH );
        LegendItemNode h2oLegendItemNode = new LegendItemNode( PHScaleImages.H2O, PHScaleStrings.LABEL_H2O );
        
        PNode legendNode = new PComposite();
        addChild( legendNode );
        legendNode.addChild( h3oLegendItemNode );
        legendNode.addChild( ohLegendItemNode );
        legendNode.addChild( h2oLegendItemNode );
        h3oLegendItemNode.setOffset( 0, 0 );
        PBounds h3ob = h3oLegendItemNode.getFullBoundsReference();
        ohLegendItemNode.setOffset( h3ob.getMaxX() + LEGEND_X_SPACING, h3ob.getY() );
        PBounds ohb = ohLegendItemNode.getFullBoundsReference();
        h2oLegendItemNode.setOffset( ohb.getMaxX() + LEGEND_X_SPACING, ohb.getY() );
        
        _graphOutlineNode.setOffset( 0, 0 );
        PBounds ob = _graphOutlineNode.getFullBoundsReference();
        PBounds lb = legendNode.getFullBoundsReference();
        legendNode.setOffset( ( ob.getWidth() - lb.getWidth() ) / 2, ob.getMaxY() + LEGEND_Y_SPACING );
    }
    
    private static class LegendItemNode extends PComposite {

        public LegendItemNode( Image image, String label ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            PImage imageNode = new PImage( image );
            imageNode.scale( 0.4 );//XXX
            addChild( imageNode );
            
            HTMLNode htmlNode = new HTMLNode( label );
            htmlNode.setFont( LEGEND_FONT );
            addChild( htmlNode );
            
            PBounds ib = imageNode.getFullBoundsReference();
            PBounds hb = htmlNode.getFullBoundsReference();
            if ( ib.getWidth() > hb.getWidth() ) {
                imageNode.setOffset( 0, 0 );
                htmlNode.setOffset( ( ib.getWidth() - hb.getWidth() ) / 2, ib.getHeight() + LEGEND_ITEM_Y_SPACING );
            }
            else {
                imageNode.setOffset( ( hb.getWidth() - ib.getWidth() ) / 2, 0 );
                htmlNode.setOffset( 0, ib.getHeight() + LEGEND_ITEM_Y_SPACING );
            }
        }
    }
}
