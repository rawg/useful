
package com.rentawebgeek.dot 

import scala.collection.mutable.{ListBuffer, HashMap}

trait HasAttributes {
  val attributes = new HashMap[String, String]
  
  def renderAttributes() = {
    val attrs = for {
      (key, value) <- attributes
    } yield key + "=" + value
    
    "[" + attrs.mkString(", ") + "]"
  }
}

trait DotObject extends HasAttributes {
  def render(): String
}

class Graph extends DotObject {
  val nodes = new ListBuffer[Node]
  val edges = new ListBuffer[Edge]

  def add(node: Node) {
    if (nodes.find(_.name == node.name) == None) {
      nodes += node
    }
  }

  def add(edge: Edge) {
    if (edges.find(_.equals(edge)) == None) {
      edges += edge
    }
  }

  def render() = {
    val sb = new StringBuilder

    sb.append("digraph {\n")
    for (node <- nodes) sb.append("  " + node.render())
    for (edge <- edges) sb.append("  " + edge.render()) 
    sb.append("}\n")
    
    sb.mkString
  }
}

abstract class Node extends DotObject {
  val name: String
  def nodeName() = "\"" + name + "\""
  def render() = nodeName + " " + renderAttributes() + ";\n"
}

abstract class Edge(val left: Node, val right: Node) extends DotObject {
  def render() = left.nodeName + " -> " + right.nodeName + " " + renderAttributes() + ";\n"
  def equals(edge: Edge) = (this.left.name == edge.left.name) && (this.right.name == edge.right.name)
}


