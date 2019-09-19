package me.xethh.console.display

import java.nio.file.Path


trait Viewable[A, O<:List[A]] {
 def viewObj:O
 def display(a:A):String
 def viewing(): Unit =   viewObj.zipWithIndex.foreach{ it=>println(s"${"% 6d".format(it._2+1)}\t${display(it._1)}")}
}
trait Pickable[A, O<:List[A]] extends Viewable[A, O]{
 def pick(index:Int):Option[A] = viewObj.zipWithIndex.find{it=>index==it._2+1}.map(_._1)
 def pick(name:String):Option[A]
 def apply(index: Int): Option[A] = this.pick(index)
 def apply(name: String): Option[A] = this.pick(name)
}
case class MapViewerable(m:Map[String,String]) extends Viewable[(String,String),List[(String, String)]] with Pickable[(String,String),List[(String, String)]] {
 override def viewObj: List[(String,String)] = m.toList.sortBy(_._2)

 override def display(a: (String,String)): String = s"${a._1}\t${a._2}"

 override def pick(name: String): Option[(String, String)] = {
  viewObj.find{it=>it._2.toLowerCase()==name.toLowerCase}
 }
}
case class List2Viewable[O](m:List[O]) extends Viewable[O,List[O]] with Pickable[O,List[O]] {
 override def viewObj: List[O] = m.sortBy(_.toString)

 override def display(a: O): String = s"${a}"

 override def pick(name: String): Option[O] = {
  viewObj.find{it=>it.toString.toLowerCase()==name.toLowerCase}
 }
}

object ViewAndPickTools {
 implicit def map2Viewable(map:Map[String, Path]):MapViewerable = MapViewerable(map.map{it=>(it._1,it._2.toString)})
 implicit def listPath2Venerable(it:List[Path]):List2Viewable[Path] = List2Viewable(it)
}


