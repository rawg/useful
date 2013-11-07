

package com.rentawebgeek.useful

import com.rentawebgeek.dot


package object useful {

  def main(args: Array[String]) = {
    import Implicits._
    "Web User" uses "Purchase Item"
    "Purchase Item" includes "Login"
    "Log Debugging Info" expands "Login"
    "Order Processing" realizes "Purchase Item"
    //"Synchronize Data" generalizes "Synchronize Serially"
    "Synchronize Wirelessly" specializes "Synchronize Data"
    print(DefaultGraph().render())
  }

  def run(filepath: String) {
    val lines = scala.io.Source.fromFile(filepath).mkString
    val sb = new StringBuilder
    sb.append("import com.rentawebgeek.useful.Implicits._\n")
    sb.append(lines)

  }
}

object Implicits {
  /** Implicit conversion from a string to an actor
   *
   * === Usage ===
   * {{{
   * scala> import com.rentawebgeek.useful.Implicits._
   * scala> import com.rentawebgeek.useful._
   * scala> "Web User" uses UseCase("Login")
   * }}}
   */
  implicit def string2Actor(str: String): Actor = Actor(str)
  
  /** Implicitly converts a string to a use case
   *
   * === Usage ===
   * {{{
   * scala> import com.rentawebgeek.useful.Implicits._
   * scala> "Order Processing" realizes "Purchase Item"
   * }}}
   */
  implicit def string2UseCase(str: String): UseCase = UseCase(str) 

}


/** An implicit "default" graph to make the DSL more approachable */
object DefaultGraph {
  private var _graph = new dot.Graph
  
  /** Returns the default graph */
  def apply() = _graph
  
  /**
   *  Assigns the default graph
   *
   *  @return the default graph
   */
  def apply(graph: dot.Graph) = {
    _graph = graph
    _graph
  }

}

abstract class UseCaseNode extends dot.Node {
}

class Actor(val name: String) extends UseCaseNode {
    attributes += "image" -> "\"actor.svg\""

    def uses(uc: UseCase) = DefaultGraph().add(new Relationship(this, uc) with Uses)
    def initiates(uc: UseCase) = DefaultGraph().add(new Relationship(this, uc) with Initiates)
}

object Actor {
  def apply(name: String): Actor = {
    val actor = new Actor(name)
    DefaultGraph().add(actor)
    return actor
  }
}


class UseCase(val name: String) extends UseCaseNode {
  private var _isAbstract = false

  def expands(uc: UseCase) = DefaultGraph().add(new Relationship(this, uc) with Expands)
  def includes(uc: UseCase) = DefaultGraph().add(new Relationship(this, uc) with Includes)
  def specializes(uc: UseCase) = DefaultGraph().add(new Relationship(this, uc) with Generalizes)
  def generalizes(uc: UseCase) = DefaultGraph().add(new Relationship(uc, this) with Generalizes)
  def realizes(uc: UseCase) = {
    DefaultGraph().add(new Relationship(this, uc) with Realizes)
    uc.isAbstract = true
  }

  def isAbstract = _isAbstract
  def isAbstract_=(isAbstract: Boolean) = _isAbstract = isAbstract

}

object UseCase {
  def apply(name: String) = new UseCase(name)
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


