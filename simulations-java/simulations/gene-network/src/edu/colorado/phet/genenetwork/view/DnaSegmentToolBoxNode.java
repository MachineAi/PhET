package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class represents the "toolbox" for DNA segements that can be grabbed
 * by the user and placed into a DNA strand.
 * 
 * @author John Blanco
 */
public class DnaSegmentToolBoxNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	// Defines the width of the tool box as a proportion of the parent window's
	// width.
	static final double WIDTH_PROPORTION = 0.8;
	
	// Aspect ratio, width divided by height, from which the height will be
	// calculated.
	static final double ASPECT_RATIO = 6; 
	
	// Offset from the bottom of the window.
	static final double OFFSET_FROM_BOTTOM = 10;
	
	// Background color used to fill the box.
	static final Color BACKGROUND_COLOR = Color.WHITE; 
	
	// Font for the check boxes.  For consistency with the nodes that are placed
	// in the tool box, we load a serif font if no preferred font is
	// specified.  For more information about why this is done, please look at
	// the code for the nodes that are added to the tool box.
	static final Font CHECK_BOX_FONT = GeneNetworkFontFactory.getFont(16, Font.PLAIN);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Main outline of the box.
	private PhetPPath boxNode;

	// The various grabbable things in the box.
	private LacIPromoterToolBoxNode lacIPromoter;
	private LacZGeneToolBoxNode lacZGene;
	private LacIGeneToolBoxNode lacIGene;
	private LacOperatorToolBoxNode lacIBindingRegion;
	private LacPromoterToolBoxNode lacPromoter;
	
	// The check box for controlling legend visibility.
	private PSwing legendControlCheckBoxPSwing;
	
	// The check box for controlling lactose meter visibility.
	private PSwing lactoseMeterCheckBoxPSwing;
	
	// Reference to the model.
	private IGeneNetworkModelControl model;
	
	// Reference to the model-view transform.
	private ModelViewTransform2D mvt;
	
	// Reference to the canvas upon which we are placed.
	private PhetPCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public DnaSegmentToolBoxNode(final PhetPCanvas canvas, final IGeneNetworkModelControl model, ModelViewTransform2D mvt) {
		
		this.canvas = canvas;
		this.model = model;
		this.mvt = mvt;
		
		// Register for events indicating that the parent window was resized.
		canvas.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	updateLayout(canvas);
		    }
		});
		
		// Create the surrounding box.
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, canvas.getWidth(), 100, 15, 15), BACKGROUND_COLOR,
				new BasicStroke(2f), Color.BLACK);
		addChild(boxNode);
		
		// Create the grabbable items in the box.
		lacIPromoter = new LacIPromoterToolBoxNode(model, mvt, canvas);
		addChild(lacIPromoter);
		lacZGene = new LacZGeneToolBoxNode(model, mvt, canvas);
		addChild(lacZGene);
		lacIGene = new LacIGeneToolBoxNode(model, mvt, canvas);
		addChild(lacIGene);
		lacIBindingRegion = new LacOperatorToolBoxNode(model, mvt, canvas);
		addChild(lacIBindingRegion);
		lacPromoter = new LacPromoterToolBoxNode(model, mvt, canvas);
		addChild(lacPromoter);
		
		// Add a check box for enabling/disabling the lactose meter.
		final JCheckBox lactoseMeterControlCheckBox = new JCheckBox(GeneNetworkStrings.LACTOSE_METER_VISIBILITY_CONTROL_CAPTION);
		lactoseMeterControlCheckBox.setFont(CHECK_BOX_FONT);
		lactoseMeterControlCheckBox.setBackground(BACKGROUND_COLOR);
		lactoseMeterControlCheckBox.setSelected(model.isLegendVisible());
		lactoseMeterControlCheckBox.setFocusable(false);
		lactoseMeterControlCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				model.setLactoseMeterVisible(lactoseMeterControlCheckBox.isSelected());
			}
		});
		lactoseMeterCheckBoxPSwing = new PSwing(lactoseMeterControlCheckBox);
		addChild(lactoseMeterCheckBoxPSwing);
		
		// Add a check box for enabling/disabling the legend.
		final JCheckBox legendControlCheckBox = new JCheckBox(GeneNetworkStrings.LEGEND_VISIBILITY_CONTROL_CAPTION);
		legendControlCheckBox.setFont(CHECK_BOX_FONT);
		legendControlCheckBox.setBackground(BACKGROUND_COLOR);
		legendControlCheckBox.setSelected(model.isLegendVisible());
		legendControlCheckBox.setFocusable(false);
		legendControlCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				model.setLegendVisible(legendControlCheckBox.isSelected());
			}
		});
		legendControlCheckBoxPSwing = new PSwing(legendControlCheckBox);
		addChild(legendControlCheckBoxPSwing);
		
		// Listen to the model for changes to the setting for the legend
		// visibility.
		model.addListener(new GeneNetworkModelAdapter(){
			public void legendVisibilityStateChange() { 
				legendControlCheckBox.setSelected(model.isLegendVisible());
			}
			public void lactoseMeterVisibilityStateChange() {
				lactoseMeterControlCheckBox.setSelected(model.isLactoseMeterVisible());
			}
		});
		
		// Do the initial layout.
		updateLayout(canvas);
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	private void updateLayout(JComponent parent){
		
		// Size the overall box.
    	double width = parent.getWidth() * WIDTH_PROPORTION;
    	boxNode.setPathTo(new RoundRectangle2D.Double(0, 0, width, width / ASPECT_RATIO, 15, 15));
    	setOffset(((double)parent.getWidth() - width) / 2,
    			parent.getHeight() - boxNode.getHeight() - OFFSET_FROM_BOTTOM);
    	
    	// Position the grabbable items.  Since the shapes of these vary quite
    	// a lot, making them line up well requires tweaking the multipliers
    	// below on an individual basis.
    	PBounds boxBounds = boxNode.getFullBounds();
    	lacIPromoter.setOffset(boxBounds.width * 0.11, boxBounds.height / 4);
    	lacIGene.setOffset(boxBounds.width * 0.3, boxBounds.height * 0.25);
    	lacZGene.setOffset(boxBounds.width * 0.5, boxBounds.height * 0.25);
    	lacPromoter.setOffset(boxBounds.width * 0.71, boxBounds.height / 4);
    	lacIBindingRegion.setOffset(boxBounds.width * 0.91, boxBounds.height * 0.25);
    	
    	// Position the check box button for turning the lactose meter on and off.
    	lactoseMeterCheckBoxPSwing.setOffset( 5, boxBounds.height - lactoseMeterCheckBoxPSwing.getFullBoundsReference().height - 5);
    	
    	// Position the check box button for turning the legend on and off.
    	legendControlCheckBoxPSwing.setOffset(
    			boxBounds.width - legendControlCheckBoxPSwing.getFullBoundsReference().width *  1.1,
    			boxBounds.height - legendControlCheckBoxPSwing.getFullBoundsReference().height - 5);
    	
    	// Let the model know our size, so that the model elements can figure
    	// out when they are being put back in the box.  Note that some of the
    	// odd-looking stuff related to the Y dimension is due to the
    	// inversion of the Y axis.
    	Point2D originInWorldCoords = localToGlobal(new Point2D.Double(boxBounds.x, boxBounds.y + boxBounds.height));
    	canvas.getPhetRootNode().screenToWorld(originInWorldCoords);
    	Point2D oppositeCornerInWorldCoords = localToGlobal(new Point2D.Double(boxBounds.getMaxX(), boxBounds.getMinY()));
    	canvas.getPhetRootNode().screenToWorld(oppositeCornerInWorldCoords);
    	Rectangle2D modelRect = new Rectangle2D.Double(mvt.viewToModelX(originInWorldCoords.getX()),
    			mvt.viewToModelY(originInWorldCoords.getY()),
    			mvt.viewToModelDifferentialX(oppositeCornerInWorldCoords.getX() - originInWorldCoords.getX()),
    			mvt.viewToModelDifferentialY(oppositeCornerInWorldCoords.getY() - originInWorldCoords.getY()));
    	model.setToolBoxRect( modelRect );
	}
}
