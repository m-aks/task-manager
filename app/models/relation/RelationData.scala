package models.relation

import play.api.libs.json.Json

case class RelationData( taskId:Int,
                         contactId: Int){

  def create: Relation = Relation.create(taskId, contactId)

  def update: Int => Relation =
    id => Relation(taskId, contactId).save()
}

object RelationData {

  implicit val relationDataReads = Json.reads[RelationData]

  implicit val relationDataWrites = Json.writes[RelationData]

  def fromRelation(r: Relation): RelationData = RelationData(r.taskId,r.contactId)
}