import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object ServerSample extends App {

  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  val rootBehavior = Behaviors.setup[Nothing] { context =>

    val publisher = new NumberProducer()(context.system, context.executionContext)
    val router = new Router(publisher)(context.system, context.executionContext)

    val host = "localhost"
    val port = Try(System.getenv("PORT")).map(_.toInt).getOrElse(8080)

    Server.startHttpServer(router.route, host, port)(context.system, context.executionContext)
    Behaviors.empty
  }

  implicit val system: ActorSystem[Nothing] = ActorSystem[Nothing](rootBehavior, "RandomNumberGeneratorService")
}