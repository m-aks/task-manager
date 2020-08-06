package models

import play.api.libs.json.Json

case class TaskData(title: String,
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

  def fromTask(e: Task): TaskData = TaskData(e.title, e.description, e.isComplete)
}
