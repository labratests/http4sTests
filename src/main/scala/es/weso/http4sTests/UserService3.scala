package es.weso.http4sTests

import cats._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import cats.effect._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.{BlazeBuilder, BlazeServerBuilder}
import org.http4s.server.middleware.CORS
import org.http4s.server.staticcontent.FileService.Config
import io.circe._
import io.circe.syntax._
import scala.util.Properties.envOrNone
import org.log4s.getLogger
import cats.implicits._
import cats.effect._
import org.http4s.twirl._
import es.weso._
import fs2.Stream
import io.circe.Json
import org.http4s.server.staticcontent._
import scala.concurrent.duration._
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder
import User.UserEncoder

class UserService3[F[_]](blocker: Blocker,
                         repo: UserRepo[F]
                       )(implicit F: Effect[F],cs: ContextShift[F], t: Timer[F])
 extends Http4sDsl[F] {

//  implicit val userEncoder: EntityEncoder[F,Stream[F,User]] = ???
  // implicit val userEncoder: EntityEncoder[F,User] =

  def drip: Stream[F, String] =
    Stream.awakeEvery[F](100.millis).map(_.toString).take(10)

  def routes: HttpRoutes[F] = HttpRoutes.of[F] {

  /*  case GET -> Root / "user" / id =>
      repo.find(id).map {
        case Some(user) => Response(status = Status.Ok).withEntity(user.asJson)
        case None => Response(status = Status.NotFound)
      } */

    case GET -> Root / "user" => {
      Ok("List of users")
    }

    case POST -> Root / "user" =>
      ???
  }
}

object UserService3 {
  def apply[F[_]: Effect: ContextShift: Timer](blocker: Blocker,
                                        repo: UserRepo[F]
                                       ): UserService3[F] =
    new UserService3[F](blocker,repo)
}
