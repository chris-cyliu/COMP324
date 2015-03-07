package common

/**
 * Function to parse query param
 * key : column
 *
 * a: 1
 * b: 2
 * c: 3
 * column[i]
 * column[i]
 */
object ArrayQueryParam {
  def apply(key:String, queryMap:Map[String,Seq[String]]):Map[Int,Map[String,String]] = {
    val regex_2d = s"$key\\[(.*)\\]\\[(.*)\\]".r
    val regex_3d = s"$key\\[(.*)\\]\\[(.*)\\]\\[(.*)\\]".r

    queryMap.keys.foldLeft(Map[Int,Map[String,String]]())({
      (last:Map[Int,Map[String,String]], next:String) =>
        val ret = collection.mutable.Map[Int,Map[String,String]]() ++ last
        next match {
          case regex_3d(_)=>
          case regex_2d(index:String , key:String) =>
            val kv= (ret.get(index.toInt) match{
              case Some(e) =>
                e
              case None =>
                Map[String,String]()
            }) + (key -> queryMap.get(next).get(0))
            ret.put(index.toInt ,kv)
          case _ =>
        }
        ret.toMap
    })
  }
}
