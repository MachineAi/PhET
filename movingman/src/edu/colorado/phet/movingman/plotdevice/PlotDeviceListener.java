/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plotdevice;

/**
 * User: Sam Reid
 * Date: Apr 4, 2005
 * Time: 9:22:17 PM
 * Copyright (c) Apr 4, 2005 by Sam Reid
 */
public interface PlotDeviceListener {
    void cursorMoved();

    void zoomChanged();

    void minimizePressed();

    void maximizePressed();

    void sliderMoved();
}
