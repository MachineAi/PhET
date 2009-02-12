package edu.colorado.phet.buildtools.util;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Feb 12, 2009
 * Time: 3:07:53 AM
 * A quick project that will always return success for build(), but used for dependencies.
 */
public class SVNDependencyProject extends PhetProject {

    public SVNDependencyProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public Simulation getSimulation(String simulationName, String locale) {
        return null;
    }

    public Locale[] getLocales() {
        return new Locale[0];
    }

    public File getTranslationFile(Locale locale) {
        return null;
    }

    public boolean build() throws Exception {
        return true;
    }

    public String getListDisplayName() {
        return null;
    }

    public void runSim(Locale locale, String simulationName) {

    }

    public void buildLaunchFiles(String URL, boolean dev) {

    }
}
