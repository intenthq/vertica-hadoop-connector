package com.vertica.hadoop;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class StatementFactory implements Serializable {
    private static final Log LOG = LogFactory.getLog("com.vertica.hadoop");

    private static final String META_STMT =
        "select ordinal_position, column_name, data_type, is_identity, data_type_name " +
                "from v_catalog.odbc_columns " +
                "where schema_name = ? and table_name = ? "+
                "order by ordinal_position;";

    public static String insertDirect(Connection conn, String writerTable) throws SQLException {
        return create("INSERT /*+ direct */ INTO ", conn, writerTable);
    }

    public static String insert(Connection conn, String writerTable) throws SQLException {
        return create("INSERT INTO ", conn, writerTable);
    }

    private static String create(String fragment, Connection conn, String writerTable) throws SQLException {
        Relation vTable = new Relation(writerTable);

        StringBuilder sb = new StringBuilder(fragment).append(vTable.getQualifiedName()).append("(");

        StringBuilder values = new StringBuilder().append(" VALUES(");

        PreparedStatement stmt = conn.prepareStatement(META_STMT);
        stmt.setString(1, vTable.getSchema());
        stmt.setString(2, vTable.getTable());

        ResultSet rs = stmt.executeQuery();
        boolean addComma = false;
        while (rs.next()) {
            if (!rs.getBoolean(4)) {
                if (addComma) {
                    sb.append(',');
                    values.append(',');
                }
                sb.append(rs.getString(2));
                values.append('?');
                addComma = true;
            } else {
                LOG.debug("Skipping identity column " + rs.getString(4));
            }
        }

        sb.append(')');
        values.append(')');
        sb.append(values.toString());

        System.out.println("prepared statement is: " + sb.toString());

        return sb.toString();
    }

}
