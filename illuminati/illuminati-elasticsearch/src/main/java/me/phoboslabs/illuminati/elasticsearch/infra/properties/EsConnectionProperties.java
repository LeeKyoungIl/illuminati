package me.phoboslabs.illuminati.elasticsearch.infra.properties;

public class EsConnectionProperties {

    private ElasticsearchInfo elasticsearchInfo;

    public EsConnectionProperties() {
    }

    public ElasticsearchInfo getElasticsearchInfo() {
        return this.elasticsearchInfo;
    }

    public void setElasticsearchInfo(ElasticsearchInfo elasticsearchInfo) {
        this.elasticsearchInfo = elasticsearchInfo;
    }

    public static class ElasticsearchInfo {

        private String user;
        private String pass;
        private String host;
        private int port;

        public void setUser(String user) {
            this.user = user;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUser() {
            return this.user;
        }

        public String getPass() {
            return this.pass;
        }

        public String getHost() {
            return this.host;
        }

        public int getPort() {
            return port;
        }
    }
}
