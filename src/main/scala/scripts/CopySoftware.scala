package scripts

import me.xethh.console.tools.{BackupUtils, MultiCopiesStrategyInstance, ReplacingStrategyInstance}


object CopySoftware {
  def main(args: Array[String]): Unit = {
//    CopyUtils.copy(
//      "C:\\Temp\\xeth\\backup tools\\source\\software",
//      Array("C:\\Temp\\xeth\\backup tools\\dest1\\software","C:\\Temp\\xeth\\backup tools\\dest2\\software")
//    )

    new BackupUtils("E:\\nextcloud",Some("Softwares"),
      Array(
//        ReplacingStrategyInstance("C:\\Temp\\xeth\\backup tools\\dest1\\"),
        MultiCopiesStrategyInstance("D:\\Users\\xcwhung\\software_bk", 8),
        MultiCopiesStrategyInstance("Q:\\bk", 8)
      ),
      None
    )

  }
}
