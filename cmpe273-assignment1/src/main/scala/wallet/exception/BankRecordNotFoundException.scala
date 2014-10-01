package exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.Exception
//remove if not needed
import scala.collection.JavaConversions._

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bank record not found")
class BankRecordNotFoundException(error: String) extends RuntimeException(error)
