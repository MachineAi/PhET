package edu.colorado.phet.buildtools.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.*;
import edu.colorado.phet.buildtools.statistics.StatisticsProject;

/**
 * A list of projects that can be selected
 */
public class ProjectList extends JList {

    private File trunk;
    private DefaultListModel model;

    public ProjectList( File trunk ) {
        this.trunk = trunk;

        model = new DefaultListModel();

        setModel( model );

        // only select one at a time
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        // render cells in a custom way
        setCellRenderer( new ProjectCellRenderer( trunk ) );

        // add all of the projects
        initializeProjects();

        final ProjectListElement defaultProject = getDefaultProject();
        if ( defaultProject != null ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    ProjectList.this.setSelectedValue( defaultProject, true );
                    onProjectChange();
                }
            } );
        }

        addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent listSelectionEvent ) {
                onProjectChange();
            }
        } );
    }

    private void onProjectChange() {
        notifyChanged();
        saveNewProjectSelection();
    }

    public PhetProject getSelectedProject() {
        int index = getSelectedIndex();
        if ( index < 0 ) {
            return null;
        }
        return ( (ProjectListElement) ( model.get( index ) ) ).getProject();
    }

    private void initializeProjects() {
        // TODO: add flex support?
        List<PhetProject> projects = new LinkedList<PhetProject>();
        for ( PhetProject phetProject : JavaSimulationProject.getJavaSimulations( trunk ) ) {
            projects.add( phetProject );
        }

        for ( PhetProject phetProject : FlashSimulationProject.getFlashSimulations( trunk ) ) {
            projects.add( phetProject );
        }

        try {
            // TODO: consider refactoring for PhetProject.getAllProjects
            projects.add( new TranslationUtilityProject( new File( trunk, BuildToolsPaths.TRANSLATION_UTILITY ) ) );
            projects.add( new PhetUpdaterProject( new File( trunk, BuildToolsPaths.PHET_UPDATER ) ) );
            projects.add( new BuildToolsProject( new File( trunk, BuildToolsPaths.BUILD_TOOLS_DIR ) ) );
            projects.add( new TimesheetProject( new File( trunk, BuildToolsPaths.TIMESHEET ) ) );
            projects.add( new StatisticsProject( trunk ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // sort by name
        Collections.sort( projects, new Comparator<PhetProject>() {
            public int compare( PhetProject a, PhetProject b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );

        for ( PhetProject project : projects ) {
            model.addElement( new ProjectListElement( project ) );
        }

    }

    private void saveNewProjectSelection() {
        Properties properties = new Properties();
        properties.setProperty( "project", getSelectedProject().getName() );
        try {
            properties.store( new FileOutputStream( getPhetBuildGUIPropertyFile() ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ProjectListElement getDefaultProject() {
        File file = getPhetBuildGUIPropertyFile();
        if ( file.exists() ) {
            Properties p = new Properties();
            try {
                p.load( new FileInputStream( file ) );
                if ( p.containsKey( "project" ) ) {
                    String proj = p.getProperty( "project" );
                    Enumeration elements = model.elements();
                    while ( elements.hasMoreElements() ) {
                        ProjectListElement element = (ProjectListElement) elements.nextElement();
                        if ( element.getProject().getName().equals( proj ) ) {
                            return element;
                        }
                    }
                    return null;
                }
                else {
                    return null;
                }
            }
            catch( IOException e ) {
                return null;
            }
        }
        else {
            return null;
        }
    }

    private File getPhetBuildGUIPropertyFile() {
        File file = new File( trunk, ".phet-build-gui.properties" );
        return file;
    }

    public static class ProjectListElement {
        private PhetProject p;

        private ProjectListElement( PhetProject p ) {
            this.p = p;
        }

        public String toString() {
            return p.getListDisplayName();
        }

        public PhetProject getProject() {
            return p;
        }

        public boolean equals( Object obj ) {
            if ( obj instanceof ProjectListElement ) {
                ProjectListElement p = (ProjectListElement) obj;
                return p.p.getName().equals( this.p.getName() );
            }
            else {
                return false;
            }
        }

        public int hashCode() {
            return p.getName().hashCode();
        }
    }


    private List<Listener> listeners = new LinkedList<Listener>();

    public static interface Listener {
        public void notifyChanged();
    }

    public void notifyChanged() {
        Listener[] listenerArray = listeners.toArray( new Listener[0] );
        for ( Listener listener : listenerArray ) {
            listener.notifyChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
