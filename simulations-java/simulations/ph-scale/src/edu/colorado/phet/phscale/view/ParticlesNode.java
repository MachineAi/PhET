/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ParticlesNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;
    
    private static final int DEFAULT_NEUTRAL_PARTICLES = 100;
    private static final int DEFAULT_MAX_PARTICLES = 5000;
    private static final double DEFAULT_DIAMETER = 4;
    private static final int DEFAULT_MAJORITY_TRANSPARENCY = 128; // 0-255, transparent-opaque
    private static final int DEFAULT_MINORITY_TRANSPARENCY = 255; // 0-255, transparent-opaque
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PBounds _containerBounds;
    private final PNode _particlesParent;
    private final Random _randomX, _randomY;
   
    private Double _pH; // used to watch for pH change in the liquid
    private int _numberOfParticlesAtPH7; // number of particles created when pH=7
    private int _numberOfParticlesAtPH15; // number of particles created when pH=15
    private double _diameter; // diameter of all particles, view coordinates
    private int _majorityTransparency; // transparency of majority type of particle
    private int _minorityTransparency; // transparency of minorty type of particle
    private Color _h3oColor, _ohColor; // base colors, no alpha
    private int _numberOfH3O, _numberOfOH; // number of each type of particle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public ParticlesNode( Liquid liquid, PBounds containerBounds ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _containerBounds = new PBounds( containerBounds );
        _randomX = new Random();
        _randomY = new Random();
        _pH = _liquid.getPH();
        
        _particlesParent = new PNode();
        addChild( _particlesParent );
        
        _numberOfParticlesAtPH15 = DEFAULT_MAX_PARTICLES;
        _numberOfParticlesAtPH7 = DEFAULT_NEUTRAL_PARTICLES;
        _diameter = DEFAULT_DIAMETER;
        _majorityTransparency = DEFAULT_MAJORITY_TRANSPARENCY;
        _minorityTransparency = DEFAULT_MINORITY_TRANSPARENCY;
        _h3oColor = PHScaleConstants.H3O_COLOR;
        _ohColor = PHScaleConstants.OH_COLOR;
        _numberOfH3O = 0;
        _numberOfOH = 0;
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setNumberOfParticlesAtPH15( int num ) {
        if ( num != _numberOfParticlesAtPH15 ) {
            _numberOfParticlesAtPH15 = num;
            createParticles();
        }
    }
    
    public int getNumberOfParticlesAtPH15() {
        return _numberOfParticlesAtPH15;
    }
    
    public int getNumberOfParticlesAtPH7() {
        return _numberOfParticlesAtPH7;
    }
    
    public void setNumberOfParticlesAtPH7( int num ) {
        if ( num != _numberOfParticlesAtPH7 ) {
            _numberOfParticlesAtPH7 = num;
            createParticles();
        }
    }
    
    public void setParticleDiameter( double diameter ) {
        if ( diameter != _diameter ) {
            _diameter = diameter;
            int count = _particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                ( (ParticleNode) _particlesParent.getChild( i ) ).setDiameter( diameter );
            }
        }
    }
    
    public double getParticleDiameter() {
        return _diameter;
    }
    
    public void setMajorityTransparency( int transparency ) {
        if ( transparency != _majorityTransparency ) {
            _majorityTransparency = transparency;
            setH3OColor( _h3oColor );
            setOHColor( _ohColor );
        }
    }
    
    public int getMajorityTransparency() { 
        return _majorityTransparency;
    }
    
    public void setMinorityTransparency( int transparency ) {
        if ( transparency != _minorityTransparency ) {
            _minorityTransparency = transparency;
            setH3OColor( _h3oColor );
            setOHColor( _ohColor );
        }
    }
    
    public int getMinorityTransparency() {
        return _minorityTransparency;
    }
    
    public void setH3OColor( Color color ) {
        _h3oColor = color;
        // compute color with alpha
        int alpha = ( _numberOfH3O >= _numberOfOH ? _majorityTransparency : _minorityTransparency );
        Color particleColor = ColorUtils.createColor( _h3oColor, alpha );
        // update all H3O particle nodes
        int count = _particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = _particlesParent.getChild( i );
            if ( node instanceof H3ONode ) {
                node.setPaint( particleColor );
            }
        }
    }
    
    public Color getH3OColor() {
        return _h3oColor;
    }
    
    public void setOHColor( Color color ) {
        _ohColor = color;
        // compute color with alpha
        int alpha = ( _numberOfOH >= _numberOfH3O ? _majorityTransparency : _minorityTransparency );
        Color particleColor = ColorUtils.createColor( _ohColor, alpha );
        // update all OH particle nodes
        int count = _particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = _particlesParent.getChild( i );
            if ( node instanceof OHNode ) {
                node.setPaint( particleColor );
            }
        }
    }
    
    public Color getOHColor() {
        return _ohColor;
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
            else {
                _pH = null;
                _particlesParent.removeAllChildren();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        if ( getVisible() ) {
            Double previousPH = _pH;
            _pH = _liquid.getPH();
            if ( _pH == null ) {
                deleteAllParticles();
            }
            else if ( !_pH.equals( previousPH ) ) {
                createParticles();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Particle creation
    //----------------------------------------------------------------------------
    
    /*
     * Deletes all particles.
     */
    private void deleteAllParticles() {
        _particlesParent.removeAllChildren();
    }
    
    /*
     * Creates particle nodes based on the current pH value.
     */
    private void createParticles() {
        assert( _pH != null );
        
        deleteAllParticles();
        
        final double pH = _pH.doubleValue();

        if ( pH >= ACID_PH_THRESHOLD && pH <= BASE_PH_THRESHOLD ) {
            // # particles varies logarithmically in this range
            _numberOfH3O = getNumberOfH3OParticles( pH );
            _numberOfOH = getNumberOfOHParticles( pH );
        }
        else {
            // # particles varies linearly in this range
            final double pH15 = 15;
            final double minParticles = 1;
            // N is the number of particles to add for each 1 unit of pH above or below the thresholds
            final double N = ( _numberOfParticlesAtPH15 - getNumberOfOHParticles( BASE_PH_THRESHOLD ) ) / ( pH15 - BASE_PH_THRESHOLD );
            if ( pH > BASE_PH_THRESHOLD ) {
                // strong base
                final double phDiff = pH - BASE_PH_THRESHOLD;
                _numberOfH3O = (int) Math.max( minParticles, ( getNumberOfH3OParticles( BASE_PH_THRESHOLD ) - phDiff ) );
                _numberOfOH = (int) ( getNumberOfOHParticles( BASE_PH_THRESHOLD ) + ( phDiff * N ) );
            }
            else {
                // strong acid
                final double phDiff = ACID_PH_THRESHOLD - pH;
                _numberOfH3O = (int) ( getNumberOfH3OParticles( ACID_PH_THRESHOLD ) + ( phDiff * N ) );
                _numberOfOH = (int) Math.max( minParticles, ( getNumberOfOHParticles( ACID_PH_THRESHOLD ) - phDiff ) );
            }
        }
        System.out.println( "#H3O=" + _numberOfH3O + " #OH=" + _numberOfOH );
        
        // create particles, minority species in foreground
        if ( _numberOfH3O > _numberOfOH ) {
            createH3ONodes( (int) _numberOfH3O, _majorityTransparency );
            createOHNodes( (int) _numberOfOH, _minorityTransparency );
        }
        else {
            createOHNodes( (int) _numberOfOH, _majorityTransparency );
            createH3ONodes( (int) _numberOfH3O, _minorityTransparency );
        }
    }
    
    private int getNumberOfH3OParticles( double pH ) {
        return (int) ( Liquid.getConcentrationH3O( pH ) * ( _numberOfParticlesAtPH7 / 2 ) / 1E-7 );
    }
    
    private int getNumberOfOHParticles( double pH ) {
        return (int) ( Liquid.getConcentrationOH( pH ) * ( _numberOfParticlesAtPH7 / 2 ) / 1E-7 );
    }
    
    /*
     * Creates H3O nodes at random locations.
     */
    private void createH3ONodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( _h3oColor, alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( _containerBounds, pOffset );
            ParticleNode p = new H3ONode( _diameter, color );
            p.setOffset( pOffset );
            _particlesParent.addChild( p );
        }
    }

    /*
     * Creates OH nodes at random locations.
     */
    private void createOHNodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( _ohColor, alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( _containerBounds, pOffset );
            ParticleNode p = new OHNode( _diameter, color );
            p.setOffset( pOffset );
            _particlesParent.addChild( p );
        }
    }

    /*
     * Gets a random point inside some bounds.
     */
    private void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( _randomX.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( _randomY.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /* Base class for all particle nodes */
    private static abstract class ParticleNode extends PPath {

        private Ellipse2D _ellipse;

        public ParticleNode( double diameter, Color color ) {
            super();
            _ellipse = new Ellipse2D.Double();
            setPaint( color );
            setStroke( null );
            setDiameter( diameter );
        }

        public void setDiameter( double diameter ) {
            _ellipse.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
            setPathTo( _ellipse );
        }
    }

    /* H30 particle node, marker class */
    private static class H3ONode extends ParticleNode {
        public H3ONode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* OH particle node, marker class */
    private static class OHNode extends ParticleNode {
        public OHNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

}
