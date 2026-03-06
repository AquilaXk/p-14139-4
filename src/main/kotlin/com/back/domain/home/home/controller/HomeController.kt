package com.back.domain.home.home.controller

import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping("/setSession")
    fun session(
        session: HttpSession,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "") value: String,
    ): String {
        if (name.isBlank()) {
            return "parameter `name` is required"
        }

        if (value.isBlank()) {
            session.removeAttribute(name)

            return "session remove: $name"
        }

        session.setAttribute(name, value)
        return "session set: $name = $value"
    }

    @GetMapping("/session")
    fun session(session: HttpSession) = session
        .attributeNames
        .toList()
        .associateWith { session.getAttribute(it) }
}