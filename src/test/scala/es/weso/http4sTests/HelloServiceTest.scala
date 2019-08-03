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
import cats.effect._
import fs2._
import cats.implicits._
import org.http4s.twirl._
import es.weso._
import org.http4s.server.staticcontent._
import org.scalatest._

import scala.concurrent.ExecutionContext


class HelloServiceTest extends FunSpec with Matchers {

  val ip = "0.0.0.0"
  val port = 8080
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def serve(req: Request[IO]): IO[Response[IO]] = {
    val blocker = Blocker[IO]
    blocker.use { case (b) =>
      HelloService[IO](b).routes.orNotFound.run(req)
    }
  }

  describe(s"Simple") {
   it(s"Should add 2 numbers") {
    (1+1) should be(2)
   }
  }

  describe(s"Simple request") {
    it(s"Should GET hi") {
      val request = Request[IO](method = Method.GET, uri = uri"/hi" )
      val bodyResponse: Stream[IO,String]  = serve(request).unsafeRunSync.bodyAsText
      bodyResponse.compile.toList.unsafeRunSync should be(List("Hello"))
    }
  }

}
