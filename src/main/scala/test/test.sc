import java.math.BigInteger
import com.roundeights.hasher.Implicits._
import scala.compat.Platform
import scala.language.postfixOps
import org.apache.commons.codec.binary.{Base64}
def b64EncodeLong(num: Long): String = {
  new String(Base64.encodeInteger(BigInteger.valueOf(num)))
}
def hash(num: Long): (String, String) = {
  val b64 = b64EncodeLong(num)
  val hashed = b64.sha256.hex
  (b64, hashed)
}
val coins = scala.collection.mutable.Map[String, String]()
val start = Platform.currentTime
for(i <- 1 until 1000000) {
  val tuple = hash(i)
//  println(tuple)
  if(tuple._2.startsWith("000")) {
    coins += (tuple._1 -> tuple._2)
  }
}
val end = Platform.currentTime
end - start
coins
coins += "a key" -> "a value"

"00000000agtretea".startsWith("000")

//10,000,000 took about 436 seconds