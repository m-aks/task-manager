package models.contact

import play.api.libs.json.Json

case class ContactData( id:Int,
                        name: String,
                        number: String){

  def create: Contact = Contact.create(name, number)

  def update: Int => Contact =
    id => Contact(id, name, number).save()
}

object ContactData {

  implicit val contactDataReads = Json.reads[ContactData]

  implicit val contactDataWrites = Json.writes[ContactData]

  def fromContact(c: Contact): ContactData = ContactData(c.id,c.name, c.number)
}