// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for one pie
 *
 * @author Sam Reid
 */
public class PieNode extends PNode {
    public PieNode( final Fraction fraction, Property<ContainerSet> containerSet ) {

        PieSetFractionNode pieSetFractionNode = new PieSetFractionNode( containerSet, new Property<Boolean>( true ) );
        addChild( new ZeroOffsetNode( pieSetFractionNode ) );

        scale( 0.5 );
    }
}