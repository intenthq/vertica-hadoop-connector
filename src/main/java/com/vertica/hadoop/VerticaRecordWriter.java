/*
Copyright (c) 2005 - 2012 Vertica, an HP company -*- Java -*-
Copyright 2013, Twitter, Inc.


Licensed under the Apache License, Version 2.0 (the "License");

you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,

WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.vertica.hadoop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class VerticaRecordWriter extends RecordWriter<Text, VerticaRecord> {
  	private static final Log LOG = LogFactory.getLog("com.vertica.hadoop");

	final Relation vTable;
	Connection connection = null;
	PreparedStatement statement = null;
	final long batchSize;
	long numRecords = 0;

	public VerticaRecordWriter(Connection conn, String writerTable, long batch) throws SQLException {
		this.connection = conn;
		this.batchSize = batch;
		this.vTable = new Relation(writerTable);
		this.statement = conn.prepareStatement(StatementFactory.insert(conn, writerTable));
	}

	@Override
	public void close(TaskAttemptContext context) throws IOException {
		try {
		  	// committing and closing the connection is handled by the VerticaTaskOutputCommitter
		  	if (LOG.isDebugEnabled()) { LOG.debug("executeBatch called during close"); }
			statement.executeBatch();
  		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(Text table, VerticaRecord record) throws IOException {
		if (table != null && !table.toString().equals(vTable.getTable()))
			throw new IOException("Writing to different table " + table.toString() + ". Expecting " + vTable.getTable());

		try {
			record.write(statement);
			numRecords++;
			if (numRecords % batchSize == 0) {
        		if (LOG.isDebugEnabled()) { LOG.debug("executeBatch called on batch of size " + batchSize); }
				statement.executeBatch();
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
