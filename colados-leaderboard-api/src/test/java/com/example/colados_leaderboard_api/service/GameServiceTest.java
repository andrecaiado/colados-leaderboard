package com.example.colados_leaderboard_api.service;

import com.example.colados_leaderboard_api.dto.PatchGameResultsStatus;
import com.example.colados_leaderboard_api.entity.AppUser;
import com.example.colados_leaderboard_api.entity.Game;
import com.example.colados_leaderboard_api.entity.GameResult;
import com.example.colados_leaderboard_api.entity.Player;
import com.example.colados_leaderboard_api.enums.GameResultsStatus;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.exceptions.IllegalGameStateException;
import com.example.colados_leaderboard_api.exceptions.IncompleteGameResultsException;
import com.example.colados_leaderboard_api.exceptions.InvalidDataInGameResultsException;
import com.example.colados_leaderboard_api.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void givenIncompleteGameResults_whenUpdateGameResultsStatus_thenThrowException() {
        List<GameResult> results = new ArrayList<>();

        GameResult result1 = mock(GameResult.class);
        when(result1.getGame()).thenReturn(mock(Game.class));
        when(result1.getPlayer()).thenReturn(mock(Player.class));
        when(result1.getPosition()).thenReturn(1);
        when(result1.getCharacterName()).thenReturn("Yoshi");
        when(result1.getScore()).thenReturn(60);

        GameResult result2 = mock(GameResult.class);

        results.add(result1); // Valid result
        results.add(result2); // Incomplete result

        Game game = new Game();
        game.setGameResults(results);
        game.setGameResultsStatus(GameResultsStatus.PENDING_ACCEPTANCE);

        when(gameRepository.findById(anyInt())).thenReturn(Optional.of(game));

        PatchGameResultsStatus patchGameResultsStatus = new PatchGameResultsStatus();
        patchGameResultsStatus.setGameResultsStatus(GameResultsStatus.ACCEPTED);

        // Call the method under test and expect an exception
        assertThrows(IncompleteGameResultsException.class, () -> gameService.updateGameResultsStatus(1, patchGameResultsStatus));
    }

    @Test
    void givenCompleteGameResults_whenUpdateGameResultsStatus_thenCallRepoSave() throws EntityNotFound, InvalidDataInGameResultsException, IncompleteGameResultsException, IllegalGameStateException {
        AppUser appUser = mock(AppUser.class);

        Player player = mock(Player.class);
        when(player.getUser()).thenReturn(appUser);

        List<GameResult> results = new ArrayList<>();

        GameResult result1 = mock(GameResult.class);
        when(result1.getGame()).thenReturn(mock(Game.class));
        when(result1.getPlayer()).thenReturn(player);
        when(result1.getPosition()).thenReturn(1);
        when(result1.getCharacterName()).thenReturn("Yoshi");
        when(result1.getScore()).thenReturn(60);

        results.add(result1); // Valid result

        Game game = new Game();
        game.setGameResults(results);
        game.setGameResultsStatus(GameResultsStatus.PENDING_ACCEPTANCE);

        when(gameRepository.findById(anyInt())).thenReturn(Optional.of(game));

        PatchGameResultsStatus patchGameResultsStatus = new PatchGameResultsStatus();
        patchGameResultsStatus.setGameResultsStatus(GameResultsStatus.ACCEPTED);

        // Call the method under test
        gameService.updateGameResultsStatus(1, patchGameResultsStatus);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    void givenDuplicateUserInGameResults_whenUpdateGameResultsStatus_thenThrowException() {
        AppUser appUser = mock(AppUser.class);

        Player player = mock(Player.class);
        when(player.getUser()).thenReturn(appUser);
        Player playerDuplicateUser = mock(Player.class);
        when(playerDuplicateUser.getUser()).thenReturn(appUser);

        List<GameResult> results = new ArrayList<>();

        GameResult result1 = mock(GameResult.class);
        when(result1.getGame()).thenReturn(mock(Game.class));
        when(result1.getPlayer()).thenReturn(player);
        when(result1.getPosition()).thenReturn(1);
        when(result1.getCharacterName()).thenReturn("Yoshi");
        when(result1.getScore()).thenReturn(60);

        GameResult result2 = mock(GameResult.class);
        when(result2.getGame()).thenReturn(mock(Game.class));
        when(result2.getPlayer()).thenReturn(playerDuplicateUser);
        when(result2.getPosition()).thenReturn(2);
        when(result2.getCharacterName()).thenReturn("Toadette");
        when(result2.getScore()).thenReturn(50);

        results.add(result1);
        results.add(result2); // Duplicate user/player

        Game game = new Game();
        game.setGameResults(results);
        game.setGameResultsStatus(GameResultsStatus.PENDING_ACCEPTANCE);

        when(gameRepository.findById(anyInt())).thenReturn(Optional.of(game));

        PatchGameResultsStatus patchGameResultsStatus = new PatchGameResultsStatus();
        patchGameResultsStatus.setGameResultsStatus(GameResultsStatus.ACCEPTED);

        // Call the method under test and expect an exception
        assertThrows(InvalidDataInGameResultsException.class, () -> gameService.updateGameResultsStatus(1, patchGameResultsStatus));
    }

    @Test
    void givenUniqueUserInGameResults_whenUpdateGameResultsStatus_thenCallRepoSave() throws EntityNotFound, InvalidDataInGameResultsException, IncompleteGameResultsException, IllegalGameStateException {
        AppUser appUser1 = mock(AppUser.class);
        when(appUser1.getId()).thenReturn(1);
        AppUser appUser2 = mock(AppUser.class);
        when(appUser2.getId()).thenReturn(2);

        Player player1 = mock(Player.class);
        when(player1.getUser()).thenReturn(appUser1);
        Player player2 = mock(Player.class);
        when(player2.getUser()).thenReturn(appUser2);

        List<GameResult> results = new ArrayList<>();

        GameResult result1 = mock(GameResult.class);
        when(result1.getGame()).thenReturn(mock(Game.class));
        when(result1.getPlayer()).thenReturn(player1);
        when(result1.getPosition()).thenReturn(1);
        when(result1.getCharacterName()).thenReturn("Yoshi");
        when(result1.getScore()).thenReturn(60);

        GameResult result2 = mock(GameResult.class);
        when(result2.getGame()).thenReturn(mock(Game.class));
        when(result2.getPlayer()).thenReturn(player2);
        when(result2.getPosition()).thenReturn(2);
        when(result2.getCharacterName()).thenReturn("Toadette");
        when(result2.getScore()).thenReturn(50);

        results.add(result1);
        results.add(result2);

        Game game = new Game();
        game.setGameResults(results);
        game.setGameResultsStatus(GameResultsStatus.PENDING_ACCEPTANCE);

        when(gameRepository.findById(anyInt())).thenReturn(Optional.of(game));

        PatchGameResultsStatus patchGameResultsStatus = new PatchGameResultsStatus();
        patchGameResultsStatus.setGameResultsStatus(GameResultsStatus.ACCEPTED);

        // Call the method under test
        gameService.updateGameResultsStatus(1, patchGameResultsStatus);
        verify(gameRepository, times(1)).save(game);
    }
}