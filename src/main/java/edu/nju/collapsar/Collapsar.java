package edu.nju.collapsar;

import edu.nju.collapsar.util.ConfigManager;

/**
 * Created by rico on 2016/11/5.
 */
public class Collapsar {
    public static void main(String[] args){
        ConfigManager.init();
        switch (ConfigManager.getServerType()){
            case BIO:
                BioServer bio = new BioServer();
                bio.serve();
                break;
            case NIO:
                NioServer nio = new NioServer();
                nio.serve();
                break;
            case AIO:
                AioServer aio = new AioServer();
                aio.serve();
                break;
            default:
                BioServer defaultServer = new BioServer();
                defaultServer.serve();
                break;

        }


    }
}
