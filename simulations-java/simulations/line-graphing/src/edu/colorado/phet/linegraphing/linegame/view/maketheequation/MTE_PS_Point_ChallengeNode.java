// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View component for a "Make the Equation" (MTE) challenge.
 * Given a graph of a line in point-slope (PS) form, make the equation by changing the Point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_PS_Point_ChallengeNode extends MTE_PS_ChallengeNode {

    public MTE_PS_Point_ChallengeNode( final LineGameModel model, MTE_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the equation portion of the view.
    @Override protected EquationNode createGuessEquationNode( Property<Line> line, Graph graph, PhetFont interactiveFont, PhetFont staticFont, Color staticColor ) {
        return new PointSlopeEquationNode( line,
                                           new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                           new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                           new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                           new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                           true, true, false,
                                           interactiveFont, staticFont, staticColor );
    }
}
