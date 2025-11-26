package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

@DataJpaTest
@Sql(scripts = "/test-data/player-data.sql")
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    static Stream<Arguments> playerTestArguments1() {
        return Stream.of(
                Arguments.of("Mario", Instant.now(), 101),
                Arguments.of("Toadette", Instant.parse("2025-04-10T09:02:00Z"), 102),
                Arguments.of("Mario", Instant.parse("2025-06-05T11:02:00Z"), 101)
        );
    }

    @BeforeAll
    static void setUpTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @ParameterizedTest
    @MethodSource("playerTestArguments1")
    void givenValidCharacterAndValidDate_whenFindPlayerByCharacterAtOrBeforeDate_thenReturnPlayer(String character, Instant date, int expectedUserId) {
        Optional<Player> player = playerRepository.findPlayerByCharacterAtOrBeforeDate(character, date);
        assert(player.isPresent());
        assert(player.get().getCharacterName().equals(character));
        assert(player.get().getCreatedAt().isBefore(date) || player.get().getCreatedAt().equals(date));
        assert(player.get().getUser().getId() == expectedUserId);
    }

    static Stream<Arguments> playerTestArguments2() {
        return Stream.of(
                Arguments.of("Mario2", Instant.now()),
                Arguments.of("Mario", Instant.parse("2024-06-05T11:02:00Z"))
        );
    }

    @ParameterizedTest
    @MethodSource("playerTestArguments2")
    void givenInvalidCharacterOrDate_whenFindPlayerByCharacterAtOrBeforeDate_thenReturnEmpty(String character, Instant date) {
        Optional<Player> player = playerRepository.findPlayerByCharacterAtOrBeforeDate(character, date);
        assert(player.isEmpty());
    }
}