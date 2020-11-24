import scala.util.control.Breaks.break
import scala.io.StdIn.readChar
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }

object Calculatorscala {
  var x: Int = 0
  var y: Int = 0
  var sign: Char = '!'

  final case class Result(input: Char)

  def apply(): Behavior[Result] = Behaviors.receive { (context, message) =>
    val a = message.input

    if ( a >= '0' && a <= '9' ){
      y = (y * 10) + (a - '0')
    }
    else if( a == '=' ){
      val s = calculate(x, y, sign)
      println(s)
    }
    else if( a == '+' || a == '-' || a == '*' || a == '/'){
      if (sign == '!'){
        sign = a
        x = y
        y = 0
      }else{
        x = calculate(x, y, sign)
        sign = a
        y = 0
      }
    }
    Behaviors.same
  }

  def calculate(first: Int,second: Int,symbol: Char): Int ={
    var sum: Int = 0
    if(symbol == '+'){
      sum = first + second
    }
    if(symbol == '-'){
      sum = first - second
    }
    if(symbol == '*'){
      sum = first * second
    }
    if(symbol == '/'){
      if(second == 0){
        println("wrong input")
        break
      }else{
        sum = first / second
      }
    }
    sum
  }

}

object Calculation{
  final case class Input(replyTo: ActorRef[Calculatorscala.Result])

  def apply(): Behavior[Input] = Behaviors.receive { (context, message) =>
    while(true){
      val n = readChar()
      message.replyTo ! Calculatorscala.Result(n)
      if(n == '=') break
    }
    Behaviors.same
  }
}

object CalcMain {

  final case class calculate()

  def apply(): Behavior[calculate] =
    Behaviors.setup { context =>
      val started = context.spawn(Calculation(), "greeter")

      Behaviors.receiveMessage { message =>
        val c = context.spawn(Calculatorscala(), "calc1")
        started ! Calculation.Input(c)
        Behaviors.same
      }
    }

  def main(args: Array[String]) {
    val system: ActorSystem[CalcMain.calculate] = {
      ActorSystem(CalcMain(), "calc")
    }
    system ! CalcMain.calculate()
  }

}