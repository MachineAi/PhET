package edu.colorado.phet.simsharinganalysis.scripts.utah_november_2011_ii

import java.io.File
import io.Source

/**
 * Emily said:
 * I’m back to working on the Molecule Polarity sim data (from the Utah study last November). I’d like to do two types of analysis:
 * 1)   What % of clickable/moveable options did each group interact with during the “play only” time? (this would include clicking on any tool/tab, and moving a moveable object.)
 * 2)   A histogram (representing all student groups) showing the amount of new moveable objects/tools clicked on over the “play only” time.
 */
case class Arg(key: String, value: String)

case class TextComponent(tab: String, component: String, name: Option[String]) {
  override def toString = component + ( if ( name.isDefined ) ": " else "" ) + name.getOrElse("")
}

case class Entry(localTime: Long, serverTime: Long, component: String, action: String, args: List[Arg]) {
  def text = args.find(_.key == "text").map(_.value)
}

case class Log(file: File, machineID: String, sessionID: String, serverTime: Long, entries: List[Entry]) {
  def id = entries(0).args.find(_.key == "id").map(_.value).getOrElse("")
}

object MoleculePolarityAnalysisAugust2012 {}

object NewParser {
  def readText(f: File) = {
    val source = Source.fromFile(f)
    val text = source.mkString
    source.close()
    text
  }

  def afterEquals(s: String) = s.split('=').last.trim

  def parseFile(file: File, text: String) = {
    val lines = text.split('\n').map(_.trim)
    val serverStartTime = afterEquals(lines(2)).toLong
    val localStartTime = lines(3).split('\t').apply(0).trim.toLong
    if ( file.getName.contains("urrpk0p2") ) {
      println("sst = " + serverStartTime + ", localstarttime=" + localStartTime)
    }
    Log(file, afterEquals(lines(0)), afterEquals(lines(1)), serverStartTime, lines.slice(3, lines.length).map(s => toEntry(s, serverStartTime, localStartTime)).toList)
  }

  def parseElement(s: String) = {
    val parsed = s.split('=').map(_.trim)
    if ( parsed.length == 2 )
      Arg(parsed(0), parsed(1))
    else
      Arg(parsed(0), "")
  }

  def toEntry(s: String, serverStartTime: Long, localStartTime: Long) = {
    val elements = s.split('\t').map(_.trim)
    val remainingElements = elements.slice(3, elements.length)
    val localTime = elements(0).toLong

    //    to get the time for any event in any log
    //    server time on line N = original server time on line 3 - local time on line 4 + local time on line N
    //    correct because line 4 happens exactly when server time is reported

    val serverTime = serverStartTime - localStartTime + localTime
    Entry(localTime, serverTime, elements(1), elements(2), remainingElements.map(parseElement).toList)
  }

  def minutesToMilliseconds(minutes: Double) = ( minutes * 60000.0 ).toLong

  def getUsedComponents(entries: Seq[Entry]) = {
    val textComponents = entries.map(entry => TextComponent("?", entry.component, entry.text)).toSet.toList
    textComponents.map(_.toString).sorted
  }

  def main(args: Array[String]) {
    val file = new File("C:\\Users\\Sam\\Desktop\\molecule-polarity")
    val logs = file.listFiles.map(file => (file, readText(file))).map(tuple => parseFile(tuple._1, tuple._2))
    logs.foreach(println)
    logs.map(_.id).foreach(println)
    logs.filter(_.id == "2").foreach(elm => println(elm.file))

    //Time on the server
    val startPlayTime: Long = 1320867727969L
    val elapsedPlayTime: Long = minutesToMilliseconds(9.5)
    val endPlayTime: Long = startPlayTime + elapsedPlayTime

    val allComponents = getUsedComponents(logs.flatMap(_.entries))
    allComponents.foreach(println)

    for ( log <- logs ) {
      val entries = getUsedComponents(log.entries)
      val used = entries.length
      println("log: " + log.id + ", used=" + used + "/" + allComponents.length)
      if ( log.sessionID == "52m2urrpk0p2j0s6b84fcs3pbu" ) {
        log.entries.foreach(println)
      }
    }
  }
}
