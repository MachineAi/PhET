package edu.colorado.phet.wickettest.content;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class ResearchPanel extends PhetPanel {
    public ResearchPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "research-additional", "research.additional", new Object[]{
                "href=\"http://phet.colorado.edu/phet-dist/publications/PhET Look and Feel.pdf\""
        } ) );

    }

    public static String getKey() {
        return "research";
    }

    public static String getUrl() {
        return "research";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}