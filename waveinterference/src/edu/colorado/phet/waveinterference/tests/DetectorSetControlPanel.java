/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.IntensityReaderSet;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:16:55 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class DetectorSetControlPanel extends VerticalLayoutPanel {
    private IntensityReaderSet intensityReaderSet;
    private PhetPCanvas canvas;
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;

    public DetectorSetControlPanel( IntensityReaderSet intensityReaderSet, PhetPCanvas canvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.intensityReaderSet = intensityReaderSet;
        this.canvas = canvas;
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        JButton addDetector = new JButton( "Add Detector" );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addIntensityReader();
            }
        } );
        add( addDetector );
    }

    public void addIntensityReader() {
        intensityReaderSet.addIntensityReader( canvas, waveModel, latticeScreenCoordinates );
    }
}
