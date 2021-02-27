package xiaot.dong.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import xiaot.dong.elasticsearch.pojo.JDPojo;
import xiaot.dong.elasticsearch.pojo.UserPojo;
import xiaot.dong.elasticsearch.util.ParseJDUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ElasticsearchApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;

    ObjectMapper objectMapper = new ObjectMapper();

    String index = "java_index_1";

    @Test
    void json() throws Exception {
        String xiaoT = objectMapper.writeValueAsString(new UserPojo("xiaoT", 24));
        System.out.println(xiaoT);
    }

    @Test
    void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void exitIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    @Test
    void add() throws Exception {
        IndexRequest request = new IndexRequest(index).id("1").source(
                objectMapper.writeValueAsString(new UserPojo("xiaoT", 24)), XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void exists() throws Exception {
        GetRequest request = new GetRequest(index,"1");
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void get() throws Exception {
        GetRequest getRequest = new GetRequest(index, "1");
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void update() throws Exception {
        UpdateRequest request = new UpdateRequest(index, "1").doc(
                objectMapper.writeValueAsString(new UserPojo().setAge(20)), XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update);
    }

    @Test
    void delete() throws Exception {
        DeleteRequest request = new DeleteRequest(index, "1");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void bulkAdd() throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        List<UserPojo> list = new ArrayList<>();
        list.add(new UserPojo("我是xxx，我爱中国！1", 18));
        list.add(new UserPojo("我是xxx，我爱中国！2", 19));
        list.add(new UserPojo("我是xxx，我爱中国！3", 20));
        list.add(new UserPojo("我是xxx，我爱中国！4", 21));
        list.add(new UserPojo("我是xxx，我爱中国！5", 22));
        list.add(new UserPojo("我是xxx，我爱中国！6", 23));
        list.add(new UserPojo("我是xxx，我爱中国！7", 24));
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest(index).id(Integer.toString(i + 1)).source(
                    objectMapper.writeValueAsString(list.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk);
    }

    @Test
    void query() throws Exception {
        SearchRequest searchRequest = new SearchRequest(index);
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("username", "爱");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(queryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : search.getHits()) {
            System.out.println(hit);
        }
    }

    @Test
    void addJD() throws Exception {
        List<JDPojo> java = ParseJDUtil.parseJD("java");
        BulkRequest bulkRequest = new BulkRequest();
        for (JDPojo jdPojo : java) {
            bulkRequest.add(new IndexRequest("jd").source(objectMapper.writeValueAsString(jdPojo), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk);
    }

    @Test
    void queryJd() throws Exception {
        String key = "java";

        SearchRequest jd = new SearchRequest("jd");
        TermQueryBuilder name = QueryBuilders.termQuery("name", key);
        SearchSourceBuilder query = new SearchSourceBuilder().query(name).from(1).size(10).highlighter(
                new HighlightBuilder().field("name").preTags("<<<").postTags(">>>"));
                        // 如果要匹配多个字段高亮，必须设置为false
//                        .requireFieldMatch(false));
        jd.source(query);
        SearchResponse search = client.search(jd, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        List<Map<String, Object>> jdPojos = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            HighlightField high = hit.getHighlightFields().get("name");
            Text[] fragments = high.fragments();
            StringBuilder sb = new StringBuilder();
            for (Text fragment : fragments) {
                sb.append(fragment);
            }
            sourceAsMap.put("name", sb);
            jdPojos.add(sourceAsMap);
        }
        System.out.println(Arrays.toString(hits));
        System.out.println(jdPojos);
    }

}
