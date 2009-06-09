package edu.colorado.phet.buildtools.test.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.SVNStatusChecker;
import edu.colorado.phet.buildtools.statistics.StatisticsDeployCommand;
import edu.colorado.phet.buildtools.statistics.StatisticsProject;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class TestStatisticsProjectPanel extends JPanel {

    private File trunk;
    private StatisticsProject project;

    public TestStatisticsProjectPanel( File trunk, StatisticsProject project ) {
        super( new BorderLayout() );

        this.trunk = trunk;
        this.project = project;

        JLabel title = new JLabel( "Statistics" );
        title.setHorizontalAlignment( SwingConstants.CENTER );
        add( title, BorderLayout.NORTH );

        JPanel controlPanel = new JPanel();

        JPanel deployProdPanel = new VerticalLayoutPanel();
        deployProdPanel.setBorder( BorderFactory.createTitledBorder( "Production Deploy" ) );
        JButton deployProdButton = new JButton( "Deploy to tigercat" );
        deployProdPanel.add( deployProdButton );

        controlPanel.add( deployProdPanel );

        add( controlPanel, BorderLayout.SOUTH );

        deployProdButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployProd();
            }
        } );
    }

    private void doDeployProd() {
        boolean confirm = TestGUI.confirmProdDeploy( project, PhetServer.PRODUCTION );

        if ( !confirm ) {
            System.out.println( "Cancelled" );
            return;
        }
        try {
            StatisticsProject project = new StatisticsProject( trunk );
            SVNStatusChecker checker = new SVNStatusChecker();

            boolean success = true;

            if ( checker.isUpToDate( project ) ) {
                StatisticsDeployCommand command = new StatisticsDeployCommand( trunk );

                success = command.deploy();
            }
            else {
                success = false;
            }

            if ( success ) {
                System.out.println( "Statistics deploy completed successfully" );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}