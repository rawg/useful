

package com.rentawebgeek.useful

import com.rentawebgeek.dot


object useful {
  implicit def string2Actor(str: String) = new Actor(str)
  implicit def string2UseCase(str: String) = new UseCase(str) 

  private var graph = new dot.Graph
  
  def defaultGraph = graph
  def defaultGraph_=(newGraph: dot.Graph) = graph = newGraph

  def main(args: Array[String]) = {
    "Web User" uses "Purchase Item"
    "Purchase Item" includes "Login"
    "Log Debugging Info" expands "Login"
    "Order Processing" realizes "Purchase Item"
    //"Synchronize Data" generalizes "Synchronize Serially"
    "Synchronize Wirelessly" specializes "Synchronize Data"
    print(graph.render())
  }

}

abstract class UseCaseNode extends dot.Node {
}

class Actor(val name: String) extends UseCaseNode {
    useful.defaultGraph.add(this)
    attributes += "image" -> "\"actor.svg\""

    def uses(uc: UseCase) = useful.defaultGraph.add(new Relationship(this, uc) with Uses)
    def initiates(uc: UseCase) = useful.defaultGraph.add(new Relationship(this, uc) with Initiates)
}

class UseCase(val name: String) extends UseCaseNode {
  private var _isAbstract = false

  def expands(uc: UseCase) = useful.defaultGraph.add(new Relationship(this, uc) with Expands)
  def includes(uc: UseCase) = useful.defaultGraph.add(new Relationship(this, uc) with Includes)
  def specializes(uc: UseCase) = useful.defaultGraph.add(new Relationship(this, uc) with Generalizes)
  def generalizes(uc: UseCase) = useful.defaultGraph.add(new Relationship(uc, this) with Generalizes)
  def realizes(uc: UseCase) = {
    useful.defaultGraph.add(new Relationship(this, uc) with Realizes)
    uc.isAbstract = true
  }

  def isAbstract = _isAbstract
  def isAbstract_=(isAbstract: Boolean) = _isAbstract = isAbstract

}


class Relationship(override val left: UseCaseNode, override val right: UseCaseNode) extends dot.Edge(left, right) {

}

trait Uses extends dot.HasAttributes {
  attributes += ("arrowhead" -> "none")
}

trait Initiates extends dot.HasAttributes {
  attributes += ("arrowhead" -> "open")
}

trait Expands extends dot.HasAttributes {
  attributes += ("label" -> "\" << extends >> \"")
  attributes += ("arrowtail" -> "open")
  attributes += ("style" -> "dashed")
}

trait Realizes extends dot.HasAttributes {
  attributes += ("arrowhead" -> "open")
  attributes += ("style" -> "dashed")
}

trait Includes extends Realizes {
  attributes += ("label" -> "\" << includes >> \" ")
}

trait Generalizes extends dot.HasAttributes {
  attributes += ("arrowhead" -> "empty")
  attributes += ("style" -> "dashed")
}


