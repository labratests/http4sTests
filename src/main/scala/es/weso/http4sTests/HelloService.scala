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


class HelloService[F[_]](blocker: Blocker
                        )(implicit F: Effect[F], cs: ContextShift[F], t: Timer[F])
  extends Http4sDsl[F] {

  def routes(implicit timer: Timer[F]): HttpRoutes[F] =
    Router[F](
      "" -> staticRoutes.combineK(rootRoutes)  // TODO: <+> could be used instead of combineK but if gives an error
    )

  private val views: HttpRoutes[F] =
    fileService(Config(systemPath = "/static", blocker = blocker))

  def staticRoutes = resourceService[F](ResourceService.Config("/static", blocker))

  def dripString: Stream[F, String] =
    Stream.awakeEvery[F](100.millis).map(_.toString)

  def users: Stream[F, User] =
    Stream.awakeEvery[F](100.millis).map(s =>
      User("pepe" + s,s.toMinutes.toInt)
    )

  def asJsonArray(users: Stream[F,User]): Stream[F, String] =
    Stream.emit("[") ++
      users.map(_.asJson.noSpaces).intersperse(",") ++
      Stream.emit("]")

  def rootRoutes: HttpRoutes[F] = {
    println(s"rootRoutes...")
    HttpRoutes.of[F] {

      case GET -> Root => Ok(s"Testing http4s")

      case GET -> Root / "hi" / name =>
        Ok(html.index(name))

      case GET -> Root / "strings" =>
        Ok(dripString)

      case GET -> Root / "users" =>
        Ok(asJsonArray(users))

    }
  }

}

object HelloService {
 def apply[F[_]: Effect: ContextShift: Timer](blocker: Blocker): HelloService[F] =
    new HelloService[F](blocker)
}
