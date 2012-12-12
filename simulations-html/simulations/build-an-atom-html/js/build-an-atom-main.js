// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'phetcommon/view/PrototypeDialog',
             'view/BuildAnAtomStage',
             'model/BuildAnAtomModel',
             'view/SymbolView',
             'view/MassNumberView',
             'view/PeriodicTableView'
         ], function ( _, Easel, PrototypeDialog, BuildAnAtomStage, BuildAnAtomModel, SymbolView, MassNumberView, PeriodicTablelView ) {

    // Create the model.
    var buildAnAtomModel = new BuildAnAtomModel();

    // Create the canvas where the user will construct the atoms.
    window.buildAnAtomStage = new BuildAnAtomStage( document.getElementById( 'atom-construction-canvas' ), buildAnAtomModel );

    // Display the "prototype" dialog.  TODO: This is temporary.
    PrototypeDialog.init( "Build an Atom" );

    // Create the widgets that will display various information about the constructed atom.
    $( document ).ready( function () {
        var atom = buildAnAtomModel.atom;
        var symbolWidget = new SymbolView( atom );
        var massNumberWidget = new MassNumberView( atom );
        var periodicTableWidget = new PeriodicTablelView( atom );
    } );

} );
