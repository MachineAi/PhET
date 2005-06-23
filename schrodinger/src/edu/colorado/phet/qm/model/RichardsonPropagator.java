package edu.colorado.phet.qm.model;


/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

/**
 * *****************************************************
 */

public class RichardsonPropagator implements Propagator {
    private double simulationTime;

    private double deltaTime;
    private int timeStep;

    private BoundaryCondition boundaryCondition;
    private Potential potential;

    private double hbar, mass, epsilon;
    private Complex alpha;
    private Complex beta;
    private Complex[][] betaeven;
    private Complex[][] betaodd;

    public Wavefunction copy;

    public RichardsonPropagator( double TAU, BoundaryCondition boundaryCondition, Potential potential ) {
        this.deltaTime = TAU;
        this.boundaryCondition = boundaryCondition;
        this.potential = potential;
        simulationTime = 0.0;
        timeStep = 0;
        hbar = 1;
        mass = 1;//0.0020;

        deltaTime = 0.8 * mass / hbar;
        betaeven = new Complex[0][0];
        betaodd = new Complex[0][0];
        update();
    }

    private void update() {

        epsilon = toEpsilon( deltaTime );

        alpha = createAlpha();
        beta = createBeta();
        for( int i = 0; i < betaeven.length; i++ ) {
            for( int j = 0; j < betaeven[i].length; j++ ) {
                betaeven[i][j] = new Complex();
                betaodd[i][j] = new Complex();
                if( ( i + j ) % 2 == 0 ) {
                    betaeven[i][j] = beta;
                }
                else {
                    betaodd[i][j] = beta;
                }
            }
        }
//        System.out.println( "deltaTime= " + deltaTime );
//        System.out.println( "epsilon = " + epsilon );
//        System.out.println( "alpha = " + alpha );
//        System.out.println( "beta = " + beta );
    }

    protected Complex createAlpha() {
        return new Complex( ( 1 + Math.cos( epsilon ) ) / 2.0, -Math.sin( epsilon ) / 2.0 );//from the paper
    }

    protected Complex createBeta() {
        return new Complex( ( 1 - Math.cos( epsilon ) ) / 2.0, Math.sin( epsilon ) / 2.0 );
    }

    private double toEpsilon( double dt ) {
        return hbar * dt / ( mass );
    }

    public void propagate( Wavefunction w ) {
        int nx = w.getWidth();
        if( betaeven.length != w.getWidth() ) {
            betaeven = new Complex[nx][nx];
            betaodd = new Complex[nx][nx];

            update();
        }

        prop2D( w );
        simulationTime += deltaTime;
        timeStep++;
    }

    protected void prop2D( Wavefunction w ) {
        copy = new Wavefunction( w.getWidth(), w.getHeight() );
        applyPotential( w );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
        stepIt( w, 1, 0 );
        stepIt( w, -1, 0 );
    }

    Complex aTemp = new Complex();
    Complex bTemp = new Complex();
    Complex cTemp = new Complex();

    protected void stepIt( Wavefunction w, int dx, int dy ) {
        w.copyTo( copy );
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                stepIt( w, i, j, dx, dy );
            }
        }
        for( int i = 0; i < w.getWidth(); i++ ) {
            stepItConstrained( w, i, 0, dx, dy );
            stepItConstrained( w, i, w.getHeight() - 1, dx, dy );
        }
        for( int j = 1; j < w.getHeight(); j++ ) {
            stepItConstrained( w, 0, j, dx, dy );
            stepItConstrained( w, w.getWidth() - 1, j, dx, dy );
        }
    }

    private void stepItConstrained( Wavefunction w, int i, int j, int dx, int dy ) {
        int nxPlus = ( i + dx + w.getWidth() ) % w.getWidth();
        int nyPlus = ( j + dy + w.getHeight() ) % w.getHeight();

        int nxMinus = ( i - dx + w.getWidth() ) % w.getWidth();
        int nyMinus = ( j - dy + w.getHeight() ) % w.getHeight();

        aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
        bTemp.setToProduct( betaeven[i][j], copy.valueAt( nxPlus, nyPlus ) );
        cTemp.setToProduct( betaodd[i][j], copy.valueAt( nxMinus, nyMinus ) );
        w.valueAt( i, j ).setToSum( aTemp, bTemp, cTemp );
    }

    private void stepIt( Wavefunction w, int i, int j, int dx, int dy ) {
        aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
        bTemp.setToProduct( betaeven[i][j], copy.valueAt( i + dx, j + dy ) );
        cTemp.setToProduct( betaodd[i][j], copy.valueAt( i - dx, j - dy ) );
        w.valueAt( i, j ).setToSum( aTemp, bTemp, cTemp );
    }

    protected void applyPotential( Wavefunction w ) {
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                double pot = potential.getPotential( i, j, timeStep );
                Complex val = new Complex( Math.cos( pot * deltaTime / hbar ), -Math.sin( pot * deltaTime / hbar ) );
                w.setValue( i, j, w.valueAt( i, j ).times( val ) );
            }
        }
    }

    public void setDeltaTime( double deltaTime ) {
        this.deltaTime = deltaTime;
        update();
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public double getEpsilon() {
        return epsilon;
    }
}