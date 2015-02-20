package error

/**
 * Exception for mongodb exception
 */
class MongodbException(message:String) extends Exception(s"Mongodb operation fail, \n message: $message")
