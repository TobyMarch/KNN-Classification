/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lab6classification;

import java.util.List;

/**
 *
 * @author tmarch1
 */
public class Evaluator {
    Index myIndex = new Index();
    SearchDB myDatabase = new SearchDB();
    String[] docSchemes = {"nnn","ltc"};
    String[] querySchemes = {"nnn","ltc"};

    public void run(){
        //Create HashMap of search queries and relevant results
        List<String> termList = myDatabase.getSearchTerms();
        String[] termArr = {"A Tale of Two Cities","The Lion King","Watership Down","Avatar","Lolita"};
        for(String searchTerm : termList){
            System.out.println("Term: " + searchTerm);
            searchTerm = searchTerm.toLowerCase();
            for(String dScheme : docSchemes){
                myIndex.rankDocs(dScheme);
                for(String qScheme : querySchemes){
                    myIndex.rankQuery(searchTerm, qScheme);
                    System.out.println("Ranking: " + dScheme + "." + qScheme);
                }
            }
        }
    }


}
