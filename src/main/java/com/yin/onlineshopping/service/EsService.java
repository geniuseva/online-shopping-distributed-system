package com.yin.onlineshopping.service;

import com.alibaba.fastjson.JSON;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EsService {

    @Resource
    RestHighLevelClient restHighLevelClient;

    public int addCommodityToEs(OnlineShoppingCommodity commodity) {

        try {
            String indexName = "commodity";
            boolean indexExists = restHighLevelClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
            if (!indexExists) {
                // Create commodity Index
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject()
                        .startObject("dynamic_templates")
                        .startObject("strings")
                        .field("match_mapping_type", "string")
                        .startObject("mapping")
                        .field("type", "text")
                        .field("analyzer", "ik_smart")
                        .endObject()
                        .endObject()
                        .endObject()
                        .endObject();
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                request.source(builder);
                CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
                if (!response.isAcknowledged()) {
                    log.error("Failed to create ES Index: commodity");
                    return RestStatus.INTERNAL_SERVER_ERROR.getStatus();
                }
            }
            // Create Document into commodity Index
            String data = JSON.toJSONString(commodity);
            IndexRequest request = new IndexRequest("commodity").source(data, XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("AddCommodity To Elastic search, commodity: {}, result: {}", data, response);
            return response.status().getStatus();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OnlineShoppingCommodity> searchCommodities(String keyword, int from, int size) {
        SearchRequest searchRequest = new SearchRequest("commodity");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder =
                QueryBuilders.multiMatchQuery(keyword, "commodityName", "commodityDesc");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        searchSourceBuilder.sort("price", SortOrder.DESC);

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = response.getHits();
            long totalNum = hits.getTotalHits().value;
            List<OnlineShoppingCommodity> result = new ArrayList<>();

            SearchHit[] hitsResult = hits.getHits();

            for (SearchHit searchHit: hitsResult) {
                String source = searchHit.getSourceAsString();
                OnlineShoppingCommodity commodity = JSON.parseObject(source, OnlineShoppingCommodity.class);
                result.add(commodity);
            }
            log.info("Find {} result ", totalNum);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
