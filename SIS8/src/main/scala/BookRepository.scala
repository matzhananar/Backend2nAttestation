import com.example.Person

import scala.concurrent.{ExecutionContext, Future}

trait sBookRepository {
  def all(): Future[Seq[Person]]
}

class InMemoryAddressBookRepository(people:Seq[Person] = Seq.empty)(implicit ex:ExecutionContext) extends AddressBookRepository{

  private var peopl : Vector[Person] = people.toVector
  override def all():Future[Seq[Person]] = Future.successful(peopl)
}
