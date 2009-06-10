package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

public class GenerationProgressPanel extends JPanel {

    private JProgressBar generationProgressBar;

    public GenerationProgressPanel( final NaturalSelectionModel model ) {
        super( new GridLayout( 2, 1 ) );

        setOpaque( false );

        add( new JLabel( "Time until next generation" ) );

        generationProgressBar = new JProgressBar( SwingConstants.HORIZONTAL );
        generationProgressBar.setValue( 0 );

        add( generationProgressBar );

        //generationProgressBar.setPreferredSize( new Dimension( (int) showGenerationChartButton.getPreferredSize().getWidth(), (int) generationProgressBar.getPreferredSize().getHeight() ) );
        //generationProgressBar.setMaximumSize( generationProgressBar.getPreferredSize() );

        model.getClock().addTimeListener( new NaturalSelectionClock.Listener() {
            public void onTick( ClockEvent event ) {
                generationProgressBar.setValue( model.getGenerationProgressPercent() );
            }
        } );
    }

}
