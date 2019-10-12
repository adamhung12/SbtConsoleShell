package me.xethh.console.sqlTools

import java.sql.ResultSet

import me.xethh.libs.toolkits.sql.ResultSetIterable
import me.xethh.utils.JDBCProvider.JDBCProviderFactory

import scala.collection.JavaConverters._

case class SqlServerDBViewer(connStr:String, username:String, password:String, db:String) {
  val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  implicit def resultSet2ResultIterable(rs:ResultSet) = new ResultSetIterable(rs)
  val provider = {
    JDBCProviderFactory.createPersistedProvider(driverClass, s"$connStr;databaseName=$db", username, password)
  }

  def apply():Map[Int, String] = {
    val conn = provider.getConnection
    conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery().iterator().asScala.map{it=>
      it.getString("TABLE_SCHEMA")+"."+it.getString("TABLE_NAME")
    }.zipWithIndex.map(it=>(it._2, it._1)).toMap
  }

  def apply(id:Int):SqlServerTableViewer = {
    SqlServerTableViewer(connStr, username, password, db, apply()(id))
  }

  override def toString: String = apply().toList.sortBy(_._1).map{it=>s"${it._1}\t${it._2}"}.mkString("\n")

}

