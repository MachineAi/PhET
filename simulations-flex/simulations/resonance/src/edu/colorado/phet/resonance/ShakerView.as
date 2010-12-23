package edu.colorado.phet.resonance {
import flash.display.*;
import flash.events.*;
import flash.filters.*;
import flash.geom.*;

public class ShakerView extends Sprite {

    public var myMainView: MainView;		//MainView
    private var model: ShakerModel;			//model of shaker bar system
    private var maxNbrResonators: int;		//maximum number or resonators
    private var nbrResonators: int;			//nbr of mass-spring systems that are displayed on stage
    private var resonatorView_arr: Array;  	//views of mass spring systems
    private var bar: Sprite;				//view of shaker bar
    private var base: Sprite;				//view of base with controls
    private var springHolder:Sprite;        //invisible holder for resonators
    private var pixPerMeter: Number;		//scale: number of pixels in 1 meter
    private var barPixPerResonator: Number; //number of pixels along bar per Resonator
    private var onOffButton: NiceButton2; 	//turn shaker on or off
    private var onLight: Sprite;			//little red light that comes on when on button pushed.
    private var glow: GlowFilter;			//glowing light bulb for better visibility
    private var fKnob: RotaryKnob;			//frequency knob
    private var hzPerTurn: Number;			//calibration of frequency knob = 2 Hz per rev
    private var ASlider: HorizontalSlider;	//amplitude slider
    private var maxAmplitude;				//maximum amplitude of shaker bar in meters
    private var stageW: int;
    private var stageH: int;

    public function ShakerView( myMainView: MainView, model: ShakerModel ) {
        this.myMainView = myMainView;
        this.model = model;
        this.maxNbrResonators = this.model.getMaxNbrResonators();
        this.nbrResonators = this.model.getNbrResonators();
        this.model.registerView( this );
        this.initialize();
    }//end of constructor

    //add "piston driver" label

    public function initialize(): void {

        this.stageW = Util.STAGEW;
        this.stageH = Util.STAGEH;
        this.pixPerMeter = 200;
        this.barPixPerResonator = 70;
        this.hzPerTurn = 1;
        this.maxAmplitude = 0.04;
        this.bar = new Sprite();
        this.base = new Sprite();
        this.springHolder = new Sprite();

        //NiceButton2(myButtonWidth:Number, myButtonHeight:Number, labelText:String, buttonFunction:Function)
        this.onOffButton = new NiceButton2( 70, 30, "On/Off", OnOrOff );
        this.onLight = new Sprite();
        this.glow = new GlowFilter( 0xff0000, 0.5, 8, 8, 10 );
        this.glow.quality = BitmapFilterQuality.HIGH;
        //RotaryKnob(action:Function, knobDiameter:Number, knobColor:Number, minTurns:Number, maxTurns:Number)
        this.fKnob = new RotaryKnob( changeF, 40, 0x00aa00, 0, 7 );
        this.fKnob.setLabelText( "frequency" );
        this.fKnob.setScale( this.hzPerTurn );
        //HorizontalSlider(owner:Object, lengthInPix:int, minVal:Number, maxVal:Number, detented:Boolean = false, nbrTics:int = 0)
        this.ASlider = new HorizontalSlider( changeA, 120, 0, 1 );
        this.ASlider.setLabelText( "amplitude" );
        this.ASlider.setScale( this.maxAmplitude );
        //this.ASlider.setVal(this.maxAmplitude/2);
        this.drawShaker();

        this.addChild(this.springHolder);
        this.addChild( this.bar );
        this.addChild( this.base );

        
        this.base.addChild( this.onOffButton );
        this.base.addChild( this.onLight );
        this.base.addChild( this.fKnob );
        this.base.addChild( this.ASlider );
        this.makeBarGrabbable();
        this.createResonatorArray();
        this.displayResonatorViews();
        this.initializeShakerControls();
        //this.startResonators();
        //this.spring.x = 0;//stageW/2;
        //this.spring.y = 0;//stageH/2;
        //trace("MassSpringView.initialize() called. stageW = "+this.stageW);
    }//end of initialize()

    public function initializeShakerControls(): void {
        //trace("initializeShakerControls() called");
        this.ASlider.setVal( 0.75 * this.maxAmplitude );
        this.fKnob.setTurns( 2.5 );

        //this.changeA();
    }

    private function createResonatorArray(): void {
        this.resonatorView_arr = new Array( this.maxNbrResonators );
        for ( var i: int = 0; i < this.maxNbrResonators; i++ ) {
            //function MassSpringView(model:MassSpringModel)
            this.resonatorView_arr[i] = new MassSpringView( this.model.getResonatorModel( i ) );
            this.springHolder.addChild( resonatorView_arr[i] );
        }//end for
    }

    public function displayResonatorViews(): void {
        var barW: Number = this.barPixPerResonator * this.nbrResonators;
        for ( var i: int = 0; i < this.nbrResonators; i++ ) {
            this.resonatorView_arr[i].visible = true;
            this.resonatorView_arr[i].y = 0;
            this.resonatorView_arr[i].x = -barW / 2 + (i + 0.5) * this.barPixPerResonator;
        }//end for
        for ( var i: int = this.nbrResonators; i < this.maxNbrResonators; i++ ) {
            this.resonatorView_arr[i].visible = false;
        }//end for
    }//end displayResonatorViews


    public function setNbrResonators( nbrR: int ): void {
        this.nbrResonators = nbrR;
        this.drawBar();
        this.displayResonatorViews()
    }

    private function startResonators(): void {
        for ( var i: int; i < this.nbrResonators; i++ ) {
            this.resonatorView_arr[i].getModel().startMotion();
        }
    }

    private function OnOrOff(): void {
        if ( this.model.getRunning() ) {
            //this.model.setResonatorsFreeRunning(true);
            this.model.stopShaker();
            this.drawOnLight( 0x000000 );
            //this.model.stopMotion();
        }
        else {
            //this.model.setResonatorsFreeRunning(false);
            this.model.startShaker();
            this.drawOnLight( 0xff0000 );
            //this.model.startMotion();
        }
        //trace("ShakerVeiw.OnOrOff callled. model.running = "+this.model.getRunning());
        //trace("ShakerVeiw.OnOrOff callled. amplitude = "+ this.model.getA());
    }

    private function changeA(): void {
        //trace("ShakerView.changeA()  "+this.ASlider.getVal());
        var amplitude: Number = this.maxAmplitude * this.ASlider.getVal();
        //trace("ShakerView.changeA() amplitude is   " + amplitude);
        this.model.setA( amplitude );
    }

    private function changeF(): void {
        var frequency = this.hzPerTurn * this.fKnob.getTurns();
        this.model.setF( frequency );
        //trace("frequency: "+frequency);
    }

    private function drawShaker(): void {
        this.drawOnLight( 0x000000 );  //argument is color of light
        this.drawBar();
        this.drawBase();
    }

    private function drawOnLight( color: Number ): void {
        var gOL: Graphics = this.onLight.graphics;
        gOL.clear();
        gOL.lineStyle( 2, 0x0000ff, 1, true, LineScaleMode.NONE );
        var radius: Number = 8;
        gOL.beginFill( color );
        gOL.drawCircle( -radius / 2, -radius / 2, radius );
        gOL.endFill();
        this.onLight.filters = [this.glow];
        if ( color == 0x000000 ) {
            this.onLight.filters = [];
        }
        else {
            this.onLight.filters = [this.glow];
        }
    }//end drawLight

    private function drawBar(): void {
        var gB: Graphics = this.bar.graphics;
        gB.clear();
        gB.lineStyle( 2, 0x000000, 1, true, LineScaleMode.NONE );
        var barW: Number = this.barPixPerResonator * this.nbrResonators;
        var barH: Number = 30;
        var pistonW: Number = this.barPixPerResonator;
        gB.beginFill( 0xaaaaaa );
        gB.drawRoundRect( -barW / 2, 0, barW, barH, 20 );
        //gB.drawRect(-pistonW/2, barH, pistonW, 100);
        gB.endFill();
        gB.beginFill( 0xcccccc );
        gB.drawRect( -pistonW / 2, barH, pistonW, 100 );
        gB.endFill();
        gB.moveTo( -pistonW / 2, 2 * barH );
        gB.lineTo( pistonW / 2, 2 * barH );
    }//end drawBar()

    private function drawBase(): void {
        var gB: Graphics = this.base.graphics;
        gB.clear();
        gB.lineStyle( 2, 0x000000, 1, true, LineScaleMode.NONE );
        var baseW: Number = 500;
        var baseH: Number = 100;
        var floorLevel: Number = 0 + 2 * baseH;
        gB.beginFill( 0xaaaaaa );
        gB.drawRoundRect( -baseW / 2, floorLevel - baseH, baseW, baseH, 20 );
        gB.endFill();
        this.onOffButton.x = -baseW / 2 + this.onOffButton.width;
        this.onOffButton.y = floorLevel - 0.7 * baseH;
        this.onLight.x = -baseW / 2 + this.onOffButton.width;
        this.onLight.y = floorLevel - 0.3 * baseH;
        this.fKnob.x = -baseW / 2 + 1.5 * this.onOffButton.width + this.fKnob.width;
        this.fKnob.y = floorLevel - baseH / 2;
        this.ASlider.x = baseW / 2 - 1.2 * this.ASlider.width;
        this.ASlider.y = floorLevel - baseH / 2;
    }//end of drawBase

    private function makeBarGrabbable(): void {
        //this.bar.buttonMode = true;
        var target = this.bar;
        var thisObject: Object = this;
        var wasRunning: Boolean;
        //var L0inPix:Number = this.orientation*this.pixPerMeter * this.model.getL0();
        //var D0inPix:Number = this.orientation*this.L0InPix;
        target.buttonMode = true;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            //problem with localX, localY if sprite is rotated.
            clickOffset = new Point( evt.localX, evt.localY );
            //trace("evt.target.y: "+evt.target.y);
            wasRunning = thisObject.model.getRunning();
            thisObject.model.stopShaker();
            //thisObject.drawOnLight();
            stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            //thisObject.spring.scaleY *= 1.5;
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            if ( wasRunning ) {thisObject.model.startShaker();}
            //thisObject.drawOnLight();
        }

        function dragTarget( evt: MouseEvent ): void {
            var barYInPix = mouseY - clickOffset.y;
            var barYInMeters = -barYInPix / thisObject.pixPerMeter;
            var limit: Number = 0.15;
            if ( barYInMeters > limit ) {
                //trace("bar high");
                barYInMeters = limit;
            } else if ( barYInMeters < -limit ) {
                barYInMeters = -limit;
                //trace("bar low");
            }
            thisObject.model.setY0( barYInMeters );
            //trace("ShakerView.makeBar..  barYInMeters = " + barYInMeters);
            //trace("evt.localY = "+evt.localY);
            //trace("mouseY = "+mouseY);
            evt.updateAfterEvent();
        }//end of dragTarget()

    }

    public function update(): void {
        this.bar.y = -this.model.getY0() * this.pixPerMeter;

    }//end update()

}//end of class

}//end of package