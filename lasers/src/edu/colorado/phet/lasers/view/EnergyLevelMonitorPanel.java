/**
 * Class: ThreeEnergyLevelPanel
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.HighEnergyState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class EnergyLevelMonitorPanel extends MonitorPanel {

    private int atomDiam = 14;

    private double panelWidth = 400;
    private double panelHeight = 100;

    private double groundLevelLineOriginX = 30;
    private double groundLevelLineLength = panelWidth - 60;

    private double highLevelLineOriginX = 80;
    private double highLevelLineLength = panelWidth / 2 - highLevelLineOriginX;

    private double middleLevelLineOriginX = highLevelLineOriginX + highLevelLineLength;
    private double middleLevelLineLength = panelWidth - ( highLevelLineOriginX + highLevelLineLength ) - 20;


    private EnergyLevelGraphic highLevelLine;
    private EnergyLevelGraphic middleLevelLine;
    private EnergyLevelGraphic groundLevelLine;

    private LaserModel model;

    /**
     *
     */
    public EnergyLevelMonitorPanel( LaserModel model ) {
        highLevelLine = new EnergyLevelGraphic( this, HighEnergyState.instance(),
                                                Color.blue, highLevelLineOriginX, highLevelLineLength );
        middleLevelLine = new EnergyLevelGraphic( this, MiddleEnergyState.instance(),
                                                  Color.red, middleLevelLineOriginX, middleLevelLineLength );
        groundLevelLine = new EnergyLevelGraphic( this, GroundState.instance(),
                                                  Color.black, groundLevelLineOriginX, groundLevelLineLength );
        this.addGraphic( highLevelLine );
        this.addGraphic( middleLevelLine );
        this.addGraphic( groundLevelLine );
        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );

        model.addObserver( this );
        this.model = model;

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                highLevelLine.update();
                middleLevelLine.update();
                groundLevelLine.update();
            }
        } );
    }

    public void setNumLevels( int numLevels ) {
        switch( numLevels ) {
            case 2:
                panelHeight = 100;
                break;
            case 3:
                panelHeight = 200;
                break;
            default:
                throw new RuntimeException( "Number of levels out of range" );
        }
        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );
        revalidate();
        repaint();
    }


    /**
     * @param graphics
     */
    protected synchronized void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        // Draw ground level atoms
        g2.setColor( Color.gray );
        for( int i = 0; i < numGroundLevel; i++ ) {
            g2.fillArc( (int)( groundLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                        (int)( groundLevelLine.getPosition().getY() - atomDiam ),
                        atomDiam,
                        atomDiam,
                        0, 360 );
        }

        // Draw middle level atoms
        g2.setColor( Color.red );
        for( int i = 0; i < numMiddleLevel; i++ ) {
            g2.fillArc( (int)( middleLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                        (int)( middleLevelLine.getPosition().getY() - atomDiam ),
                        atomDiam,
                        atomDiam,
                        0, 360 );
        }

        // Draw high level atoms
        g2.setColor( Color.blue );
        for( int i = 0; i < numHighLevel; i++ ) {
            g2.fillArc( (int)( highLevelLine.getPosition().getX() + ( atomDiam * i ) ),
                        (int)( highLevelLine.getPosition().getY() - atomDiam ),
                        atomDiam,
                        atomDiam,
                        0, 360 );
        }

        gs.restoreGraphics();
    }

    public void update() {
        numGroundLevel = model.getNumGroundStateAtoms();
        numMiddleLevel = model.getNumMiddleStateAtoms();
        numHighLevel = model.getNumHighStateAtoms();
        this.invalidate();
        this.repaint();
    }

}
