/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.cck3.grabbag.GrabBagResistor;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:31:24 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Circuit {
    private ArrayList branches = new ArrayList();
    private ArrayList junctions = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private CircuitChangeListener circuitChangeListener;
    private boolean fireKirkhoffChanges = true;

    public Circuit() {
        this( new CompositeCircuitChangeListener() );
    }

    public Circuit( CircuitChangeListener circuitChangeListener ) {
        this.circuitChangeListener = circuitChangeListener;
    }

    public void addCircuitListener( CircuitListener listener ) {
        listeners.add( listener );
        //        System.out.println( "Added " + listeners.size() + "th Circuit listener = " + listener );
    }

    public int numCircuitListeners() {
        return listeners.size();
    }

    public void removeCircuitListener( CircuitListener listener ) {
        listeners.remove( listener );
        //        System.out.println( "Removed  " + listeners.size() + "th Circuit listener = " + listener );
    }

    public String toString() {
        return "Junctions=" + junctions + ", Branches=" + branches;
    }

    public void addJunction( Junction junction ) {
        if( !junctions.contains( junction ) ) {
            junctions.add( junction );
            fireJunctionAdded( junction );
        }
        else {
            //            System.out.println( "Already contained junction." );
        }
    }

    private void fireJunctionAdded( Junction junction ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionAdded( junction );
        }
    }

    public Branch[] getAdjacentBranches( Junction junction ) {
        ArrayList out = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( junction ) ) {
                out.add( branch );
            }
        }
        return (Branch[])out.toArray( new Branch[0] );
    }

    public void updateNeighbors( Junction junction ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( junction ) ) {
                branch.notifyObservers();
            }
        }
    }

    public void updateAll() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            branch.notifyObservers();
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction)junctions.get( i );
            junction.notifyObservers();
        }
    }

    public int numJunctions() {
        return junctions.size();
    }

    public Junction junctionAt( int i ) {
        return (Junction)junctions.get( i );
    }

    public boolean hasBranch( Junction a, Junction b ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( a ) && branch.hasJunction( b ) ) {
                return true;
            }
        }
        return false;
    }

    public Junction[] getNeighbors( Junction a ) {
        ArrayList n = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( a ) ) {
                n.add( branch.opposite( a ) );
            }
        }
        return (Junction[])n.toArray( new Junction[0] );
    }

    void replaceJunction( Junction old, Junction newJunction ) {
        junctions.remove( old );
        old.delete();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.getStartJunction() == old ) {
                branch.setStartJunction( newJunction );
            }
            if( branch.getEndJunction() == old ) {
                branch.setEndJunction( newJunction );
            }
        }
        fireJunctionRemoved( old );
    }

    private void fireJunctionRemoved( Junction junction ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionRemoved( junction );
        }
    }

    public boolean areNeighbors( Junction a, Junction b ) {
        if( a == b ) {
            return false;
        }
        Junction[] na = getNeighbors( a );
        return Arrays.asList( na ).contains( b );
    }

    public void addBranch( Branch component ) {
        if( component == null ) {
            throw new RuntimeException( "Null component." );
        }
        addJunction( component.getStartJunction() );
        addJunction( component.getEndJunction() );
        branches.add( component );
    }

    public void notifyNeighbors( Branch b ) {
        ArrayList alreadyNotified = new ArrayList();
        Branch[] br1 = getAdjacentBranches( b.getStartJunction() );
        Branch[] br2 = getAdjacentBranches( b.getEndJunction() );
        ArrayList all = new ArrayList();
        all.addAll( Arrays.asList( br1 ) );
        all.addAll( Arrays.asList( br2 ) );
        for( int i = 0; i < all.size(); i++ ) {
            Branch branch = (Branch)all.get( i );
            if( !alreadyNotified.contains( branch ) ) {
                alreadyNotified.add( branch );
                branch.notifyObservers();
            }
        }
    }

    public Junction[] split( Junction junction ) {
        Branch[] b = getAdjacentBranches( junction );
        Junction[] newJunctions = new Junction[b.length];
        for( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            Junction opposite = branch.opposite( junction );
            AbstractVector2D vec = new Vector2D.Double( opposite.getPosition(), junction.getPosition() );
            double curLength = vec.getMagnitude();
            double newLength = Math.abs( curLength - CCK3Module.JUNCTION_RADIUS * 1.5 );
            vec = vec.getInstanceOfMagnitude( newLength );
            Point2D desiredDst = vec.getDestination( opposite.getPosition() );
            Point2D dst = desiredDst;
            if( branch instanceof CircuitComponent ) {
                dst = junction.getPosition();
            }

            Junction newJ = new Junction( dst.getX(), dst.getY() );
            branch.replaceJunction( junction, newJ );
            addJunction( newJ );
            newJunctions[i] = newJ;

            if( branch instanceof CircuitComponent ) {
                AbstractVector2D tx = new ImmutableVector2D.Double( junction.getPosition(), desiredDst );
                Branch[] stronglyConnected = getStrongConnections( newJ );
                BranchSet bs = new BranchSet( this, stronglyConnected );
                bs.translate( tx );
            }
            else {
                updateNeighbors( newJ );
            }
        }
        remove( junction );
        fireJunctionsMoved();
        circuitChangeListener.circuitChanged();
        fireJunctionsSplit( junction, newJunctions );
        return newJunctions;
    }

    private void fireJunctionsSplit( Junction junction, Junction[] newJunctions ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsSplit( junction, newJunctions );
        }
    }

    public void remove( Junction junction ) {
        junctions.remove( junction );
        junction.delete();
        fireJunctionRemoved( junction );
    }

    public Branch[] getStrongConnections( Junction junction ) {
        ArrayList visited = new ArrayList();
        getStrongConnections( visited, junction );
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    public Branch[] getStrongConnections( Branch wrongDir, Junction junction ) {
        ArrayList visited = new ArrayList();
        if( wrongDir != null ) {
            visited.add( wrongDir );
        }
        getStrongConnections( visited, junction );
        if( wrongDir != null ) {
            visited.remove( wrongDir );
        }
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    private void getStrongConnections( ArrayList visited, Junction junction ) {
        Branch[] out = getAdjacentBranches( junction );
        for( int i = 0; i < out.length; i++ ) {
            Branch branch = out[i];
            Junction opposite = branch.opposite( junction );
            if( !visited.contains( branch ) ) {
                if( branch instanceof CircuitComponent ) {
                    visited.add( branch );
                    getStrongConnections( visited, opposite );
                }//Wires end the connectivity.
            }
        }
    }

    public Branch[] getConnectedSubgraph( Junction junction ) {
        ArrayList visited = new ArrayList();
        getConnectedSubgraph( visited, junction );
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    private void getConnectedSubgraph( ArrayList visited, Junction junction ) {
        Branch[] adj = getAdjacentBranches( junction );
        for( int i = 0; i < adj.length; i++ ) {
            Branch branch = adj[i];
            Junction opposite = branch.opposite( junction );
            if( !visited.contains( branch ) ) {
                visited.add( branch );
                getConnectedSubgraph( visited, opposite );
            }
        }
    }

    public void fireJunctionsMoved() {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsMoved();
        }
    }

    public int numBranches() {
        return branches.size();
    }

    public int indexOf( Branch branch ) {
        return branches.indexOf( branch );
    }

    public int indexOf( Junction junction ) {
        return junctions.indexOf( junction );
    }

    public Branch branchAt( int i ) {
        return (Branch)branches.get( i );
    }

    public void remove( Branch branch ) {
        branches.remove( branch );
        branch.delete();
        fireBranchRemoved( branch );
        fireKirkhoffChanged();
    }

    public void setFireKirkhoffChanges( boolean fireKirkhoffChanges ) {
        this.fireKirkhoffChanges = fireKirkhoffChanges;
    }

    private void fireBranchRemoved( Branch branch ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchRemoved( branch );
        }
    }

    public void fireKirkhoffChanged() {
        if( fireKirkhoffChanges ) {
            circuitChangeListener.circuitChanged();
        }
    }

    public Branch[] getBranches() {
        return (Branch[])branches.toArray( new Branch[0] );
    }

    private void translate( Junction[] j, AbstractVector2D vec ) {
        for( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            junction.translate( vec.getX(), vec.getY() );
        }
    }

    public void translate( Branch[] branchs, AbstractVector2D vec ) {
        Junction[] j = getJunctions( branchs );
        translate( j, vec );
        for( int i = 0; i < branchs.length; i++ ) {
            Branch b = branchs[i];
            b.notifyObservers();
        }
    }

    public Junction[] getJunctions() {
        return (Junction[])junctions.toArray( new Junction[0] );
    }

    public static Junction[] getJunctions( Branch[] branchs ) {
        ArrayList list = new ArrayList();
        for( int i = 0; i < branchs.length; i++ ) {
            Branch branch = branchs[i];
            if( !list.contains( branch.getStartJunction() ) ) {
                list.add( branch.getStartJunction() );
            }
            if( !list.contains( branch.getEndJunction() ) ) {
                list.add( branch.getEndJunction() );
            }
        }
        return (Junction[])list.toArray( new Junction[0] );
    }

    public void fireBranchesMoved( Branch[] moved ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchesMoved( moved );
        }
    }

    public boolean hasJunction( Junction junction ) {
        return junctions.contains( junction );
    }

    public double getVoltage( VoltageCalculation.Connection a, VoltageCalculation.Connection b ) {
        VoltageCalculation vc = new VoltageCalculation( this );
        return vc.getVoltage( a, b );
    }


    public static Circuit parseXML( IXMLElement xml, CircuitChangeListener kl, CCK3Module module ) {
        Circuit cir = new Circuit( kl );
        for( int i = 0; i < xml.getChildrenCount(); i++ ) {
            IXMLElement child = xml.getChildAtIndex( i );
            //            int index = child.getAttribute( "index", -1 );
            if( child.getName().equals( "junction" ) ) {
                String xStr = child.getAttribute( "x", "0.0" );
                String yStr = child.getAttribute( "y", "0.0" );
                double x = Double.parseDouble( xStr );
                double y = Double.parseDouble( yStr );
                Junction j = new Junction( x, y );
                cir.addJunction( j );
            }
            else if( child.getName().equals( "branch" ) ) {
                int startIndex = child.getAttribute( "startJunction", -1 );
                int endIndex = child.getAttribute( "endJunction", -1 );
                Junction startJunction = cir.junctionAt( startIndex ); //this only works if everything stays in order.
                Junction endJunction = cir.junctionAt( endIndex );
                Branch branch = toBranch( module, kl, startJunction, endJunction, child );
                cir.addBranch( branch );
            }
        }
        return cir;
    }

    public static Branch toBranch( CCK3Module module, CircuitChangeListener kl, Junction startJunction, Junction endJunction, IXMLElement xml ) {
        String type = xml.getAttribute( "type", "null" );
        if( type.equals( Branch.class.getName() ) ) {
            Branch branch = new Branch( kl, startJunction, endJunction );
            return branch;
        }
        double length = Double.parseDouble( xml.getAttribute( "length", "-1" ) );
        double height = Double.parseDouble( xml.getAttribute( "height", "-1" ) );

        if( type.equals( Resistor.class.getName() ) ) {
            Resistor res = new Resistor( kl, startJunction, endJunction, length, height );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            res.setResistance( val );
            return res;
        }
        else if( type.equals( ACVoltageSource.class.getName() ) ) {
            double amplitude = Double.parseDouble( xml.getAttribute( "amplitude", Double.NaN + "" ) );
            double freq = Double.parseDouble( xml.getAttribute( "frequency", Double.NaN + "" ) );
            double internalResistance = Double.parseDouble( xml.getAttribute( "internalResistance", Double.NaN + "" ) );
            ACVoltageSource voltageSource = new ACVoltageSource( kl, startJunction, endJunction, length, height, CCK3Module.MIN_RESISTANCE, module.isInternalResistanceOn() );
            voltageSource.setInternalResistance( internalResistance );
            voltageSource.setAmplitude( amplitude );
            voltageSource.setFrequency( freq );
            return voltageSource;
        }
        else if( type.equals( Capacitor.class.getName() ) ) {
            Capacitor capacitor = new Capacitor( kl, startJunction, endJunction, length, height );
            capacitor.setVoltageDrop( Double.parseDouble( xml.getAttribute( "voltage", Double.NaN + "" ) ) );
            capacitor.setCurrent( Double.parseDouble( xml.getAttribute( "current", Double.NaN + "" ) ) );
            capacitor.setCapacitance( Double.parseDouble( xml.getAttribute( "capacitance", Double.NaN + "" ) ) );
            return capacitor;
        }
        else if( type.equals( Battery.class.getName() ) ) {
//            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
//            double resistance = Double.parseDouble( resVal );

            double internalResistance = Double.parseDouble( xml.getAttribute( "internalResistance", Double.NaN + "" ) );
            //            String internalResistanceOnStr = xml.getAttribute( "connectAtRight", "false" );
            //            boolean internalResistanceOn = internalResistanceOnStr != null && internalResistanceOnStr.equals( new Boolean( true ).toString() );
            Battery batt = new Battery( kl, startJunction, endJunction, length, height, CCK3Module.MIN_RESISTANCE, module.isInternalResistanceOn() );
            batt.setInternalResistance( internalResistance );
            String voltVal = xml.getAttribute( "voltage", Double.NaN + "" );
            double val = Double.parseDouble( voltVal );
            batt.setVoltageDrop( val );
            return batt;
        }
        else if( type.equals( Switch.class.getName() ) ) {
            String closedVal = xml.getAttribute( "closed", "false" );
            boolean closed = closedVal != null && closedVal.equals( new Boolean( true ).toString() );
            //            boolean closed = Boolean.getBoolean( closedVal );
            return new Switch( kl, startJunction, endJunction, closed, length, height );
        }
        else if( type.equals( Bulb.class.getName() ) ) {
            String widthStr = xml.getAttribute( "width", Double.NaN + "" );
            double width = Double.parseDouble( widthStr );
            boolean schematic = !module.getCircuitGraphic().isLifelike();
            Bulb bulb = new Bulb( kl, startJunction, endJunction, width, length, height, schematic );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            bulb.setResistance( val );
            String connectAtRightStr = xml.getAttribute( "connectAtRight", "true" );
            boolean connectAtRight = connectAtRightStr != null && connectAtRightStr.equals( new Boolean( true ).toString() );
            bulb.setConnectAtRightXML( connectAtRight );
            return bulb;
        }
        else if( type.equals( SeriesAmmeter.class.getName() ) ) {
            return (Branch)new SeriesAmmeter( kl, startJunction, endJunction, length, height );
        }
        else if( type.equals( GrabBagResistor.class.getName() ) ) {
            Resistor res = new Resistor( kl, startJunction, endJunction, length, height );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            res.setResistance( val );
            return res;
        }
        else if( type.equals( Inductor.class.getName() ) ) {
            Inductor inductor = new Inductor( kl, startJunction, endJunction, length, height );
            inductor.setVoltageDrop( Double.parseDouble( xml.getAttribute( "voltage", Double.NaN + "" ) ) );
            inductor.setCurrent( Double.parseDouble( xml.getAttribute( "current", Double.NaN + "" ) ) );
            inductor.setInductance( Double.parseDouble( xml.getAttribute( "inductance", Double.NaN + "" ) ) );
            return inductor;
        }
        return null;
    }

    public XMLElement toXML() {
        XMLElement xe = new XMLElement( "circuit" );
        for( int i = 0; i < numJunctions(); i++ ) {
            Junction j = junctionAt( i );
            XMLElement junctionElement = new XMLElement( "junction" );
            junctionElement.setAttribute( "index", i + "" );
            junctionElement.setAttribute( "x", j.getPosition().getX() + "" );
            junctionElement.setAttribute( "y", j.getPosition().getY() + "" );
            xe.addChild( junctionElement );
        }
        for( int i = 0; i < numBranches(); i++ ) {
            Branch branch = branchAt( i );
            XMLElement branchElement = new XMLElement( "branch" );
            Junction startJ = branch.getStartJunction();
            Junction endJ = branch.getEndJunction();
            int startIndex = indexOf( startJ );
            int endIndex = indexOf( endJ );
            branchElement.setAttribute( "index", "" + i );
            branchElement.setAttribute( "type", branch.getClass().getName() );
            branchElement.setAttribute( "startJunction", startIndex + "" );
            branchElement.setAttribute( "endJunction", endIndex + "" );
            if( branch instanceof CircuitComponent ) {
                CircuitComponent cc = (CircuitComponent)branch;
                branchElement.setAttribute( "length", cc.getLength() + "" );
                branchElement.setAttribute( "height", cc.getHeight() + "" );
            }
            if( branch instanceof ACVoltageSource ) {
                ACVoltageSource batt = (ACVoltageSource)branch;
                branchElement.setAttribute( "amplitude", batt.getAmplitude() + "" );
                branchElement.setAttribute( "frequency", batt.getFrequency() + "" );
                branchElement.setAttribute( "internalResistance", batt.getInteralResistance() + "" );
            }
            else if( branch instanceof Battery ) {
                Battery batt = (Battery)branch;
                branchElement.setAttribute( "voltage", branch.getVoltageDrop() + "" );
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
                branchElement.setAttribute( "internalResistance", batt.getInteralResistance() + "" );
            }
            else if( branch instanceof Resistor ) {
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
            }
            else if( branch instanceof Bulb ) {
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
                Bulb bulb = (Bulb)branch;
                branchElement.setAttribute( "width", bulb.getWidth() + "" );
                branchElement.setAttribute( "length", branch.getStartJunction().getDistance( branch.getEndJunction() ) + "" );
                branchElement.setAttribute( "schematic", bulb.isSchematic() + "" );
                branchElement.setAttribute( "connectAtRight", bulb.isConnectAtRight() + "" );
            }
            else if( branch instanceof Switch ) {
                Switch sw = (Switch)branch;
                branchElement.setAttribute( "closed", sw.isClosed() + "" );
            }
            else if( branch instanceof SeriesAmmeter ) {
            }
            else if( branch instanceof Capacitor ) {
                Capacitor cap = (Capacitor)branch;
                branchElement.setAttribute( "capacitance", cap.getCapacitance() + "" );
                branchElement.setAttribute( "voltage", cap.getVoltageDrop() + "" );
                branchElement.setAttribute( "current", cap.getCurrent() + "" );
            }
            else if( branch instanceof Inductor ) {
                Inductor ind = (Inductor)branch;
                branchElement.setAttribute( "inductance", ind.getInductance() + "" );
                branchElement.setAttribute( "voltage", ind.getVoltageDrop() + "" );
                branchElement.setAttribute( "current", ind.getCurrent() + "" );
            }
            xe.addChild( branchElement );
        }
        try {
            new XMLWriter( System.out ).write( xe );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return xe;
    }

    public void setSelection( Branch branch ) {
        clearSelection();
        branch.setSelected( true );
    }

    public void setSelection( Junction junction ) {
        clearSelection();
        junction.setSelected( true );
    }

    public void clearSelection() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch1 = (Branch)branches.get( i );
            branch1.setSelected( false );
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction1 = (Junction)junctions.get( i );
            junction1.setSelected( false );
        }
    }

    public Branch[] getSelectedBranches() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Branch[])sel.toArray( new Branch[0] );
    }

    public Junction[] getSelectedJunctions() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction branch = (Junction)junctions.get( i );
            if( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Junction[])sel.toArray( new Junction[0] );
    }

    public void selectAll() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            branch.setSelected( true );
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction)junctions.get( i );
            junction.setSelected( true );
        }
    }

    public void fireJunctionsCollapsed( Junction j1, Junction j2, Junction replacement ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsConnected( j1, j2, replacement );
        }
    }

    public CircuitListener[] getCircuitListeners() {
        return (CircuitListener[])listeners.toArray( new CircuitListener[0] );
    }

    public void moveToFirst( Junction junction ) {
        junctions.remove( junction );
        junctions.add( 0, junction );
    }

    public CircuitChangeListener getKirkhoffListener() {
        return circuitChangeListener;
    }

    public boolean isDynamic() {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                return true;
            }
        }
        return false;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch)branchAt( i );
                b.stepInTime( dt );
            }
        }
    }

    public void resetDynamics() {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch)branchAt( i );
                b.resetDynamics();
            }
        }
    }

    public void setTime( double time ) {
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch)branchAt( i );
                b.setTime( time );
            }
        }
    }

    public int getInductorCount() {
        int sum = 0;
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch instanceof Inductor ) {
                sum++;
            }
        }
        return sum;
    }

    public int getCapacitorCount() {
        int sum = 0;
        for( int i = 0; i < numBranches(); i++ ) {
            if( branchAt( i ) instanceof Capacitor ) {
                sum++;
            }
        }
        return sum;
    }
}
