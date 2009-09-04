package edu.colorado.phet.wickettest.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class TroubleshootingJavaPanel extends PhetPanel {
    public TroubleshootingJavaPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );


        add( new LocalizedText( "intro", "troubleshooting.java.intro", new Object[]{
                "href=\"mailto:phethelp@colorado.edu\""
        } ) );

        add( new LocalizedText( "troubleshooting-java-q1-answer", "troubleshooting.java.q1.answer", new Object[]{
                "<img style=\"float: left;\" src=\"/images/unhappy-mac-jnlp-logo-small.jpg\" alt=\"Unhappy JNLP Mac Logo\"/>"
        } ) );

        add( new LocalizedText( "troubleshooting-java-q2-answer", "troubleshooting.java.q2.answer", new Object[]{
                "<a href=\"http://www.java.com/en/index.jsp\"><img src=\"/images/java-jump.gif\" alt=\"Java Jump\"/></a>"
        } ) );

        add( new LocalizedText( "troubleshooting-java-q3-answer", "troubleshooting.java.q3.answer", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "troubleshooting-java-q4-answer", "troubleshooting.java.q4.answer", new Object[]{
                "http://www.apple.com/java/",
                "href=\"#q1\""
        } ) );

        add( new LocalizedText( "troubleshooting-java-q5-answer", "troubleshooting.java.q5.answer" ) );

        add( new LocalizedText( "troubleshooting-java-q6-answer", "troubleshooting.java.q6.answer" ) );

        add( new LocalizedText( "troubleshooting-java-q7-answer", "troubleshooting.java.q7.answer" ) );

        add( new LocalizedText( "troubleshooting-java-q8-answer", "troubleshooting.java.q8.answer" ) );

    }

    public static String getKey() {
        return "troubleshooting.java";
    }

    public static String getUrl() {
        return "troubleshooting/java";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}