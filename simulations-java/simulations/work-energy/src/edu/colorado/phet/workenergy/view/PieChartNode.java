package edu.colorado.phet.workenergy.view;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class PieChartNode extends PNode {
    public PieChartNode( final Property<Boolean> visibleProperty ) {
        addChild( new PText( "Pie chart" ) );
        visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visibleProperty.getValue() );
            }
        } );
    }
}
