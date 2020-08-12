package models.task

import play.api.libs.json.Json

case class TaskData(id:Int,
                    title: String,
                    description: String,
                    isComplete: Boolean) {

  def create: Task =
    Task.create(title, description, isComplete)

  def update: Int => Task =
    id => Task(id, title, description, isComplete).save()

}

object TaskData {

  implicit val taskDataReads = Json.reads[TaskData]

  implicit val taskDataWrites = Json.writes[TaskData]

  def fromTask(e: Task): TaskData = TaskData(e.id, e.title, e.description, e.isComplete)
}
