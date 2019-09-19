package dynamic

import java.nio.file.Path

import me.xethh.console.tools.Sys

object StaticPaths {
  import scripts.PredefScript._
  import me.xethh.console.tools.StringConversionExt._
  import me.xethh.console.tools.FileTraverseUtils._
  val rootDrive:Path = "/".p

}
