package es.weso.http4sTests

import cats.effect._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.{FollowRedirect, Logger}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global


class WikidataService[F[_]](blocker: Blocker,
                           )(implicit F: ConcurrentEffect[F], cs: ContextShift[F], t: Timer[F])
  extends Http4sDsl[F] {

  implicit val clientResource = BlazeClientBuilder[F](global).resource
  val wikidataEntityUrl = "http://www.wikidata.org/entity/Q"

  def routes(implicit timer: Timer[F]): HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "hello" / name => Ok(s"Hello, $name!")

    case GET -> Root / "e" => Ok(s"Entity")

    case GET -> Root / "e" / entity => {
      println(s"GET entity $entity")
      val uri =  uri"http://www.wikidata.org/entity/" / ("Q" + entity)
      val req: Request[F] = Request(uri = uri)
      println(s"URI = $uri")
      clientResource.use { c => {
        val req: Request[F] = Request(Method.GET, uri)
        def cb(resp: Response[F]): F[Response[F]] = Ok(resp.bodyAsText)
        val redirectClient = Logger(true,true,_ => false)(FollowRedirect[F](10, _ => true)(c))
        redirectClient.fetch[Response[F]](req)(cb)
       }
      }
    }
  }
}




object WikidataService {
  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](blocker: Blocker): WikidataService[F] =
    new WikidataService[F](blocker)
}
