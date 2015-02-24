package common

case class MissRequestParam(param_name:String) extends Exception(s"Missing param : $param_name")