package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This class represents that atom in the model.  It supplies static
 * information such as the position of the atom, as well as dynamic
 * information such as the number of protons present.
 */
public class Atom {

    private static final Random RAND = new Random();

    // Nuclear radius, in picometers.  This is not to scale - we need it
    // to be larger than real life.
    private static final double NUCLEUS_RADIUS = 10;

    // Electron shell radii.
    private static final double ELECTRON_SHELL_1_RADIUS = 34;
    public static final double ELECTRON_SHELL_2_RADIUS = 102;

    // Position in model space.
    private final Point2D position = new Point2D.Double();

    // List of the subatomic particles that are currently in the nucleus.
    // Note that the electrons are maintained in the shells.
    private final ArrayList<Proton> protons = new ArrayList<Proton>();
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();

    // Shells for containing electrons.
    private final ElectronShell electronShell1 = new ElectronShell( ELECTRON_SHELL_1_RADIUS, 2 );
    private final ElectronShell electronShell2 = new ElectronShell( ELECTRON_SHELL_2_RADIUS, 8 );

    // Observer for electron shells.
    private final SimpleObserver electronShellChangeObserver = new SimpleObserver() {
        public void update() {
            checkAndReconfigureShells();
        }
    };

    /**
     * Constructor.
     */
    public Atom( Point2D position ) {
        this.position.setLocation( position );
        electronShell1.addObserver( electronShellChangeObserver );
        electronShell2.addObserver( electronShellChangeObserver );
    }

    /**
     * Check if the shells need to be reconfigured.  This can be necessary
     * if an electron was removed from shell 1 while there were electrons
     * present in shell 2.
     */
    protected void checkAndReconfigureShells() {
        if ( !electronShell1.isFull() && !electronShell2.isEmpty() ) {

            // Need to move an electron from shell 2 to shell 1.
            ArrayList<Point2D> openLocations = electronShell1.getOpenShellLocations();

            // We expect there to be one and only one open location, so test that this is true.
            assert openLocations.size() == 1;

            // Get the electron that is nearest to this location in shell 2
            // and move it to shell 1.
            Electron electronToMove = electronShell2.getClosestElectron( openLocations.get( 0 ));
            electronShell2.removeElectron( electronToMove );
            electronShell1.addElectron( electronToMove );
        }
    }

    public void reset() {
        protons.clear();
        neutrons.clear();
        electronShell1.reset();
        electronShell2.reset();
    }

    public ArrayList<Double> getElectronShellRadii() {
        ArrayList<Double> electronShellRadii = new ArrayList<Double>();
        electronShellRadii.add( new Double( electronShell1.getRadius() ) );
        electronShellRadii.add( new Double( electronShell2.getRadius() ) );
        return electronShellRadii;
    }

    public double getNucleusRadius() {
        return NUCLEUS_RADIUS;
    }

    public Point2D getPosition() {
        return position;
    }

    public void addProton( final Proton proton ) {
        assert !protons.contains( proton );

        // Add to the list of protons that are in the atom.
        protons.add( proton );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reorganizeNucleus();

        // Add observer for when this particle gets picked up by the user.
        proton.addUserControlListener( new SimpleObserver() {

            public void update() {
                if ( proton.isUserControlled() ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the atom.
                    protons.remove( proton );
                    proton.removeUserControlListener( this );
                }
            }
        } );
    }

    public void addNeutron( final Neutron neutron ) {
        assert !neutrons.contains( neutron );

        // Add to the list of neutrons that are in the atom.
        neutrons.add( neutron );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reorganizeNucleus();

        // Add observer for when this particle gets picked up by the user.
        neutron.addUserControlListener( new SimpleObserver() {

            public void update() {
                if ( neutron.isUserControlled() ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the atom.
                    neutrons.remove( neutron );
                    neutron.removeUserControlListener( this );
                }
            }
        } );
    }

    public void addElectron( final Electron electron ) {
        if ( !electronShell1.isFull() ) {
            electronShell1.addElectron( electron );
        }
        else if ( !electronShell2.isFull() ) {
            electronShell2.addElectron( electron );
        }
        else {
            // Too many electrons.  The sim should be designed such that this
            // does not occur.  If it does, it should be debugged.
            assert false;
        }

        electron.addUserControlListener( new SimpleObserver() {
            public void update() {
                if ( electron.isUserControlled() ) {
                    // This electron is being removed.  Do we need to reconfigure?
                    if ( !electronShell1.isFull() && !electronShell2.isEmpty() ) {
                        // Yes we do.  Move an electron from the shell 2 to
                        // shell 1.
                    }
                }
            }
        } );
    }

    private static class ElectronShell extends SimpleObservable {
        private final double radius;
        private final int electronCapacity;
        private final ArrayList<Point2D> openShellLocations = new ArrayList<Point2D>();
        private final HashMap<Electron, Point2D> occupiedShellLocations = new HashMap<Electron, Point2D>();

        protected ElectronShell( double radius, int electronCapacity ) {
            this.radius = radius;
            this.electronCapacity = electronCapacity;
            // Initialize the open shell locations.
            double angleBetweenElectrons = 2 * Math.PI / electronCapacity;
            for ( int i = 0; i < electronCapacity; i++ ) {
                double angle = i * angleBetweenElectrons;
                openShellLocations.add( new Point2D.Double( this.radius * Math.cos( angle ), this.radius * Math.sin( angle ) ) );
            }
        }

        /**
         * @param point2d
         * @return
         */
        public Electron getClosestElectron( Point2D point2d ) {
            Electron closestElectron = null;
            for (Electron candidateElectron : occupiedShellLocations.keySet()){
                if (closestElectron == null){
                    closestElectron = candidateElectron;
                }
                else if (candidateElectron.getPosition().distance( point2d ) < closestElectron.getPosition().distance( point2d )){
                    // This electron is closer.
                    closestElectron = candidateElectron;
                }
            }
            return closestElectron;
        }

        /**
         * @return
         */
        public ArrayList<Point2D> getOpenShellLocations() {
            return new ArrayList<Point2D>(openShellLocations);
        }

        protected double getRadius() {
            return radius;
        }

        protected boolean isFull() {
            return occupiedShellLocations.size() == electronCapacity;
        }

        protected boolean isEmpty() {
            return occupiedShellLocations.size() == 0;
        }

        protected void reset() {
            for ( Point2D occupiedLocation : occupiedShellLocations.values() ) {
                assert !openShellLocations.contains( occupiedLocation );
                openShellLocations.add( occupiedLocation );
            }
            occupiedShellLocations.clear();
        }

        protected void addElectron( final Electron electronToAdd ) {
            Point2D shellLocation = findClosestOpenLocation( electronToAdd.getPosition() );
            assert shellLocation != null;
            electronToAdd.setDestination( shellLocation );
            openShellLocations.remove( shellLocation );
            occupiedShellLocations.put( electronToAdd, shellLocation );
            electronToAdd.addUserControlListener( new SimpleObserver() {
                public void update() {
                    if ( electronToAdd.isUserControlled() ) {
                        // The user has picked up this electron, so consider
                        // it to be removed from the shell.
                        removeElectron( electronToAdd );
                        electronToAdd.removeUserControlListener( this );
                        notifyObservers();
                    }
                }
            } );
            notifyObservers();

        }

        private void removeElectron( Electron electronToRemove ) {
            if (!occupiedShellLocations.containsKey( electronToRemove )){
                System.out.println("Error!!!");
            }
            assert occupiedShellLocations.containsKey( electronToRemove );
            Point2D newlyFreedShellLocation = occupiedShellLocations.get( electronToRemove );
            openShellLocations.add( newlyFreedShellLocation );
            occupiedShellLocations.remove( electronToRemove );
        }

        private Point2D findClosestOpenLocation( Point2D comparisonPt ) {
            if ( openShellLocations.size() == 0 ) {
                return null;
            }

            Point2D closestPoint = null;
            for ( Point2D candidatePoint : openShellLocations ) {
                if ( closestPoint == null || closestPoint.distance( comparisonPt ) > candidatePoint.distance( comparisonPt ) ) {
                    // Candidate point is better.
                    closestPoint = candidatePoint;
                }
            }
            return closestPoint;
        }
    }


    /**
     * Distribute the nucleons in the nucleus in such a way that the nucleus
     * will look good when shown in the view.
     */
    private void reorganizeNucleus() {

        double nucleonRadius = Proton.RADIUS;

        // Get all the nucleons onto one list.
        ArrayList<SubatomicParticle> nucleons = new ArrayList<SubatomicParticle>();
        for ( SubatomicParticle nucleon : protons ) {
            nucleons.add( nucleon );
        }
        for ( SubatomicParticle nucleon : neutrons ) {
            nucleons.add( nucleon );
        }

        if ( nucleons.size() == 0 ) {
            // Nothing to do.
            return;
        }
        else if ( nucleons.size() == 1 ) {
            // There is only one nucleon present, so place it in the center
            // of the atom.
            nucleons.get( 0 ).setDestination( getPosition() );
        }
        else if ( nucleons.size() == 2 ) {
            double angle = RAND.nextDouble() * 2 * Math.PI;
            nucleons.get( 0 ).setDestination( nucleonRadius * Math.cos( angle ), nucleonRadius * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( -nucleonRadius * Math.cos( angle ), -nucleonRadius * Math.sin( angle ) );
        }
        else if ( nucleons.size() == 3 ) {
            // Form a triangle where they all touch.
            double angle = RAND.nextDouble() * 2 * Math.PI;
            double distFromCenter = nucleonRadius * 1.155;
            nucleons.get( 0 ).setDestination( distFromCenter * Math.cos( angle ), distFromCenter * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( distFromCenter * Math.cos( angle + 2 * Math.PI / 3 ),
                    distFromCenter * Math.sin( angle + 2 * Math.PI / 3 ) );
            nucleons.get( 2 ).setDestination( distFromCenter * Math.cos( angle + 4 * Math.PI / 3 ),
                    distFromCenter * Math.sin( angle + 4 * Math.PI / 3 ) );
        }
        else if ( nucleons.size() == 4 ) {
            double angle = RAND.nextDouble() * 2 * Math.PI;
            nucleons.get( 0 ).setDestination( nucleonRadius * Math.cos( angle ), nucleonRadius * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( -nucleonRadius * Math.cos( angle ), -nucleonRadius * Math.sin( angle ) );
            double distFromCenter = nucleonRadius * 2 * Math.cos(Math.PI / 3);
            nucleons.get( 2 ).setDestination( distFromCenter * Math.cos( angle + Math.PI / 2 ),
                    distFromCenter * Math.sin( angle + Math.PI / 2 ) );
            nucleons.get( 3 ).setDestination( -distFromCenter * Math.cos( angle + Math.PI / 2 ),
                    -distFromCenter * Math.sin( angle + Math.PI / 2 ) );
        }
    }
}