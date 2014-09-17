function Database(dbName, dbDescription, dbVersion, dbSize) {
	this.dbName = dbName;
	this.dbDescription = dbDescription;
	this.dbVersion = dbVersion;
	this.dbSize = dbSize;

	this.database = null;

	this.type = "";

	this.totalTables = 0;
	this.tablesCreated = 0;

	this.logEnabled = localStorage["OneDBLogger"];
}

Database.prototype.get = function (type, params, success, error) {};
Database.prototype.set = function (type, IDs, params, success, error) {};
Database.prototype.add = function (type, params, success, error) {};
Database.prototype.delete = function (type, IDs, success, error) {};
Database.prototype.search = function(type, params, success, error) {};
Database.prototype.updateSchema = function() {};

Database.prototype.saveObjects = function(objects, success, error) {
	var totalObjects = objects.length;
	var objectsAdded = 0;

	for(var i = 0; i < totalObjects; i++) {
		objects[i].save(success ? function(){
			objectsAdded++;

			if(objectsAdded == totalObjects) {
				success(objects);
			}
		}.bind(this) : null, function(err, object) {
			this.log("COULD NOT SAVE ALL OBJECTS: " + object);

			if(error) {
				error(err, object);
			}
		}.bind(this));
	}
};

Database.prototype.getObjects = function(table, params, success, error) {
	this.get(table, params, function(objects, table) {
		alert("Got objects");
		var OneDBObjects = {};

		for(var j = 0; j < objects.length; j++) {
			OneDBObjects[objects[j].id] = new OneDBObject(objects[j], table, this);
		}

		success(OneDBObjects);
	}.bind(this), error);
};

Database.prototype.deleteObjects = function (objects, success, error) {
	var totalObjects = objects.length;
	var objectsDeleted = 0;

	for(var i = 0; i < totalObjects; i++) {
		objects[i].delete(success ? function(){
			objectsDeleted++;

			if(objectsDeleted == totalObjects) {
				success();
			}
		}.bind(this) : null, function(err, object) {
			this.log("COULD NOT DELETE ALL OBJECTS: " + object);

			if(error) {
				error(err, object);
			}
		}.bind(this));
	}
};


Database.prototype.log = function(message) {
	if(this.logEnabled) {
		console.log(message);
	}
};

// ******************* OneDBObject ********************

function OneDBObject(fields, type, database) {
	this.fields = fields;
	this.type = type;
	this.id = fields.id;
	this.database = database;
}

OneDBObject.prototype.save = function(success, error) {
	if(!this.id) {
		this.database.add(this.type, {"objects": [this.fields]}, function(type, params) {
			this.id = this.fields.id;
			this.database.log("OBJECT ADDED SUCCESSFULLY: " + this.type);
			if(success) {
				success(type, params);
			}
		}.bind(this), function(tx, err, type, params) {
			this.database.log(err);
			this.database.log("COULD NOT ADD OBJECT; ATTEMPTING TO UPDATE...");

			this.database.set(this.type, this.fields, function(type, params) {
				this.database.log("OBJECT UPDATED SUCCESSFULLY: " + type);
				this.refresh(success, error);
			}.bind(this), function(err, type, params) {
				this.database.log("COULD NOT UPDATE OBJECT: " + type + ", " + params);
				if(error) {
					error(err, this);
				}
			}.bind(this));
		}.bind(this));
	}
	else {
		this.database.set(this.type, {"objects": [this.fields]}, function(type, params) {
			this.database.log("OBJECT UPDATED SUCCESSFULLY: " + type);
			this.refresh(success, error);
		}.bind(this), function(err, type, params) {
			this.database.log("COULD NOT UPDATE OBJET; ATTEMPTING TO ADD...");

			this.database.add(this.type, this.fields, function(type, params) {
				this.database.log("OBJECT ADDED SUCCESSFULLY: " + type);
				if(success) {
					success(type, params);
				}
			}.bind(this), function(err, type, params) {
				this.database.log("COULD NOT ADD OBJECT: " + type + ", " + params);
				if(error) {
					error(err, this);
				}
			}.bind(this));
		}.bind(this));
	}
};

OneDBObject.prototype.delete = function(success, error) {
	this.database.delete(this.type, this.id, function(type, id) {
		this.database.log("OBJECT DELETED SUCCESSFULLY: " + type);
		if(success) {
			success(type, id);
		}
	}.bind(this),
	function(err, type, id) {
		this.database.log("COULD NOT DELETE OBJET: " + type);
		if(error) {
			error(err, this);
		}
	}.bind(this));
};

OneDBObject.prototype.refresh = function(success, error) {
	this.database.get(this.type, this.id, function(object, type, id) {
		this.database.log("OBJECT REFRESHED: " + type + ", " + id);
	}.bind(this),
	function(err, type, id) {
		this.database.log("COULD NOT REFRESH OneDBObject: " + type + ", " + id);
		if(error) {
			error(err, this);
		}
	}.bind(this));
};

// *************** Web SQL Database *****************

function WebSQLDatabase(name, desc, version, size, callback) {
	Database.call(this, name, desc, version, size);

	try {
		this.database = openDatabase(name, version, desc, size);
		this.type = "WebSQLDatabase";
		callback(this);
	}
	catch (e) {
		this.log("This browser does not support Web SQL databases.");
	}

}

WebSQLDatabase.prototype = new Database();
WebSQLDatabase.prototype.constructor = WebSQLDatabase;

WebSQLDatabase.prototype.updateSchema = function() {
	$.get("schema.json", function(schema) {
		this.dropTables(schema);
		this.createTables(schema);
	}.bind(this), "json");
};

WebSQLDatabase.prototype.createTables = function(schema) {
	this.database.transaction(function(tx) {
		for(var tableName in schema) {
			var table = schema[tableName];
			var query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

			for(var column in table) {
				if(column == "foreignkeys") { continue }

				query += column;

				var options = table[column];

				if(options.indexOf("type") > -1) {
					query += " " + options.type;
				}

				if(options.indexOf("unique") > -1) {
					query += " UNIQUE";
				}

				if(options.indexOf("notnull") > -1) {
					query += " NOT NULL";
				}

				query += ","
			}

			var foreignKeys = table["foreignkeys"];

			if(foreignKeys) {
				for(var i = 0; i < foreignKeys.length; i++) {
					query += "FOREIGN KEY (" + foreignKeys[i].column + ") ";
					query += "REFERENCES " + foreignKeys[i].reftable + " (" + foreignKeys[i].refcolumn + ")";

					var cascade = foreignKeys[i].cascade;

					if(cascade) {
						if(cascade.delete) {
							query += " ON DELETE CASCADE";
						}

						if(cascade.update) {
							query += " ON UPDATE CASCADE";
						}
					}

					query += ","
				}
			}

			query = query.substring(0, query.length - 1) + ");";

			tx.executeSql(query, [], function(tx) {
				this.log("TABLE CREATED SUCCESSFULLY!");

				if(this.totalTables) {
					this.tablesCreated++;

					if(this.tablesCreated == this.totalTables) {
						$(document).trigger("schemaComplete");
					}
				}
			}.bind(this),
			function(tx, e) {
				this.log("ERROR CREATING TABLE: " + e.message);
				this.log("QUERY: " + query);
			}.bind(this));
		}
	}.bind(this));
};

WebSQLDatabase.prototype.dropTables = function(schema) {
	this.database.transaction(function(tx) {
		for(var tableName in schema) {
			this.totalTables++;

			tx.executeSql("DROP TABLE IF EXISTS " + tableName, [], function() {
					this.log(tableName + " TABLE DROPPED SUCCESSFULLY")
			}.bind(this),
			function(tx, e) {
				this.log("COULD NOT DROP TABLE " + tableName);
				this.log(e.message);
			}.bind(this));
		}
	}.bind(this));
};

WebSQLDatabase.prototype.get = function(table, params, success, error) {
	var query = this.prepareSelectFullQuery(table, params);

	this.database.transaction(function(tx) {
		tx.executeSql(query["query"], query["args"], success ? function(tx, results){
			var objects = [];

			for(var i = 0; i < results.rows.length; i++) {
				objects[i] = results.rows.item(i);
			}

			success(objects, table, params)
		}.bind(this) : function(tx, result) { this.successfulSelectCallback(tx, result);}.bind(this),
		error ? error : function(tx, e) {
			this.errorCallback(tx, e);
		}.bind(this));
	}.bind(this));
};

WebSQLDatabase.prototype.add = function(table, params, success, error) {
	this.database.transaction(function(tx) {
		var objects = params.objects;
		for(var i = 0; i < objects.length; i++) {
			var query = this.prepareSingleInsertQuery(table, objects[i]);
			tx.executeSql(query["query"], query["args"],
				function(tx, result) { this.successfulInsertCallback(tx, result);}.bind(this),
				error ? error : function(tx, e) { this.errorCallback(tx, e);}.bind(this));
		}

		if(success) {
			success.call(table, params);
		}
	}.bind(this));
};

WebSQLDatabase.prototype.set = function(table, params, success, error) {
	this.database.transaction(function(tx) {
		var objects = params.objects;
		for(var i = 0; i < objects.length; i++) {
			var query = this.prepareUpdateQuery(table, objects[i], {"id": objects[i].id});
			tx.executeSql(query["query"], query["args"],
				function(tx, result) { this.successfulUpdateCallback(tx, result);}.bind(this),
				error ? error : function(tx, e) { this.errorCallback(tx, e)}.bind(this));
		}

		if(success) {
			success.call(table, params);
		}
	}.bind(this));
};

WebSQLDatabase.prototype.delete = function(table, id, success, error) {
	var params = { "id": id };

	var query = this.prepareDeleteQuery(table, params);

	this.database.transaction(function(tx) {
		tx.executeSql(query["query"], query["args"], success ? function(tx, results) {
			success(table, id);
		} : function(tx, result) { this.successfulDeleteCallback(tx, result);}.bind(this),
		error ? error : function(tx, e) { this.errorCallback(tx, e);}.bind(this));
	}.bind(this));
};

WebSQLDatabase.prototype.descTable = function(tableName) {
	this.database.transaction(function(tx) {
		tx.executeSql("SELECT * FROM sqlite_master WHERE name = '" + tableName + "'", [],
			function(tx, results) {
				for(var i = 0; i < results.rows.length; i++) {
					this.log(results.rows.item(i).sql);
				}
			}.bind(this),
			function(tx, e) {
				this.log(e.message);
			}.bind(this));
	}.bind(this));
};

WebSQLDatabase.prototype.prepareSingleInsertQuery = function(table, params) {
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
};

WebSQLDatabase.prototype.prepareUpdateQuery = function(table, setParams, whereParams) {
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
};

WebSQLDatabase.prototype.prepareSelectFullQuery = function(table, params) {
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
};

WebSQLDatabase.prototype.prepareDeleteQuery = function(table, params) {
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
};

// ************* DEFAULT CALLBACKS ***************

WebSQLDatabase.prototype.successfulInsertCallback = function(tx, results) {
	this.log("RECORD INSERTED SUCCESSFULLY");
};

WebSQLDatabase.prototype.successfulUpdateCallback = function(tx, results) {
	this.log("RECORD UPDATED SUCCESSFULLY");
};

WebSQLDatabase.prototype.successfulSelectCallback = function(tx, results) {
	this.log(results.rows.length + " ROW" + (results.rows.length != 1 ? "S" : "") + " SELECTED");

	for(var i = 0; i < results.rows.length; i++) {
		var item = results.rows.item(i);

		var printout = "";

		for(key in item) {
			printout += key + ": " + item[key] + "; ";
		}

		this.log(printout);
	}
};

WebSQLDatabase.prototype.successfulDeleteCallback = function(tx, results) {
	this.log(results.rowsAffected + " RECORD" + (results.rowsAffected != 1 ? "S" : "") + " DELETED SUCCESSFULLY");
};

WebSQLDatabase.prototype.successfulDropTableCallback = function() {
	this.log("TABLE DROPPED SUCCESSFULLY");
};

WebSQLDatabase.prototype.errorCallback = function(tx, e) {
	this.log("ERROR: " + e.message);
};

// ************** IndexedDB ****************

function IndexedDB(name, desc, version, size, callback) {
	Database.call(this, name, desc, version, size);

	try {
		var dbReq = indexedDB.open(name);

		dbReq.onsuccess = function(e) {
			this.database = e.target.result;
			this.type = "IndexedDB";
			this.database.close();

			callback(this);
		}.bind(this);

		dbReq.onerror = function(e) {
			this.log("This browser does not support IndexedDB object stores.");
		}.bind(this);
	}
	catch (e) {
		this.log("This browser does not support IndexedDB object stores.");
		this.log(e);
	}
}

IndexedDB.prototype = new Database();
IndexedDB.prototype.constructor = IndexedDB;

IndexedDB.prototype.openCurrentDatabase = function(success) {
	var request = indexedDB.open(this.dbName, this.database.version);

	request.onsuccess = function(e) {
		this.database = e.target.result;
		success.call();
	}.bind(this);
};

IndexedDB.prototype.updateSchema = function() {
	$.get("schema.json", function(schema) {
		var request = indexedDB.open(this.dbName, this.database.version + 1);

		request.onupgradeneeded = function(e) {
			this.database = e.target.result;

			for(var table in schema) {
				if(this.database.objectStoreNames.contains(table)) {
					this.database.deleteObjectStore(table);
					this.log(table + " store deleted");
				}

				var tableSchema = schema[table];
				var keyPath = "";

				for(var column in tableSchema) {
					if(tableSchema[column].indexOf("unique") > -1) {
						keyPath = column;
						break;
					}
				}

				this.database.createObjectStore(table, {"keyPath": keyPath});
			}

			this.database.close();
		}.bind(this);

		request.onsuccess = function(e) {
			this.database = e.target.result;
			this.database.close();

			for(var i = 0; i < this.database.objectStoreNames.length; i++){
				this.log(this.database.objectStoreNames[i] + " store created");
			}

			$(document).trigger("schemaComplete");
		}.bind(this);

		request.onerror = function(e) {
			this.database.close();
			this.log("Could not update IndexedDB schema");
			this.log(e);
		}.bind(this);

		request.onblocked = function(e) {
			this.database.close();
			this.log("IndexedDB is blocked. Make sure there are no open connections.");
			this.log(e);
		}.bind(this);
	}.bind(this), "json");
};

IndexedDB.prototype.get = function(table, params, success, error) {
	this.openCurrentDatabase(function() {
		var transaction = this.database.transaction([table]);
		var store = transaction.objectStore(table);
		var request;

		if(params && params.id) {
			var keyRange = IDBKeyRange.only(params.id);
			request = store.openCursor(keyRange);
		}
		else {
			request = store.openCursor();
		}

		var allObjects = []
		var returnObjects = [];

		request.onsuccess = function(e) {
			var cursor = e.target.result;

			if(cursor == null) {
				for(var object in allObjects) {
					if(!allObjects[object].__NO_MATCH) {
						returnObjects.push(allObjects[object]);
					}
				}

				success(returnObjects, table, params);
				return;
			}

			var object = [cursor.value][0];
			allObjects.push(object);

			for(var param in params) {
				if(!object.__NO_MATCH && object[param] != params[param]) {
					object.__NO_MATCH = true;
				}
			}

			cursor.continue();
		}.bind(this);

		request.onerror = function(e) {
			alert("Transaction unsuccessful");
		}
	}.bind(this));
};

IndexedDB.prototype.add = function(table, params, success, error) {
	this.set(table, params, success, error);
};

IndexedDB.prototype.set = function(table, params, success, error) {
	this.openCurrentDatabase(function() {
		var transaction = this.database.transaction([table], "readwrite");
		var store = transaction.objectStore(table);
		var objects = params.objects;
		var i = 0;
		var that = this;

		putNext();

		function putNext() {
			if (i < objects.length) {
				if(!objects[i].id) {
					objects[i].id = getNewID();
				}

				store.put(objects[i]).onsuccess = putNext;
				++i;
			} else {
				that.log("ALL OBJECTS ADDED");
				success(table, params);
			}
		}
	}.bind(this));
};

IndexedDB.prototype.delete = function(table, id, success, error) {
	var request = this.database.transaction([table], "readwrite").objectStore(table).delete(id);

	request.onsuccess = function(event) {
		success(table, id);
	}.bind(this);

	request.onerror = function(event) {
		if(error) {
			error();
		}
	}
};

// ************* HELPER METHODS ***************

function getDatabase(dbName, dbDescription, dbVersion, dbSize, callback) {
	var webSQLDatabase = new WebSQLDatabase(dbName, dbDescription, dbVersion, dbSize, callback);

	if(webSQLDatabase.database != null) {
		return webSQLDatabase;
	}
	else {
		var indexedDB = new IndexedDB(dbName, dbDescription, dbVersion, dbSize, callback);

		if(indexedDB != null) {
			return indexedDB;
		}

		return null;
	}
}

function getNewID() {
	return Math.ceil(Math.random() * 1000000);
}