package models.relation

import scalikejdbc._

case class Relation ( taskId:Int,
                      contactId: Int){
  def save()(implicit session: DBSession = Relation.autoSession): Relation = Relation.save(this)(session)

  def destroy()(implicit session: DBSession = Relation.autoSession): Unit = Relation.destroy(this)(session)
}

object Relation extends SQLSyntaxSupport[Relation] {

  override val tableName = "relation"

  override val columns = Seq(
    "task_id",
    "contact_id")

  val r = Relation.syntax("r")

  override val autoSession = AutoSession

  def find(taskId: Int)(implicit session: DBSession = autoSession): List[Relation] = {
    withSQL {
      select.from(Relation as r).where.eq(r.taskId, taskId)
    }.map(Relation(r.resultName)).list.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Relation] = {
    withSQL(select.from(Relation as r)).map(Relation(r.resultName)).list.apply()
  }

  def findByIds(taskId: Option[Int], contactId: Option[Int])(implicit session: DBSession = autoSession) =
    withSQL {
      select.from(Relation as r)
        .where(sqls.toAndConditionOpt(
          taskId.map(taskId => sqls.eq(r.taskId, taskId)),
          contactId.map(contactId => sqls.eq(r.contactId, contactId))
        ))
    }.map(Relation(r.resultName)).single.apply()

  def create( taskId: Int,
              contactId: Int)(implicit session: DBSession = autoSession): Relation = {
    withSQL {
      insert.into(Relation).columns(
        column.taskId,
        column.contactId
      ).values(
        taskId,
        contactId
      )
    }.update.apply()
    Relation(
      taskId = taskId,
      contactId = contactId)
  }

  def save(entity: Relation)(implicit session: DBSession = autoSession): Relation = {
    withSQL {
      update(Relation).set(
        column.taskId -> entity.taskId,
        column.contactId -> entity.contactId
      ).where.eq(column.taskId, entity.taskId)
    }.update.apply()
    entity
  }

  def destroy(entity: Relation)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Relation).
        where.eq(column.taskId, entity.taskId)
        .and
        .eq(column.contactId, entity.contactId)
    }.update.apply()
  }

  def apply(relation: SyntaxProvider[Relation])(rs: WrappedResultSet):Relation =
    apply(relation.resultName)(rs)

  def apply(relation: ResultName[Relation])(rs:WrappedResultSet):Relation = {
    Relation(
      taskId = rs.get(relation.taskId),
      contactId = rs.get(relation.contactId)
    )
  }
}