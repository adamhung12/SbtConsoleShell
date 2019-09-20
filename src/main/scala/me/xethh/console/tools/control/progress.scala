package me.xethh.console.tools.control

import scala.language.implicitConversions

case class process[Item,Out](p:Item=>Out, boxedItem:progress[Item]){
  private def formatInt(int:Int):String=s"% 6d".format(int)
  def noLog:Out = p(boxedItem.item)
  def withLog(msg:String):Out = {
    println(s"[${formatInt(boxedItem.index+1)}/${formatInt(boxedItem.total)}] ${msg}")
    p(boxedItem.item)
  }
}
class progress[Item](val total:Int, val index:Int, val item:Item) {
  def exec[Out](p:Item=>Out):process[Item,Out]=Exec(this,p)
}
object Monitor{
  def apply[Item,Out](total: Int, index: Int, item: Item): progress[Item] = new progress(total, index, item)
  def apply[Item,Out](seq:Seq[Item]): Seq[progress[Item]] = {
    val size = seq.size
    seq.zipWithIndex.map{it=>Monitor[Item,Out](size, it._2, it._1)}
  }
}
case class Exec[Item,Out](item:progress[Item])

object Exec{
  def apply[Item,Out](item:progress[Item], p:Item=>Out): process[Item,Out] = process.apply(p,item)

  implicit def seq2Exec[Item,Out](seq:Seq[Item]):Seq[progress[Item]] = {
    val size = seq.size
    seq.zipWithIndex.map{it=> new progress(size, it._2, it._1)}
  }

  def main(args: Array[String]): Unit = {
    Monitor(Array(1,2,3)).filter{ it=>
      it exec {it=>println(s"execute ${it}");it==2} withLog(s"log ${it.item}")
    }
  }
}
