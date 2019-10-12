package me.xethh.console.sqlTools

object imports {
  trait ShowMap{
    def showMap:Unit
  }

  class ShowMapImpl(m:Map[Int, Any]) extends ShowMap {
    override def showMap: Unit = m.toList.sortBy(_._1).foreach(it=>println(s"${it._1}\t${it._2.toString}"))
  }

  implicit def map2ShowMap(m:Map[Int, Any])= new ShowMapImpl(m)

}
