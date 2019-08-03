package es.weso.http4sTests
import fs2.Stream

trait UserRepo[F[_]] {
  def find(userId: String): F[Option[User]]
  def findAll: Stream[F,User]
}



