package br.com.zup.edu

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

//Para poder consumir o serviço GRCPC tem que estender FretesServiceBlockingStub. Nesse caso ele vai consumir
//um serviço do projeto grpc-service-fretes e ele está na porta 50051
@Factory //diz p o micronaut que a classe é uma factory
class GrpcClientFactory {
    @Singleton//metodo que o micronaut vai chamar toda vez que pedir para injetar a classe FretesServiceBlockingStub
    fun fretesClientStub(@GrpcChannel("fretes") channel: ManagedChannel): FretesServiceGrpc.FretesServiceBlockingStub? {
        return FretesServiceGrpc.newBlockingStub(channel)
    }//@GrpcChannel é onde o cliente vai fazer a requisiçã p o endpoint
    //as config estão no application.yml
}