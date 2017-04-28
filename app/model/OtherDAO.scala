package model

case class Song(title:String, comments:List[Comment]=List(), commentCount:Int=0,
                artists:List[Artist]=List(), artistCount: Int=0)
case class Instrument(name:String, proficiencyScore:Int)
case class Comment(commenter: String , comment:String)