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

class MyServer[F[_]:ConcurrentEffect: Timer](host: String, port: Int)(implicit F: Effect[F], cs: ContextShift[F]) {

  private val logger = getLogger

  logger.info(s"Starting RDFShape on '$host:$port'")

  def routesService(blocker: Blocker): HttpRoutes[F] =
    CORS(HelloService[F](blocker).routes)

  def httpApp(blocker: Blocker): HttpApp[F] =
    routesService(blocker).orNotFound

  def resource: Resource[F, Server[F]] =
    for {
      blocker <- Blocker[F]
      server <- BlazeServerBuilder[F]
        .bindHttp(8080)
        .withHttpApp(httpApp(blocker))
        .resource
    } yield server

}

object MyServer extends IOApp {
  val ip = "0.0.0.0"
  val port = envOrNone("PORT") map (_.toInt) getOrElse (8080)

  override def run(args: List[String]): IO[ExitCode]  =
    new MyServer[IO](ip,port).resource.use(_ => IO.never).as(ExitCode.Success)

}
