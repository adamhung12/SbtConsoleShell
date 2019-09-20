package me.xethh.console.tools.control

import scala.language.implicitConversions

case class IndexedObjectWithFlow[Item,Out](p:Item=>Out, boxedItem:IndexingObject[Item]){
  private def formatInt(int:Int):String=s"% 6d".format(int)
  def noLog:Out = p(boxedItem.item)
  def withLog(msg:String):Out = {
    println(s"[${formatInt(boxedItem.index+1)}/${formatInt(boxedItem.total)}] ${msg}")
    p(boxedItem.item)
  }
}
class IndexingObject[Item](val total:Int, val index:Int, val item:Item) {
  def exec[Out](p:Item=>Out):IndexedObjectWithFlow[Item,Out]=Exec(this,p)
}
object Monitor{
  def apply[Item,Out](total: Int, index: Int, item: Item): IndexingObject[Item] = new IndexingObject(total, index, item)
  def apply[Item,Out](seq:Seq[Item]): Seq[IndexingObject[Item]] = {
    val size = seq.size
    seq.zipWithIndex.map{it=>Monitor[Item,Out](size, it._2, it._1)}
  }
}
case class Exec[Item,Out](item:IndexingObject[Item])

object Exec{
  def apply[Item,Out](item:IndexingObject[Item], p:Item=>Out): IndexedObjectWithFlow[Item,Out] = IndexedObjectWithFlow.apply(p,item)

  implicit def seq2Exec[Item,Out](seq:Seq[Item]):Seq[IndexingObject[Item]] = {
    val size = seq.size
    seq.zipWithIndex.map{it=> new IndexingObject(size, it._2, it._1)}
  }
}
