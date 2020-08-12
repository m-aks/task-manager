package controllers

import javax.inject.Inject
import models._
import models.contact.{Contact, ContactData}
import play.api.libs.json.Json
import play.api.mvc._

class ContactController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def getContacts = Action { implicit request =>

    val contacts = Contact.findAll()
    val contactsJson = Json.obj("contacts" -> contacts.map{ c =>
      val contactData = ContactData.fromContact(c)
      Json.toJson(contactData)
    }
    )
    Ok(contactsJson)
  }

  def getContact(id:Int) = Action { implicit request =>
    Contact.find(id) match{
      case Some(contact) =>
        val contactData = ContactData.fromContact(contact)
        val contactJson = Json.toJson(contactData)
        Ok(contactJson)

      case None =>
        NotFound("The requested resource could not be found")
    }
  }

  def createContact = Action(parse.json) { implicit request =>
    request.body.validate[ContactData].fold(
      errors => BadRequest(Json.obj("error" -> "Not found!")),
      contactData =>{
        val contact = contactData.create
        Created.withHeaders(LOCATION -> routes.ContactController.getContact(contact.id).absoluteURL())
      }
    )
  }

  def editContact( id: Int) = Action(parse.json) { implicit request =>
    request.body.validate[ContactData].fold(
      errors => BadRequest(Json.obj("error" -> "Not found!")),
      contactData =>
        Contact.find(id) match{
          case Some(contact) => contactData.update(contact.id); NoContent
          case None => NotFound(Json.obj("error" -> "The request resource could not be found"))
        }
    )
  }

  def deleteContact(id: Int) = Action(parse.empty) { implicit request =>
    Contact.find(id) match{
      case Some(contact) => contact.destroy(); NoContent
      case None => NotFound(Json.obj("error" -> "The requested resource could not be found"))
    }
  }
}