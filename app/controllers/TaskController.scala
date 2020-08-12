package controllers

import javax.inject.Inject
import models._
import models.task.{Task, TaskData}
import play.api.libs.json.Json
import play.api.mvc._

class TaskController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def getTasks = Action { implicit request =>

    val tasks = Task.findAll()
    val tasksJson = Json.obj("tasks" -> tasks.map{ t =>
        val taskData = TaskData.fromTask(t)
        Json.toJson(taskData)
      }
    )
    Ok(tasksJson)
  }

  def getTask(id: Int) = Action { implicit request =>
    Task.find(id) match{
      case Some(task) =>
        val taskData = TaskData.fromTask(task)
        val taskJson = Json.toJson(taskData)
        Ok(taskJson)

      case None =>
        NotFound("The requested resource could not be found")
    }
  }

  def createTask = Action(parse.json) { implicit request =>
    request.body.validate[TaskData].fold(
      errors => BadRequest(Json.obj("error" -> "Not found!")),
      taskData =>{
        val task = taskData.create
        Created.withHeaders(LOCATION -> routes.TaskController.getTask(task.id).absoluteURL())
      }
    )
  }

  def editTask( id: Int) = Action(parse.json) { implicit request =>
    request.body.validate[TaskData].fold(
      errors => BadRequest(Json.obj("error" -> "Not found!")),
      taskData =>
        Task.find(id) match{
          case Some(task) => taskData.update(task.id); NoContent
          case None => NotFound(Json.obj("error" -> "The request resource could not be found"))
        }
    )
  }

  def deleteTask(id: Int) = Action(parse.empty) { implicit request =>
    Task.find(id) match{
      case Some(task) => task.destroy(); NoContent
      case None => NotFound(Json.obj("error" -> "The requested resource could not be found"))
    }
  }
}