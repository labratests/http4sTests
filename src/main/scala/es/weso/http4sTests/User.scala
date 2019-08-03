package es.weso.http4sTests

import cats.implicits._
import io.circe._
import io.circe.syntax._
import cats.effect._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._

case class User(name: String,age: Int)

object User {
  implicit val UserEncoder: Encoder[User] = deriveEncoder[User]
}

