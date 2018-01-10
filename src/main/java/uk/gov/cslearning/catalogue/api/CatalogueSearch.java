package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.domain.CatalogueEntry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/search")
public class CatalogueSearch {

    private DgraphClient client;

    @Autowired
    public CatalogueSearch(DgraphClient client) {
        checkArgument(client != null, "client is null");
        this.client = client;
    }

    @RequestMapping(method = GET)
    public SearchResults search(@RequestParam(name = "q") String searchTerms) {

        String query =
                "query entries($a: string) {\n" +
                    "results(func: alloftext(title, $a)) {\n" +
                    "    title\n" +
                    "    location\n" +
                    "}\n" +
                "}";

        Map<String, String> vars = Collections.singletonMap("$a", searchTerms);
        DgraphProto.Response response = client.newTransaction().queryWithVars(query, vars);

        Gson gson = new Gson();

        return gson.fromJson(response.getJson().toStringUtf8(), SearchResults.class);
    }
}
