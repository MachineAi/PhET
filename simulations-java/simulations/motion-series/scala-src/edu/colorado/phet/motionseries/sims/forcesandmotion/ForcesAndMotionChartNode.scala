package edu.colorado.phet.motionseries.sims.forcesandmotion

import charts._
import graphics.MotionSeriesCanvas
import model.MotionSeriesModel
import phet.common.motion.graphs._
import motionseries.MotionSeriesResources
import motionseries.MotionSeriesResources._

class ForcesAndMotionChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  val graphs = Array(
    new MinimizableControlGraph("forces.parallel-title".translate, forceGraph),
    new MinimizableControlGraph("acceleration", accelerationGraph, true),
    new MinimizableControlGraph("velocity", velocityGraph, true),
    new MinimizableControlGraph("position", positionGraph, true))

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updateLayout()
}