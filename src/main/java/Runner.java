import java.io.File;
import java.util.List;
import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.dao.SimpleFileRatingDAO;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;

/**
 * Created by Dmytro_Kovalskyi on 22.09.2015.
 */
public class Runner {
    public static void main(String[] args) throws RecommenderBuildException {
        LenskitConfiguration config = new LenskitConfiguration();
// Use item-item CF to score items
        config.bind(ItemScorer.class).to(ItemItemScorer.class);
// let's use personalized mean rating as the baseline/fallback predictor.
// 2-step process:
// First, use the user mean rating as the baseline scorer
        config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);
// Second, use the item mean rating as the base for user means
        config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);
// and normalize ratings by baseline prior to computing similarities
        config.bind(UserVectorNormalizer.class).to(BaselineSubtractingUserVectorNormalizer.class);

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File("D:\\ratings.csv"), ","));

        LenskitRecommender rec = LenskitRecommender.build(config);
        System.out.println(rec.getRatingPredictor().predict(100, 110));
        ItemRecommender irec = rec.getItemRecommender();
        List<ScoredId> recommendations = irec.recommend(3, 1);
        System.out.println(recommendations);
    }
}
