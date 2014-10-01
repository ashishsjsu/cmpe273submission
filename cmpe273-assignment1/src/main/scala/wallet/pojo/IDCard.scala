package pojo

import javax.validation.constraints.NotNull
import scala.collection.JavaConversions._

class IDCard
{
	var cardid : String =_
	
	@NotNull
	var cardname : String = _
	
	@NotNull
	var cardnumber : Int = _
	
	var expirydate : String = _

	def getCardid():String = cardid

	def getCardname(): String = cardname

	def getCardnumber(): Int = cardnumber

	def getExpirydate():String = expirydate

	def setCardid(cardid:String)
	{
		this.cardid = cardid
	}

	def setCardname(cardname:String)
	{
		this.cardname = cardname
	}

	def setCardno(cardnumber:Int)
	{
		this.cardnumber = cardnumber
	}

	def setExpirydate(expirydate:String)
	{
		this.expirydate = expirydate
	}
	
}