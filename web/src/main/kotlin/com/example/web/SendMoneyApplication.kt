package com.example.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SendMoneyApplication

fun main(args: Array<String>) {
    runApplication<SendMoneyApplication>(*args)
}
