/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that starts off linear and then becomes a random walk.
 * This tends to be useful when things need to get out of the way of one
 * another and then start moving randomly.
 * 
 * @author John Blanco
 */
public class LinearThenRandomMotionStrategy extends AbstractMotionStrategy {
	
	private RandomWalkMotionStrategy randomWalkStrategy;
	private LinearMotionStrategy linearMotionStrategy;
	private boolean movingLinearly = true;
	
	public LinearThenRandomMotionStrategy(IModelElement modelElement, Rectangle2D bounds, Vector2D initialVelocity, double timeBeforeTransition) {
		super(modelElement);
		linearMotionStrategy = new LinearMotionStrategy(modelElement, bounds, initialVelocity, timeBeforeTransition);
		randomWalkStrategy = new RandomWalkMotionStrategy(modelElement, bounds);
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		if (movingLinearly){
			linearMotionStrategy.updatePositionAndMotion(dt);
			if (linearMotionStrategy.isDestinationReached()){
				// Time to switch to a random walk.
				movingLinearly = false;
				linearMotionStrategy = null;
			}
		}
		else{
			randomWalkStrategy.updatePositionAndMotion(dt);
		}
	}
}
