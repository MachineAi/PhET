/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:32:09 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public interface UpdateMode {
    void stepInTime( Body body, double dt );

    void init( Body body );
}
