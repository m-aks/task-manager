package models

import scalikejdbc._

case class Task( id: Int,
                 title: String,
                 description: String,
                 isComplete: Boolean){

  def save()(implicit session: DBSession = Task.autoSession): Task = Task.save(this)(session)

  def destroy()(implicit session: DBSession = Task.autoSession): Unit = Task.destroy(this)(session)
}

object Task extends SQLSyntaxSupport[Task] {

  override val tableName = "task"

  override val columns = Seq(
    "id",
    "title",
    "description",
    "is_complete")

  val t = Task.syntax("t")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Task] = {
    withSQL {
      select.from(Task as t).where.eq(t.id, id)
    }.map(Task(t.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Task] = {
    withSQL(select.from(Task as t)).map(Task(t.resultName)).list().apply()
  }

  def findByTitleAndDescription(titleOpt: Option[String], descOpt: Option[String])(implicit session: DBSession = autoSession) =
    if(titleOpt.isEmpty && descOpt.isEmpty) Left("No query parameters supplied")
    else Right(
      withSQL {
        select.from(Task as t)
          .where(sqls.toAndConditionOpt(
            titleOpt.map(title => sqls.eq(t.title, title)),
            descOpt.map(description => sqls.eq(t.description, description))
          ))
      }.map(Task(t.resultName)).list().apply()
    )

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Task as t)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Task] = {
    withSQL {
      select.from(Task as t).where.append(sqls"$where")
    }.map(Task(t.resultName)).single().apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Task] = {
    withSQL {
      select.from(Task as t).where.append(sqls"$where")
    }.map(Task(t.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Task as t).where.append(sqls"$where")
    }.map(_.long(1)).single().apply().get
  }

  def create(
              title: String,
              description: String,
              isComplete: Boolean)(implicit session: DBSession = autoSession): Task = {
    val generatedKey = withSQL {
      insert.into(Task).columns(
        column.title,
        column.description,
        column.isComplete
      ).values(
        title,
        description,
        isComplete
      )
    }.updateAndReturnGeneratedKey().apply()

    Task(
      id = generatedKey.toInt,
      title = title,
      description = description,
      isComplete = isComplete)
  }

  def save(entity: Task)(implicit session: DBSession = autoSession): Task = {
    withSQL {
      update(Task).set(
        column.id -> entity.id,
        column.title -> entity.title,
        column.description -> entity.description,
        column.isComplete -> entity.isComplete
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: Task)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(Task).where.eq(column.id, entity.id) }.update().apply()
  }

  def apply(task: SyntaxProvider[Task])(rs: WrappedResultSet):Task =
    apply(task.resultName)(rs)

  def apply(task: ResultName[Task])(rs:WrappedResultSet):Task = {
    Task(
      id = rs.get(task.id),
      title = rs.get(task.title),
      description = rs.get(task.description),
      isComplete = rs.get(task.isComplete),
    )
  }
}