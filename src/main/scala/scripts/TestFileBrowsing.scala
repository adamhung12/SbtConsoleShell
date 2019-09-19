package scripts

class TestFileBrowsing {

}
object TestFileBrowsing{
  def main(args: Array[String]): Unit = {
    import me.xethh.console.tools.StringConversionExt._
    import me.xethh.console.tools.FileStatusUtils._
    import me.xethh.console.tools.FileTraverseUtils._
    val path="D:\\Users\\xcwhung\\cust\\test".p.checkExist.checkIsDirector.toPath
    val x1 = path.tree()
    val x2 = path.tree(1)
    val x3 = path.tree(2)
    val x4 = path.tree(3)
    val x5 = path.cd("sss")



    println("hehi")




  }
}
