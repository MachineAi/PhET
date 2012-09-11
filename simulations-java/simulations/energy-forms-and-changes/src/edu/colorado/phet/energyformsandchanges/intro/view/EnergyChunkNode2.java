// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunkNode2 extends PNode {

    private static final double WIDTH = 24; // In screen coords, which is close to pixels.
    private static final double Z_DISTANCE_WHERE_FULLY_FADED = 0.1; // In meters.
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 2 );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;

    public EnergyChunkNode2( final EnergyChunk energyChunk, final ModelViewTransform mvt ) {

        // Control the overall visibility of this node.
        energyChunk.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Set up updating of transparency based on existence strength.
        energyChunk.getExistenceStrength().addObserver( new VoidFunction1<Double>() {
            public void apply( Double existenceStrength ) {
                updateTransparency( existenceStrength, energyChunk.zPosition.get() );
            }
        } );

        // Set up updating of transparency based on Z position.
        energyChunk.zPosition.addObserver( new VoidFunction1<Double>() {
            public void apply( Double zPosition ) {
                updateTransparency( energyChunk.getExistenceStrength().get(), zPosition );
            }
        } );

        // Draw the energy chunks.
        PNode body = new PhetPPath( new RoundRectangle2D.Double( -WIDTH / 2, -WIDTH / 2, WIDTH, WIDTH, WIDTH / 4, WIDTH / 4 ),
                                    Color.PINK,
                                    OUTLINE_STROKE,
                                    OUTLINE_STROKE_COLOR );
        addChild( body );

        // Set this node's position when the corresponding model element moves.
        energyChunk.position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }

    // Update the transparency, which is a function of several factors.
    private void updateTransparency( double existenceStrength, double zPosition ) {

        double zFadeValue = 1;
        if ( zPosition < 0 ) {
            zFadeValue = Math.max( ( Z_DISTANCE_WHERE_FULLY_FADED + zPosition ) / Z_DISTANCE_WHERE_FULLY_FADED, 0 );
        }
        setTransparency( (float) Math.min( existenceStrength, zFadeValue ) );
    }


    // Test harness.
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 200, 100 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new EnergyChunkNode2( new EnergyChunk( new ConstantDtClock( 30 ), 0, 0, new BooleanProperty( true ), false ), mvt ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
