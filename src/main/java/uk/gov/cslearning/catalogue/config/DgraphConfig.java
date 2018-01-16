package uk.gov.cslearning.catalogue.config;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto.Operation;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class DgraphConfig {

    @Bean
    public DgraphClient client(DgraphProperties properties) {

        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort()).usePlaintext(true).build();
        DgraphGrpc.DgraphBlockingStub blockingStub = DgraphGrpc.newBlockingStub(channel);

        DgraphClient client = new DgraphClient(Collections.singletonList(blockingStub));

        String schema =
                "title: string @index(fulltext) .\n" +
                "location: string .\n" +
                "tag: [string] @index(term) .";

        Operation op = Operation.newBuilder().setSchema(schema).build();
        client.alter(op);

        return client;
    }
}
