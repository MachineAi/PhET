// Copyright 2002-2012, University of Colorado

/**
 * Compass model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property' ], function( Logger, Property ) {

    function FieldMeter( location, visible, magnet ) {

        var logger = new Logger( "FieldMeter" ); // logger for this source file

        // initialize properties
        this.location = new Property( location );
        this.visible = new Property( visible );
        this.value = new Property( magnet.getFieldVector( location ) );

        // Update the value displayed by the meter.
        var thisInstance = this;
        var updateValue = function() {
           thisInstance.value.set( magnet.getFieldVector( thisInstance.location.get() ) );
        };
        this.location.addObserver( updateValue );
        magnet.location.addObserver( updateValue );
        magnet.strength.addObserver( updateValue );

        //DEBUG
        var DEBUG = true;
        if ( DEBUG ) {
            this.location.addObserver( function ( newValue ) {
                logger.debug( "location=" + newValue );
            } );
            this.visible.addObserver( function ( newValue ) {
                logger.debug( "visible=" + newValue );
            } );
            this.value.addObserver( function ( newValue ) {
                logger.debug( "value=" + newValue );
            } );
        }
    }

    // Resets all properties
    FieldMeter.prototype.reset = function() {
        this.location.reset();
        this.visible.reset();
        // this.value is derived
    };

    return FieldMeter;
} );
