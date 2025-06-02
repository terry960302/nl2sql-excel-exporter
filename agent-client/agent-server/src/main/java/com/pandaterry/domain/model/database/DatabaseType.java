package com.pandaterry.domain.model.database;

public enum DatabaseType {
    MYSQL("com.mysql.cj.jdbc.Driver"),
    POSTGRESQL("org.postgresql.Driver"),
    ORACLE("oracle.jdbc.OracleDriver")
    ;

    private final String driver;

    DatabaseType(final String driver){
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }
}