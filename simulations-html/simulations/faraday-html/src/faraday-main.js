// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [ 'easel',
           'common/Logger',
           'common/ModelViewTransform',
           'model/FaradayModel',
           'view/ControlPanel',
           'view/FaradayStage'
         ],
         function ( Easel, Logger, ModelViewTransform, FaradayModel, ControlPanel, FaradayStage ) {

    var logger = new Logger( "faraday-main" ); // logger for this source file

    var canvas = document.getElementById( 'faraday-canvas' );

    // Model ----------------------------------------------------------

    var MVT_SCALE = 1; // 1 model unit == 1 view unit
    var MVT_OFFSET = new Easel.Point( 0.5 * canvas.width / MVT_SCALE, 0.5 * canvas.height / MVT_SCALE ); // origin in center of canvas
    var mvt = new ModelViewTransform( MVT_SCALE, MVT_OFFSET );

    var model = new FaradayModel();

    // View ----------------------------------------------------------

    var view = new FaradayStage( canvas, model, mvt );

    // Controls ----------------------------------------------------------

    var controls = new ControlPanel( model, view );

    // Animation loop ----------------------------------------------------------

    Easel.Ticker.addListener( view.stage );
    Easel.Ticker.setFPS( 60 );
} );