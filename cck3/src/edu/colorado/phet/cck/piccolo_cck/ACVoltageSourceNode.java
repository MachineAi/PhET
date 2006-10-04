package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.ACVoltageSource;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 8:59:30 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class ACVoltageSourceNode extends ComponentImageNode {
    private ICCKModule module;
    private ACVoltageSource acVoltageSource;

    public ACVoltageSourceNode( CCKModel model, ACVoltageSource acVoltageSource, Component component, ICCKModule module ) {
        super( model, acVoltageSource, CCKImageSuite.getInstance().getACVoltageSourceImage(), component );
        this.module = module;
        this.acVoltageSource = acVoltageSource;
    }

    protected JPopupMenu createPopupMenu() {
        return new PiccoloACVoltageSourceMenu( acVoltageSource, module ).getMenuComponent();
    }
}
