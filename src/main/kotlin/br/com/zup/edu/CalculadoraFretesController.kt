package br.com.zup.edu

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException

@Controller
class CalculaFretesController(val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {
    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse {
        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()

        try {
            val response = grpcClient.calculaFrete(request)
            return FreteResponse(cep = response.cep, valor = response.valor)
        } catch (e: StatusRuntimeException) {//captura a exceção lançada lá no outro projeto fretes
            val statusCode = e.status.code
            val description = e.status.description
            if (statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode == Status.Code.PERMISSION_DENIED) {
                val statusProto =
                    StatusProto.fromThrowable(e)

                if (statusProto == null) {//se n tiver detalhes da exceção lança forbidden
                    throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                }

                val anyDetails = statusProto.detailsList.get(0)//extrai os detalhes da exceção
                val errorDetails =
                    anyDetails.unpack(ErrorDetails::class.java)//extrai o erro para o formato code/message
                //para o grpc pode lidar
                throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}


data class FreteResponse(val cep: String, val valor: Double) {

}