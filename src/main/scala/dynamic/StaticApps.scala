package dynamic

case class Apps(name:String){
  def start:Unit = Runtime.getRuntime.exec(s"$name")
}
object StaticApps {
  implicit def str2App(app:String):Apps=Apps(app)
}
