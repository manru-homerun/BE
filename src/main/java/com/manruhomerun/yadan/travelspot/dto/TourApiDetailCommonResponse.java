package com.manruhomerun.yadan.travelspot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiDetailCommonResponse(
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
    public record Body(
            Items items
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            List<Item> item
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String contentid,
            String contenttypeid,
            String title,
            String tel,
            String homepage,
            String firstimage,
            String lDongRegnCd,
            String lDongSignguCd,
            String addr1,
            String addr2,
            String mapx,
            String mapy,
            String overview
    ) {
    }
}
