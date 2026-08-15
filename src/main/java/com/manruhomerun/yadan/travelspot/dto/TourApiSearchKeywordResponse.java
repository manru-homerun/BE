package com.manruhomerun.yadan.travelspot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiSearchKeywordResponse(
        Response response
) implements TourApiResponse {

    @Override
    public String getResultCode() {
        if (response == null || response.header() == null) {
            return null;
        }

        return response.header().resultCode();
    }

    @Override
    public String getResultMessage() {
        if (response == null || response.header() == null) {
            return null;
        }

        return response.header().resultMsg();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            Header header,
            Body body
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
            String resultCode,
            String resultMsg
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Body {

        private Items items;
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;

        public Items items() {
            return items;
        }

        public Integer numOfRows() {
            return numOfRows;
        }

        public Integer pageNo() {
            return pageNo;
        }

        public Integer totalCount() {
            return totalCount;
        }

        @JsonSetter("items")
        public void setItems(JsonNode items) {
            if (items == null || items.isNull() || items.isTextual()) {
                this.items = null;
                return;
            }

            JsonNode itemNode = items.get("item");
            if (itemNode == null || itemNode.isNull() || !itemNode.isArray()) {
                this.items = null;
                return;
            }

            this.items = new Items(
                    java.util.stream.StreamSupport.stream(itemNode.spliterator(), false)
                            .map(node -> new Item(
                                    node.path("addr1").asText(null),
                                    node.path("addr2").asText(null),
                                    node.path("contentid").asText(null),
                                    node.path("contenttypeid").asText(null),
                                    node.path("firstimage").asText(null),
                                    node.path("title").asText(null),
                                    node.path("lDongRegnCd").asText(null),
                                    node.path("lDongSignguCd").asText(null)
                            ))
                            .toList()
            );
        }

        public void setNumOfRows(Integer numOfRows) {
            this.numOfRows = numOfRows;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            List<Item> item
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String addr1,
            String addr2,
            String contentid,
            String contenttypeid,
            String firstimage,
            String title,
            String lDongRegnCd,
            String lDongSignguCd
    ) {
    }
}
