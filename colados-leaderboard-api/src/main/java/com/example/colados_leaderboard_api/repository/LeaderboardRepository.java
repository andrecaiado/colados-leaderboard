package com.example.colados_leaderboard_api.repository;

import com.example.colados_leaderboard_api.dto.MonthlyLeaderboard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LeaderboardRepository {

    @PersistenceContext
    private EntityManager em;

    public List<MonthlyLeaderboard> getMonthlyLeaderboard(Integer championshipId, Integer month, Integer year) {
        String sql = """
                with results_by_month_2 as (
                    with results_by_month as (
                        select
                            position,
                            player_id,
                            sum(score) as total_score,
                            count(CASE WHEN max_score_achieved THEN 1 END) as max_score_achieved,
                            game_id
                        FROM game_result gr
                        left join game g
                        on gr.game_id = g.id
                        where EXTRACT(MONTH FROM g.played_at) = :month and EXTRACT(YEAR FROM g.played_at) = :year and position in (1,2,3,4,5,6,7,8,9,10,11,12) and championship_id = :championshipId and game_results_status = 'ACCEPTED'
                        group by player_id, position, game_id
                    )
                    select player_id, count(1) as count_position, position, sum(total_score) as total_score, sum(max_score_achieved) as max_score_achieved_count
                    from results_by_month
                    group by position, player_id
                    order by position asc, total_score desc
                )
                select row_number() over (
                    order by position asc, total_score desc
                ) as rank, *
                from results_by_month_2
        """;

        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter("championshipId", championshipId)
                .setParameter("month", month)
                .setParameter("year", year)
                .getResultList();

        return results.stream()
                .map(row -> new MonthlyLeaderboard(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).intValue(),
                        ((Number) row[5]).intValue()
                ))
                .toList();
    }
}
