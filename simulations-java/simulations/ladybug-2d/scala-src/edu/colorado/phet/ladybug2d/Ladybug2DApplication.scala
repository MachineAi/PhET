package edu.colorado.phet.ladybug2d

import model.LadybugModel
import scalacommon.{ScalaApplicationLauncher, ScalaClock}

object Ladybug2DApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "ladybug-2d", "ladybug-2d",
      () => new LadybugModule[LadybugModel](new ScalaClock(30, 30 / 1000.0)))
  }
}