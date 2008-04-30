/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.ContainmentVessel;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.model.Nucleon;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.model.Uranium235Nucleus;
import edu.colorado.phet.nuclearphysics2.model.Uranium238Nucleus;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate nuclear chain reaction within this
 * sim.
 *
 * @author John Blanco
 */
public class ChainReactionModel {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Constants that control the range within the model where nuclei may be
    // initially located.
    private static final double MAX_NUCLEUS_RANGE_X = 400;
    private static final double MAX_NUCLEUS_RANGE_Y = MAX_NUCLEUS_RANGE_X * 0.75;
    private static final double INTER_NUCLEUS_PROXIMITRY_LIMIT = 12;
    private static final double INITIAL_CONTAINMENT_VESSEL_RADIUS = MAX_NUCLEUS_RANGE_X / 6;
    private static final double CONTAINMENT_VESSEL_MARGIN = 12;
    
    // Constants that control the position of the neutron source.
    private static final double NEUTRON_SOURCE_POS_X = -50;
    private static final double NEUTRON_SOURCE_POS_Y = 0;
    
    // Constant rect that defines a space around the neutron source where
    // nuclei cannot initially be located.  This is just tweaked until things
    // look right.
    private static final Rectangle2D NEUTRON_SOURCE_OFF_LIMITS_RECT = 
        new Rectangle2D.Double( NEUTRON_SOURCE_POS_X - 70, NEUTRON_SOURCE_POS_Y - 20, 80, 50 );
    
    // Constants that control the behavior of fission products.
    private static final double FREED_NEUTRON_VELOCITY = 3;
    private static final double INITIAL_DAUGHTER_NUCLEUS_VELOCITY = 0;
    private static final double DAUGHTER_NUCLEUS_ACCELERATION = 0.2;
    
    // Constants for convenience and optimization.
    private static final Vector2D ZERO_ACCELERATION = new Vector2D.Double(0, 0);
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysics2Clock _clock;
    private ArrayList _listeners = new ArrayList();
    private ArrayList _u235Nuclei = new ArrayList();
    private ArrayList _u238Nuclei = new ArrayList();
    private ArrayList _daughterNuclei = new ArrayList();
    private ArrayList _inactiveNuclei = new ArrayList();
    private ArrayList _freeNeutrons = new ArrayList();
    private Random _rand = new Random();
    private NeutronSource _neutronSource;
    private ContainmentVessel _containmentVessel;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public ChainReactionModel(NuclearPhysics2Clock clock)
    {
        _clock = clock;

        // Register as a listener to the clock.
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Reset the model.
                // TODO: JPB TBD.
            }
        });
        
        // Add the neutron source to the model.
        _neutronSource = new NeutronSource(NEUTRON_SOURCE_POS_X, NEUTRON_SOURCE_POS_Y);
        
        // Register as a listener to the neutron source so that we know when
        // new neutrons are generated.
        _neutronSource.addListener( new NeutronSource.Adapter (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the list of free particles and let
                // any listeners know that it has come into existence.
                _freeNeutrons.add( neutron );
                notifyModelElementAdded( neutron );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
        
        // Add the containment vessel to the model.
        _containmentVessel = new ContainmentVessel(INITIAL_CONTAINMENT_VESSEL_RADIUS);
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock(){
        return _clock;
    }
    
    /**
     * Get a reference to the neutron source, of which there is only one
     * in this model.
     * 
     * @return - Reference to the neutron generator model element.
     */
    public NeutronSource getNeutronSource(){
        return _neutronSource;
    }
    
    /**
     * Get a reference to the containment vessel, of which there is only one
     * in this model.
     * 
     * @return - Reference to the containment vessel model element.
     */
    public ContainmentVessel getContainmentVessel(){
        return _containmentVessel;
    }
    
    public int getNumU235Nuclei(){
        return _u235Nuclei.size();
    }
    
    public int getNumU238Nuclei(){
        return _u238Nuclei.size();
    }
    
    public ArrayList getNuclei(){
        ArrayList nucleiList = new ArrayList(_u235Nuclei.size() + _u238Nuclei.size() + _daughterNuclei.size());
        nucleiList.addAll( _u235Nuclei );
        nucleiList.addAll( _u238Nuclei );
        nucleiList.addAll( _daughterNuclei );
        nucleiList.addAll( _inactiveNuclei );
        return nucleiList;
    }
    
    /**
     * Get the percentage of U235 nuclei that have fissioned.
     */
    public double getPercentageU235Fissioned(){
        
        double totalU235Nuclei = 0;
        double fissionedU235Nuclei = 0;
        
        // First check the collection of active U235 nuclei.
        for ( Iterator iterator = _u235Nuclei.iterator(); iterator.hasNext(); ) {
            
            // Increment the total count.
            totalU235Nuclei++;
            
            Uranium235Nucleus nucleus = (Uranium235Nucleus) iterator.next();
            
            if ( nucleus.getNumNeutrons() < Uranium235Nucleus.ORIGINAL_NUM_NEUTRONS ){
                // Fission has occurred, so increment the counter.
                fissionedU235Nuclei++;
            }
        }
        
        // Now go through the daughter nuclei.
        for ( Iterator iterator = _daughterNuclei.iterator(); iterator.hasNext(); ) {
            
            AtomicNucleus nucleus = (AtomicNucleus) iterator.next();
            
            if (nucleus instanceof Uranium235Nucleus){
                // Increment the total count.
                totalU235Nuclei++;
                
                if ( nucleus.getNumNeutrons() < Uranium235Nucleus.ORIGINAL_NUM_NEUTRONS ){
                    // Fission has occurred, so increment the counter.
                    fissionedU235Nuclei++;
                }
            }
        }
        
        if (totalU235Nuclei == 0){
            // There are no U235 nuclei present, to return 0 and thereby
            // avoid any divide-by-zero issues.
            return 0;
        }
        
        return 100 * (fissionedU235Nuclei/totalU235Nuclei);
    }
        
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

    /**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        assert !_listeners.contains( listener );
        
        _listeners.add( listener );
    }
    
    /**
     * Resets the model.
     */
    public void reset(){

        // Get rid of all the existing model elements.
        int i;
        
        for (i = 0; i < _freeNeutrons.size(); i++){
            notifyModelElementRemoved( _freeNeutrons.get( i ) );
        }
        _freeNeutrons.removeAll( _freeNeutrons );
        
        for (i = 0; i < _u235Nuclei.size(); i++){
            notifyModelElementRemoved( _u235Nuclei.get( i ) );
            ((AtomicNucleus)_u235Nuclei.get( i )).removedFromModel();
        }
        _u235Nuclei.removeAll( _u235Nuclei );
        
        for (i = 0; i < _u238Nuclei.size(); i++){
            notifyModelElementRemoved( _u238Nuclei.get( i ) );
            ((AtomicNucleus)_u238Nuclei.get( i )).removedFromModel();
        }
        _u238Nuclei.removeAll( _u238Nuclei );
        
        for (i = 0; i < _daughterNuclei.size(); i++){
            notifyModelElementRemoved( _daughterNuclei.get( i ) );
            ((AtomicNucleus)_daughterNuclei.get( i )).removedFromModel();
        }
        _daughterNuclei.removeAll( _daughterNuclei );
        
        // Set ourself back to the original state, which is with a single u235
        // nucleus in the center.
        setNumU235Nuclei( 1 );
        
        // Let listeners know that things have changed.
        notifyPercentFissionedChanged();
        notifyResetOccurred();
    }
    
    /**
     * Set the number of U235 nuclei that are present in the model.  Note that
     * this can only be done before the chain reaction has been started.
     * 
     * @param numU235Nuclei
     * @return Number of this type of nuclei now present in model.
     */
    public int setNumU235Nuclei(int numU235Nuclei){
        if ( numU235Nuclei == _u235Nuclei.size() ){
            // Nothing to do - we've got what we need.
        }
        else if ( numU235Nuclei > _u235Nuclei.size() ){
            
            // We need to add some new nuclei.
            int initialArraySize = _u235Nuclei.size();
            for (int i = 0; i < numU235Nuclei - initialArraySize; i++){
                Point2D position = null;
                if (_u235Nuclei.size() == 0){
                    // This is the first nucleus, so put it at the origin.
                    position = new Point2D.Double(0, 0);
                }
                else{
                    position = findOpenNucleusLocation();
                }
                if (position == null){
                    // We were unable to find a spot for this nucleus.
                    continue;
                }
                // TODO: JPB TBD - Need to either create a different nucleus or refactor FissionOneNucleus.
                AtomicNucleus nucleus = new Uranium235Nucleus(_clock, position, 0);
                nucleus.setDynamic( false );
                _u235Nuclei.add(nucleus);
                notifyModelElementAdded( nucleus );
                nucleus.addListener( new AtomicNucleus.Adapter(){
                    public void atomicWeightChanged(AtomicNucleus nucleus, int numProtons, int numNeutrons,
                            ArrayList byProducts){
                        // Handle a potential fission event.
                        handleU235AtomicWeightChange(nucleus, numProtons, numNeutrons, byProducts);
                    }
                });
            }
        }
        else{
            // We need to remove some nuclei.  Take them from the back of the
            // list, since this leaves the nucleus at the origin for last.
            int numNucleiToRemove = _u235Nuclei.size() - numU235Nuclei;
            for (int i = 0; i < numNucleiToRemove; i++){
                if (_u235Nuclei.size() > 0){
                    Object nucleus = _u235Nuclei.get( _u235Nuclei.size() - 1 );
                    _u235Nuclei.remove( nucleus );
                    notifyModelElementRemoved( nucleus );
                }
            }
        }
        
        // This may have changed the percentage of fissioned nuclei, so send
        // an update just in case.
        notifyPercentFissionedChanged();
        
        // Return the number of U235 nuclei present.
        return _u235Nuclei.size();
    }

    /**
     * Set the number of U238 nuclei that are present in the model.  Note that
     * this can only be done before the chain reaction has been started.
     * 
     * @param numU238Nuclei
     * @return Number of this type of nuclei now present in model.
     */
    public int setNumU238Nuclei(int numU238Nuclei){
        if ( numU238Nuclei == _u238Nuclei.size() ){
            // Nothing to do - we've got what we need.
        }
        else if ( numU238Nuclei > _u238Nuclei.size() ){
            
            // We need to add some new nuclei.
            
            // We need to add some new nuclei.
            for (int i = 0; i < numU238Nuclei - _u238Nuclei.size(); i++){
                Point2D position = findOpenNucleusLocation();
                if (position == null){
                    // We were unable to find a spot for this nucleus.
                    continue;
                }
                AtomicNucleus nucleus = new Uranium238Nucleus(_clock, position);
                nucleus.setDynamic( false );
                _u238Nuclei.add(nucleus);
                notifyModelElementAdded( nucleus );
            }
        }
        else{
            // We need to remove some nuclei.  Take them from the back of the
            // list, since this leaves the nucleus at the origin for last.
            int numNucleiToRemove = _u238Nuclei.size() - numU238Nuclei;
            for (int i = 0; i < numNucleiToRemove; i++){
                if (_u238Nuclei.size() > 0){
                    Object nucleus = _u238Nuclei.get( _u238Nuclei.size() - 1 );
                    _u238Nuclei.remove( nucleus );
                    notifyModelElementRemoved( nucleus );
                }
            }
        }
        
        return _u238Nuclei.size();
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){

        // Move any free particles that exist.
        for (int i = 0; i < _freeNeutrons.size(); i++){
            Nucleon freeNucleon = (Nucleon)_freeNeutrons.get( i );
            assert freeNucleon instanceof Nucleon;
            freeNucleon.translate();

            // Check if any of the free particles have collided with a nucleus
            // and, if so, give the nucleus the opportunity to absorb the
            // neutron (and possibly fission as a result).
            boolean particleAbsorbed = false;
            int j, numNuclei;
            numNuclei = _u235Nuclei.size();
            for (j = 0; (j < numNuclei) && (particleAbsorbed == false); j++){
                AtomicNucleus nucleus = (AtomicNucleus)_u235Nuclei.get( j );
                if (freeNucleon.getPosition().distance( nucleus.getPositionReference() ) <=
                    nucleus.getDiameter() / 2)
                {
                    // The particle is within capture range - see if the nucleus can capture it.
                    particleAbsorbed = nucleus.captureParticle( freeNucleon );
                }
            }
            numNuclei = _u238Nuclei.size();
            for (j = 0; (j < numNuclei) && (particleAbsorbed == false); j++){
                AtomicNucleus nucleus = (AtomicNucleus)_u238Nuclei.get( j );
                if (freeNucleon.getPosition().distance( nucleus.getPositionReference() ) <=
                    nucleus.getDiameter() / 2)
                {
                    // The particle is within capture range - see if the nucleus can capture it.
                    particleAbsorbed = nucleus.captureParticle( freeNucleon );
                }
            }
            
            if (particleAbsorbed){
                // The particle has become part of a larger nucleus, so we
                // need to take it off the list of free particles and let the
                // view know that it has disappeared as a separate entity.
                _freeNeutrons.remove( i );
                notifyModelElementRemoved( freeNucleon );
            }
            else if ((_containmentVessel.getIsEnabled() && 
                    _containmentVessel.isPositionContained( freeNucleon.getPosition() ))){
                // This particle is contained by the containment vessel, so freeze it where it is.
                freeNucleon.setVelocity( 0, 0 );
            }
        }
        
        if (_containmentVessel.getIsEnabled()){
            
            // The containment vessel is on, so we need to freeze any
            // particles that are contained by it.

            checkContainment(_u235Nuclei);
            checkContainment(_daughterNuclei);
        }
    }
    
    /**
     * Search for a location that is not already occupied by another nucleus.
     * 
     * @return
     */
    private Point2D findOpenNucleusLocation(){
        for (int i = 0; i < 100; i++){
            // Randomly select an x & y position
            double xPos = (MAX_NUCLEUS_RANGE_X / 2) * (_rand.nextDouble() - 0.5); 
            double yPos = (MAX_NUCLEUS_RANGE_Y / 2) * (_rand.nextDouble() - 0.5);
            Point2D position = new Point2D.Double(xPos, yPos);
            
            // Check if this point is available.
            boolean pointAvailable = true;
            if ((_containmentVessel.getIsEnabled() == true) && 
                    ((Point2D.distance( xPos, yPos, 0, 0 )) > (_containmentVessel.getRadius() - CONTAINMENT_VESSEL_MARGIN))){
                pointAvailable = false;
            }
            if (NEUTRON_SOURCE_OFF_LIMITS_RECT.contains( position )){
                // Too close to the neutron source.
                pointAvailable = false;
            }
            for (int j = 0; (j < _u235Nuclei.size()) && (pointAvailable == true); j++){
                if (position.distance( ((AtomicNucleus)_u235Nuclei.get(j)).getPositionReference()) < INTER_NUCLEUS_PROXIMITRY_LIMIT){
                    // This point is taken.
                    pointAvailable = false;
                }
            }
            for (int j = 0; (j < _u238Nuclei.size()) && (pointAvailable == true); j++){
                if (position.distance( ((AtomicNucleus)_u238Nuclei.get(j)).getPositionReference()) < INTER_NUCLEUS_PROXIMITRY_LIMIT){
                    // This point is taken.
                    pointAvailable = false;
                }
            }
            
            if (pointAvailable == true){
                // We have found a usable location.  Return it.
                return position;
            }
        }
        
        // If we get to this point in the code, it means that we were unable
        // to locate a usable point.  Return null.
        return null;
    }

    /**
     * Checks an array of nuclei for coming in contact with the containment
     * vessel and "freezes" any that have.
     * 
     * @param nuclei
     */
    private void checkContainment(ArrayList nuclei){
        AtomicNucleus nuke;
        int numNuclei = nuclei.size();
        for (int i = 0; i < numNuclei; i++){
            nuke = (AtomicNucleus)nuclei.get(i);
            if ((nuke.getVelocity().getMagnitude() != 0 ) && 
                (_containmentVessel.isPositionContained( nuke.getPositionReference() ))){
                // Freeze the nucleus at the edge of the containment vessel.
                nuke.setAcceleration( ZERO_ACCELERATION );
                nuke.setVelocity( 0, 0 );
                nuke.setPosition( _containmentVessel.getNearestContainmentPoint( nuke.getPositionReference() ));
            }
        }
    }
    
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void notifyModelElementRemoved (Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void notifyModelElementAdded (Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementAdded( addedElement );
        }
    }

    /**
     * Notify listeners that the percentage of U235 that has fissioned has
     * changed.
     */
    private void notifyPercentFissionedChanged(){
        double percentU235Fissioned = getPercentageU235Fissioned();
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).percentageU235FissionedChanged(percentU235Fissioned);
        }
    }
    
    /**
     * Notify listeners that the model has been reset.
     */
    private void notifyResetOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).resetOccurred();
        }
    }
    
    /**
     * Handle a change in atomic weight signaled by a U235 nucleus, which
     * may indicate that fission has occurred.
     * 
     * @param numProtons
     * @param numNeutrons
     * @param byProducts
     */
    private void handleU235AtomicWeightChange(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
            ArrayList byProducts){
        
        if (byProducts != null){
            // There are some byproducts of this event that need to be
            // managed by this object.
            for (int i = 0; i < byProducts.size(); i++){
                Object byProduct = byProducts.get( i );
                if ((byProduct instanceof Neutron) || (byProduct instanceof Proton)){
                    // Let any listeners know that a new element has appeared
                    // separately in the model.
                    notifyModelElementAdded(byProduct);
                    
                    // Set a direction and velocity for this neutron.
                    double angle = (_rand.nextDouble() * Math.PI * 2);
                    double xVel = Math.sin( angle ) * FREED_NEUTRON_VELOCITY;
                    double yVel = Math.cos( angle ) * FREED_NEUTRON_VELOCITY;
                    ((Nucleon)byProduct).setVelocity( xVel, yVel );
                    
                    // Add this new particle to our list.
                    _freeNeutrons.add( byProduct );
                }
                else if (byProduct instanceof AtomicNucleus){
                    // Save the new daughter and let any listeners
                    // know that it exists.
                    AtomicNucleus daughterNucleus = (AtomicNucleus)byProduct;
                    notifyModelElementAdded( daughterNucleus );

                    // Set random but opposite directions for the produced
                    // nuclei.
                    double angle = (_rand.nextDouble() * Math.PI * 2);
                    double xVel = Math.sin( angle ) * INITIAL_DAUGHTER_NUCLEUS_VELOCITY;
                    double yVel = Math.cos( angle ) * INITIAL_DAUGHTER_NUCLEUS_VELOCITY;
                    double xAcc = Math.sin( angle ) * DAUGHTER_NUCLEUS_ACCELERATION;
                    double yAcc = Math.cos( angle ) * DAUGHTER_NUCLEUS_ACCELERATION;
                    nucleus.setVelocity( xVel, yVel );
                    nucleus.setAcceleration( xAcc, yAcc );
                    daughterNucleus.setVelocity( -xVel, -yVel );
                    daughterNucleus.setAcceleration( -xAcc, -yAcc );
                    
                    // Add the daughter nucleus to our collection.
                    _daughterNuclei.add( daughterNucleus );
                    
                    // Move the 'parent' nucleus to the list of daughter
                    // nuclei so that it doesn't continue to be involved in
                    // the fission detection calculations.
                    assert (nucleus instanceof Uranium235Nucleus);
                    _u235Nuclei.remove( nucleus );
                    _daughterNuclei.add( nucleus );
                    
                    // Signal any listeners that the percentage of fissioned
                    // U235 nuclei has probably changed.
                    notifyPercentFissionedChanged();
                }
                else {
                    // We should never get here, debug it if it does.
                    System.err.println("Error: Unexpected byproduct of decay event.");
                    assert false;
                }
            }
        }
        else if (nucleus instanceof Uranium238Nucleus){
            
            // This event may indicate that a U238 nucleus absorbed a neutron,
            // which means that we no longer need to check if it wants to
            // absorb any more.
            if (nucleus.getNumNeutrons() == Uranium238Nucleus.ORIGINAL_NUM_NEUTRONS + 1){
                _u238Nuclei.remove( nucleus );
                _inactiveNuclei.add( nucleus );
            }
        }
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces
    //------------------------------------------------------------------------
    
    /**
     * This listener interface allows listeners to get notified when an alpha
     * particle is added (i.e. come in to existence by separating from the
     * nucleus) or is removed (i.e. recombines with the nucleus).
     */
    public static interface Listener {
        /**
         * This informs the listener that a model element was added.
         * 
         * @param modelElement - Reference to the newly added model element.
         */
        public void modelElementAdded(Object modelElement);
        
        /**
         * This is invoked when a model element is removed from the model.
         * 
         * @param modelElement - Reference to the model element that was removed.
         */
        public void modelElementRemoved(Object modelElement);
        
        /**
         * This is invoked to signal that the model has been reset.
         * 
         */
        public void resetOccurred();
        
        /**
         * This method is called to inform listeners that a change has
         * occurred in the percentage of U235 that has fissioned.
         */
        public void percentageU235FissionedChanged(double percentU235Fissioned);
    }
    
    /**
     * Adapter for the interface described above.
     *
     * @author John Blanco
     */
    public static class Adapter implements Listener {
        public void modelElementAdded(Object modelElement){}
        public void modelElementRemoved(Object modelElement){}
        public void resetOccurred(){}
        public void percentageU235FissionedChanged(double percentU235Fissioned){}
    }
}
