package controllers
import javax.inject._
import model.Artist
import play.api.mvc._
import play.api.libs.json.Json
import services.{ArtistServices}
import play.api.Logger

@Singleton
class ArtistController @Inject() (service : ArtistServices) extends Controller {

  def getArtist(name: String) = Action {
    val response = service.findAllByType(name)
    Logger.debug("Attempting risky calculation.")
    Ok(Json.toJson(response))

  }

  def addArtist = Action { request =>
    val json = request.body.asJson.get
    val stock = Json.fromJson[Artist](json)
    println(stock)
    stock.isSuccess match {
      case true => {
        service.add(stock.get);
        Ok("The Artist has been added !!!")
      }
      case _ => Ok("The Artist Didnt get Added!!")
    }
  }
}
