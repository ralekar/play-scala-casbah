package model

import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory

object PersistentStore {
  private val config = ConfigFactory.load()
  private val client =  MongoClient("localhost", 27017)
  def getDatabase(db : String ) = client(db)
  def close() = client.close()
}


