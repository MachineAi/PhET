// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;

/**
 * Intro module for sugar and salt solutions
 *
 * @author Sam Reid
 */
public class IntroModule extends SugarAndSaltSolutionsModule {
    private final IntroModel model;

    public IntroModule() {
        super( "Intro" );
        model = new IntroModel();
        setSimulationPanel( new SugarAndSaltSolutionsCanvas( model ) );
    }
}
