/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 */
public class FeatureLoader {
    
     public static  Map LoadFeaturesFromFile()
     {
        String FeaturesFileName = ApplicationSetup.getProperty("demir.features.MI", "");
        if (FeaturesFileName.equals(null) || FeaturesFileName.equals("")) {
            return null;
        }
         return LoadFeaturesFromFile(FeaturesFileName);
     }
    
     public static  Map LoadFeaturesFromFile(String FeaturesFileName)  {

         if ( FeaturesFileName.equals(null) || FeaturesFileName.equals("") ) {
            return null;
        }
         
        Map<String, Double> featureValues = new HashMap<>();
        BufferedReader br = null;
        String sCurrentLine = null;
        try {
            br = new BufferedReader(new FileReader(FeaturesFileName));
            while ((sCurrentLine = br.readLine()) != null) {
             String [] sTerm = sCurrentLine.split("\t");
             featureValues.put(sTerm[0], Double.parseDouble(sTerm[1]));
             //System.out.println(sCurrentLine);
            }
            if (br != null) {
                    br.close();
                }
            System.out.println(FeaturesFileName + " loaded");
        } 
        catch (IOException e) {
            
            System.out.println(sCurrentLine);
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println(sCurrentLine);
            e.printStackTrace();
        }
        
        return featureValues;
    }
    
}
