/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lab6classification;

/**
 *
 * @author tmarch1
 */
public class Triple {
    String artistName;
    String artistGenre;
    String filePath;

    public Triple(){
        artistName = "";
        artistGenre = "";
        filePath = "";
    }

    public Triple(String nameIn,String genreIn,String pathIn){
        artistName = nameIn;
        artistGenre = genreIn;
        filePath = pathIn;
    }

    //Accessors
    public String getArtist(){
        return artistName;
    }
    public String getGenre(){
        return artistGenre;
    }
    public String getPath(){
        return filePath;
    }

    //Mutators
    public void setArtist(String nameIn){
        artistName = nameIn;
    }
    public void setGenre(String genreIn){
        artistGenre = genreIn;
    }
    public void setPath(String pathIn){
        filePath = pathIn;
    }

}
