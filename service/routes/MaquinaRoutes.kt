package com.maquina.routes
import com.maquina.Maquina
import com.maquina.maquinaStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Time
import java.time.LocalTime

fun Route.maquinaRouting() {
    route("/machine-halt") {
        get {
            call.respondText { "Bem vindo novo estágiario Iam Henrique :) ?!" }
        }
        post{
            val maquina = call.receive<Maquina>()
                maquinaStorage.add(maquina)

            call.respondText("Maquina cadastrada com sucesso !", status = HttpStatusCode.Created)
        }

        post ("/{machine_tag?}/{start_time?}"){
            val tags = call.parameters["machine_tag"] ?: return@post call.respondText(
                "machine_tag invalida!",
                status = HttpStatusCode.BadRequest
            )
            val start = call.parameters["start_time"] ?: return@post call.respondText(
                "start_time invalido!",
                status = HttpStatusCode.BadRequest
            )

            val maquina =
                maquinaStorage.find { it.machine_tag == tags && it.start_time == start } ?: return@post call.respondText(
                    "Maquina com a $tags não encontrada!",
                    status = HttpStatusCode.NotFound
                )
            call.respond(maquina)
        }
        get("/{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Identificador não em encontrado!",
                status = HttpStatusCode.BadRequest
            )
            val maquina =
                maquinaStorage.find { it.id == id } ?: return@get call.respondText(
                    "Maquina com o  id: $id não encontrada!",
                    status = HttpStatusCode.NotFound
                )
            call.respond(maquina)
        }
        //está com falha, 500
        get("/list/{machine_tag?}/{interval_start?}/{interval_end?}"){
            val tags = call.parameters["machine_tag"] ?: return@get call.respondText(
                "Maquina machine_tag não encontrada!",
                status = HttpStatusCode.BadRequest
            )
            val interval_start = call.parameters["interval_start"] ?: return@get call.respondText(
                "Intervalo inicial não encontrado!",
                status = HttpStatusCode.BadRequest
            )
            val interval_end = call.parameters["interval_end"] ?: return@get call.respondText(
                "Intervalo final não encontrado!",
                status = HttpStatusCode.BadRequest
            )
            var time_start = Time.valueOf(interval_start)
            var time_end = Time.valueOf(interval_end)
            val maquina = maquinaStorage.find { it.machine_tag == tags }
            var recebe_start = Time.valueOf(maquina?.start_time)
            var recebe_end = Time.valueOf(maquina?.end_time)


                if(maquina != null && recebe_start != null && recebe_end != null) {

                    if (time_start >= recebe_start && time_end <= recebe_end) {
                        call.respond((maquina.toString()))
                        call.respondText("Maquina e seu intervalo encontrado com sucesso", status = HttpStatusCode.OK)

                    }
                        call.respond(maquina)
                        call.respondText(
                            "intervalo não encotrado! $time_start && $time_end && $recebe_start && $recebe_end",
                            status = HttpStatusCode.BadRequest
                        )

                }
        }

        get("/list"){
            if (maquinaStorage.isNotEmpty()) {
                call.respond(maquinaStorage)
                call.respondText("",status = HttpStatusCode.OK)
            } else {
                call.respondText("Maquina e seu intervalo não encontrado", status = HttpStatusCode.OK)
            }
        }
        put("/{id?}/{end_time?}"){
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Identificador não encontrado!",
                status = HttpStatusCode.BadRequest
            )
            
            val endTimes = call.parameters["end_time"] ?: return@put call.respondText(
                "end_time invalido!",
                status = HttpStatusCode.BadRequest
            )

            
            val maquina =
                maquinaStorage.find { it.id == id } ?: return@put call.respondText(
                    "Maquina com o  id: $id não encontrada!",
                    status = HttpStatusCode.NotFound
                )
            val novo = maquinaStorage.find { it.id == id }
            maquina.end_time.replace(maquina.end_time,endTimes)
            if (novo != null) {
                novo.end_time = endTimes
            }
            call.respond(novo.toString())
            call.respondText(" Atualizado com sucesso", status = HttpStatusCode.UpgradeRequired)


        }
        // alteração na url, para entrar no desejado, melhorar isso
        put("/list/{id?}/{reason?}"){
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Identificador não encontrado!",
                status = HttpStatusCode.BadRequest
            )
            val reasons = call.parameters["reason"] ?: return@put call.respondText(
                "reason invalido!",
                status = HttpStatusCode.BadRequest
            )
            val maquina =
                maquinaStorage.find { it.id == id } ?: return@put call.respondText(
                    "Maquina com o  id: $id não encontrada!",
                    status = HttpStatusCode.NotFound
                )
            val novo = maquinaStorage.find { it.id == id }
            maquina.reason.replace(maquina.reason,reasons)
            if (novo != null) {
                novo.reason = reasons
            }
            call.respond(novo.toString())
            call.respondText(" Atualizado com sucesso", status = HttpStatusCode.UpgradeRequired)

        }

        delete("/all") {

            var deletar= maquinaStorage.removeAll { it != null }
            if(deletar == true){
                call.respondText("Todas máquinas foram excluídas!", status = HttpStatusCode.Accepted)
            }else{
                call.respondText("Não foi possível excluir as máquinas!", status = HttpStatusCode.BadRequest)
            }

        }
    }
}

