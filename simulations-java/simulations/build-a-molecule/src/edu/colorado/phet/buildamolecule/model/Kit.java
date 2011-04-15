// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Contains multiple buckets of different types of atoms
 */
public class Kit {
    private final List<Bucket> buckets;
    private final List<AtomModel> atoms = new LinkedList<AtomModel>(); // our master list of atoms (in and out of buckets)
    private final LewisDotModel lewisDotModel; // lewis-dot connections between atoms on the play area
    private final Set<MoleculeStructure> molecules = new HashSet<MoleculeStructure>(); // molecule structures in the play area
    public final Property<Boolean> visible = new Property<Boolean>( false );
    private LayoutBounds layoutBounds;

    private final List<MoleculeListener> moleculeListeners = new LinkedList<MoleculeListener>();

    public static final double BOND_DISTANCE_THRESHOLD = 200;
    public static final double BUCKET_PADDING = 50;
    public static final double INTER_MOLECULE_PADDING = 100;

    public Kit( List<Bucket> buckets, final LayoutBounds layoutBounds ) {
        this.buckets = buckets;
        this.layoutBounds = layoutBounds;

        lewisDotModel = new LewisDotModel();

        // keep track of all atoms in our kit
        for ( Bucket bucket : buckets ) {
            atoms.addAll( bucket.getAtoms() );

            for ( AtomModel atom : bucket.getAtoms() ) {
                lewisDotModel.addAtom( atom.getAtomInfo() );
                atom.addListener( new AtomModel.Adapter() {
                    @Override
                    public void grabbedByUser( AtomModel atom ) {
                        // TODO: remove from bucket??? check for leaks here
                    }

                    @Override
                    public void droppedByUser( AtomModel atom ) {
                        // dropped on kit, put it in a bucket
                        if ( getAvailableKitBounds().contains( atom.getPosition().toPoint2D() ) ) {
                            if ( isAtomInPlay( atom.getAtomInfo() ) ) {
                                recycleMoleculeIntoBuckets( getMoleculeStructure( atom ) );
                            }
                            else {
                                recycleAtomIntoBuckets( atom.getAtomInfo() );
                            }
                        }
                        else {
                            // dropped in play area
                            if ( isAtomInPlay( atom.getAtomInfo() ) ) {
                                attemptToBondMolecule( getMoleculeStructure( atom ) );
                                separateMoleculeDestinations();
                            }
                            else {
                                addAtomToPlay( atom );
                            }
                        }
                    }
                } );
            }
        }

        /*---------------------------------------------------------------------------*
        * bucket layout
        *----------------------------------------------------------------------------*/

        double kitY = getAvailableKitBounds().getCenterY();
        double kitXCenter = getAvailableKitBounds().getCenterX();

        double usedWidth = 0;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < buckets.size(); i++ ) {
            Bucket bucket = buckets.get( i );
            if ( i != 0 ) {
                usedWidth += BUCKET_PADDING;
            }
            bucket.setPosition( new ImmutableVector2D( usedWidth, kitY ) );
            usedWidth += bucket.getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( Bucket bucket : buckets ) {
            bucket.setPosition( new ImmutableVector2D( bucket.getPosition().getX() - usedWidth / 2 + kitXCenter + bucket.getWidth() / 2, kitY ) );
        }
    }

    public void show() {
        visible.setValue( true );
    }

    public void hide() {
        visible.setValue( false );
    }

    public boolean isContainedInBucket( AtomModel atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.containsAtom( atom ) ) {
                return true;
            }
        }
        return false;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public Bucket getBucketForAtomType( Atom atom ) {
        for ( Bucket bucket : buckets ) {
            if ( bucket.getAtomType().isSameTypeOfAtom( atom ) ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for atom type: " + atom );//oh noes
    }

    public PBounds getAvailableKitBounds() {
        return layoutBounds.getAvailableKitBounds();
    }

    public PBounds getAvailablePlayAreaBounds() {
        return layoutBounds.getAvailablePlayAreaBounds();
    }

    /**
     * Called when an atom is dragged, with the corresponding delta
     *
     * @param atom  Atom that was dragged
     * @param delta How far it was dragged (the delta)
     */
    public void atomDragged( AtomModel atom, ImmutableVector2D delta ) {
        // move our atom
        atom.setPositionAndDestination( atom.getPosition().getAddedInstance( delta ) );

        // move all other atoms in the molecule
        if ( isAtomInPlay( atom.getAtomInfo() ) ) {
            for ( Atom atomInMolecule : getMoleculeStructure( atom ).getAtoms() ) {
                if ( atom.getAtomInfo() == atomInMolecule ) {
                    continue;
                }
                AtomModel atomModel = getAtomModel( atomInMolecule );
                atomModel.setPositionAndDestination( atomModel.getPosition().getAddedInstance( delta ) );
            }
        }
    }

    /**
     * @param atom An atom
     * @return Is this atom registered in our molecule structures?
     */
    public boolean isAtomInPlay( Atom atom ) {
        return getMoleculeStructure( atom ) != null;
    }

    public MoleculeStructure getMoleculeStructure( AtomModel atom ) {
        return getMoleculeStructure( atom.getAtomInfo() );
    }

    public MoleculeStructure getMoleculeStructure( Atom atom ) {
        for ( MoleculeStructure molecule : molecules ) {
            for ( Atom otherAtom : molecule.getAtoms() ) {
                if ( otherAtom == atom ) {
                    return molecule;
                }
            }
        }
        return null;
    }

    public AtomModel getAtomModel( Atom atom ) {
        for ( AtomModel atomModel : atoms ) {
            if ( atomModel.getAtomInfo() == atom ) {
                return atomModel;
            }
        }
        throw new RuntimeException( "atom model not found" );
    }

    public PBounds getMoleculePositionBounds( MoleculeStructure molecule ) {
        PBounds bounds = null;
        for ( Atom atom : molecule.getAtoms() ) {
            AtomModel atomModel = getAtomModel( atom );
            PBounds atomBounds = atomModel.getPositionBounds();
            if ( bounds == null ) {
                bounds = atomBounds;
            }
            else {
                bounds.add( atomBounds );
            }
        }
        return bounds;
    }

    public PBounds getMoleculeDestinationBounds( MoleculeStructure molecule ) {
        PBounds bounds = null;
        for ( Atom atom : molecule.getAtoms() ) {
            AtomModel atomModel = getAtomModel( atom );
            PBounds atomBounds = atomModel.getDestinationBounds();
            if ( bounds == null ) {
                bounds = atomBounds;
            }
            else {
                bounds.add( atomBounds );
            }
        }
        return bounds;
    }

    /**
     * Breaks apart a molecule into separate atoms that remain in the play area
     *
     * @param moleculeStructure The molecule to break
     */
    public void breakMolecule( MoleculeStructure moleculeStructure ) {
        removeMolecule( moleculeStructure );
        for ( final Atom atom : moleculeStructure.getAtoms() ) {
            lewisDotModel.breakBondsOfAtom( atom );
            MoleculeStructure newMolecule = new MoleculeStructure() {{
                addAtom( atom );
            }};
            addMolecule( newMolecule );
        }
        separateMoleculeDestinations();
    }

    public void addMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.add( listener );
    }

    public void removeMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.remove( listener );
    }

    public static interface MoleculeListener {
        public void addedMolecule( MoleculeStructure moleculeStructure );

        public void removedMolecule( MoleculeStructure moleculeStructure );
    }

    public static class MoleculeAdapter implements MoleculeListener {
        public void addedMolecule( MoleculeStructure moleculeStructure ) {
        }

        public void removedMolecule( MoleculeStructure moleculeStructure ) {
        }
    }

    /*---------------------------------------------------------------------------*
    * model implementation
    *----------------------------------------------------------------------------*/

    private void addMolecule( MoleculeStructure moleculeStructure ) {
        molecules.add( moleculeStructure );
        for ( MoleculeListener listener : moleculeListeners ) {
            listener.addedMolecule( moleculeStructure );
        }
    }

    private void removeMolecule( MoleculeStructure moleculeStructure ) {
        molecules.remove( moleculeStructure );
        for ( MoleculeListener listener : moleculeListeners ) {
            listener.removedMolecule( moleculeStructure );
        }
    }

    /**
     * Takes an atom that was in a bucket and hooks it up within our structural model. It allocates a molecule for the
     * atom, and then attempts to bond with it.
     *
     * @param atom An atom to add into play
     */
    private void addAtomToPlay( final AtomModel atom ) {
        // add the atoms to our models
        MoleculeStructure moleculeStructure = new MoleculeStructure() {{
            addAtom( atom.getAtomInfo() );
        }};
        addMolecule( moleculeStructure );

        // attempt to bond
        attemptToBondMolecule( moleculeStructure );

        separateMoleculeDestinations();
    }

    /**
     * Takes an atom, invalidates the structural bonds it may have, and puts it in the correct bucket
     *
     * @param atom The atom to recycle
     */
    private void recycleAtomIntoBuckets( Atom atom ) {
        lewisDotModel.breakBondsOfAtom( atom );
        Bucket bucket = Kit.this.getBucketForAtomType( atom );
        bucket.addAtom( getAtomModel( atom ), true );
    }

    /**
     * Recycles an entire molecule by invalidating its bonds and putting its atoms into their respective buckets
     *
     * @param molecule The molecule to recycle
     */
    private void recycleMoleculeIntoBuckets( MoleculeStructure molecule ) {
        for ( Atom atom : molecule.getAtoms() ) {
            recycleAtomIntoBuckets( atom );
        }
        removeMolecule( molecule );
    }

    private PBounds padMoleculeBounds( PBounds bounds ) {
        double halfPadding = INTER_MOLECULE_PADDING / 2;
        return new PBounds( bounds.x - halfPadding, bounds.y - halfPadding, bounds.width + INTER_MOLECULE_PADDING, bounds.height + INTER_MOLECULE_PADDING );
    }

    private void shiftMoleculeDestination( MoleculeStructure moleculeStructure, ImmutableVector2D delta ) {
        for ( Atom atom : moleculeStructure.getAtoms() ) {
            AtomModel atomModel = getAtomModel( atom );
            atomModel.setDestination( atomModel.getDestination().getAddedInstance( delta ) );
        }
    }

    /**
     * Update atom destinations so that separate molecules will be separated visually
     */
    private void separateMoleculeDestinations() {
        int maxIterations = 100;
        double pushAmount = 10; // how much to push two molecules away

        boolean foundOverlap = true;
        while ( foundOverlap && maxIterations-- >= 0 ) {
            foundOverlap = false;
            for ( MoleculeStructure a : molecules ) {
                PBounds aBounds = padMoleculeBounds( getMoleculeDestinationBounds( a ) );

                // separate it from other molecules
                for ( MoleculeStructure b : molecules ) {
                    if ( a.getMoleculeId() >= b.getMoleculeId() ) {
                        // this removes the case where a == b, and will make sure we don't run the following code twice for (a,b) and (b,a)
                        continue;
                    }
                    PBounds bBounds = padMoleculeBounds( getMoleculeDestinationBounds( b ) );
                    if ( aBounds.intersects( bBounds ) ) {
                        foundOverlap = true;

                        // get perturbed centers. this is so that if two molecules have the exact same centers, we will push them away
                        ImmutableVector2D aCenter = new ImmutableVector2D( aBounds.getCenter2D() ).getAddedInstance( Math.random() - 0.5, Math.random() - 0.5 );
                        ImmutableVector2D bCenter = new ImmutableVector2D( bBounds.getCenter2D() ).getAddedInstance( Math.random() - 0.5, Math.random() - 0.5 );

                        // delta from center of A to center of B, scaled to half of our push amount.
                        ImmutableVector2D delta = bCenter.getSubtractedInstance( aCenter ).getNormalizedInstance().getScaledInstance( pushAmount / 2 );

                        // push B half of the way, then push A the same amount in the opposite direction
                        shiftMoleculeDestination( b, delta );
                        shiftMoleculeDestination( a, delta.getScaledInstance( -1 ) );

                        aBounds = padMoleculeBounds( getMoleculeDestinationBounds( a ) );
                    }
                }

                // then push it away from the outsides
                if ( aBounds.getMinX() < getAvailablePlayAreaBounds().getMinX() ) {
                    shiftMoleculeDestination( a, new ImmutableVector2D( getAvailablePlayAreaBounds().getMinX() - aBounds.getMinX(), 0 ) );
                    aBounds = padMoleculeBounds( getMoleculeDestinationBounds( a ) );
                }
                if ( aBounds.getMaxX() > getAvailablePlayAreaBounds().getMaxX() ) {
                    shiftMoleculeDestination( a, new ImmutableVector2D( getAvailablePlayAreaBounds().getMaxX() - aBounds.getMaxX(), 0 ) );
                    aBounds = padMoleculeBounds( getMoleculeDestinationBounds( a ) );
                }
                if ( aBounds.getMinY() < getAvailablePlayAreaBounds().getMinY() ) {
                    shiftMoleculeDestination( a, new ImmutableVector2D( 0, getAvailablePlayAreaBounds().getMinY() - aBounds.getMinY() ) );
                    aBounds = padMoleculeBounds( getMoleculeDestinationBounds( a ) );
                }
                if ( aBounds.getMaxY() > getAvailablePlayAreaBounds().getMaxY() ) {
                    shiftMoleculeDestination( a, new ImmutableVector2D( 0, getAvailablePlayAreaBounds().getMaxY() - aBounds.getMaxY() ) );
                }
            }
        }
    }

    /**
     * Bonds one atom to another, and handles the corresponding structural changes between molecules.
     *
     * @param a       An atom A
     * @param dirAtoB The direction from A that the bond will go in (for lewis-dot structure)
     * @param b       An atom B
     */
    private void bond( AtomModel a, LewisDotModel.Direction dirAtoB, AtomModel b ) {
        lewisDotModel.bond( a.getAtomInfo(), dirAtoB, b.getAtomInfo() );
        MoleculeStructure molA = getMoleculeStructure( a );
        MoleculeStructure molB = getMoleculeStructure( b );
        if ( molA == molB ) {
            throw new RuntimeException( "WARNING: loop or other invalid structure detected in a molecule" );
        }
        else {
            removeMolecule( molA );
            removeMolecule( molB );
            addMolecule( MoleculeStructure.getCombinedMoleculeFromBond( molA, molB, a.getAtomInfo(), b.getAtomInfo() ) );
        }

        // TODO: remove following dev testing checks and debugging statements. ONLY after testing molecule structure comparison
        assert ( getMoleculeStructure( a ) == getMoleculeStructure( b ) );
//        MoleculeStructure molecule = getMoleculeStructure( a );
//        CompleteMolecule completeMolecule = CompleteMolecule.findMatchingCompleteMolecule( molecule );
//        if ( completeMolecule != null ) {
//            System.out.println( "You made: " + completeMolecule.getCommonName() );
//        }
    }

    private MoleculeStructure getPossibleMoleculeStructureFromBond( AtomModel a, AtomModel b ) {
        MoleculeStructure molA = getMoleculeStructure( a );
        MoleculeStructure molB = getMoleculeStructure( b );
        if ( molA == molB ) {
            return molA.getCopy();
        }
        else {
            return MoleculeStructure.getCombinedMoleculeFromBond( molA, molB, a.getAtomInfo(), b.getAtomInfo() );
        }
    }

    /**
     * @param moleculeStructure A molecule that should attempt to bind to other atoms / molecules
     * @return Success
     */
    private boolean attemptToBondMolecule( MoleculeStructure moleculeStructure ) {
        BondingOption bestLocation = null;
        double bestDistanceFromIdealLocation = Double.POSITIVE_INFINITY;
        for ( Atom atomInfo : moleculeStructure.getAtoms() ) {
            AtomModel atom = getAtomModel( atomInfo );
            for ( AtomModel otherAtom : atoms ) {
                if ( !isContainedInBucket( otherAtom ) ) {
                    if ( otherAtom == atom || !canBond( atom, otherAtom ) ) {
                        continue;
                    }
                    for ( LewisDotModel.Direction direction : lewisDotModel.getOpenDirections( otherAtom.getAtomInfo() ) ) {
                        if ( !lewisDotModel.getOpenDirections( atomInfo ).contains( LewisDotModel.Direction.opposite( direction ) ) ) {
                            // the spot on otherAtom was open, but the corresponding spot on our main atom was not
                            continue;
                        }
                        BondingOption location = new BondingOption( otherAtom, direction, atom );
                        double distance = atom.getPosition().getDistance( location.getIdealLocation() );
                        if ( distance < bestDistanceFromIdealLocation ) {
                            bestLocation = location;
                            bestDistanceFromIdealLocation = distance;
                        }
                    }
                }
            }
        }
        if ( bestLocation == null || bestDistanceFromIdealLocation > BOND_DISTANCE_THRESHOLD ) {
            return false;
        }

        // cause all atoms in the molecule to move to that location
        ImmutableVector2D delta = bestLocation.getIdealLocation().getSubtractedInstance( bestLocation.b.getPosition() );
        for ( Atom atomInMolecule : getMoleculeStructure( bestLocation.b ).getAtoms() ) {
            AtomModel atomModel = getAtomModel( atomInMolecule );
            atomModel.setDestination( atomModel.getPosition().getAddedInstance( delta ) );
        }

        // we now will bond the atom
        bond( bestLocation.a, bestLocation.direction, bestLocation.b ); // model bonding
        return true;
    }

    private boolean canBond( AtomModel a, AtomModel b ) {
        return getMoleculeStructure( a ) != getMoleculeStructure( b ) && CompleteMolecule.isAllowedStructure( getPossibleMoleculeStructureFromBond( a, b ) );
    }

    /**
     * A bond option from A to B. B would be moved to the location near A to bond.
     */
    private static class BondingOption {
        public final AtomModel a;
        public final LewisDotModel.Direction direction;
        public final AtomModel b;

        private BondingOption( AtomModel a, LewisDotModel.Direction direction, AtomModel b ) {
            this.a = a;
            this.direction = direction;
            this.b = b;
        }

        /**
         * @return The location the atom should be placed
         */
        public ImmutableVector2D getIdealLocation() {
            return a.getPosition().getAddedInstance( direction.getVector().getScaledInstance( a.getRadius() + b.getRadius() ) );
        }
    }
}
