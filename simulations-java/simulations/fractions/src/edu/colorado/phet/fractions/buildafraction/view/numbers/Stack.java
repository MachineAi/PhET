// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.common.util.FJUtils;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * Keep track of a stack or pile of cards for purposes of making sure only the top one is grabbable, and if a middle card is taken out, then the top card will fall down one slot.
 *
 * @author Sam Reid
 */
public @Data class Stack {
    public final List<NumberCardNode> cards;
    private final Integer stackIndex;
    private final StackContext context;

    public Stack( List<NumberCardNode> cards, final Integer stackIndex, final StackContext context ) {
        this.cards = cards;
        this.stackIndex = stackIndex;
        this.context = context;

        updatePickable();
    }

    //find the frontmost card in the stack, and make it grabbable
    private void updatePickable() {
        for ( NumberCardNode card : cards ) {
            card.setAllPickable( false );
        }
        final Option<NumberCardNode> front = getFrontCardInStack();
        front.foreach( NumberCardNode._setAllPickable );
        if ( front.isSome() ) {
            front.some().moveToFront();
        }
    }

    private Option<NumberCardNode> getFrontCardInStack() {
        List<NumberCardNode> sorted = cards.sort( FJUtils.ord( new F<NumberCardNode, Double>() {
            @Override public Double f( final NumberCardNode numberCardNode ) {
                return numberCardNode.getPositionInStack().isSome() ? numberCardNode.getPositionInStack().some() : -1.0;
            }
        } ) );
        return sorted.isEmpty() ? Option.<NumberCardNode>none() : Option.some( sorted.last() );
    }

    //Fix Z ordering so that stacks will look like they did on startup
    public void update() {
        for ( NumberCardNode card : cards ) {
            card.moveToFront();
        }
        updatePickable();
    }

    private final F<Option<Integer>, List<Integer>> optionToList = FJUtils.optionToList();

    public Vector2D getLocation( final int index, NumberCardNode card ) {
        return context.getLocation( stackIndex, index, card );
    }

    //Find the location for the topmost (in z-ordering) card and move the card there.  Also mark the site as used so no other cards will go there.
    public void moveToTopOfStack( final NumberCardNode cardNode ) {
        List<Integer> sites = List.range( 0, cards.length() );
        for ( Integer site : sites ) {
            if ( !hasCardAtSite( site ) ) {
                cardNode.setPositionInStack( Option.some( site ) );
                cardNode.animateTo( getLocation( site, cardNode ) );
                cardNode.moveToFront();
                break;
            }
        }
    }

    private boolean hasCardAtSite( final Integer site ) {
        for ( NumberCardNode card : cards ) {
            if ( card.isAtStackIndex( site ) ) {
                return true;
            }
        }
        return false;
    }

    public void cardMoved( final NumberCardNode numberCardNode ) {
        updatePickable();
    }
}