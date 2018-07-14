package me.phoboslabs.illuminati.elasticsearch.model;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface IlluminatiEsModel {

    String getJsonString ();

    String getEsUrl (String baseUrl);

    String getBaseEsUrl (String baseUrl);

    void setEsUserAuth (String esUserName, String esUserPass);

    boolean isSetUserAuth ();

    String getEsAuthString ();

    String getIndexMapping ();
}
