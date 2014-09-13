function Database(name, desc, version, size) {
	this.name = name;
	this.description = desc;
	this.version = version;
	this.size = size;
	this.database = null;
}

Database.prototype.createTable = function(xmlSchema) {
	console.log("Parent method should not be called: createTable");
};

Database.prototype.dropTable = function(tableName) {
	console.log("Parent method should not be called: dropTable");
};

Database.prototype.selectQuery = function(query, args, success, error) {
	console.log("Parent method should not be called: selectQuery");
};

Database.prototype.selectFullRecord = function(table, params, success, error) {
	console.log("Parent method should not be called: selectFullRecord");
};

Database.prototype.insertRecord = function(table, params, success, error) {
	console.log("Parent method should not be called: insertRecord");
};

Database.prototype.insertRecords = function(table, params, success, error) {
	console.log("Parent method should not be called: insertRecords");
};

Database.prototype.updateRecord = function(table, setParams, whereParams, success, error) {
	console.log("Parent method should not be called: updateRecord");
};

Database.prototype.deleteRecord = function(table, params, success, error) {
	console.log("Parent method should not be called: deleteRecords");
};

Database.prototype.descTable = function(tableName) {
	console.log("Parent method should not be called: descTable");
};


// *************** Web SQL Database *****************

function WebSQLDatabase(name, desc, version, size) {
	Database.call(this, name, desc, version, size);
	
	try {
		this.database = openDatabase(name, version, desc, size);
	}
	catch (e) {
		console.log("This browser does not support Web SQL databases.");
	}
}

WebSQLDatabase.prototype = new Database();
WebSQLDatabase.prototype.constructor = WebSQLDatabase;

WebSQLDatabase.prototype.createTable = function(xmlSchema) {
	var query = "CREATE TABLE IF NOT EXISTS " + $(xmlSchema).children("name").text() + " (";
	
	$(xmlSchema).children("column").each(function() {
		query += $(this).children("name").text();
		
		if($(this).children("type").length == 1) {
			query += " " + $(this).children("type").text();
		}
		
		if($(this).children("unique").length == 1) {
			query += " UNIQUE";
		}
		
		if($(this).children("null").text() == "true") {
			query += " NOT NULL";
		}
		
		query += ","
	});
	
	$(xmlSchema).children("foreignkey").each(function() {
		query += "FOREIGN KEY (" + $(this).children("column").text() + ") ";
		query += "REFERENCES " + $(this).children("reftable").text() + " (" + $(this).children("refcolumn").text() + ")";
		
		if($(this).children("cascade").length == 1) {
			if($(this).children("cascade").children("delete").length == 1) {
				query += " ON DELETE CASCADE";
			}
			
			if($(this).children("cascade").children("update").length == 1) {
				query += " ON UPDATE CASCADE";
			}
		}
		
		query += ","
	});
	
	query = query.substring(0, query.length - 1) + ");";
	
	this.database.transaction(function(tx) {
		tx.executeSql(query, [], function(tx) {
				console.log("TABLE CREATED SUCCESSFULLY!");

				if(tableCount) {
					tablesFinished++;

					if(tablesFinished == tableCount) {
						$(document).trigger("schemacomplete");
					}
				}
			},
			function(tx, e) {
				console.log("ERROR CREATING TABLE: " + e.message);
				console.log("QUERY: " + query);
			});
		});
};

WebSQLDatabase.prototype.dropTable = function(tableName, success, error) {
	console.log(tableName);
	this.database.transaction(function(tx) {
		tx.executeSql("DROP TABLE IF EXISTS " + tableName, [], success ? success : successfulDropTableCallback, error ? error : errorCallback);
	});
};

WebSQLDatabase.prototype.selectFullRecord = function(table, params, success, error) {
	var query = prepareSelectFullQuery(table, params);
	
	this.database.transaction(function(tx) {
		tx.executeSql(query["query"], query["args"], success ? success : successfulSelectCallback, error ? error : errorCallback);
	});
};

WebSQLDatabase.prototype.selectQuery = function(query, args, success, error) {
	this.database.transaction(function(tx) {
		tx.executeSql(query, args, success ? success : successfulSelectCallback, error ? error : errorCallback);
	});
};

WebSQLDatabase.prototype.insertRecord = function(table, params, success, error) {
	var query = prepareSingleInsertQuery(table, params);
	
	this.database.transaction(function(tx) {
		tx.executeSql(query["query"], query["args"], success ? success : successfulInsertCallback, error ? error : errorCallback);
	});
};

WebSQLDatabase.prototype.insertRecords = function(table, params, finish, success, error) {
	this.database.transaction(function(tx) {
		for(var i = 0; i < params.length; i++) {
			var query = prepareSingleInsertQuery(table, params[i]);
			tx.executeSql(query["query"], query["args"], success ? success : successfulInsertCallback, error ? error : errorCallback);
		}

		if(finish) {
			finish.call();
		}
	});
};

WebSQLDatabase.prototype.updateRecord = function(table, setParams, whereParams, success, error) {
	var query = prepareUpdateQuery(table, setParams, whereParams);
	
	this.database.transaction(function(tx) {
		tx.executeSql(query["query"], query["args"], success ? success : successfulUpdateCallback, error ? error : errorCallback);
	});
};

WebSQLDatabase.prototype.deleteRecord = function(table, params, success, error) {
	var query = prepareDeleteQuery(table, params);
	
	this.database.transaction(function(tx) {
		tx.executeSql(query["query"], query["args"], success ? success : successfulDeleteCallback, error ? error : errorCallback);
	});
};

WebSQLDatabase.prototype.descTable = function(tableName) {
	this.database.transaction(function(tx) {
		tx.executeSql("SELECT * FROM sqlite_master WHERE name = '" + tableName + "'", [], 
		function(tx, results) {
			for(var i = 0; i < results.rows.length; i++) {
				console.log(results.rows.item(i).sql);
			}
		},
		function(tx, e) {
			console.log(e.message);
		});
	});
};

// **************** Indexed DB ****************

function IndexedDB(name, desc, version, size) {
	Database.call(this, name, desc, version, size);

	try {
		this.database = openDatabase(name, version, desc, size);
	}
	catch (e) {
		console.log("This browser does not support Web SQL databases.");
	}
}

IndexedDB.prototype = new Database();
IndexedDB.prototype.constructor = IndexedDB;

// ************* HELPER METHODS ***************

function getDatabase() {
	var webSQLDatabase = new WebSQLDatabase(dbName, dbDescription, dbVersion, dbSize); //These variables need to be initialized before this is called
	
	if(webSQLDatabase.database != null) {
		return webSQLDatabase;
	}
	else {
		//TODO This will try to return an Indexed DB in the future
		return null;
	}
}

// *** Never call this if you have data in your database that you want to keep! It will delete any existing data. ***
function pullSchema() {
	$.get("schema.xml", createDatabase, "xml");
}

// *** Drop any existing tables with the same names and the tables in the schema, then add the new ones. ***
function createDatabase(xml) {
	var database = getDatabase();
	
	if(database != null) {
		tableCount = $(xml).find("table").length;
		tablesFinished = 0;
		$(xml).find("table").each(function() {
			database.dropTable($(this).children("name").text());
			database.createTable(this);
		});
	}
}

function insertRecord(table, params, success, error) {
	var database = getDatabase();
		
	if(database != null) {
		database.insertRecord(table, params, success, error);
	}
}

function insertRecords(table, params, success, error) {
	var database = getDatabase();

	if(database != null) {
		database.insertRecords(table, params, success, error);
	}
}

function selectRecord(table, params, success, error) {
	var database = getDatabase();
	
	if(database != null) {
		database.selectFullRecord(table, params, success, error);
	}
}

function selectQuery(query, args, success, error) {
	var database = getDatabase();

	if(database != null) {
		database.selectQuery(query, args, success, error);
	}
}

function deleteRecord(table, params, success, error) {
	var database = getDatabase();
	
	if(database != null) {
		database.deleteRecord(table, params, success, error);
	}
}

function updateRecord(table, setParams, whereParams, success, error) {
	var database = getDatabase();
	
	if(database != null) {
		database.updateRecord(table, setParams, whereParams, success, error);
	}
}

function dropTable(tableName, success, error) {
	var database = getDatabase();
	
	if(database != null) {
		database.dropTable(tableName, success, error);
	}
}

function descTable(tableName) {
	var database = getDatabase();
	
	if(database != null) {
		database.descTable(tableName);
	}
}

function prepareSingleInsertQuery(table, params) {
	var query = "INSERT INTO " + table + " (";
	
	for(param in params) {
		query += param + ",";
	}
	
	query = query.substring(0, query.length - 1);
	query += ") VALUES (";
	
	var args = new Array();
	var i = 0;
	
	for(param in params) {
		query += "?,";
		args[i] = params[param];
		i++;
	}
	
	query = query.substring(0, query.length - 1);
	query += ")";
	
	var returnObject = new Object();
	returnObject["query"] = query;
	returnObject["args"] = args;
	
	return returnObject;
}

function prepareUpdateQuery(table, setParams, whereParams) {
	var query = "UPDATE " + table + " SET ";
	
	for(param in setParams) {
		query += param + "=?,";
	}
	
	query = query.substring(0, query.length - 1);
	query += " WHERE ";
	
	for(param in whereParams) {
		query += param + "=?,";
	}
	
	query = query.substring(0, query.length - 1);
	
	var args = new Array();
	var i = 0;
	
	for(param in setParams) {
		args[i] = setParams[param];
		i++;
	}
	
	for(param in whereParams) {
		args[i] = whereParams[param];
		i++;
	}
	
	var returnObject = new Object();
	returnObject["query"] = query;
	returnObject["args"] = args;
	
	return returnObject;
}

function prepareSelectFullQuery(table, params) {
	var query = "SELECT * FROM " + table;
	var args = new Array();
	
	if(params != null) {
		query += " WHERE ";
		
		var first = true;
		for(param in params) {
			if(params[param] == null) {
				query += (first ? "" : " AND ") + param + " IS ?";
			}
			else if(typeof params[param] == "object") {
				query += (first ? "(" : " AND (");

				for(var n = 0; n < params[param].length; n++) {
					query += (n == 0 ? "" : " OR ") + param + "=?";
				}

				query += ")"
			}
			else {
				query += (first ? "" : " AND ") + param + "=?";
			}
			
			if(first) {
				first = false;
			}
		}
		
		var i = 0;
		
		for(param in params) {
			if(params[param] != null && typeof params[param] == "object") {
				for(var n = 0; n < params[param].length; n++) {
					args[i] = params[param][n];
					i++
				}
			}
			else {
				args[i] = params[param];
			}
			i++;
		}
	}
	
	var returnObject = new Object();
	returnObject["query"] = query;
	returnObject["args"] = args;
	
	return returnObject;
}

function prepareDeleteQuery(table, params) {
	var query = "DELETE FROM " + table;
	var args = new Array();
	
	if(params != null) {
		query += " WHERE ";
		
		var first = true;
		for(param in params) {
			query += (first ? "" : " AND ") + param + "=?";
			
			if(first) {
				first = false;
			}
		}
		
		var i = 0;
		
		for(param in params) {
			args[i] = params[param];
			i++;
		}
	}
	
	var returnObject = new Object();
	returnObject["query"] = query;
	returnObject["args"] = args;
	
	return returnObject;
}


// ************* DEFAULT CALLBACKS ***************

function successfulInsertCallback(tx, results) {
	console.log("RECORD INSERTED SUCCESSFULLY");
}

function successfulUpdateCallback(tx, results) {
	console.log("RECORD UPDATED SUCCESSFULLY");
}

function successfulSelectCallback(tx, results) {
	console.log(results.rows.length + " ROW" + (results.rows.length != 1 ? "S" : "") + " SELECTED");
	
	for(var i = 0; i < results.rows.length; i++) {
		var item = results.rows.item(i);
		
		var printout = "";
		
		for(key in item) {
			printout += key + ": " + item[key] + "; ";
		}
		
		console.log(printout);
	}
}

function successfulDeleteCallback(tx, results) {
	console.log(results.rowsAffected + " RECORD" + (results.rowsAffected != 1 ? "S" : "") + " DELETED SUCCESSFULLY");
}

function successfulDropTableCallback() {
	console.log("TABLE DROPPED SUCCESSFULLY");
}

function errorCallback(tx, e) {
	console.log("ERROR: " + e.message);
}