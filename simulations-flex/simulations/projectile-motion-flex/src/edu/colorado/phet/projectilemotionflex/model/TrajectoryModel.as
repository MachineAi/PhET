/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/16/12
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.projectilemotionflex.model {
import edu.colorado.phet.flexcommon.AboutDialog;
import edu.colorado.phet.projectilemotionflex.view.MainView;

import flash.events.TimerEvent;

import flash.utils.Timer;
import flash.utils.getTimer;

//model of projectile motion, including air resistance
public class TrajectoryModel {

    public var views_arr: Array;     //views associated with this model
    public var mainView: MainView;
    private var stageH: Number;
    private var stageW: Number;
    private var g: Number;          //acceleration of gravity, all units are SI
    private var altitude: Number;   //altitude above sea level in meters
    private var rho: Number;        //density of air in kg/m^3.  At sea level, rho = 1.6 m/s^3

    private var _airResistance: Boolean;  //true if air resistance is on
    private var _xP: Number;        //current x- and y_coords of position of projectile in meters
    private var _yP: Number;
    private var _xP0: Number;       //x- and y- coordinates of initial position of projectile
    private var _yP0: Number;       //relative to origin, which is at ground level (y = 0)
    private var vX: Number;         //x- and y-coords of velocity of projectile
    private var vY: Number;
    private var v: Number;          //current speed of projectile
    private var aX: Number;         //x- and y-components of acceleration
    private var aY: Number;
    private var _vX0: Number;       //x- and y-components of initial velocity
    private var _vY0: Number;
    private var _v0: Number;        //initial speed of projectile
    private var _mass: Number;        //mass of current projectile in kg
    private var _diameter: Number;    //diameter of curent projectile in kg
    private var dragCoefficient: Number;      //used in drag calc, when air resistance is on
    private var B: Number;              //drag acceleration ~ -B*v*v
    private var _angleInDeg: Number;    //angle of cannon barrel in degrees, measured CCW from horizontal
    private var _theta: Number;         //initial angle of projectile, in radians, measured CCW from horizontal
    private var _t: Number;             //time in seconds, projectile fired at t = 0
    public var startTime: Number;       //real time in seconds that the projectile was fired, from flash.utils.getTimer() 
    public var previousTime: Number;    //previous real time in seconds that getTimer() was called.
    public var elapsedTime: Number;     //real elapsed time in seconds since previous call to getTimer()
    private var _inFlight: Boolean;     //true if projectile is in flight
    private var stepsPerFrame: int;     //number of algorithm steps between screen/view updates
    private var frameCounter: int;      //counts algorithm steps between view updates
    private var _tRate:Number;          //Normal time rate: tRate = 1;
    private var dt: Number;             //time step for trajectory algorithm, all times in seconds

    private var trajectoryTimer: Timer;	        //millisecond timer



    public function TrajectoryModel( mainView: MainView ) {
        this.mainView = mainView;
        this.views_arr = new Array();
        this.stageW = this.mainView.stageW;
        this.stageH = this.mainView.stageH;
        this.initialize();
    }

    private function initialize():void{
        this.g = 9.8;            //acceleration of gravity in m/s^2; all units in SI
        this._xP = 0;            //origin is near (but not at) lower left corner of stage
        this._yP = 0;
        this._xP0 = 0;
        this._yP0 = 0;
        this.vX = 0;
        this.vY = 0;
        this.v = Math.sqrt( vX*vX + vY*vY );
        this._v0 = 18;
        this._angleInDeg = 45;
        this._vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        this._vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
        this._t = 0;
        this._inFlight = false;
        this._airResistance = false;
        this.rho = 1.6;
        this.mass = 1;
        this.diameter = 0.1;
        this.dragCoefficient = 1;
        this.setDragFactor();
        this.dt = 0.01;
        this._tRate = 1;
        this.stepsPerFrame = 2;
        this.frameCounter = 0;
        this.trajectoryTimer = new Timer( dt * 1000 );   //argument of Timer constructor is time step in ms
        this.trajectoryTimer.addEventListener( TimerEvent.TIMER, stepForward );
        this.updateViews();
    }

    private function setDragFactor():void{
        var area: Number = Math.PI*diameter*diameter/4;
        B = dragCoefficient*rho*area/mass;
    }

    public function fireCannon():void{
        _xP = xP0;
        _yP = yP0;
        vX = _vX0;
        vY = _vY0;
        v = Math.sqrt( vX*vX + vY*vY );
        this._t = 0;
        this.startTime = getTimer()/1000;     //getTimer() returns time in milliseconds
        this.previousTime = startTime;
        trajectoryTimer.start();
    }

    //time-based animation
    private function stepForward( evt: TimerEvent ):void{
        var currentTime:Number = getTimer()/1000;
        elapsedTime = currentTime - previousTime;
        previousTime = currentTime;
        if(elapsedTime > 2*dt){    //if cpu can't keep up, revert to frame-based animation
            elapsedTime = dt;
        }
        _t += elapsedTime;
        frameCounter += 1;
        if( !_airResistance ){
            aX = 0;
            aY = -g;
        }else{       //if air resistance on
            aX = - B*vX*v;
            aY = -g - B*vY*v;
        }
        _xP += vX * dt + (0.5) * aX * dt*dt;
        _yP += vY * dt + (0.5) * aY * dt*dt;
        vX += aX * dt;
        vY += aY * dt;
        v = Math.sqrt( vX*vX + vY*vY );

        if( _yP <= 0 ){       //stop when projectile hits the ground (y = 0)
            //must first backtrack to exact moment when y = 0
            var vY0: Number = -Math.sqrt( vY*vY - 2*aY*_yP );   //vY at y = 0, assumes aY = constant
            var delT: Number;  //time elapsed from y = 0 to y = yFinal
            if( aY != 0 ){
                delT = ( vY - vY0 ) / aY;
            }else{
                delT = _yP / vY ;
            }
            _t -= delT;
            _yP = 0;
            var vX0: Number = vX - aX*delT;
            _xP = _xP - vX0*delT - (0.5)*aX*delT*delT;
            _inFlight = false;
            trajectoryTimer.stop();
        }//end if (_yP < 0 )

        if( frameCounter > stepsPerFrame ){
            frameCounter = 0;
            this.updateViews();
            this._inFlight = true;
            //trace( "Trajectory Model:  y = " + this._yP );
        }

    }//stepForward()

    public function registerView( view: Object ): void {
        this.views_arr.push( view );
    }

    public function unregisterView( view: Object ):void{
        var indexLocation:int = -1;
        indexLocation = this.views_arr.indexOf( view );
        if( indexLocation != -1 ){
            this.views_arr.splice( indexLocation, 1 )
        }
    }

    public function updateViews(): void {
        for(var i:int = 0; i < this.views_arr.length; i++){
            this.views_arr[ i ].update();
        }
    }//end updateView()

    //SETTERS and GETTERS
    public function set xP0( xP0: Number ):void{
        this._xP0 = xP0;
        this.updateViews();
    }

    public function get xP0():Number{
        return this._xP0;
    }

    public function set yP0( yP0: Number ):void{
        this._yP0 = yP0;
        this.updateViews();
    }

    public function get yP0():Number{
        return this._yP0;
    }

    public function get xP(): Number{
        return this._xP;
    }

    public function get yP(): Number{
        return this._yP;
    }

    public function get t(): Number{
        return this._t;
    }

    public function get tRate():Number {
        return _tRate;
    }

    public function set tRate(value:Number):void {
        _tRate = value;
    }

    public function get angleInDeg():Number {
        return _angleInDeg;
    }

    public function set angleInDeg(value:Number):void {
        _angleInDeg = value;
        _vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        _vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
        this.updateViews();
    }

    public function get v0():Number {
        return _v0;
    }

    public function set v0( value:Number ):void {
        _v0 = value;
        _vX0 = v0*Math.cos( angleInDeg*Math.PI/180 );
        _vY0 = v0*Math.sin( angleInDeg*Math.PI/180 );
    }

    public function get inFlight():Boolean {
        return _inFlight;
    }

    public function get airResistance():Boolean {
        return _airResistance;
    }

    public function set airResistance(value:Boolean):void {
        _airResistance = value;
    }

    public function get mass():Number {
        return _mass;
    }

    public function set mass(value:Number):void {
        _mass = value;
    }

    public function get diameter():Number {
        return _diameter;
    }

    public function set diameter(value:Number):void {
        _diameter = value;
    }
}//end class
}//end package
