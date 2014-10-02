var inspection = angular.module('inspection', [])
	.config(function($routeProvider, $locationProvider) {
		$routeProvider.when('/customer', {
				templateUrl: 'partials/inspection/content.html',
				controller: 'InspectionCtrl'
			}).
		when('/inspection', {
			templateUrl: 'partials/inspection/content.html',
			controller: 'InspectionCtrl'
		}).
		when('/review', {
			templateUrl: 'partials/inspection/content.html',
			controller: 'InspectionCtrl'
		}).
		otherwise({
				redirectTo: '/customer'
			});
	})
	.factory('inspectionFactory', function() {
		var company = null;
		var inspection = null;
		var state = null;
		var areas = null;
		var ready = false;

		var database = getDatabase("inspections", "Inspections", "1.0", (1024*1024*5), function(db) {
			db.updateSchema();
			db.getObjects("inspection", {"finished": false}, function(inspections) {
				if($.isEmptyObject(inspections)) {
					db.updateSchema();
				}
//				else if(!$.isEmptyObject(inspections)) {
//					var toDelete = [];
//					for(var i in inspections) {
//						toDelete.push(inspections[i]);
//					}
//					db.deleteObjects(toDelete, function() {
//						console.log("deleted");
//					});
//				}
				else {
					inspection = inspections[Object.keys(inspections)[0]];

					db.getObjects("inspectionarea", {"inspection": inspection.id}, function(inspectionAreas) {
						if(!$.isEmptyObject(inspectionAreas)) {
							inspection.fields.inspectionAreas = inspectionAreas;

							var i = Object.size(inspectionAreas);

							for(var inspectionAreaKey in inspectionAreas) {
								db.getObjects("inspectionarealineitem", {"inspectionarea": inspectionAreas[inspectionAreaKey].id}, function(inspectionAreaLineItems) {
									var inspectionAreaLineItemMap = {};

									for(var inspectionAreaLineItemKey in inspectionAreaLineItems) {
										inspectionAreaLineItemMap[inspectionAreaLineItems[inspectionAreaLineItemKey].fields.lineitem] = inspectionAreaLineItems[inspectionAreaLineItemKey];
									}

									inspection.fields.inspectionAreas[inspectionAreaKey].fields.inspectionAreaLineItems = inspectionAreaLineItemMap;

									i--;

									if(i == 0) {
										ready = true;
										$(document).trigger("dataReady");
									}
								});
							}
						}
						else {
							$(document).trigger("dataReady");
						}
					});
				}
			}, function(event) {
				alert("Did not get inspections");
			});
		});


		$(document).bind("schemaComplete", function() {
			$.get("api/company/1?userid=1&fields=*", function(data) { //TODO use the correct company
				company = data[0];
				state = data[0].state;

				var newAreas = {objects: []};
				var sections = {objects: []};
				var lineItems = {objects: []};
				for(var areaID in data[0].areas) {
					var areaData = data[0].areas[areaID];
					var area = {};
					area.id = areaID;
					area.name = areaData.name;

					newAreas.objects[newAreas.objects.length] = area;

					for(var sectionID in areaData.sections) {
						var sectionData = areaData.sections[sectionID];
						var section = {};
						section.id = sectionID;
						section.name = sectionData.name;
						section.parent = 0;
						section.area = sectionData.area;

						sections.objects[sections.objects.length] = section;

						for(var lineItemID in sectionData.lineItems) {
							var lineItemData = sectionData.lineItems[lineItemID];
							var lineItem = {};
							lineItem.id = lineItemID;
							lineItem.name = lineItemData.name;
							lineItem.parent = sectionID;
							lineItem.area = lineItemData.area;

							lineItems.objects[lineItems.objects.length] = lineItem;
						}
					}
				}

				var categories = {objects: []};
				var subCategories = {objects: []};
				var tasks = {objects: []};
				for(var categoryID in data[0].categories) {
					var categoryData = data[0].categories[categoryID];
					var category = {};
					category.id = categoryID;
					category.name = categoryData.name;
					category.parent = 0;

					categories.objects[categories.objects.length] = category;

					for(var subCategoryID in categoryData.subCategories) {
						var subCategoryData = categoryData.subCategories[subCategoryID];
						var subCategory = {};
						subCategory.id = subCategoryID;
						subCategory.name = subCategoryData.name;
						subCategory.parent = categoryID;

						subCategories.objects[subCategories.objects.length] = subCategory;

						for(var taskID in subCategoryData.tasks) {
							var taskData = subCategoryData.tasks[taskID];

							tasks.objects[tasks.objects.length] = taskData;
						}
					}
				}

				database.add("area", newAreas, function() {
					database.add("lineitem", sections, function() {
						database.add("lineitem", lineItems, function() {
							database.add("category", categories, function() {
								database.add("category", subCategories, function() {
									database.add("task", tasks, function() {
										ready = true;
										$(document).trigger("dataReady");
									})
								})
							})
						})
					});
				});
			});
		});

		return {
			getDatabase: function() {
				return database;
			},
			getCompany: function() {
				return company;
			},
			getState: function() {
				return state;
			},
			isReady: function() {
				return ready;
			},
			getCurrentInspection: function(callback) {
				if(inspection == null) {
					database.getObjects("inspection", {"finished": false}, function(inspections) {
						if($.isEmptyObject(inspections)) {
							callback(null, true);
						}
						else {
							inspection = inspections[Object.keys(inspections)[0]];

							database.getObjects("inspectionarea", {"inspection": inspection.id}, function(inspectionAreas) {
								if($.isEmptyObject(inspectionAreas)) {
									callback(inspection, true);
								}
								else {
									inspection.fields.inspectionAreas = inspectionAreas;

									var i = Object.size(inspectionAreas);

									for(var inspectionAreaKey in inspectionAreas) {
										database.getObjects("inspectionarealineitem", {"inspectionarea": inspectionAreas[inspectionAreaKey].id}, function(inspectionAreaLineItems) {
											var inspectionAreaLineItemMap = {};

											for(var inspectionAreaLineItem in inspectionAreaLineItems) {
												inspectionAreaLineItemMap[inspectionAreaLineItem.fields.lineitem] = inspectionAreaLineItem
											}

											inspection.fields.inspectionAreas[inspectionAreaKey].fields.inspectionAreaLineItems = inspectionAreaLineItemMap;

											i--;

											if(i == 0) {
												callback(inspection, true);
											}
										});
									}
								}
							});
						}
					});
				}
				else {
					return callback(inspection);
				}
			},
			deleteInspection: function(id, callback) {
				database.delete("inspection", id, function() {
					ready = false;
					company = null;
					inspection = null;
					state = null;
					areas = null;
					callback();
				});
			},
			getAreas: function(callback) {
				if(areas == null) {
					database.getObjects("area", null, callback);
				}
				else {
					callback(areas);
				}
			},
			getSections: function(areaID, callback) {
				var params = {};
				params.area = areaID;
				params.parent = 0;

				database.getObjects("lineitem", params, callback);
			},
			getLineItems: function(sectionID, callback) {
				var params = {};
				params.parent = sectionID;

				database.getObjects("lineitem", params, callback);
			},
			getFailedItems: function(inspectionAreaID, callback) {
				var params = {};
				params.inspectionarea = inspectionAreaID;
				params.result = "fail";

				database.getObjects("inspectionarealineitem", params, callback);
			},
			getTopLevelCategories: function(callback) {
				var params = {};
				params.parent = 0;

				database.getObjects("category", params, callback);
			},
			getSubCategories: function(parentID, callback) {
				var params = {};
				params.parent = parentID;

				database.getObjects("category", params, callback);
			},
			getTasks: function(categoryID, callback) {
				var params = {};
				params.category = parseInt(categoryID);

				database.getObjects("task", params, callback);
			}
		};
	});

var toggleMenu = function() {
	var menu = $("#main-menu");
	var arrow = $("#menu-arrow");

	if(menu[0].offsetTop < 0) {
		if(document.documentElement.clientWidth > 600) {
			menu.animate({ top:"52px"}, 250);
		}
		else {
			menu.animate({ bottom:"-390%"}, 250);
		}

		arrow.animateRotate(180, 0);
	}
	else {
		if(document.documentElement.clientWidth > 600) {
			menu.animate({ top:"-300px"}, 250);
		}
		else {
			menu.animate({ bottom:"100%"}, 250);
		}

		arrow.animateRotate(0, 180);
	}
};

Object.size = function(obj) {
	var size = 0, key;
	for (key in obj) {
		if (obj.hasOwnProperty(key)) size++;
	}
	return size;
};