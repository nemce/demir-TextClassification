/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.test;
/**
 *
 * @author nmeltem
 */
public class RankingByJforestTest {

    public static void main(String[] args) {
        args = new String[10];
        String TETopics = "D:\\Datasets\\REUTERS\\reuters_cat7\\RANKING5\\4_test.txt";
        String RankingHome ="D:\\terrier_home_v4\\ranking_bat\\ranking5";
        String TerrierHome = "D:\\terrier_home_v4";
        String Test_id = "5";
                
        args[0] = "-r";
        args[1] = "-Dtrec.model=DPH";
        args[2] = "-Dtrec.topics=" + TETopics;
        args[3] = "-Dtrec.matching=JforestsModelMatching,FatFeaturedScoringMatching,org.terrier.matching.daat.FatFull";
        args[4] = "-Dfat.featured.scoring.matching.features=FILE";
        args[5] = "-Dfat.featured.scoring.matching.features.file="+TerrierHome+"\\etc\\features.list";
        args[6] = "-Dtrec.results.file=te.res";
        args[7] = "-Dfat.matching.learned.jforest.model="+TerrierHome+"\\var\\results\\ensemble%test_id%.txt";
        args[8] = "-Dfat.matching.learned.jforest.statistics="+TerrierHome+"\\var\\results\\jforests-feature-stats.txt";
        args[9] = "-Dproximity.dependency.type=SD";

        org.terrier.applications.TrecTerrier.main(args);
    }
}
