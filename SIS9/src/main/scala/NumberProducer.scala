import akka.Done
import akka.actor.typed.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

class NumberProducer()(implicit system: ActorSystem[_], ex: ExecutionContext) {

  val config: Config = system.settings.config.getConfig("akka.kafka.producer")
  val server: String = system.settings.config.getString("akka.kafka.producer.kafka-clients.server")
  val topic: String = system.settings.config.getString("akka.kafka.producer.kafka-clients.topic")
  val generator: Random.type = scala.util.Random

  val producerSettings: ProducerSettings[String, String] =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(server)

  def produce(): Future[String] = {
    val done: Future[Done] = {
      Source.single(generator.nextInt(100))
        .map(_.toString)
        .map(value => new ProducerRecord[String, String](topic, value))
        .runWith(Producer.plainSink(producerSettings))
    }
    done.onComplete {
      case Success(number) =>
        system.log.info(s"Published random number $number to topic $topic")
      case Failure(exception) =>
        system.log.error(exception.toString)
    }
    Future.successful("Request sent")
  }
}