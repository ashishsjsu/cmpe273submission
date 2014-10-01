package main

import controller.WalletController;
import org.springframework.boot.SpringApplication
import scala.collection.JavaConversions._


object Application{

  def main(args: Array[String]) {
    SpringApplication.run(classOf[WalletController])
  }
}