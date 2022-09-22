package com.example.springwebbug

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/my"],
    produces = [MediaType.APPLICATION_JSON_VALUE],
    headers = ["Accept=" + MediaType.APPLICATION_JSON_VALUE]
)
class Controller{

    @PostMapping
    fun countries(
        @RequestBody bytes: ByteArray
    ): String {
        return "Success"
    }

}