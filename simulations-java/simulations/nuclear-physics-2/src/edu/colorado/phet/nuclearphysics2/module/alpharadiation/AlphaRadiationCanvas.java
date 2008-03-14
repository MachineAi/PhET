/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationChart;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed.
 *
 * @author John Blanco
 */
public class AlphaRadiationCanvas extends PhetPCanvas {
    
    // Constant that sets the scale of this sim, which is in femtometers.
    private final double SCALE = 0.8;
    
    private AtomicNucleusNode _atomicNucleusNode; 
    private AlphaRadiationChart _alphaRadiationChart;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AlphaRadiationCanvas(AlphaRadiationModel alphaRadiationModel) {
        
        // Call the constructor that will allow the canvas to be scaled.  The
        // units are assumed to be femtometers (fm).  As a point of reference,
        // the nucleus of an atom of uranium is about 15 fm in diameter.  We
        // are also assuming an initial aspect ratio of 4:3.
        setTransformStrategy( new RenderingSizeStrategy(this, new PDimension(150.0d * SCALE, 115.0d * SCALE) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/2, getHeight()/4 );
            }
        });
        
        // Register with the model for notifications of alpha particles coming
        // and going.
        alphaRadiationModel.addListener( new AlphaRadiationModel.Listener(  ){
            
            /**
             * A new particle has been added in the model, so we need to
             * display it on the canvas.
             */
            public void particleAdded(AlphaParticle alphaParticle){
                
                AlphaParticleNode alphaParticleNode = new AlphaParticleNode(alphaParticle);
                
                // Add at layer 0 so it is behind other things on the canvas,
                // such as the chart.
                addWorldChild( 0, alphaParticleNode );
                
                // Map the particle to the node so that we can remove it later.
                _mapAlphaParticlesToNodes.put( alphaParticle, alphaParticleNode );
            }
            
            /**
             * A particle has been removed from the model, so we need to
             * remove its representation from the canvas (i.e. view).
             */
            public void particleRemoved(AlphaParticle alphaParticle){
                AlphaParticleNode alphaParticleNode = 
                    (AlphaParticleNode)_mapAlphaParticlesToNodes.get( alphaParticle );
                assert alphaParticleNode != null;
                removeWorldChild( alphaParticleNode );
                _mapAlphaParticlesToNodes.remove( alphaParticleNode );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add the nucleus node to the canvas.
        _atomicNucleusNode = new AtomicNucleusNode(alphaRadiationModel.getAtom());
        addWorldChild( _atomicNucleusNode );

        // Add the chart that depicts the tunneling energy threshold to the
        // canvas.  The initial size is arbitrary and will be scaled when the
        // canvas is painted.
        _alphaRadiationChart = new AlphaRadiationChart();
        addScreenChild( _alphaRadiationChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                _alphaRadiationChart.componentResized( getWidth(), getHeight() );
            }
        } );
    }

}
