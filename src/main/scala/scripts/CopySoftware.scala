package scripts



object CopySoftware {
  def main(args: Array[String]): Unit = {
//    CopyUtils.copy(
//      "C:\\Temp\\xeth\\backup tools\\source\\software",
//      Array("C:\\Temp\\xeth\\backup tools\\dest1\\software","C:\\Temp\\xeth\\backup tools\\dest2\\software")
//    )

    import me.xethh.console.tools.{BackupUtils, MultiCopiesStrategyInstance, ReplacingStrategyInstance}
    new BackupUtils("E:\\nextcloud",Some("Softwares"),
      Array(
        MultiCopiesStrategyInstance("D:\\Users\\xcwhung\\software_bk", 8),
        MultiCopiesStrategyInstance("Q:\\bk", 8)
      ),
      None
    )

  }
}
