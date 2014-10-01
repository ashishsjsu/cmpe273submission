package pojo

import org.joda.time.DateTime
import javax.validation.constraints.NotNull
//remove if not needed
import scala.collection.JavaConversions._


class User {

  var user_id: String = _
 
  @NotNull
  var email: String = _

  @NotNull
  var password: String = _
  
  var createdAt: String = _

  def getId(): String = user_id

	def getEmail():String = email

	def getPassword():String = password

	def getCreatedAt():String = createdAt
 
  def setId(user_id: String) {
    this.user_id = user_id
  }

	def setEmail(email:String){
		this.email = email;
	}

	def setpassword(password:String){
		this.password = password;
	}
	
	def setCreatedAt(createdAt:String){
		this.createdAt = createdAt;
	}

   override def toString(): String = {"[user_id: " + user_id + ", email: " + email + ", password: " + password + 
      ", createdAt: " + createdAt.toString + "]" + "\n"
  }
 
}



