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
import scala.util.Properties.envOrNone
import org.log4s.getLogger
import cats.implicits._
import cats.effect._
import org.http4s.twirl._
import es.weso._
import org.http4s.server.staticcontent._


class HelloService[F[_]](blocker: Blocker)(implicit F: Effect[F], cs: ContextShift[F])
  extends Http4sDsl[F] {

  def routes(implicit timer: Timer[F]): HttpRoutes[F] =
    Router[F](
      "" -> staticRoutes.combineK(rootRoutes)  // TODO: <+> could be used instead of combineK but if gives an error
    )

  private val views: HttpRoutes[F] =
    fileService(Config(systemPath = "/static", blocker = blocker))

  def staticRoutes = resourceService[F](ResourceService.Config("/static", blocker))

  def rootRoutes(implicit timer: Timer[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case GET -> Root => Ok(s"Testing http4s")

      case GET -> Root / "hi" =>
        // Ok(html.index())
        Ok("Hello")
    }

}

object HelloService {
 def apply[F[_]: Effect: ContextShift](blocker: Blocker): HelloService[F] =
    new HelloService[F](blocker)
}
