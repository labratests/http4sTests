package es.weso.http4sTests

import cats.effect._
import cats.implicits._
import es.weso._
import es.weso.http4sTests.User.UserEncoder
import fs2.Stream
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.staticcontent.FileService.Config
import org.http4s.server.staticcontent._
import org.http4s.twirl._

import scala.concurrent.duration._


class UserService[F[_]](blocker: Blocker,
                        repo: UserRepo[F]
                        )(implicit F: Effect[F], cs: ContextShift[F], t: Timer[F])
  extends Http4sDsl[F] {

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

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {

      case GET -> Root / "users" =>
        Ok(asJsonArray(users))

      case GET -> Root / "user" =>
        Ok(asJsonArray(repo.findAll))

    }

}
object UserService {
  def apply[F[_]: Effect: ContextShift: Timer](
                                                blocker: Blocker,
                                                repo: UserRepo[F]
                                              ): UserService[F] =
    new UserService[F](blocker,repo)
}


