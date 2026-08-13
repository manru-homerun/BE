package com.manruhomerun.yadan.baseball.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.manruhomerun.yadan.baseball.client.KboScheduleCrawlerClient;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballGame;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballStadium;
import com.manruhomerun.yadan.baseball.domain.entity.BaseballTeam;
import com.manruhomerun.yadan.baseball.repository.BaseballGameRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BaseballGameCrawlingService {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final KboScheduleCrawlerClient kboScheduleCrawlerClient;
    private final BaseballGameRepository baseballGameRepository;
    private final EntityManager entityManager;

    public void syncNextMonthSchedules() {
        // 매달 15일 새벽 1시 다음 달 경기 일정을 동기화합니다.
        YearMonth nextMonth = YearMonth.from(LocalDate.now(KOREA_ZONE_ID).plusMonths(1));
        log.info("다음 달({}) 경기 일정 정기 동기화 작업을 진행합니다.", nextMonth);
        syncGameSchedules(nextMonth.atDay(1), nextMonth.atEndOfMonth());
    }

    public void syncGameSchedules(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException(
                "시작일은 종료일보다 늦을 수 없습니다.");

        int savedCount = 0;
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        for (YearMonth targetMonth = startMonth; !targetMonth.isAfter(endMonth); targetMonth = targetMonth.plusMonths(1)) {
            List<KboScheduleCrawlerClient.CrawledGame> crawledGames = kboScheduleCrawlerClient.crawlMonthlyGames(targetMonth)
                    .stream()
                    .filter(crawledGame -> !crawledGame.gameDateTime().toLocalDate().isBefore(startDate))
                    .filter(crawledGame -> !crawledGame.gameDateTime().toLocalDate().isAfter(endDate))
                    .toList();

            for (KboScheduleCrawlerClient.CrawledGame crawledGame : crawledGames) {
                BaseballTeam awayTeam = entityManager.getReference(BaseballTeam.class, crawledGame.awayTeamCode().getTeamId());
                BaseballTeam homeTeam = entityManager.getReference(BaseballTeam.class, crawledGame.homeTeamCode().getTeamId());
                if (crawledGame.stadiumCode() == null) {
                    log.error(
                            "구장 코드 매핑에 실패해 경기 일정 동기화를 중단합니다. awayTeamCode={}, homeTeamCode={}, gameDateTime={}",
                            crawledGame.awayTeamCode(),
                            crawledGame.homeTeamCode(),
                            crawledGame.gameDateTime()
                    );
                    throw new IllegalStateException("구장 코드 매핑에 실패했습니다.");
                }
                BaseballStadium stadium = entityManager.getReference(BaseballStadium.class, crawledGame.stadiumCode().getStadiumId());

                BaseballGame baseballGame = findExistingGame(crawledGame, homeTeam, awayTeam)
                        .orElseGet(() -> BaseballGame.builder()
                                .stadium(stadium)
                                .homeTeam(homeTeam)
                                .awayTeam(awayTeam)
                                .gameDate(crawledGame.gameDateTime())
                                .gameType(crawledGame.gameType())
                                .isCanceled(Boolean.FALSE)
                                .build()
                        );

                baseballGame.updateSchedule(stadium, homeTeam, awayTeam, crawledGame.gameDateTime(), crawledGame.gameType());
                baseballGameRepository.save(baseballGame);
                savedCount++;
            }
        }

        log.info("경기 일정 동기화를 완료했습니다. startDate={}, endDate={}, savedCount={}", startDate, endDate, savedCount);
    }

    public void updatePreviousDayResults() {
        // 매일 새벽 1시 전날 경기 결과를 업데이트합니다.
        // 취소 경기 편성 시 월요일에도 경기를 진행할 수 있기 때문에 크롤링을 진행합니다.
        LocalDate previousDate = LocalDate.now(KOREA_ZONE_ID).minusDays(1);
        log.info("전날({}) 경기 결과 정기 동기화 작업을 진행합니다.", previousDate);
        updateGameResults(previousDate);
    }

    public void updateGameResults(LocalDate targetDate) {
        List<KboScheduleCrawlerClient.CrawledGame> crawledGames = kboScheduleCrawlerClient.crawlMonthlyGames(YearMonth.from(targetDate))
                .stream()
                .filter(crawledGame -> crawledGame.gameDateTime().toLocalDate().isEqual(targetDate))
                .toList();

        int updatedCount = 0;
        for (KboScheduleCrawlerClient.CrawledGame crawledGame : crawledGames) {
            BaseballTeam awayTeam = entityManager.getReference(BaseballTeam.class, crawledGame.awayTeamCode().getTeamId());
            BaseballTeam homeTeam = entityManager.getReference(BaseballTeam.class, crawledGame.homeTeamCode().getTeamId());
            if (crawledGame.stadiumCode() == null) {
                log.error(
                        "구장 코드 매핑에 실패해 경기 결과 동기화를 중단합니다. awayTeamCode={}, homeTeamCode={}, gameDateTime={}",
                        crawledGame.awayTeamCode(),
                        crawledGame.homeTeamCode(),
                        crawledGame.gameDateTime()
                );
                throw new IllegalStateException("구장 코드 매핑에 실패했습니다.");
            }
            BaseballStadium stadium = entityManager.getReference(BaseballStadium.class, crawledGame.stadiumCode().getStadiumId());

            BaseballGame baseballGame = findExistingGame(crawledGame, homeTeam, awayTeam)
                    .orElseThrow(() -> {
                        log.error(
                                "전날 경기 결과를 업데이트할 기존 경기를 찾을 수 없습니다. homeTeam={}, awayTeam={}, gameDateTime={}",
                                homeTeam.getTeamName(),
                                awayTeam.getTeamName(),
                                crawledGame.gameDateTime()
                        );
                        return new IllegalStateException("전날 경기 결과를 업데이트할 기존 경기를 찾을 수 없습니다.");
                    });

            baseballGame.updateSchedule(stadium, homeTeam, awayTeam, crawledGame.gameDateTime(), crawledGame.gameType());
            baseballGame.updateResult(crawledGame.awayTeamScore(), crawledGame.homeTeamScore(), crawledGame.canceled());
            baseballGameRepository.save(baseballGame);
            updatedCount++;
        }

        log.info("경기 결과 동기화를 완료했습니다. targetDate={}, updatedCount={}", targetDate, updatedCount);
    }

    private Optional<BaseballGame> findExistingGame(
            KboScheduleCrawlerClient.CrawledGame crawledGame,
            BaseballTeam homeTeam,
            BaseballTeam awayTeam
    ) {
        LocalDate targetDate = crawledGame.gameDateTime().toLocalDate();
        List<BaseballGame> sameDayGames = baseballGameRepository.findByHomeTeamIdAndAwayTeamIdAndGameDateBetween(
                homeTeam.getId(),
                awayTeam.getId(),
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay().minusSeconds(1)
        );

        // 더블헤더 가능성을 고려해 같은 날 경기 중 가장 가까운 시간을 기존 경기로 본다.
        return sameDayGames.stream()
                .min(Comparator.comparing(baseballGame ->
                        Math.abs(Duration.between(baseballGame.getGameDate(), crawledGame.gameDateTime()).toMinutes())
                ));
    }
}
