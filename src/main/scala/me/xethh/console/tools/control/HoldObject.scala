package me.xethh.console.tools.control

object HoldObject {
  def let[A,B](a:A)= (oper:A=>B)=> if(a!=null) oper(a) else null
  def let2[A,B,C](a:A,b:B)= (oper:(A,B)=>C)=> oper(a,b)
  def let3[A,B,C,D](a:A,b:B,c:C)= (oper:(A,B,C)=>D)=> oper(a,b,c)
  def let3[A,B,C,D,E](a:A,b:B,c:C,d:D)= (oper:(A,B,C,D)=>E)=> oper(a,b,c,d)
  def let4[A,B,C,D,E,F](a:A,b:B,c:C,d:D,e:E)= (oper:(A,B,C,D,E)=>F)=> oper(a,b,c,d,e)

  case class keep[A](a:A){
    def check(cond:A=>Boolean) = ver(if(cond(a)) Some(this.a) else None)
    def exec[B](oper:A=>B):Option[B] = Option(oper(a))
    def checkIf(cond:A=>Boolean) = verIfTrue(a, cond(a))
  }
  protected case class ver[A](optionA:Option[A]){
    def exec[B](oper:A=>B):Option[B] = if(optionA.isEmpty) None else Some(oper(optionA.get))
  }
  protected case class verIfTrue[A,B](a:A,result:Boolean){
    def ifTrue(oper:A=>B) = verIfFalse(a, result, oper)
  }
  protected case class verIfFalse[A,B](a:A, result:Boolean, operTrue:A=>B){
    def ifTrue(operFalser:A=>B) = if(result) operTrue(a) else operFalser(a)
  }

  case class keep2[A,B](a:A, b:B){
    def check(cond:(A,B)=>Boolean) = ver2(if(cond(a,b)) Some((a,b)) else None)
    def exec[C](oper:(A,B)=>C):Option[C] = Option(oper(a,b))
  }
  protected case class ver2[A,B](optionA:Option[(A,B)]){
    def exec[C](oper:(A,B)=>C):Option[C] = if(optionA.isEmpty) None else Some(oper(optionA.get._1, optionA.get._2))
  }
}
