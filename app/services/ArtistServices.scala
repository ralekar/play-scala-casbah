package services

import com.google.inject.{ImplementedBy, Inject}
import model.{Artist, ArtistDAO, Temp}
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[ArtistServicesImpl])
trait ArtistServices {
  def add (discActor : Artist) : Boolean
  def findAllByType(value: String)  : ArtistResponse
}

case class ArtistResponse(artists : List[Artist])

object ArtistResponse {

  //implicit val formatter1 = Json.format[Artist]
  implicit val formatter2 = Json.format[ArtistResponse]

  implicit val implicitFooWrites = new Writes[ArtistResponse] {
    def writes(discActors : ArtistResponse): JsValue = {
      Json.obj(
        "artists" -> discActors.artists
      )
    }
  }
}

class ArtistServicesImpl @Inject()(dao: ArtistDAO )(implicit ec: ExecutionContext) extends ArtistServices  {

  override def add(artist : Artist ): Boolean = {
    if(dao.addNew(artist)!=null)
       true
    else
      false
  }

  override def findAllByType(value: String): ArtistResponse = {
    println("Find Artist by Name: " + value)
    ArtistResponse(dao.getAllByName(value).filter(a => a.isDefined).map(a => a.get))
  }

}