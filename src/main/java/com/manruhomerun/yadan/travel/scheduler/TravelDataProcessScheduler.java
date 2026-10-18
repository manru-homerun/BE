package com.manruhomerun.yadan.travel.scheduler;

import com.manruhomerun.yadan.global.properties.S3Properties;
import com.manruhomerun.yadan.travel.domain.entity.Travel;
import com.manruhomerun.yadan.travel.domain.entity.TravelTravelSpot;
import com.manruhomerun.yadan.travel.domain.entity.TravelUser;
import com.manruhomerun.yadan.travel.error.exception.TravelDataExportException;
import com.manruhomerun.yadan.travel.repository.TravelRepository;
import com.manruhomerun.yadan.travel.repository.TravelTravelSpotRepository;
import com.manruhomerun.yadan.user.domain.entity.TravelPreference;
import com.manruhomerun.yadan.user.domain.entity.User;
import com.manruhomerun.yadan.user.repository.TravelPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TravelDataProcessScheduler {
    private final Logger logger = LoggerFactory.getLogger(TravelDataProcessScheduler.class);
    private final TravelRepository travelRepository;
    private final TravelTravelSpotRepository travelTravelSpotRepository;
    private final TravelPreferenceRepository travelPreferenceRepository;
    private final PlatformTransactionManager transactionManager;
    private final S3Properties s3Properties;

    private List<File> processTravelData(LocalDate date) {
        List<Travel> travels = travelRepository.findTravelByEndDate(date);
        if (travels.isEmpty()) return List.of();

        List<String> travelIds = travels.stream().map(Travel::getId).toList();
        Map<String, List<TravelTravelSpot>> spotsByTravelId = travelTravelSpotRepository
                .findAllByTravelIdInOrderByDayAscOrderAscIdAsc(travelIds).stream()
                .collect(Collectors.groupingBy(spot -> spot.getTravel().getId()));
        Set<String> leaderIds = travels.stream()
                .flatMap(travel -> travel.getTravelUserList().stream())
                .filter(TravelUser::isLeader)
                .map(travelUser -> travelUser.getUser().getId())
                .collect(Collectors.toSet());
        Map<String, TravelPreference> preferencesByUserId = leaderIds.isEmpty() ? Map.of()
                : travelPreferenceRepository.findAllByUserIdIn(leaderIds).stream()
                        .collect(Collectors.toMap(preference -> preference.getUser().getId(), preference -> preference));

        String suffix = date.format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        Path directory = null;
        List<File> files = new ArrayList<>();
        try {
            directory = Files.createTempDirectory("travel-export-");
            Path tripsPath = directory.resolve("trips_" + suffix);
            Path itemsPath = directory.resolve("trip_items_" + suffix);
            files.add(tripsPath.toFile());
            files.add(itemsPath.toFile());
            MessageDigest userIdDigest = MessageDigest.getInstance("SHA-256");

            try (BufferedWriter tripsWriter = Files.newBufferedWriter(tripsPath, StandardCharsets.UTF_8);
                 BufferedWriter itemsWriter = Files.newBufferedWriter(itemsPath, StandardCharsets.UTF_8)) {
                tripsWriter.write("tripId,userId,completedAt,areaCode,travelDuration,travelPersona,ageGroup,gender,travelerStyle,preferredArea,residenceArea,companionCount\r\n");
                itemsWriter.write("tripId,dayIndex,visitOrder,contentId,title,itemSource\r\n");

                for (Travel travel : travels) {
                    List<TravelUser> leaders = travel.getTravelUserList().stream()
                            .filter(TravelUser::isLeader).toList();
                    User leader = leaders.getFirst().getUser();
                    TravelPreference preference = preferencesByUserId.get(leader.getId());

                    long duration = ChronoUnit.DAYS.between(travel.getStartDate(), travel.getEndDate()) + 1;
                    List<TravelTravelSpot> spots = spotsByTravelId.getOrDefault(travel.getId(), List.of());

                    // 요청 당시 스냅샷이 없어 방장의 현재 취향을 사용하고, 종료일 다음 날 자정을 완료 시각으로 기록한다.
                    String completedAt = travel.getEndDate().plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul"))
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    int koreanAge = date.getYear() - leader.getBirthday().getYear() + 1;
                    int ageGroup = Math.max(20, Math.min(60, koreanAge / 10 * 10));
                    long companionCount = travel.getTravelUserList().stream()
                            .map(travelUser -> travelUser.getUser().getId())
                            .filter(userId -> !userId.equals(leader.getId())).distinct().count();
                    List<String> tripValues = List.of(
                            travel.getId(),
                            "u_" + HexFormat.of().formatHex(userIdDigest.digest(leader.getId().getBytes(StandardCharsets.UTF_8))),
                            completedAt,
                            travel.getRegionCode(),
                            String.valueOf(duration),
                            String.valueOf(travel.getTheme().getId()),
                            String.valueOf(ageGroup),
                            leader.getGender().getDisplayName(),
                            String.valueOf(preference.getTravelStyleValue()),
                            preference.getPreferredRegionCodes().stream().map(region -> region.getCode())
                                    .sorted().collect(Collectors.joining(";")),
                            preference.getResidenceRegionCode().getCode(),
                            String.valueOf(companionCount));

                    // 모든 필드를 인용하고 따옴표를 이스케이프해 쉼표·줄바꿈이 포함된 값도 보존한다.
                    tripsWriter.write(tripValues.stream()
                            .map(value -> "\"" + value.replace("\"", "\"\"") + "\"")
                            .collect(Collectors.joining(",")) + "\r\n");

                    Set<String> includedContentIds = new HashSet<>();
                    Map<Integer, Integer> visitOrders = new HashMap<>();
                    for (TravelTravelSpot spot : spots) {
                        String contentId = spot.getTravelSpot().getId();
                        if (!includedContentIds.add(contentId)) {
                            continue;
                        }
                        // 일차·기존 순서대로 첫 방문만 남기고, 일차별 방문 순서를 1부터 다시 매긴다.
                        int visitOrder = visitOrders.merge(spot.getDay(), 1, Integer::sum);
                        List<String> itemValues = List.of(
                                travel.getId(), String.valueOf(spot.getDay()), String.valueOf(visitOrder),
                                contentId, Objects.toString(spot.getTravelSpot().getName(), ""), "unknown");
                        itemsWriter.write(itemValues.stream()
                                .map(value -> "\"" + value.replace("\"", "\"\"") + "\"")
                                .collect(Collectors.joining(",")) + "\r\n");
                    }
                }
            }

            logger.info("여행 CSV 생성 완료. date={}, exported={}", date, travels.size());
            return files;
        } catch (IOException | NoSuchAlgorithmException | RuntimeException exception) {
            // 파일 생성 중 실패하면 불완전한 CSV를 남기거나 업로드하지 않는다.
            for (File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            if (directory != null) {
                try {
                    Files.deleteIfExists(directory);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw new TravelDataExportException(exception);
        }
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void uploadData() {
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        // 지연 로딩에 필요한 트랜잭션은 CSV 생성까지만 유지하고 S3 통신 전에 종료한다.
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setReadOnly(true);
        List<File> files = transaction.execute(status -> processTravelData(date));
        if (files == null || files.isEmpty()) {
            return;
        }

        try (S3AsyncClient s3Client = S3AsyncClient.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .build()) {
            for (File file : files) {
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(s3Properties.getBucketName())
                        .key("incoming/" + file.getName())
                        .contentType("text/csv; charset=utf-8")
                        .build();

                CompletableFuture<PutObjectResponse> uploadFuture = s3Client.putObject(
                        request, AsyncRequestBody.fromFile(file.toPath()));

                // 각 파일 업로드가 끝난 뒤 다음 파일을 전송하고 클라이언트를 닫는다.
                uploadFuture.join();
            }
            logger.info("여행 CSV 업로드 완료. date={}, files={}", date, files.size());
        } finally {
            for (File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException exception) {
                    logger.warn("여행 CSV 임시 파일 삭제에 실패했습니다. file={}", file.getName(), exception);
                }
            }
            try {
                Files.deleteIfExists(files.getFirst().toPath().getParent());
            } catch (IOException exception) {
                logger.warn("여행 CSV 임시 디렉터리 삭제에 실패했습니다.", exception);
            }
        }
    }

}
