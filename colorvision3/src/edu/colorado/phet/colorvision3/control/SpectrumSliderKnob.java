/* SpectrumSliderKnob.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * SpectrumSliderKnob is the knob on a SpectrumSlider.
 * Note that the knob's origin is at the upper-left corner of its 
 * bounding box, not at the knob's tip.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$ $Name$
 */
public class SpectrumSliderKnob extends PhetShapeGraphic
{
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

  // Location, upper-left corner of the bounding box.
  private Point _location;
  
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
   * Sole constructor.
   * Creates a white knob with a black border, located at (0,0).
   * 
   * @param component the parent Component
   */
  public SpectrumSliderKnob( Component component )
  {    
    super( component, null, null );

    //  Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );
    
    super.setPaint( Color.WHITE );
    super.setStroke( new BasicStroke( 1f ) );
    super.setBorderColor( Color.BLACK );
    
    GeneralPath path = new GeneralPath();
    path.moveTo(  10,  0 );
    path.lineTo(   0, 10 );
    path.lineTo(   0, 30 );
    path.lineTo(  20, 30 );
    path.lineTo(  20, 10 );
    path.closePath();
    super.setShape( path );
    
    setLocation( 0, 0 );
  }
  
	//----------------------------------------------------------------------------
	// Accessors
  //----------------------------------------------------------------------------

  /**
   * Sets the knob's location.
   * 
   * @param location the location
   */
  public void setLocation( Point location )
  { 
    if ( _location != null )
    {
      super.translate( -_location.x, -_location.y );
    }
    _location = location;
    super.translate( location.x, location.y );
  }
  
  /**
   * Convenience method for setting the knob's location.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public void setLocation( int x, int y )
  {
    this.setLocation( new Point( x, y ) );
  }
  
  /**
   * Gets the knob's location.
   */
  public Point getLocation()
  {
    return _location;
  }

}


/* end of file */