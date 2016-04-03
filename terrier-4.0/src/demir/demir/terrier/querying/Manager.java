/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.querying;

import java.util.HashMap;
import java.util.Map;
import org.terrier.matching.Matching;
import static org.terrier.querying.Manager.NAMESPACE_MATCHING;
import org.terrier.querying.Request;
import org.terrier.structures.Index;

/**
 *
 * @author nmeltem
 */
public class Manager extends org.terrier.querying.Manager {

    Map featureValues = null;

    public Manager() {
        super();
    }

    public Manager(Index _index) {
        super(_index);
    }

    public Manager(Index _index, Map features) {
        super(_index);
        featureValues = features;
    }

    public Map GetFeatures() {
        return featureValues;
    }

    /*-------------------------------- helper methods -----------------------------------*/
	//helper methods. These get the appropriate modules named Name of the appropate type
    //from a hashtable cache, or instantiate them should they not exist.
    /**
     * Returns the matching model indicated to be used, based on the Index and
     * the Matching name specified in the passed Request object. Caches already
     * instantiaed matching models in Map Cache_Matching. If the matching model
     * name doesn't contain '.', then NAMESPACE_MATCHING is prefixed to the
     * name.
     *
     * @param rq The request indicating the Matching class, and the
     * corresponding instance to use
     * @return null If an error occurred obtaining the matching class
     */
    protected Matching getMatchingModel(Request rq) {
        Matching rtr = null;
        Index _index = rq.getIndex();
        String ModelName = rq.getMatchingModel();
        logger.info("Model Name : " + ModelName);
		//add the namespace if the modelname is not fully qualified

        final String ModelNames[] = ModelName.split("\\s*,\\s*");
        final int modelCount = ModelNames.length;
        StringBuilder entireSequence = new StringBuilder();
        for (int i = 0; i < modelCount; i++) {
            if (ModelNames[i].indexOf(".") < 0) {
                ModelNames[i] = NAMESPACE_MATCHING + ModelNames[i];
            } else if (ModelNames[i].startsWith("uk.ac.gla.terrier")) {
                ModelNames[i] = ModelNames[i].replaceAll("uk.ac.gla.terrier", "org.terrier");
            }
            entireSequence.append(ModelNames[i]);
            entireSequence.append(",");
        }
        ModelName = entireSequence.substring(0, entireSequence.length() - 1);
        //check for already instantiated class
        Map<String, Matching> indexMap = Cache_Matching.get(_index);
        if (indexMap == null) {
            Cache_Matching.put(_index, indexMap = new HashMap<String, Matching>());
        } else {
            rtr = indexMap.get(ModelName);
        }
        if (rtr == null) {
            boolean first = true;
            for (int i = modelCount - 1; i >= 0; i--) {
                try {
                    //load the class
                    if (ModelNames[i].equals("org.terrier.matching.Matching")) {
                        ModelNames[i] = "org.terrier.matching.daat.Full";
                    }
                    Class<? extends Matching> formatter = Class.forName(ModelNames[i], false, this.getClass().getClassLoader()).asSubclass(Matching.class);
					//get the correct constructor - an Index class in this case

                    Class<?>[] params;
                    Object[] params2;
                    if (first) {
//			params = new Class[1];
//			params2 = new Object[1];
//						
//			params[0] = Index.class;
//			params2[0] = _index;

                        params = new Class[2];
                        params2 = new Object[2];

                        params[0] = Index.class;
                        params2[0] = _index;
                        params[1] = Map.class;
                        params2[1] = GetFeatures();
                    } else {
                        
                        /// Hangi Constructor'ı çalıştıracağını bilmediğim için 
                        /// 3. parametre olarak Map.Class -> GetFeatures eklemedim.
                        params = new Class[2];
                        params2 = new Object[2];

                        params[0] = Index.class;
                        params2[0] = _index;
                        params[1] = Matching.class;
                        params2[1] = rtr;
                    }
                    //and instantiate
                    rtr = (Matching) (formatter.getConstructor(params).newInstance(params2));
                    first = false;
                } catch (java.lang.reflect.InvocationTargetException ite) {
                    logger.error("Recursive problem with matching model named: " + ModelNames[i], ite);
                    return null;
                } catch (Exception e) {
                    logger.error("Problem with matching model named: " + ModelNames[i], e);
                    return null;
                }
            }
        }
        Cache_Matching.get(_index).put(ModelName, rtr);
        return rtr;
    }
}
