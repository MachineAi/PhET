/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.ContainmentGraphic;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class MultipleNucleusFissionModule extends ChainReactionModule implements Containment.ResizeListener {

    private Containment containment;
    private ContainmentGraphic containmentGraphic;
    private long orgDelay;
    private double orgDt;

    public MultipleNucleusFissionModule( IClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleNucleusFissionModule" ), clock );

        // set the SCALE of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setPhysicalScale( 0.5 );
        super.addControlPanelElement( new MultipleNucleusFissionControlPanel( this ) );

        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( MultipleNucleusFissionModule.this.neutronToAdd != null ) {
                    MultipleNucleusFissionModule.this.addNeutron( neutronToAdd );
                    MultipleNucleusFissionModule.this.neutronToAdd = null;
                }
            }
        } );
    }

    public void activate() {
        super.activate();
        start();
    }

    public void deactivate() {
        super.deactivate();
        stop();
    }

    public void start() {
        // Add a bunch of nuclei, including one in the middle that we can fire a neutron at
        Uranium235 centralNucleus = new Uranium235( new Point2D.Double(), (NuclearPhysicsModel)getModel() );
        getModel().addModelElement( centralNucleus );
        u235Nuclei.add( centralNucleus );
        addNucleus( centralNucleus );

        // If the containment is enabled, recreate it, so it will be fully
        // displayed
        if( containment != null ) {
            setContainmentEnabled( false );
            setContainmentEnabled( true );
        }
        computeNeutronLaunchParams();
    }

    protected void computeNeutronLaunchParams() {
        // Compute how we'll fire the neutron
        if( containment != null ) {
            neutronLaunchPoint = containment.getNeutronLaunchPoint();
            neutronLaunchAngle = 0;
            neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
        }
        else {
            double bounds = 600 / getPhysicalPanel().getScale();
            neutronLaunchAngle = random.nextDouble() * Math.PI * 2;
            double x = bounds * Math.cos( neutronLaunchAngle );
            double y = bounds * Math.sin( neutronLaunchAngle );
            neutronLaunchAngle += Math.PI;
            neutronLaunchPoint = new Point2D.Double( x, y );
            neutronPath = new Line2D.Double( neutronLaunchPoint, new Point2D.Double( 0, 0 ) );
        }
    }

    public void fission( FissionProducts products ) {
        super.fission( products );
        // If the conatinment vessel is being used, make it dissovle
        if( containment != null ) {
            containment.dissolve();
        }
    }

    private void addContainment() {
        containment = new Containment( new Point2D.Double( 0, 0 ), 400 );
        containment.addResizeListener( this );
        containmentGraphic = new ContainmentGraphic( containment, getPhysicalPanel(), getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( containmentGraphic, 10 );
    }

    private void removeContainment() {
        containment = null;
        getPhysicalPanel().removeGraphic( containmentGraphic );
    }

    public void setContainmentEnabled( boolean selected ) {
        if( selected ) {
            addContainment();
            // This call will cause any nuclei that are outside the containment
            // to be removed
            containementResized( containment );
        }
        else {
            removeContainment();
        }
        computeNeutronLaunchParams();
    }

    protected Point2D.Double findLocationForNewNucleus() {
        // Determine the area in which the nucleus can be place. This depends on whether the
        // containment vessel is enabled or not.

        // Determine the model bounds represented by the current size of the apparatus panel
        Rectangle2D r = getPhysicalPanel().getBounds();
        AffineTransform atx = new AffineTransform( getPhysicalPanel().getNucleonTx() );
        AffineTransform gtx = getPhysicalPanel().getGraphicTx();
        atx.preConcatenate( gtx );
        Rectangle2D modelBounds = new Rectangle2D.Double();
        try {
            modelBounds.setFrameFromDiagonal( atx.inverseTransform( new Point2D.Double( r.getMinX(), r.getMinY() ), null ),
                                              atx.inverseTransform( new Point2D.Double( r.getMinX() + r.getWidth(), r.getMinY() + r.getHeight() ), null ) );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }

        Shape bounds = null;
        if( containment != null ) {
            bounds = containment.getShape();
        }
        else {
            bounds = modelBounds;
        }
        boolean overlapping = false;
        Point2D.Double location = new Point2D.Double();
        int attempts = 0;
        do {
            // If there is already a nucleus at (0,0), then generate a random location
            boolean centralNucleusExists = false;
            for( int i = 0; i < getNuclei().size() && !centralNucleusExists; i++ ) {
                Nucleus testNucleus = (Nucleus)getNuclei().get( i );
                if( testNucleus.getPosition().getX() == 0 && testNucleus.getPosition().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }

            double x = centralNucleusExists ? random.nextDouble() * ( modelBounds.getWidth() - 50 ) + modelBounds.getMinX() + 25 : 0;
            double y = centralNucleusExists ? random.nextDouble() * ( modelBounds.getHeight() - 50 ) + modelBounds.getMinY() + 25 : 0;
            location.setLocation( x, y );

            overlapping = false;
            for( int j = 0; j < getNuclei().size() && !overlapping; j++ ) {
                Nucleus testNucleus = (Nucleus)getNuclei().get( j );
                if( testNucleus.getPosition().distance( location ) < testNucleus.getRadius() * 3 ) {
                    overlapping = true;
                }
            }

            // todo: the hard-coded 50 here should be replaced with the radius of a Uranium nucleus
            if( location.getX() != 0 && location.getY() != 0 ) {
                overlapping = overlapping
                              || getNeutronPath().ptSegDist( location ) < 50
                              || location.distance( 0, 0 ) + 50 > bounds.getBounds2D().getWidth() / 2;
            }
            attempts++;
        } while( overlapping && attempts < s_maxPlacementAttempts );

        if( overlapping ) {
            location = null;
        }
        return location;
    }

    public void containementResized( Containment containment ) {
        Shape bounds = containment.getShape();
        ArrayList removeList = new ArrayList();
        for( int i = 0; i < nuclei.size(); i++ ) {
            Nucleus nucleus = (Nucleus)nuclei.get( i );
            if( nucleus.getPosition().distance( 0, 0 ) + 50 > bounds.getBounds2D().getWidth() / 2 ) {
                removeList.add( nucleus );
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            Nucleus nucleus = (Nucleus)removeList.get( i );
            getModel().removeModelElement( nucleus );
            // This is lazy and crude, but let's just remove from all the lists, so
            // we don't have to check types, use logic, etc.
            nuclei.remove( nucleus );
            u235Nuclei.remove( nucleus );
            u238Nuclei.remove( nucleus );
            u239Nuclei.remove( nucleus );
        }
    }

}