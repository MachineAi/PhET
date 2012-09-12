// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode.GraphTheLineChallengeNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationFactory;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all challenges that use slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SI_ChallengeNode extends GraphTheLineChallengeNode {

    public SI_ChallengeNode( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, audioPlayer, challengeSize );
    }

    // Creates the equation portion of the view.
    @Override public EquationNode createEquationNode( Line line, Color color, PhetFont font ) {
        return new SlopeInterceptEquationFactory().createNode( line.withColor( color ), font );
    }

    // Graph for all challenges that use slope-intercept form.
    public static abstract class SI_ChallengeGraphNode extends ChallengeGraphNode {

        public SI_ChallengeGraphNode( Graph graph, ModelViewTransform mvt ) {
            super( graph, mvt );
        }

        @Override public LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
            return new SlopeInterceptLineNode( line.withColor( GameConstants.CORRECT_ANSWER_COLOR ), graph, mvt );
        }

        @Override public LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
            return new SlopeInterceptLineNode( line, graph, mvt );
        }
    }
}