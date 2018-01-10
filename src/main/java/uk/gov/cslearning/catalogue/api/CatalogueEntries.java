package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphClient.Transaction;
import io.dgraph.DgraphProto.Mutation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.domain.CatalogueEntry;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/entries")
public class CatalogueEntries {

    private DgraphClient client;

    @Autowired
    public CatalogueEntries(DgraphClient client) {
        checkArgument(client != null, "client is null");
        this.client = client;
    }

    @RequestMapping(method = POST)
    public ResponseEntity<Void> addEntry(@RequestBody CatalogueEntry entry) {

        Gson gson = new Gson();

        Transaction txn = client.newTransaction();
        try {
            String json = gson.toJson(entry);

            Mutation mu = Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json)).build();
            txn.mutate(mu);
            txn.commit();
        } finally {
            txn.discard();
        }

        // TODO: Should be created (201), but no location yet
        return ResponseEntity.ok()
                .build();
    }
}
