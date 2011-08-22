// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * 2D isosurface for a diatomic molecule.
 * Jmol's method of computing isosurface is documented at http://people.reed.edu/~alan/ACS97/elpot.html
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicIsosurfaceNode extends PComposite {

    private static final double DIAMETER_SCALE = 2; // multiply atom diameters by this scale when computing surface size

    private final DiatomicMolecule molecule;
    private final PPath pathNode;

    /**
     * Constructor
     *
     * @param molecule
     * @param colors   color scheme for the surface, ordered from negative to positive
     */
    public DiatomicIsosurfaceNode( final DiatomicMolecule molecule, Color[] colors ) {

        this.molecule = molecule;

        this.pathNode = new PPath() {{
            setStroke( null );
            setPaint( new Color( 100, 100, 100, 100 ) );
        }};
        addChild( pathNode );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        for ( Atom atom : molecule.getAtoms() ) {
            atom.location.addObserver( observer );
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
    }

    private void updateNode() {
        //TODO using circles doesn't really cut it, need to smooth out the places where the circles overlap.
        Area area = new Area();
        for ( Atom atom : molecule.getAtoms() ) {
            area.add( new Area( getCircle( atom ) ) );
        }
        pathNode.setPathTo( area );
    }

    private Ellipse2D getCircle( Atom atom ) {
        final double diameter = DIAMETER_SCALE * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }
}
