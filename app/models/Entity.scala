package models

import scalikejdbc._
//error: super constructor cannot be passed a self reference unless parameter is declared by-name
case class Entity[A] (entity:Entity[A]) extends SQLSyntaxSupport[A] {

  override val autoSession = AutoSession

  val e = entity.syntax("e")

  def find(id: Int)(implicit session: DBSession = autoSession): Option[A] = {
    withSQL {
      select.from(entity as e).where.eq(e.id, id)
    }.map(entity(e.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[A] = {
    withSQL(select.from(entity as e)).map(entity(e.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long= {
    withSQL(select(sqls"count(1)").from(entity as e)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[A] = {
    withSQL {
      select.from(entity as e).where.append(sqls"$where")
    }.map(entity(e.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[A] ={
    withSQL {
      select.from(entity as e).where.append(sqls"$where")
    }.map(entity(e.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long ={
    withSQL {
      select(sqls"count(1)").from(entity as e).where.append(sqls"$where")
    }.map(_.long(1)).single.apply().get
  }

  //def create()(implicit session: DBSession = autoSession):A = ???

  def save(entity: A)(implicit session: DBSession = autoSession): A = ???

  def destroy(entity: A)(implicit session: DBSession = autoSession): Unit = ???

  def apply(entity: SyntaxProvider[A])(rs: WrappedResultSet):A = {
    apply(entity.resultName)(rs)
  }

  def apply(entity: ResultName[A])(rs:WrappedResultSet):A = ???
}
