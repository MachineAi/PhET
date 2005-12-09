package edu.colorado.phet.qm.view.colormaps3;

import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colormaps.ColorMap;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Dec 8, 2005
 * Time: 12:37:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ComplexColorMapAdapter implements ColorMap {
    Wavefunction wavefunction;
    ComplexColorMap complexColorMap;

    public ComplexColorMapAdapter( Wavefunction wavefunction, ComplexColorMap complexColorMap ) {
        this.wavefunction = wavefunction;
        this.complexColorMap = complexColorMap;
    }

    public Paint getColor( int i, int k ) {
        return complexColorMap.getColor( wavefunction.valueAt( i, k ) );
    }

    public Wavefunction getWavefunction() {
        return wavefunction;
    }

    public void setWavefunction( Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
    }

    public ComplexColorMap getComplexColorMap() {
        return complexColorMap;
    }

    public void setComplexColorMap( ComplexColorMap complexColorMap ) {
        this.complexColorMap = complexColorMap;
    }
}
