package pojo

import javax.validation.constraints.NotNull
import scala.collection.JavaConversions._


class WebLogin{
	
	var login_id:String = _

	@NotNull
	var url:String = _
	
	@NotNull
	var login:String = _
	
	@NotNull
	var password:String = _

	def getLoginid():String = login_id

	def getUrl():String  = url

	def getLogin():String = login

	def getPassword():String = password

	def setLoginid(login_id:String)
	{
		this.login_id = login_id
	}

	def setUrl(url:String)
	{
		this.url = url
	}

	def setLogin(login:String)
	{
		this.login = login
	}

	def setPassword(password:String)
	{
		this.password = password
	}

}