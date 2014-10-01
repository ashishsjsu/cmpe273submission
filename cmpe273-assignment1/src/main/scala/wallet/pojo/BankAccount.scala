package pojo;

import javax.validation.constraints.NotNull
import scala.collection.JavaConversions._

class BankAccount{

	var bank_id:String = _

	var accountname:String = _ 

	@NotNull
    var accountnumber:String = _

	@NotNull
	var routingnumber:String = _

	def getBankid():String = bank_id

	def getAccountname():String = accountname

	def getAccountnumber():String = accountnumber

	def getRoutingnumber():String = routingnumber

	def setBankid(bank_id: String) = {
		this.bank_id = bank_id
	}

	def setAccountname(accountname: String) = {
		this.accountname = accountname
	}

	def setAccountnumber(accountnumber: String) = {
		this.accountnumber = accountnumber
	}

	def setRoutingnumber(routingnumber:String)={
		this.routingnumber = routingnumber
	}
}