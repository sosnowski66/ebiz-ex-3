package com.example

import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.request.Request
import com.slack.api.bolt.request.RequestHeaders
import com.slack.api.bolt.response.Response
import com.slack.api.bolt.util.QueryStringParser
import com.slack.api.bolt.util.SlackRequestParser
import com.slack.api.model.event.AppMentionEvent
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.util.*

val appConfig = AppConfig()
val requestParser = SlackRequestParser(appConfig)
val app = App(appConfig)


fun Routing.homeRoute() {
    val data = Data()

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        println(payload.event.text)
        ctx.ack()
    }

    app.command("/categories") {_, ctx ->
        ctx.ack(combineListString(data.categories))
    }

    app.command("/products") {req, ctx ->
        if (data.products.containsKeyIgnoreCase(req.payload.text)) {
            ctx.ack(data.products.getIgnoreCase(req.payload.text)?.let { combineListString(it) })
        } else {
            ctx.ack("Nieprawidłowa kategoria: ${req.payload.text}")
        }
    }


    post("/commands") {
        respond(call, app.run(parseRequest(call)))
    }
}

suspend fun parseRequest(call: ApplicationCall): Request<*> {
    val body = call.receiveText()
    return requestParser.parse(
        SlackRequestParser.HttpRequest.builder()
            .requestUri(call.request.uri)
            .queryString(QueryStringParser.toMap(call.request.queryString()))
            .requestBody(body)
            .headers(RequestHeaders(call.request.headers.toMap()))
            .remoteAddress(call.request.origin.remoteHost)
            .build()
    )
}

suspend fun respond(call: ApplicationCall, slackResp: Response) {
    for (header in slackResp.headers) {
        for (value in header.value) {
            call.response.header(header.key, value)
        }
    }

    call.response.status(HttpStatusCode.fromValue(slackResp.statusCode))
    if (slackResp.body != null) {
        call.respond(TextContent(slackResp.body, ContentType.parse(slackResp.contentType)))
    }
}

fun combineListString(elements: Iterable<String>): String = elements.joinToString("\n") { "• $it" }

fun Map<String, List<String>>.containsKeyIgnoreCase(key: String) =
    this.keys.map { it.lowercase() }.contains(key.lowercase())

fun Map<String, List<String>>.getIgnoreCase(key: String) = this.get(key.lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })