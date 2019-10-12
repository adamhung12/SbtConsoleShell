package me.xethh.console.sqlTools

import java.sql.{ResultSet, ResultSetMetaData}

case class ColumnInfo(columnMeta:ResultSetMetaData) {
  case class ColumnInfoMeta(columnType:String, columnLabel:String, columnName:String)
  val meta = (for (col <- (1 to columnMeta.getColumnCount)) yield (
    col,
    columnMeta.getColumnTypeName(col),
    columnMeta.getColumnLabel(col),
    columnMeta.getColumnName(col)
  )).map(it=>(it._1,ColumnInfoMeta(it._2,it._3,it._4))).toList.sortBy(_._1)

  def apply(index:Int): ColumnInfo = ColumnInfo(columnMeta)

  override def toString = meta.map(it=>s"${it._1}\t${it._2.columnType}\t${it._2.columnLabel}\t${it._2.columnName}").mkString("\n")
}
