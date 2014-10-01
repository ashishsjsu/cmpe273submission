package controller

import pojo._
import exception._
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ResponseBuilder
import javax.ws.rs.core.CacheControl
import javax.ws.rs.core.EntityTag
import org.joda.convert.FromString
import org.joda.convert.ToString
import org.joda.convert
import org.joda.time.DateTime
import java.util.concurrent.atomic.AtomicLong
import org.springframework.web.bind.annotation._
import org.springframework.http.HttpStatus
import java.util.ArrayList
import scala.util.control.Breaks
import collection.mutable.{Set, Map, HashMap, MultiMap}
import scala.collection.JavaConversions._
import org.springframework.context.annotation.Configuration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import javax.validation.Valid
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletRequest


@Configuration
@EnableAutoConfiguration
@ComponentScan
@RestController
class WalletController{

  var etag: EntityTag = null
  val counter = new AtomicLong()
  val count = new AtomicLong()
  val logincount = new AtomicLong()
  val bankcount = new AtomicLong()
  var useral: ArrayList[User] = new ArrayList[User]()
  val cardmap = new HashMap[String, Set[IDCard]] with MultiMap[String, IDCard]
  val loginmap = new HashMap[String, Set[WebLogin]] with MultiMap[String, WebLogin]
  val bankmap = new HashMap[String, Set[BankAccount]] with MultiMap[String, BankAccount]
/****           USER  POST            ********/

  @RequestMapping(value = Array("api/v1/users"), method = Array(RequestMethod.POST), headers = Array("content-type=application/json"), consumes = Array("application/json"))
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  def addUser(@Valid @RequestBody user: User, result: BindingResult): User = {
   
    if (result.hasErrors()) {
      throw new ParameterMissingException(result.toString)
    } 
    else 
    {   	
      var userId = counter.incrementAndGet()
      user.setId("U-"+userId.toString())		
      val currentTime = DateTime.now;
      user.setCreatedAt(currentTime.toString("yyyy-mm-dd'T'hh-mm-ss.S'Z'"))
      useral.add(user)
      return user
    }
  }

/****           USER  PUT            ********/

    @RequestMapping(value = Array("/api/v1/users/{id}"), method=Array(RequestMethod.PUT), headers = Array("content-type=application/json"), consumes=Array("application/json"))
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	def updateUser(@Valid @RequestBody user:User, result: BindingResult, @PathVariable id: String):User = {

			var user1 : User = null
			var flag : Boolean = true
			val loop = new Breaks
			var index : Int = 0
			 
			if(result.hasErrors())
			{
					throw new ParameterMissingException(result.toString)	
			}
			else
			{
						var itr = useral.iterator
						loop.breakable
						{	
								while(itr.hasNext())
								{						
									user1 = itr.next()
									if(user1.user_id.equals(id))
									{
										flag = true
										user.user_id = user1.user_id
										user.createdAt = user1.createdAt
										useral.set(index, user);
										loop.break
									}
									else
									{					
										flag = false;
									}
									index = index + 1
							}
						}
						if(!flag)
						{
								throw new UserNotFoundException("User with user_id "+id+" not found")
						}
						else
						{
							return user
						}
			}
	}//user put

	/****           USER  GET            ********/

@RequestMapping(value = Array("/api/v1/users/{user_id}"), method=Array(RequestMethod.GET))
	@ResponseBody
	def getUser(@PathVariable user_id:String, @RequestHeader(value = "If-None-Match", required= false) ETag: String):ResponseEntity[_]={
	

		var httpresponseHeader:HttpHeaders = new HttpHeaders
		var cc: CacheControl = new CacheControl()
		var user: User = getUserInfo(user_id)

		cc.setMaxAge(86400)
		httpresponseHeader.setCacheControl(cc.toString())

		var tag: String = ETag
		println(tag)

		println("header1 :"+httpresponseHeader.toString())
		
		etag = new EntityTag(Integer.toString(user.hashCode()))
		httpresponseHeader.add("Etag", etag.getValue())
		println("header2 :"+httpresponseHeader.toString())

		
		if(etag.getValue().equalsIgnoreCase(tag))
		{
         	System.out.println("foo")
        	return new ResponseEntity[String]( null, httpresponseHeader, HttpStatus.NOT_MODIFIED )
        } 
        else 
        {
        	return new ResponseEntity[User](user, httpresponseHeader, HttpStatus.OK )  
        }
		
	}


	def getUserInfo(userid:String): User = {


		var flag : Boolean = false
		var user : User = null

		var itr = useral.iterator

		while(itr.hasNext())
		{
			user = itr.next()
			if(user.user_id.equals(userid))
			{
				flag = true
				return user
			}
			else
			{
				flag = false;
			}
		}
		if(!flag)
		{
			throw new UserNotFoundException("User with user_id "+userid+" not found")
		}
			
		return user 

	}


/****           IDCARD  POST            ********/

	@RequestMapping(value = Array("/api/v1/users/{userid}/idcards"), method = Array(RequestMethod.POST),headers=Array("content-type=application/json"), consumes=Array("application/json"))
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	def createIDCard(@Valid @RequestBody card:IDCard, result: BindingResult, @PathVariable userid:String):IDCard={
	 				
		if (result.hasErrors()) 
		{
      		throw new ParameterMissingException(result.toString)
       	}
       else
       {
       		var cardid= count.incrementAndGet()
       		card.setCardid("C-"+cardid.toString())
     		cardmap.addBinding(userid, card)
       		return card
       }
	}
/**************      ID Card GET         ***********/
	@RequestMapping(value = Array("/api/v1/users/{userid}/idcards"), method = Array(RequestMethod.GET), headers = Array("content-type=application/json"))
	def getIDCards(@PathVariable userid:String):Array[IDCard]=
	{
		return cardmap(userid).toArray;
			
	}
/************    ID Card DELETE ***********/
	@RequestMapping(value=Array("/api/v1/users/{userid}/idcards/{card_id}"), method=Array(RequestMethod.DELETE),headers=Array("content-type=application/json"))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def deleteIdCard(@PathVariable userid:String, @PathVariable card_id:String)
	{
		var userflag:Boolean = false
		var cardflag:Boolean = false


		cardmap.keys.foreach(key => {

				if(key == userid){
					userflag = true	
					cardmap(key).foreach(cardvalue => {

							if(cardvalue.cardid == card_id){
								cardmap.removeBinding(key, cardvalue)
								cardflag = true
								return
							}
						})
				}

			})

				if(!userflag){
					throw new UserNotFoundException("User with user_id "+userid+" not found")
				}
				else if(!cardflag){
					throw new CardNotFoundException("Card with card_id "+card_id+" not found")
				}

	}

/**********        Weblogins POST     *************/
	@RequestMapping(value=Array("/api/v1/users/{user_id}/weblogins"), method=Array(RequestMethod.POST), headers=Array("content-type=application/json"), consumes=Array("application/json"))
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	def addWebLogin(@Valid @RequestBody weblogin:WebLogin, result:BindingResult, @PathVariable user_id:String): WebLogin ={

		if(result.hasErrors)
		{
			throw new ParameterMissingException(result.toString())
		}
		else
		{
			var login_id = logincount.incrementAndGet()
			weblogin.setLoginid("l-" + login_id.toString())
			loginmap.addBinding(user_id, weblogin)
		}

		return weblogin

	}//addWebLogin
	
/***************     Weblogins GET  ****************/	
	@RequestMapping(value=Array("/api/v1/users/{user_id}/weblogins"), method=Array(RequestMethod.GET), headers=Array("content-type=application/json"))
	def getWebLogins(@PathVariable user_id:String): Array[WebLogin]=
	{
		return loginmap(user_id).toArray;
	}

/**************
*    WebLogins DELETE   ************/

	@RequestMapping(value=Array("/api/v1/users/{user_id}/weblogins/{loginid}"), method = Array(RequestMethod.DELETE), headers=Array("content-type=application/json"))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def deleteWebLogin(@PathVariable user_id:String, @PathVariable loginid:String)
	{
		var userflag:Boolean = false
		var loginflag:Boolean = false

		loginmap.keys.foreach(key =>{
			if(key == user_id){
				userflag = true
				loginmap(key).foreach(login => {

					if(login.login_id == loginid){
						loginmap.removeBinding(key, login)
						loginflag = true
						return
					}
				})
	
			}
		})

			if(!userflag){
				throw new UserNotFoundException("User with user_id "+user_id+" not found")

			}
			else if(!loginflag){
				throw new LoginNotFoundException("Login Id "+ loginid+ " not found")
			}

	}

	/********* Bank Account POST ***********/

	@RequestMapping(value=Array("/api/v1/users/{user_id}/bankaccounts"), method=Array(RequestMethod.POST), headers=Array("content-type=application/json"), consumes=Array("application/json"))
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	def addBankAccount(@Valid @RequestBody bankaccount: BankAccount, result:BindingResult, @PathVariable user_id: String): BankAccount=
	{
		if(result.hasErrors())
		{
			throw new ParameterMissingException(result.toString())
		}
		else
		{
			var bankid = bankcount.incrementAndGet()
			bankaccount.setBankid("b-"+bankid)
			bankmap.addBinding(user_id, bankaccount)
		}

		return bankaccount
	}

	/*********** Bank Account GET **********************/

	@RequestMapping(value=Array("/api/v1/users/{user_id}/bankaccounts"), method = Array(RequestMethod.GET), headers=Array("content-type=application/json"))
	def getBankAccounts(@PathVariable user_id:String) : Array[BankAccount]=
	{
		return bankmap(user_id).toArray
	}

	/************* Bank Account Delete ****************/

	@RequestMapping(value=Array("/api/v1/users/{userid}/bankaccounts/{bank_id}"), method=Array(RequestMethod.DELETE), headers=Array("content-type=application/json"))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def deleteBankAccount(@PathVariable userid:String, @PathVariable bank_id:String)
	{
		var userflag:Boolean = false
		var bankflag:Boolean = false

		bankmap.keys.foreach(key => {
			if(key == userid){
				userflag = true
				bankmap(key).foreach(bankrecord => {
					if(bankrecord.bank_id == bank_id){
						bankmap.removeBinding(key, bankrecord)
						bankflag = true
						return
					}
				})
			}

		})

		if(!userflag){
			throw new UserNotFoundException("User with id "+userid+" not found")
		}
		else if(!bankflag){
			throw new BankRecordNotFoundException("Bank record with id "+bank_id+" not found")
		}
	}


}//WalletController