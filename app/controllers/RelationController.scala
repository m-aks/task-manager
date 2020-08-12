package controllers

import javax.inject.Inject
import models.relation.{Relation, RelationData}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}

class RelationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def getAllRelations = Action { implicit request =>
    val relations = Relation.findAll()
    val relationsJson = Json.obj("relations" -> relations.map{ r =>
      Json.toJson(RelationData.fromRelation(r))
    }
    )
    Ok(relationsJson)
  }

  def getRelations(taskId:Int) = Action { implicit request =>
    val relations = Relation.find(taskId)
    val relationsJson = Json.obj("relations" -> relations.map{ r =>
      val relationData = RelationData.fromRelation(r)
      Json.toJson(relationData)
    }
    )
    Ok(relationsJson)
  }

  def creatRelation = Action(parse.json) { implicit request =>
    request.body.validate[RelationData].fold(
      errors => BadRequest(Json.obj("error" -> "Not found!")),
      relationData =>{
        val res = relationData.create
        Created(Json.toJson(relationData))
      }
    )
  }

  /*def editContact( id: Int) = Action(parse.json) { implicit request =>
    request.body.validate[ContactData].fold(
      errors => BadRequest(Json.obj("error" -> "Not found!")),
      contactData =>
        Contact.find(id) match{
          case Some(contact) => contactData.update(contact.id); NoContent
          case None => NotFound(Json.obj("error" -> "The request resource could not be found"))
        }
    )
  }*/

  def deleteRelation(taskId: Int, contactId: Int) = Action(parse.empty) { implicit request =>
    val tId = Some(taskId); val cId = Some(contactId)
    Relation.findByIds(tId,cId) match{
      case Some(relation) => relation.destroy(); NoContent
      case None => NotFound(Json.obj("error" -> "The requested resource could not be found"))
    }
  }

}
