package controllers

import scala.concurrent.Future
import play.api._
import play.api.Play.current
import play.api.mvc._
import org.pac4j.play._
import org.pac4j.core.client._
import org.pac4j.core.context._
import org.pac4j.oauth.client._
import play.api.mvc.Results._
import com.undeploy.cassandra._
import com.undeploy.play.pac4j._
import com.undeploy.flipper.ApplicationContext
import com.undeploy.flipper.ApplicationContext
import com.undeploy.flipper.Users

object Global extends GlobalSettings {

  private var _context: ApplicationContext = null

  def context(): ApplicationContext = _context

  override def onStart(app: Application) = {
    Pac4j.init(app)

    val cass = for {
      conf <- app.configuration.getConfig("cassandra")
      hosts <- conf.getStringSeq("hosts")
      port <- conf.getInt("port")
      keyspace <- conf.getString("keyspace")
    } yield {
      Cassandra.migrate(keyspace, hosts, port)
    }

    _context = new ApplicationContext {
      val cassandra = cass.orNull
      val users = Users(cassandra)
    }

  }

  override def onStop(app: Application) = {
    context.cassandra.close()
  }
}