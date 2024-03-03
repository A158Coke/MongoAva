package com.dam.U5EX01_YC.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {
    @Value("${MongoDataBasePort}")
    private String port;

    @Value("${MongoDataBaseUrl}")
    private String url;

    @Value("${MongoDataBaseName}")
    private String databaseName;

    @Value("${MongoDataBaseCollection}")
    private String collection;

    @Value("${CsvFilePath}")
    private String csvFilePath;

    @Value("${DateFormatRegex}")
    private String dataFormatRegex;


    public String getDataFormatRegex() {
        return dataFormatRegex;
    }

    public String getConnectionString() {
        return url + port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCollectionName() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }
}
