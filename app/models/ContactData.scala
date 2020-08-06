package models

import play.api.libs.json.Json

case class ContactData( name: String,
                        number: String){

  def create: Contact = Contact.create(name, number)

  def update: Int => Contact =
    id => Contact(id, name, number).save()
}

object ContactData {

  implicit val contactDataReads = Json.reads[ContactData]

  implicit val contactDataWrites = Json.writes[ContactData]

  def fromContact(c: Contact): ContactData = ContactData(c.name, c.number)
}