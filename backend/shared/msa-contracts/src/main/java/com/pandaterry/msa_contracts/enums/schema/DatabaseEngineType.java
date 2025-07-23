package com.pandaterry.msa_contracts.enums.schema;

public enum DatabaseEngineType {
    MYSQL("com.mysql.cj.jdbc.Driver"),
    POSTGRESQL("org.postgresql.Driver"),
    ORACLE("oracle.jdbc.OracleDriver");

    private final String driver;

    DatabaseEngineType(final String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }
}
