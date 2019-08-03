package es.weso.http4sTests
import com.typesafe.scalalogging._

object Main extends App with LazyLogging {
    try {
      run(args)
    } catch {
      case (e: Exception) => {
        println(s"Error: ${e.getMessage}")
      }
    }

  def run(args: Array[String]): Unit = {
    MyServer.main(args)
  }
}

