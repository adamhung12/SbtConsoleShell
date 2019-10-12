package me.xethh.console.sqlTools

import java.sql.{ResultSet, ResultSetMetaData}

import me.xethh.libs.toolkits.sql.ResultSetIterable
import me.xethh.utils.JDBCProvider.JDBCProviderFactory

import scala.collection.JavaConverters._

case class SqlServerTableViewer(connStr:String, username:String, password:String, db:String, table:String, param:Array[Int]=Array.empty) {
  val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  implicit def resultSet2ResultIterable(rs:ResultSet) = new ResultSetIterable(rs)
  val provider = {
    JDBCProviderFactory.createPersistedProvider(driverClass, s"$connStr;databaseName=$db", username, password)
  }

  private def genSql(columnInfo: ColumnInfo,top1:Boolean) = {
    val paramSql =
      if(param.size==0) "*"
      else (
        param
          .map{
            index=>columnInfo.meta.filter{
              it=>it._1==index
            }.map{
              it=>it._2.columnName
            }.head
          }.mkString(", ")
        )
    s"select ${if(top1) "top 1" else ""} $paramSql from $table"
  }
  def list():ResultSet = provider.getConnection.prepareStatement(genSql(columnInfo,false)).executeQuery()
  def iterator():Iterator[ResultSet]=list().iterator().asScala

  def columnInfo:ColumnInfo = ColumnInfo(columnMeta)

  def columnCount:Int = columnMeta.getColumnCount
  def columnMeta:ResultSetMetaData = provider.getConnection.prepareStatement(s"select top 1 * from ${table}").executeQuery().getMetaData
  def apply(array:Array[Int]):SqlServerTableViewer={SqlServerTableViewer(connStr, username, password, db, table, array)}

  def view: String = iterator().map{it=>
    "|| "+(for(col <- (1 to (if(param.size==0) columnCount else param.size))) yield {
      it.getMetaData.getColumnTypeName(col) match{
        case "bigint" => s"${it.getInt(col)}"
        case "int" => s"${it.getInt(col)}"
        case "nvarchar" => s"'${it.getString(col)}'"
        case "varchar" => s"'${it.getString(col)}'"
        case "char" => s"'${it.getString(col)}'"
        case "datetime2" => s"${it.getDate(col)}"
        case "datetime" => s"${it.getDate(col)}"
        case x => throw new RuntimeException(s"Column Type[${x}] not support")
      }
    }).mkString(" | ")
  }.mkString("\n")

}

