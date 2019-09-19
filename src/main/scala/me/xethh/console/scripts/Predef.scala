package me.xethh.console.scripts

import java.util.Properties

import me.xethh.console.tools.{IndexedKeyPair, PropertiesViewer}
import scala.collection.JavaConverters._

object Predef {
 implicit def properties2PorpertiesViewer(properties: Properties) = PropertiesViewer(properties.entrySet().asScala
   .zipWithIndex.map{it=>IndexedKeyPair(it._2,it._1.getKey.asInstanceOf[String], it._1.getValue.asInstanceOf[String])}.toList.sortBy(_.index))
 implicit def mapToPropertiesViewer(map:java.util.Map[_,_]) = PropertiesViewer(map.asScala.zipWithIndex.map{it=>IndexedKeyPair(it._2,it._1._1.toString,it._1._2.toString)}.toList.sortBy(_.index))
 implicit def list2PropertiesViewer(list:List[IndexedKeyPair]): PropertiesViewer = PropertiesViewer(list.sortBy(_.index))

}
