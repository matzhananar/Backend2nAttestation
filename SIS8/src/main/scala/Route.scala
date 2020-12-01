package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.example.AddressBook._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
// import JsonFormats._

class Route(val addressBookRepo : A)(implicit val system: ActorSystem[_]) {

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getPeople(): Future[People] =
    AddressBook.ask(GetPeople)
  def getPerson(name: String): Future[GetPersonResponse] =
    addressBook.ask(GetPerson(name, _))
  def createPerson(person: Person): Future[ActionPerformed] =
    addressBook.ask(CreatePerson(person, _))
  def deletePerson(name: String): Future[ActionPerformed] =
    addressBook.ask(DeletePerson(name, _))

  val routes: Route = {
    pathPrefix("people") {
      concat(
        pathEndOrSingleSlash {
          get {
            complete(AddressBoo)
          }
        }
      )
    }
  }
