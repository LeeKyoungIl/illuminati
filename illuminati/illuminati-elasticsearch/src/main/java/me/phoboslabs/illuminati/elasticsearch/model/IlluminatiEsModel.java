package me.phoboslabs.illuminati.elasticsearch.model;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface IlluminatiEsModel {

    String getJsonString ();

    String getEsUrl (String baseUrl) throws Exception;

    String getBaseEsUrl (String baseUrl) throws Exception;

    void setEsUserAuth (String esUserName, String esUserPass);

    boolean isSetUserAuth ();

    String getEsAuthString () throws Exception;

    String getIndexMapping ();
}
