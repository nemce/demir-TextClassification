/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets.Generator;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Transaction;

/**
 *
 * @author nmeltem
 */
public class RandomTrainTestSetGenerator {

    /**
     * Generate random integers in a certain range.
     */
    public static final void main(String... aArgs) {

        CollectionSetGenerator csg = new CollectionSetGenerator();
        String SourceFolder = "D:\\Datasets\\TTCP\\TTC-3600_Orj\\ALL_ZEMBEREK\\";
        String DestFolder = "D:\\Datasets\\TTCP\\test\\";

        log("Generating random integers in the range 1..10.");

        int START = 500000;
        int END = 500599;
        int iCollectionId = 19;
        int iSetId = 1;

        Random random = new Random();
        Transaction tx = csg.getSession().beginTransaction();
        for (int idx = 1; idx <= 120; ) {
            int iFileId = showRandomInteger(START, END, random);
            try {
                File source = new File(SourceFolder + iFileId);
                File dest = new File(DestFolder + iFileId);

                java.nio.file.Files.copy(source.toPath(), dest.toPath());
                ++idx;
                csg.UpdateTtDocs(iCollectionId, iSetId, String.valueOf(iFileId), "TE", csg.getSession());
                java.nio.file.Files.delete(source.toPath());
            } catch (java.nio.file.FileAlreadyExistsException ex) {
                System.out.println(iFileId);
            } catch (IOException ex) {
                Logger.getLogger(RandomTrainTestSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        tx.commit();
    }

    private static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        log("Generated : " + randomNumber);
        return randomNumber;
    }

    private static void log(String aMessage) {
        System.out.println(aMessage);
    }
}
