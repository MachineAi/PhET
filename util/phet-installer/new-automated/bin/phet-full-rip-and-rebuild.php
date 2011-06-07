<?php

    //=========================================================================
    // This file contains a script that rips (i.e. creates a local copy or
    // "mirror" of) the of the PhET web site, makes modifications to the files,
    // and then rebuilds the PhET installers that enable the installation of a
    // local mirror of the site on a user's machine.
    //=========================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("args.php");
    require_once("config.php");
    require_once("deploy-util.php");
    require_once("file-util.php");
    require_once("global.php");
    require_once("installer-util.php");
    require_once("ripper-util.php");

    //-------------------------------------------------------------------------
    // Rip the web site and rebuild the installer.
    //-------------------------------------------------------------------------
    function full_rip_and_rebuild() {

        $args = new Args();

        // Grab a file lock to prevent multiple simultaneous executions.
        if ( !file_lock( LOCK_FILE_STEM_NAME ) ){
            flushing_echo( "ERROR: The PhET installer builder appears to be completing another build." );
            flushing_echo( "If you believe this to be incorrect, use the appropriate script to force" );
            flushing_echo( "an unlock (something like \"force-unlock.sh\") and try again." );
            return;
        }

        // Log the start time of this operation.
        $start_time = exec( "date" );
        flushing_echo( "Starting full rip and rebuild of PhET installers at time $start_time" );

        // Remove previous copy of web site.
        ripper_remove_website_copy();

        // Rip the web site.
        ripper_rip_website( "PHET" );
        ripper_download_sims();

        // Log the time at which the rip completed.
        $rip_finish_time = exec( "date" );
        flushing_echo( "Rip without activities completed at time $start_time" );

        // Make sure permissions of the ripped website are correct.
        file_chmod_recursive( RIPPED_WEBSITE_ROOT, 0775, 0775 );

        // Incorporate the timestamp information that indicates when this
        // version of the installer was created.
        installer_create_marker_file();

        // Move the individually translated jar files out of the sims directory
        // so that they won't be included in the local mirror installers.
        ripper_move_out_translated_jars();

        // Insert the creation time into the marker file.  Note that there is
        // no distribution tag for the standard PhET installers.
        installer_insert_installer_creation_time( null );

        // Build the local installers, meaning installers that can be used to
        // install a local mirror of the PhET web site.
        installer_build_local_mirror_installers( BITROCK_PHET_LOCAL_MIRROR_BUILDFILE, OUTPUT_DIR );

        // Build the rommable distribution, which contains all of the installers
        // installers and is suitable for burning on CD.
        installer_build_rommable_distribution( OUTPUT_DIR, OUTPUT_DIR.CDROM_FILE_NAME );

        // If specified, deploy the sims to the production web site.
        if ( $args->flag( 'deploy' ) ){
           deploy_all();
        }

        // Re-rip the site, but this time include the activities.  This should
        // be an update of the initial rip, since we didn't delete that rip
        // before kicking off this one.
        ripper_rip_website( "PHET_WITH_ACTIVITIES" );

        // Log the time at which the rip completed.
        $rip_finish_time = exec( "date" );
        flushing_echo( "Rip with activities completed at time $start_time" );

        // Make sure permissions of the ripped website are correct.
        file_chmod_recursive( RIPPED_WEBSITE_ROOT, 0775, 0775 );

        // Build the local installers, meaning installers that can be used to
        // install a local mirror of the PhET web site.
        installer_build_local_mirror_installers( BITROCK_PHET_LOCAL_MIRROR_BUILDFILE, INSTALLERS_WITH_ACTIVITIES_DIR );

        // Build the rommable distribution, which contains all of the installers
        // installers and is suitable for burning on DVD.
        installer_build_rommable_distribution( INSTALLERS_WITH_ACTIVITIES_DIR, OUTPUT_DIR.DVDROM_FILE_NAME );

        // Deploy the rommable file to the web site.
        if ( $args->flag( 'deploy' ) ){
           // TODO: Deploy the rommable file with activities to the web site.
        }

        // Output the time of completion.
        $end_time = exec( "date" );
        flushing_echo( "\nCompleted rebuild at time $end_time" );

        // Release the lock.
        file_unlock( LOCK_FILE_STEM_NAME );
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    full_rip_and_rebuild();

?>
