package me.xethh.console.tools.control

object HoldObject {
  case class keep[A](a:A){
    def check(cond:A=>Boolean) = ver(if(cond(a)) Some(this.a) else None)
  }
  protected case class ver[A](optionA:Option[A]){
    def exec[B](oper:A=>B):Option[B] = if(optionA.isEmpty) None else Some(oper(optionA.get))
  }

  case class keep2[A,B](a:A, b:B){
    def check(cond:(A,B)=>Boolean) = ver2(if(cond(a,b)) Some((a,b)) else None)
  }
  protected case class ver2[A,B](optionA:Option[(A,B)]){
    def exec[C](oper:(A,B)=>C):Option[C] = if(optionA.isEmpty) None else Some(oper(optionA.get._1, optionA.get._2))
  }
}
