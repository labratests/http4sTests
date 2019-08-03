package es.weso.http4sTests

import fs2.Stream
import cats._
import cats.implicits._
import cats.effect._

case class UserRepoInMemory[F[_]:Monad: Sync](
  private val table: Map[String, User]
) extends UserRepo[F] {

  def find(userId: String): F[Option[User]] =
    Monad[F].pure(table.get(userId))

  def findAll: Stream[F,User] =
    Stream.fromIterator(table.valuesIterator)

}

object UserRepoInMemory {
  def empty[F[_]: Monad: Sync] = UserRepoInMemory[F](Map[String,User]())

  def fromMap[F[_]:Monad: Sync](map: Map[String,User]) =
    UserRepoInMemory[F](map)
}




