package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

case class Person(name: String, surname:String, phone:String, address:String)
case class People(people: Seq[Person])

object AddressBook{
  sealed trait Command
  case class GetPeople(replyTo: ActorRef[People]) extends Command
  case class CreatePerson(person: Person, replyTo: ActorRef[ActionPerformed]) extends Command
  case class GetPerson(name: String, replyTo: ActorRef[GetPersonResponse]) extends Command
  case class DeletePerson(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetPersonResponse(maybePerson: Option[Person])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(people: Set[Person]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetPeople(replyTo) =>
        replyTo ! People(people.toSeq)
        Behaviors.same
      case CreatePerson(person, replyTo) =>
        replyTo ! ActionPerformed(s"Person ${person.name} created.")
        registry(people + person)
      case GetPerson(name, replyTo) =>
        replyTo ! GetPersonResponse(people.find(_.name == name))
        Behaviors.same
      case DeletePerson(name, replyTo) =>
        replyTo ! ActionPerformed(s"Person $name deleted.")
        registry(people.filterNot(_.name == name))
    }
}
