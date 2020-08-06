package models

import scalikejdbc._

case class Contact(id: Int,
                   name: String,
                   number: String){

  def save()(implicit session: DBSession = Contact.autoSession): Contact = Contact.save(this)(session)

  def destroy()(implicit session: DBSession = Contact.autoSession): Unit = Contact.destroy(this)(session)
}

object Contact extends SQLSyntaxSupport[Contact] {

  override val tableName = "contact"

  override val columns = Seq(
    "id",
    "name",
    "number")

  val c = Contact.syntax("c")

  override val autoSession= AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Contact] = {
    withSQL {
      select.from(Contact as c).where.eq(c.id, id)
    }.map(Contact(c.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Contact] = {
    withSQL(select.from(Contact as c)).map(Contact(c.resultName)).list.apply()
  }

  def findByNameAndNumber(nameOpt: Option[String], numderOpt: Option[String])(implicit session: DBSession = autoSession) =
    if(nameOpt.isEmpty && numderOpt.isEmpty) Left("No query parameters supplied")
    else Right(
      withSQL {
        select.from(Contact as c)
          .where(sqls.toAndConditionOpt(
            nameOpt.map(name => sqls.eq(c.name, name)),
            numderOpt.map(number => sqls.eq(c.number, number))
          ))
      }.map(Contact(c.resultName)).list.apply()
    )

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Contact as c)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Contact] = {
    withSQL {
      select.from(Contact as c).where.append(sqls"$where")
    }.map(Contact(c.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Contact] = {
    withSQL {
      select.from(Contact as c).where.append(sqls"$where")
    }.map(Contact(c.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Contact as c).where.append(sqls"$where")
    }.map(_.long(1)).single.apply().get
  }

  def create(
              name: String,
              number: String)(implicit session: DBSession = autoSession): Contact = {
    val generatedKey = withSQL {
      insert.into(Contact).columns(
        column.name,
        column.number
      ).values(
        name,
        number
      )
    }.updateAndReturnGeneratedKey.apply()

    Contact(
      id = generatedKey.toInt,
      name = name,
      number = number)
  }

  def save(entity: Contact)(implicit session: DBSession = autoSession): Contact = {
    withSQL {
      update(Contact).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.number -> entity.number
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Contact)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(Contact).where.eq(column.id, entity.id) }.update.apply()
  }

  def apply(contact: SyntaxProvider[Contact])(rs: WrappedResultSet):Contact =
    apply(contact.resultName)(rs)

  def apply(contact: ResultName[Contact])(rs:WrappedResultSet):Contact = {
    Contact(
      id = rs.get(contact.id),
      name = rs.get(contact.name),
      number = rs.get(contact.number)
    )
  }
}
