/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * ChargeExcessNode displays the excess charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargeExcessNode extends AbstractChargeNode {
    
    private static final double MAX_CHARGE_SIZE = 80; // nm, size of biggest dimension for the charges
    private static final double MAX_CHARGE_STROKE_WIDTH = 20; // nm, width of the stroke used to draw the charges
    private static final double MARGIN = 10; // nm, how close the charges are to the edge of the bead
    
    private PNode _positiveNode, _negativeNode;
    private double _viewBeadRadius;
    private double _viewMargin;
    private double _maxElectricFieldX;
    
    /**
     * Constructor.
     * 
     * @param bead
     * @param laser
     * @param modelViewTransform
     */
    public ChargeExcessNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform ) {
        super( bead, laser, modelViewTransform );
    }
    
    /*
     * Initializes the nodes and other member data.
     * The positive and negative charge nodes are created at a size that 
     * corresponds to the laser's maximum electric field.
     */
    protected void initialize() {
        
        ModelViewTransform modelViewTransform = getModelViewTransform();
        final double size = modelViewTransform.modelToView( MAX_CHARGE_SIZE );
        final double strokeWidth = modelViewTransform.modelToView( MAX_CHARGE_STROKE_WIDTH );
        
        _positiveNode = createPositiveNode( size, strokeWidth );
        addChild( _positiveNode );
        
        _negativeNode = createNegativeNode( size, strokeWidth );
        addChild( _negativeNode );
        
        Bead bead = getBead();
        _viewBeadRadius = modelViewTransform.modelToView( bead.getDiameter() / 2 );
        _viewMargin = modelViewTransform.modelToView( MARGIN );
        
        Laser laser = getLaser();
        _maxElectricFieldX = laser.getMaxElectricFieldX();
        
        updateCharge();
    }
    
    /*
     * Scales the positive and negative charge nodes to 
     * reflect the magnitude of the electric field's x-component.
     */
    protected void updateCharge() {
        
        // Calculate the scale. 
        // Very small values can cause problems, so anything under some threshold is effectively zero.
        Bead bead = getBead();
        final double electricFieldX = bead.getElectricFieldX();
        double scale = Math.abs( electricFieldX / _maxElectricFieldX );
        if ( scale < 0.01 ) {
            scale = 0;
        }
        
        // if the scale is zero, hide the charges so we don't attempt to apply a zero scale
        _positiveNode.setVisible( scale > 0 );
        _negativeNode.setVisible( scale > 0 );
        
        // position and scale the charges
        if ( scale > 0 ) {
            
            double x, y;
            
            // positive charge
            _positiveNode.setScale( scale );
            if ( electricFieldX > 0 ) {
                x = -_viewBeadRadius + _viewMargin;
            }
            else {
                x = _viewBeadRadius - _positiveNode.getFullBoundsReference().getWidth() - _viewMargin;
            }
            y = -_positiveNode.getFullBoundsReference().getHeight() / 2;
            _positiveNode.setOffset( x, y );
        
            // negative charge
            _negativeNode.setScale( scale );
            if ( electricFieldX > 0 ) {
                x = _viewBeadRadius - _negativeNode.getFullBoundsReference().getWidth() - _viewMargin;
            }
            else {
                x = -_viewBeadRadius + _viewMargin;
            }
            y = -_negativeNode.getFullBoundsReference().getHeight() / 2;
            _negativeNode.setOffset( x, y );
        }
    }
    
}
