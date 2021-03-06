package controllers

import java.util.Locale
import scala.concurrent._
import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.Future
import ExecutionContext.Implicits.global
import play.api._
import play.api.mvc._
import org.pac4j.core.profile._
import org.pac4j.play._
import org.pac4j.play.scala._
import play.api.libs.json.Json
import com.undeploy.play.auth.Secured

object Application extends Secured {

  def index = Authenticated(profile =>
    Action.async { request =>
      val locale = request2lang(request).toLocale
      Global.users.saveUser(locale, profile)
        .map { res => Ok(views.html.index(profile)) }
    },
    Action { request =>
      val newSession = getOrCreateSessionId(request)
      val urlGoogle = getRedirectAction(request, newSession, "Google2Client").getLocation()
      Ok(views.html.login(urlGoogle)).withSession(newSession)
    })

}