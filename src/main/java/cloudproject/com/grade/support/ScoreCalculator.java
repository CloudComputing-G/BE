package cloudproject.com.grade.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ScoreCalculator {

    private ScoreCalculator() {
    }

    public static Double calculateCorrectRate(Integer totalScore, int maxScore) {
        if (totalScore == null || maxScore == 0) {
            return null;
        }
        return BigDecimal.valueOf(totalScore)
                .divide(BigDecimal.valueOf(maxScore), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
