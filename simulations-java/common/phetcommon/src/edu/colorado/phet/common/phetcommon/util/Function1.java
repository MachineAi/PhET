// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

/**
 * A one-parameter function that takes an argument of type U and returns a value of type T.
 *
 * @author Sam Reid
 */
public interface Function1<U, T> {
    T apply( U u );

    /**
     * Identity function that returns its input.
     *
     * @param <U>
     */
    public static class Identity<U> implements Function1<U, U> {
        public U apply( U u ) {
            return u;
        }
    }
}
