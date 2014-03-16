package lab6classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author tmarch1
 */
/*
 * Class: KNN Classifier
 * Purpose: Performs K-Nearest-Neighbor comparisons on music artists
 *      based on webcrawled text, and checks accuracy by genre
 */
public class KNNClassifier {
    //Database containing original documents
    SearchDB myDatabase = new SearchDB();
    //Sort class to make sense of my comparison results
    Quicksort mySorter = new Quicksort();
    
    //Confusion matrix to model the accuracy of results
    double[][] confusionMatrix = new double[4][4];
    //HashMap to contain name of Artist and entire text content of relevant documents
    HashMap<String,String> artistMap = new HashMap<String,String>();
    //HashMap to contain name of Artists and their specific genre.
    HashMap<String,String> truthMap = new HashMap<String,String>();
    //HashMaps to contain name of Artists and a specific index in the comparison array
    HashMap<String,String> ArtisttoIndexMap = new HashMap<String,String>();
    HashMap<String,String> IndextoArtistMap = new HashMap<String,String>();


    /* Function: appendToMap
     * Purpose: pulls the webcrawled text asssociated with each artist,
     *      and stores the completet text in a hashmap
     * Parameter:
     *      String artist - the name of the artist to retrieve
     * Return Value: none
     * Precondition: the run() method is going through the list of artists
     * Postcondition: the text associated with the passed artist has been added to the hashmap
     */
    public void appendToMap(String artist){
        List<String> docList = new ArrayList<String>();
        String artistText = "";
        StringBuilder content = new StringBuilder();
        
        //Populate docList with names of relevant clean docs
        docList = myDatabase.getSearchResults(artist);

        //Open each of the documents and read their content
        for(String artistDoc : docList){
            try {
                String filePath = "/Users/tmarch1/Development/NetBeansProjects/490/490 Lab6-Classification/data/clean/" + artistDoc + ".txt";
                
                //Checks that the file exists before trying to read from it. I hate error messages.
                File docFile = new File(filePath);
                if(docFile.exists()){
                    Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(docFile)));
                    while(fileScanner.hasNext()){
                        String nextText = fileScanner.nextLine();
                        //System.out.print(nextText + " ");
                        content.append(nextText);
                        content.append(" ");
                    }
                    //System.out.println();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(KNNClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        artistText = content.toString();
        artistMap.put(artist, artistText);

    }

    /* Function: decideGenre
     * Purpose: calculates an artist's K nearest neighbors, and guesses the artist's genre
     * Parameters:
     *      String artist - the name of the artist to be added to the hashmap
     *      double[] unsortedArr - an array of the relevance scores for other artists
     * Return Value: none
     * Precondition: the run() is testing a random selection of artists, based on those already in the training set
     * Postcondition: the artist guess has been recorded in the confusion matrix
     */
    public void decideGenre(String artist, double[] unsortedArr){
        int kVal = 7;
        int[] indexArr = new int[80];
        double[] genreArr = new double[4];
        int[] genreIndex = new int[4];
        
        //Initialize an index for tracking genre guesses
        for(int i = 0;i < genreArr.length;i++){
            genreIndex[i] = i;
        }
        //Initialize and index for tracking artist scores
        for(int a = 0;a < indexArr.length;a++){
            indexArr[a] = a;
        }
        //Quicksort the artistScores, organizing indexArr in the same order
        mySorter.quicksort(unsortedArr, indexArr);
        System.out.println("Most relevant Artists:");
        for(int i = indexArr.length-1;i > indexArr.length-1-kVal;i--){
            //gets the names and scores of most-relevant artists
            String guessArtist = IndextoArtistMap.get(Integer.toString(indexArr[i]));
            String guessGenre = truthMap.get(guessArtist);
            //Prints the artists and their scores
            System.out.print(guessArtist);
            System.out.println(": " + unsortedArr[i]);

            //uses the relevant artists to compile guesses about the original artist's genre
            if(guessGenre.equals("alternative"))
                genreArr[0]++;
            else if(guessGenre.equals("classic_rock"))
                genreArr[1]++;
            else if(guessGenre.equals("country_pop"))
                genreArr[2]++;
            else if(guessGenre.equals("hip_hop"))
                genreArr[3]++;
        }
        String correctGenre = truthMap.get(artist);
        mySorter.quicksort(genreArr, genreIndex);
        //Take the highest-sorted index as the predicted genre
        int p = genreIndex[3];
        //Take the actual genre from the database
        int a = 0;
        if(correctGenre.equals("alternative"))
            a = 0;
        else if(correctGenre.equals("classic_rock"))
            a = 1;
        else if(correctGenre.equals("country_pop"))
            a = 2;
        else if(correctGenre.equals("hip_hop"))
            a = 3;
        //Increment the intersecting cell of the confusion matrix
        confusionMatrix[p][a]++;

        System.out.println("Actual Genre: " + correctGenre);
        System.out.println();


    }

    /* Function: run
     * Purpose: initializes the KNN classifier, assigns artists into training and test sets
     * Parameters: none
     * Return Value: none
     * Precondition: A webcrawler has pulled text from webpages relating to each artist,
     * and the text has been stored in a positional index of nested hashmaps
     * Postcondition: All artists have been compared, and an accuracy score has been computed from the confusion matrix
     */
    public void run(){
        List<String> termList = new ArrayList<String>();
        termList = myDatabase.getSearchTerms();
        double numFolds = 0.0;

        //Populate the indexMap for later use in comparison
        int indexVal = 0;
        for(String searchTerm : termList){
            truthMap.put(searchTerm, myDatabase.getType_byName(searchTerm));
            ArtisttoIndexMap.put(searchTerm, Integer.toString(indexVal));
            IndextoArtistMap.put(Integer.toString(indexVal), searchTerm);
            indexVal++;
        }
        //Go through clean files to build a HashMap holding the appended text
        for(String searchTerm : termList){
            if(!searchTerm.isEmpty()){
                //this.appendDocs(searchTerm);
                //System.out.println("termList entry:" + searchTerm);
                this.appendToMap(searchTerm);
            }
        }
        //Commence Folding!
        for(int fold = 0;fold < 2;fold++){
            //Blank constructor for positional Index - will be filled later
            Index myIndex = new Index("KNN");
            System.out.println("Fold #" + (fold + 1));
            Collections.shuffle(termList);
            String[] termArr = termList.toArray(new String[termList.size()]);
            System.out.println("Training Set:");
            for(int i = 0;i < 64;i++){
                //System.out.println(i);
                //Pass the appended text of each artist in the Training Set to the index, to be stored and weighted
                //A stored index corresponding to each artistName must be passed, because my Rank methods expect
                //numeric document names
                System.out.println("Artist " + ArtisttoIndexMap.get(termArr[i]) + ": " + termArr[i]);
                //System.out.println(artistMap.get(termArr[i]));
                myIndex.ReadArtist(ArtisttoIndexMap.get(termArr[i]), artistMap.get(termArr[i]));
            }
            System.out.println("Ranking...");
            myIndex.rankDocs("ltc");
            //Rank and Classify each of the artists in the Test Set
            System.out.println("Test Set:");
            for(int j = 64;j < termArr.length;j++){
                System.out.println("Artist " + ArtisttoIndexMap.get(termArr[j]) + ": " + termArr[j]);
                //System.out.println(artistMap.get(termArr[j]));
                //System.out.println();
                myIndex.rankQuery(artistMap.get(termArr[j]),"ltc");
                double[] unsortedArr = myIndex.compareArtists(termList);

                this.decideGenre(termArr[j],unsortedArr);

            }
            numFolds++;
        }
        
        double correctGuesses = (confusionMatrix[0][0] + confusionMatrix[1][1] + confusionMatrix[2][2] + confusionMatrix[3][3]);
        correctGuesses /= (16.0 * numFolds);
        System.out.println("Accuracy: " + correctGuesses);

    }

}
