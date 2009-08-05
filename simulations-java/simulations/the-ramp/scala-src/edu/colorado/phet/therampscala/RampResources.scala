package edu.colorado.phet.therampscala

import common.phetcommon.resources.PhetResources
import java.text.MessageFormat

object Predef{
  implicit def toMyRichString(s:String) = new MyRichString(s)
}
import Predef._
object RampResources extends PhetResources("the-ramp".literal) {
  val forcePattern = getLocalizedString("force.pattern")
  val energyPattern = getLocalizedString("energy.pattern")
  val workPattern = getLocalizedString("work.pattern")

  def formatForce(force: String) = MessageFormat.format(forcePattern, force)

  def formatEnergy(energy: String) = MessageFormat.format(energyPattern, energy)

  def formatWork(work: String) = MessageFormat.format(workPattern, work)

  implicit def toMyRichString(s:String) = new MyRichString(s)
}

class MyRichString(s:String){
  lazy val literal = s
  lazy val translate = RampResources.getLocalizedString(s)
  def messageformat(x:Object*) = MessageFormat.format(s,x: _*) 
}