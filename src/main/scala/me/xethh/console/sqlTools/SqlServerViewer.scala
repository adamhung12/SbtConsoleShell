package me.xethh.console.sqlTools

import java.sql.ResultSet

import me.xethh.libs.toolkits.sql.ResultSetIterable
import me.xethh.utils.JDBCProvider.JDBCProviderFactory
import scala.collection.JavaConverters._

case class SqlServerViewer(connStr:String, username:String, password:String) {
  val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  implicit def resultSet2ResultIterable(rs:ResultSet) = new ResultSetIterable(rs)
  val provider = {
    JDBCProviderFactory.createPersistedProvider(driverClass, connStr, username, password)
  }

  def dbs():Map[Int, String] = {
    val conn = provider.getConnection
    conn.prepareStatement("SELECT name FROM master.dbo.sysdatabases").executeQuery().iterator().asScala.map{it=>
      it.getString("name")
    }.zipWithIndex.map(it=>(it._2, it._1)).toMap
  }


  override def toString: String = dbs().toList.sortBy(_._1).map{it=>s"${it._1}\t${it._2}"}.mkString("\n")

  def apply(id: Int): SqlServerDBViewer = SqlServerDBViewer(connStr, username, password, dbs()(id))
  def apply(name:String):SqlServerDBViewer =
    SqlServerDBViewer(connStr, username, password, dbs.map(_._2.toLowerCase).filter(_==name.toLowerCase).head)
}
object SqlServerViewer{
  def main(args: Array[String]): Unit = {
  }
}
