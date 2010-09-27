/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class BuildAnAtomCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final BuildAnAtomModel model;

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomCanvas( BuildAnAtomModel model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteringBoxStrategy( this, BuildAnAtomDefaults.INTERMEDIATE_RENDERING_SIZE ) );

        // Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.5 ),
                (int) Math.round( BuildAnAtomDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.5 ) ),
                2.5, // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Put up the bounds of the model.
        //        rootNode.addChild( new PhetPPath( mvt.createTransformedShape( model.getBounds() ), Color.PINK, new BasicStroke( 3f ), Color.BLACK ) );
        //        rootNode.addChild( new PhetPPath( new Rectangle2D.Double(-178, 2, 1124, 764), Color.PINK, new BasicStroke( 3f ), Color.BLACK ) );

        // Add the atom to the canvas.
        Shape nucleusOutlineShape = mvt.createTransformedShape( new Ellipse2D.Double(
                -model.getAtom().getNucleusRadius(),
                -model.getAtom().getNucleusRadius(),
                model.getAtom().getNucleusRadius() * 2,
                model.getAtom().getNucleusRadius() * 2 ) );
        PNode nucleusOutlineNode = new PhetPPath( nucleusOutlineShape, Color.GREEN );
        nucleusOutlineNode.setOffset( model.getAtom().getPosition() );
        rootNode.addChild( nucleusOutlineNode );

        // TODO: Temp - put a sketch of the tab up as a very early prototype.
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "tab-1-sketch-01.png" ));
        //        image.scale( 2.05 );
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "first-tab-of-build-an-atom-sim-with-color.png" ));
        //        image.scale( 1.2 );
        //        image.setOffset( 50, 0 );
        //        rootNode.addChild(image);
    }


    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
