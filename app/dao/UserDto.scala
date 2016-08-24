package dao

import scala.slick.driver.PostgresDriver

import scala.slick.driver.PostgresDriver.simple._
/**
  * Created by arcearta on 2016/08/16.
  */
case class UserDto ( idUser:   Int, userName:  String )

/*
case class Task(
                 taskId:   Int,
                 content:  String,
                 created:  DateTime,
                 finished: Boolean,
                 assignee: String
               )*/

abstract class Users(tag: Tag) extends Table[UserDto](tag, "users") {
  def id = column[Int]("id")
  def username = column[String]("username")
  //def * = (id, username)
 // def *   = (id ~ username) <> (UserDto, UserDto.unapply _)
}

/*
object TaskTable extends Table[Task]("tasks") {
  def taskId    = column[Int]     ("taskId", O.AutoInc, O.PrimaryKey, O.DBType("BIGINT"))
  def content   = column[String]  ("content", O.DBType("VARCHAR(50)"), O.NotNull)
  def created   = column[DateTime]("created", O.DBType("TIMESTAMP"), O.NotNull)
  def finished  = column[Boolean] ("finished", O.DBType("BOOLEAN"), O.NotNull)
  def assignee  = column[String]  ("assignee", O.DBType("VARCHAR(20)"), O.NotNull)

  def *         = (taskId ~ content ~ created ~ finished ~ assignee) <> (Task, Task.unapply _)

  def forInsert = (content ~ created ~ finished ~ assignee) returning taskId
}*/