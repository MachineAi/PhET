package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import umd.cs.piccolo.PNode
import LadybugUtil._

class AphidSetNode(model: AphidMazeModel, transform: ModelViewTransform2D) extends PNode {
  val update = defineInvokeAndPass(model.addListenerByName){
    removeAllChildren
    model.aphids.foreach((aphid: Aphid) => addChild(new AphidNode(aphid, transform)))
  }
}