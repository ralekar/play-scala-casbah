package model

import com.google.inject.{Inject, ImplementedBy}
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.Imports._
import play.api.libs.json.{JsValue, Writes, Json}


case class Temp ( test : List[Int] , age : String )
object Temp {
  implicit val formatter1 = Json.reads[Temp]
  val age = "age"
  val test = "test"
  implicit val implicitFooWrites = new Writes[Temp] {
    def writes(discActors : Temp): JsValue = {
      Json.obj(
        test -> discActors.test,
        age  -> discActors.age
      )
    }
  }

  def fromDBObject(obj: MongoDBObject): Temp = {
    val age1 = obj.getAs[String](age).get
    val test1 = obj.getAs[List[Int]](test).get
    Temp(test1 , age1)
  }

  def toDBObject(artist : Temp) : MongoDBObject = {
    MongoDBObject(
      age -> artist.age,
      test -> artist.test
    )
  }
}

case class Artist(name:String, band:String, fame:Temp, instrumentsCount:Int = 0)
object Artist{

  implicit val formatter1 = Json.reads[Artist]
  val name = "name"
  val band = "band"
  val fame = "fame"
  val instrumentsCount = "instrumentsCount"

  implicit val implicitFooWrites = new Writes[Artist] {
    def writes(discActors : Artist): JsValue = {
      Json.obj(
        name -> discActors.name,
        band -> discActors.band,
        fame -> discActors.fame,
        instrumentsCount -> discActors.instrumentsCount
      )
    }
  }

  def fromDBObject(obj: MongoDBObject): Artist = {
    val name1 = obj.getAs[String](name).get
    val band1 = obj.getAs[String](band).get
    val fame1 = Temp.fromDBObject(obj.getAs[DBObject](fame).get)
    val inscount = obj.getAs[Int](instrumentsCount).get
    Artist(name1, band1 , fame1 , inscount)
  }

  def toDBObject(artist : Artist) : MongoDBObject = {
       MongoDBObject(
         name -> artist.name,
         band -> artist.band,
         fame -> Temp.toDBObject(artist.fame),
         instrumentsCount -> artist.instrumentsCount
       )
  }


}

@ImplementedBy(classOf[ArtistDAOImpl])
trait ArtistDAO {

  def addNew(artist: Artist): Artist
  def getAllByName(name : String): List[Option[Artist]]
  def getByName(name : String): Option[Artist]
  def updateName(newName:String , bandname :String ):Boolean
  def deleteByName(naam : String) : Boolean

}

class ArtistDAOImpl @Inject() () extends  ArtistDAO {



  private val artistsCollection = PersistentStore.getDatabase("test").getCollection("artists")

  artistsCollection.createIndex(Artist.name)

  def addNew(artist: Artist): Artist = {

    val dBObject = Artist.toDBObject(artist)
    if(!getByName(artist.name).isDefined) {
      artistsCollection.insert(dBObject, WriteConcern.Safe)
    }else{
      println("Cannot Insert the Document as its not Unique!!")
    }
    artist
  }

  def getAllByName(name : String): List[Option[Artist]] = {

    val query = MongoDBObject(Artist.name -> name) // queries are in terms of MongoDBObject, which works almost like a map
    val obj = artistsCollection.find(query).iterator()
    val result  = collection.mutable.ListBuffer[Option[Artist]]()
    while(obj.hasNext){
      result.append(Some(Artist.fromDBObject(obj.next())))
    }
    result.toList
  }


  def getByName(name : String): Option[Artist] = {
    val query = MongoDBObject(Artist.name -> name) // queries are in terms of MongoDBObject, which works almost like a map
    val obj = artistsCollection.findOne(query)
    obj match {
      case o : DBObject => Some(Artist.fromDBObject(obj))
      case _ => None
    }

  }

  def updateName(newName:String , bandname :String ):Boolean={

    val query = MongoDBObject(Artist.name -> newName)
    val update = $set(Artist.band -> bandname)
    val result = artistsCollection.findAndModify(query , update)
    result match {
      case x:DBObject => true
      case _ => false
    }
  }

  def deleteByName(naam : String) : Boolean = {
    val query = MongoDBObject(Artist.name -> naam )
    artistsCollection.remove(query) match {
      case x:DBObject => true
      case _ => false
    }
  }

}