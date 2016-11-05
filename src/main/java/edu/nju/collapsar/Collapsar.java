package edu.nju.collapsar;

import edu.nju.collapsar.util.ConfigManager;

/**
 * Created by rico on 2016/11/5.
 */
public class Collapsar {
    public static void main(String[] args){
        ConfigManager.init();
        BioServer bio = new BioServer();
        bio.serve();
    }
}
