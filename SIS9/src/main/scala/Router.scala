import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._

import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

class Router(val producer: NumberProducer)
            (implicit system: ActorSystem[_],  ex: ExecutionContext) {

  def route: Route = {
    concat(
      path("ping") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "pong"))
        }
      },
      path("produce") {
        get {
          complete(producer.produce())
        }
      },
      path("receive") {
        post {
          entity(as[Number]) { number => {
            system.log.info("Received: {}", number.value)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "logged info"))
          }
          }
        }
      }
    )
  }
}