package com.manruhomerun.yadan.baseball.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.manruhomerun.yadan.baseball.domain.enums.BaseballGameType;
import com.manruhomerun.yadan.baseball.domain.enums.KboStadiumCode;
import com.manruhomerun.yadan.baseball.domain.enums.KboTeamCode;
import com.manruhomerun.yadan.baseball.properties.KboScheduleProperties;
import com.manruhomerun.yadan.global.error.exception.ExternalApiCallException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class KboScheduleCrawlerClient {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.ENGLISH);
    private static final String DAILY_SCHEDULE_PATH = "/Schedule/DailySchedule.aspx";
    private static final String MONTH_LABEL_ID = "cphContainer_cphContainer_cphContent_cphContent_lblGameMonth";
    private static final String NEXT_BUTTON_NAME = "ctl00$ctl00$ctl00$ctl00$cphContainer$cphContainer$cphContent$cphContent$btnNext";
    private static final String PREVIOUS_BUTTON_NAME = "ctl00$ctl00$ctl00$ctl00$cphContainer$cphContainer$cphContent$cphContent$btnBefore";

    private final KboScheduleProperties kboScheduleProperties;

    public List<CrawledGame> crawlMonthlyGames(YearMonth yearMonth) {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                    .build();
            String responseBody = navigateToMonth(httpClient, yearMonth);

            if (responseBody == null || responseBody.isBlank()) {
                throw new ExternalApiCallException("KBO 경기 일정 응답이 비어 있습니다. requestedMonth=" + yearMonth);
            }

            return parseMonthlyGames(responseBody, yearMonth);
        } catch (ExternalApiCallException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExternalApiCallException("KBO 경기 일정 호출에 실패했습니다. requestedMonth=" + yearMonth);
        } catch (IOException exception) {
            throw new ExternalApiCallException("KBO 경기 일정 호출에 실패했습니다. requestedMonth=" + yearMonth);
        }
    }

    private String navigateToMonth(HttpClient httpClient, YearMonth requestedMonth) throws IOException, InterruptedException {
        String responseBody = sendGet(httpClient);
        Document document = Jsoup.parse(responseBody);
        YearMonth currentMonth = extractYearMonth(document);

        long monthGap = ChronoUnit.MONTHS.between(currentMonth, requestedMonth);
        if (monthGap == 0) {
            return responseBody;
        }

        String buttonName = monthGap > 0 ? NEXT_BUTTON_NAME : PREVIOUS_BUTTON_NAME;
        for (long index = 0; index < Math.abs(monthGap); index++) {
            responseBody = sendMonthMovePost(httpClient, document, buttonName);
            document = Jsoup.parse(responseBody);
        }

        return responseBody;
    }

    private String sendGet(HttpClient httpClient) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kboScheduleProperties.getBaseUrl() + DAILY_SCHEDULE_PATH))
                .GET()
                .header("User-Agent", "Mozilla/5.0")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private String sendMonthMovePost(HttpClient httpClient, Document document, String buttonName) throws IOException, InterruptedException {
        Map<String, String> formData = new LinkedHashMap<>();
        addHiddenField(document, formData, "__VIEWSTATE");
        addHiddenField(document, formData, "__VIEWSTATEGENERATOR");
        addHiddenField(document, formData, "__EVENTVALIDATION");
        addHiddenField(document, formData, "ctl00$ctl00$ctl00$ctl00$cphContainer$cphContainer$cphContent$cphContent$hdTeamCD");
        formData.put(buttonName + ".x", "10");
        formData.put(buttonName + ".y", "10");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kboScheduleProperties.getBaseUrl() + DAILY_SCHEDULE_PATH))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0")
                .POST(HttpRequest.BodyPublishers.ofString(toFormBody(formData)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private void addHiddenField(Document document, Map<String, String> formData, String fieldName) {
        Element field = document.selectFirst("input[name=\"" + fieldName + "\"]");
        if (field == null) {
            throw new ExternalApiCallException("KBO 경기 일정 폼 필드를 찾을 수 없습니다. fieldName=" + fieldName);
        }
        formData.put(fieldName, field.attr("value"));
    }

    private String toFormBody(Map<String, String> formData) {
        return formData.entrySet()
                .stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private List<CrawledGame> parseMonthlyGames(String responseBody, YearMonth requestedMonth) {
        Document document = Jsoup.parse(responseBody);
        String crawledMonth = extractYearMonth(document).format(MONTH_FORMATTER);

        // 다른 달 화면이 내려오면 잘못된 일정이 저장될 수 있어 즉시 실패시킨다.
        if (!requestedMonth.format(MONTH_FORMATTER).equals(crawledMonth)) {
            throw new ExternalApiCallException(
                    "요청한 월과 KBO 응답 월이 다릅니다. requestedMonth="
                            + requestedMonth
                            + ", crawledMonth="
                            + crawledMonth
            );
        }

        Element scheduleTableBody = document.selectFirst("table[summary=schdule] tbody");
        if (scheduleTableBody == null) {
            throw new ExternalApiCallException("KBO 경기 일정 표를 찾을 수 없습니다. requestedMonth=" + requestedMonth);
        }

        List<CrawledGame> crawledGames = new ArrayList<>();
        String currentDateText = null;
        String currentGameTypeText = null;

        for (Element row : scheduleTableBody.select("tr")) {
            Element dateCell = row.selectFirst("td[title=DATE]");
            if (dateCell != null) {
                currentDateText = dateCell.text().trim();
            }

            Element gameTypeCell = row.selectFirst("td[title=TYPE]");
            if (gameTypeCell != null) {
                currentGameTypeText = gameTypeCell.text().trim();
            }

            if (currentDateText == null || currentGameTypeText == null) {
                continue;
            }

            if (isAllStarGame(currentGameTypeText)) {
                continue;
            }

            Elements gameCells = row.select("td[title=GAME]");
            if (gameCells.size() < 3) {
                continue;
            }

            KboTeamCode awayTeamCode = KboTeamCode.from(gameCells.get(0).text().trim());
            String scoreText = gameCells.get(1).text().trim();
            KboTeamCode homeTeamCode = KboTeamCode.from(gameCells.get(2).text().trim());
            String timeText = getCellText(row, "td.TIME");
            KboStadiumCode stadiumCode = KboStadiumCode.from(getCellText(row, "td.LOCATION"));
            String etcText = getCellText(row, "td.ETC");

            if (timeText.isBlank()) {
                continue;
            }

            if (awayTeamCode == null || homeTeamCode == null) {
                throw new ExternalApiCallException(
                        "매핑되지 않은 KBO 팀 코드가 있습니다. awayTeamCode="
                                + gameCells.get(0).text().trim()
                                + ", homeTeamCode="
                                + gameCells.get(2).text().trim()
                );
            }

            LocalDate gameDate = LocalDate.parse(
                    requestedMonth.getYear() + "." + extractMonthDay(currentDateText),
                    DATE_FORMATTER
            );

            crawledGames.add(new CrawledGame(
                    awayTeamCode,
                    homeTeamCode,
                    stadiumCode,
                    LocalDateTime.of(gameDate, LocalTime.parse(timeText)),
                    mapGameType(currentGameTypeText),
                    parseAwayScore(scoreText),
                    parseHomeScore(scoreText),
                    isCanceled(etcText),
                    etcText
            ));
        }

        return crawledGames;
    }

    private YearMonth extractYearMonth(Document document) {
        Element gameMonthElement = document.getElementById(MONTH_LABEL_ID);
        if (gameMonthElement == null) {
            throw new ExternalApiCallException("KBO 경기 일정 월 정보가 없습니다.");
        }

        return YearMonth.parse(gameMonthElement.text().trim(), MONTH_FORMATTER);
    }

    private String getCellText(Element row, String cssQuery) {
        Element cell = row.selectFirst(cssQuery);
        return cell == null ? "" : cell.text().trim();
    }

    private String extractMonthDay(String dateText) {
        int dayOfWeekStartIndex = dateText.indexOf('(');
        return dayOfWeekStartIndex >= 0 ? dateText.substring(0, dayOfWeekStartIndex) : dateText;
    }

    private BaseballGameType mapGameType(String gameTypeText) {
        String normalizedGameTypeText = gameTypeText.toUpperCase(Locale.ENGLISH);

        if (normalizedGameTypeText.contains("EXHIBITION")) {
            return BaseballGameType.EXHIBITION;
        }
        if (normalizedGameTypeText.contains("REGULAR")) {
            return BaseballGameType.REGULAR;
        }
        return BaseballGameType.POSTSEASON;
    }

    private boolean isAllStarGame(String gameTypeText) {
        String normalizedGameTypeText = gameTypeText
                .toUpperCase(Locale.ENGLISH)
                .replace(" ", "")
                .replace("-", "");

        return normalizedGameTypeText.contains("ALLSTAR");
    }

    private Integer parseAwayScore(String scoreText) {
        return parseScore(scoreText, 0);
    }

    private Integer parseHomeScore(String scoreText) {
        return parseScore(scoreText, 1);
    }

    private Integer parseScore(String scoreText, int index) {
        if (!scoreText.contains(":")) {
            return null;
        }

        String[] tokens = scoreText.split(":");
        if (tokens.length != 2) {
            return null;
        }

        String score = tokens[index].trim();
        return score.isEmpty() ? null : Integer.parseInt(score);
    }

    private boolean isCanceled(String etcText) {
        return "POSTPONED".equalsIgnoreCase(etcText)
                || "CANCELLED".equalsIgnoreCase(etcText)
                || "CANCELED".equalsIgnoreCase(etcText);
    }

    public record CrawledGame(
            KboTeamCode awayTeamCode,
            KboTeamCode homeTeamCode,
            KboStadiumCode stadiumCode,
            LocalDateTime gameDateTime,
            BaseballGameType gameType,
            Integer awayTeamScore,
            Integer homeTeamScore,
            boolean canceled,
            String note
    ) {
    }
}
