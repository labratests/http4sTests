package es.weso.http4sTests

import cats.implicits._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import cats.effect._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import fs2.Stream
import User.UserEncoder
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class UserService[F[_]](blocker: Blocker,
                        repo: UserRepo[F]
                       )(implicit F: Effect[F],cs: ContextShift[F], t: Timer[F]) {

//  implicit val userEncoder: EntityEncoder[F,Stream[F,User]] = ???
  // implicit val userEncoder: EntityEncoder[F,User] =

  def drip: Stream[F, String] =
    Stream.awakeEvery[F](100.millis).map(_.toString).take(10)

  def routes = HttpRoutes.of[F] {

    case GET -> Root / "user" / id =>
      repo.find(id).map {
        case Some(user) => Response(status = Status.Ok).withEntity(user.asJson)
        case None => Response(status = Status.NotFound)
      }

/*    case GET -> Root / "user" => {
      Ok("List of users")
    } */

    case POST -> Root / "user" =>
      ???
  }
}

object UserService {
  def apply[F[_]: Effect: ContextShift: Timer](blocker: Blocker,
                                        repo: UserRepo[F]
                                       ): UserService[F] =
    new UserService[F](blocker,repo)
}
