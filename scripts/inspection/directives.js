inspection.directive("customerInfo", function() {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/customerInfo.html',
		scope: {callback: '&'},
		link: function(scope, element, attributes) {
			scope.validateInfo = function() {
				scope.callback();
			};

			element.find(".row input").bind("keypress", function(e) {
				if(e.keyCode == 13) {
					var inputs = $("#customer-info .row input");

					for(var i = 0; i < inputs.length; i++) {
						if(e.target == inputs[i]) {
							if(i < inputs.length - 1) {
								inputs[i+1].focus();
								break;
							}
							else {
								$("#customer-info .row button").click();
							}
						}
					}
				}
			});
		}
	}
});

inspection.directive("inspection", ['inspectionFactory', '$location', function(inspectionFactory, $location) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/inspection.html',
		scope: true,
		link: function($scope, $element, $attributes) {
			if($scope.inspection.fields.inspectionAreas) {
				$scope.selectInspectionArea($scope.inspection.fields.inspectionAreas[Object.keys($scope.inspection.fields.inspectionAreas)[0]].id)
			}

			$scope.toggleAddArea = function() {
				if(!$scope["showAddArea"]) {
					$scope.showAddArea = true;
					$scope.showChangeArea = false;
					$scope.showConfirmReview = false;
				}
				else {
					$scope.showAddArea = false;
				}
			};

			$scope.toggleChangeArea = function() {
				if(!$scope["showChangeArea"]) {
					$scope.showChangeArea = true;
					$scope.showAddArea = false;
					$scope.showConfirmReview = false;
				}
				else {
					$scope.showChangeArea = false;
				}
			};

			$scope.toggleConfirmReview = function() {
				if(!$scope["showConfirmReview"]) {
					$scope.showConfirmReview = true;
					$scope.showChangeArea = false;
					$scope.showAddArea = false;
				}
				else {
					$scope.showConfirmReview = false;
				}
			};
		}
	}
}]);

inspection.directive("addarea", ['inspectionFactory', function(inspectionFactory) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/addArea.html',
		link: function($scope, $element, $attributes) {
			inspectionFactory.getAreas(function(objects) {
				$scope.$apply(function() {
					$scope.areas = objects;

					$scope.selectArea = function() {
						$scope.newAreaName = $("#new-area")[0].options[$("#new-area")[0].selectedIndex].innerHTML.trim();

						setTimeout(function() {
							$("#new-area").focus();
							$("#new-area-name").focus();
							$("#new-area-name").select();
						}, 10);
					};

					$scope.addArea = function() {
						var database = inspectionFactory.getDatabase();
						var newArea = new OneDBObject({}, "inspectionarea", database);
						newArea.fields.inspection = $scope.inspection.id;
						newArea.fields.area = $scope.selectedNewArea;
						newArea.fields.name = $("#new-area-name").val();
						newArea.fields.id = getNewID();

						newArea.save(function() {
							$scope.$apply(function() {
								$scope.selectedID = newArea.id;
								$scope.selectedInspectionArea = newArea;

								if(!$scope.inspection.fields.inspectionAreas) {
									$scope.inspection.fields.inspectionAreas = {};
								}

								$scope.inspection.fields.inspectionAreas[newArea.id] = newArea;

								inspectionFactory.getSections(parseInt(newArea.fields.area), function(objects) {
									$scope.$apply(function() {
										$scope.sections = objects;
										$scope.toggleAddArea();
									});
								});
							});
						});
					};
				});
			});
		}
	}
}]);

inspection.directive("changearea", ['inspectionFactory', function(inspectionFactory) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/changeArea.html',
		scope: true,
		link: function($scope, $element, $attributes) {

		}
	}
}]);

inspection.directive("confirmreview", ['$location', function($location) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/confirmReview.html',
		scope: true,
		link: function($scope, $element, $attributes) {
			$scope.startReview = function() {
				$scope.path = "/review";
				$location.path("/review");
			};
		}
	}
}]);

inspection.directive("section", ['inspectionFactory', function(inspectionFactory) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/section.html',
		scope: true,
		link: function($scope, $element, $attributes) {
			inspectionFactory.getLineItems($scope.$eval($attributes.sectionid), function(objects) {
				$scope.$apply(function() {
					$scope.lineItems = objects;
					$scope.inspectionAreaLineItems = $scope.selectedInspectionArea.fields.inspectionAreaLineItems;

					for(var key in $scope.inspectionAreaLineItems) {
						var comment = $scope.inspectionAreaLineItems[key].fields.comment;
						if(comment != null && comment != "") {
							$scope.showComment($scope.inspectionAreaLineItems[key].lineitem);
						}
					}

					$scope.inspectItem = function(result, lineItem) {
						var inspectionAreaLineItem;

						if($scope.selectedInspectionArea.fields.inspectionAreaLineItems) {
							inspectionAreaLineItem = $scope.selectedInspectionArea.fields.inspectionAreaLineItems[lineItem.id]
						}

						if(inspectionAreaLineItem == null) {
							inspectionAreaLineItem = new OneDBObject({}, "inspectionarealineitem", inspectionFactory.getDatabase());
							inspectionAreaLineItem.fields.inspectionarea = $scope.selectedID;
							inspectionAreaLineItem.fields.name = lineItem.fields.name;
							inspectionAreaLineItem.fields.lineitem = lineItem.id;
							inspectionAreaLineItem.fields.parent = lineItem.fields.parent;
							inspectionAreaLineItem.fields.result = result;
							inspectionAreaLineItem.fields.id = $scope.getNewID();
						}
						else {
							inspectionAreaLineItem.fields.result = result;
						}

						if(result == 'fail') {
							$scope.showComment(lineItem.id);
						}
						else {
							$scope.hideComment(lineItem.id);
						}

						inspectionAreaLineItem.save(function() {
							$scope.$apply(function() {
								if(!$scope.selectedInspectionArea.fields.inspectionAreaLineItems) {
									$scope.selectedInspectionArea.fields.inspectionAreaLineItems = {};
								}

								var inspectionAreaLineItems = $scope.selectedInspectionArea.fields.inspectionAreaLineItems;
								inspectionAreaLineItems[inspectionAreaLineItem.fields.lineitem] = inspectionAreaLineItem;
							})
						});
					};

					$scope.getResult = function(lineItemID, defaultResult){
						if($scope.selectedInspectionArea.fields.inspectionAreaLineItems) {
							var inspectionAreaLineItem = $scope.selectedInspectionArea.fields.inspectionAreaLineItems[lineItemID];

							if(inspectionAreaLineItem) {
								return inspectionAreaLineItem.fields.result == defaultResult ? defaultResult : '';
							}
							else {
								return defaultResult;
							}
						}

						return defaultResult;
					};

					$scope.showComment = function(lineItemID) {
						if(!$scope.visibleComments){
							$scope.visibleComments = {};
						}

						$scope.visibleComments[lineItemID] = true;
					};

					$scope.hideComment = function(lineItemID) {
						if(!$scope.visibleComments){
							$scope.visibleComments = {};
						}

						$scope.visibleComments[lineItemID] = false;
					};

					$scope.setComment = function(lineItem) {
						if(!$scope.selectedInspectionArea.fields.inspectionAreaLineItems) {
							$scope.selectedInspectionArea.fields.inspectionAreaLineItems = {};
						}

						var inspectionAreaLineItem = $scope.selectedInspectionArea.fields.inspectionAreaLineItems[lineItemID];

						if(inspectionAreaLineItem == null) {
							inspectionAreaLineItem = new OneDBObject({}, "inspectionarealineitem", inspectionFactory.getDatabase());
							inspectionAreaLineItem.fields.inspectionarea = $scope.selectedID;
							inspectionAreaLineItem.fields.name = lineItem.fields.name;
							inspectionAreaLineItem.fields.lineitem = lineItem.id;
							inspectionAreaLineItem.fields.parent = lineItem.fields.parent;
							inspectionAreaLineItem.fields.id = $scope.getNewID();
						}

						inspectionAreaLineItem.fields.comment = $scope.comments[lineItem.id];

						inspectionAreaLineItem.save(function() {
							$scope.$apply(function() {
								var inspectionAreaLineItems = $scope.selectedInspectionArea.fields.inspectionAreaLineItems;
								inspectionAreaLineItems[inspectionAreaLineItem.fields.lineitem] = inspectionAreaLineItem;
							})
						});
					};
				})
			});
		}
	}
}]);

inspection.directive("review", ['inspectionFactory', '$location', function(inspectionFactory, $location) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/review.html',
		scope: true,
		link: function($scope, $element, $attributes) {
			if(!$scope.inspection) {
				$location.path("/customer");
			}
			else {
				$scope.selectInspectionArea($scope.inspection.fields.inspectionAreas[Object.keys($scope.inspection.fields.inspectionAreas)[0]].id);

				$scope.failedLineItems = {};

				inspectionFactory.getFailedItems($scope.selectedID, function(failedItems) {
					$scope.$apply(function() {
						for(var key in failedItems) {
							var failedItem = failedItems[key];

							if(!$scope.failedLineItems[failedItem.fields.parent]) {
								$scope.failedLineItems[failedItem.fields.parent] = {};
							}

							$scope.failedLineItems[failedItem.fields.parent][key] = failedItem;
						}
					});
				});

				$scope.toggleChangeArea = function() {
					if(!$scope["showChangeArea"]) {
						$scope.showChangeArea = true;
						$scope.showPricing = false;
					}
					else {
						$scope.showChangeArea = false;
					}
				};

				$scope.togglePricing = function(failedLineItemID, inspectionAreaID) {
					if(!$scope["showPricing"]) {
						$scope.showPricing = true;
						$scope.showChangeArea = false;

						$scope.pricingLineItem = failedLineItemID;
						$scope.pricingInspectionArea = inspectionAreaID;
					}
					else {
						$scope.showPricing = false;
						$scope.pricingLineItem = null;
					}
				};
			}
		}
	}
}]);

inspection.directive("pricing", ['inspectionFactory', function(inspectionFactory) {
	return {
		restrict: 'A',
		templateUrl: 'partials/inspection/pricing.html',
		scope: true,
		link: function($scope, $element, $attributes) {
			$scope.expandedCategories = {};
			$scope.expandedSubCategories = {};

			inspectionFactory.getTopLevelCategories(function(categories) {
				if(!$.isEmptyObject(categories)) {
					$scope.$apply(function() {
						$scope.categories = categories;

						for(var categoryKey in categories) {
							inspectionFactory.getSubCategories(categoryKey, function(subCategories) {
								if(!$.isEmptyObject(subCategories)) {
									$scope.$apply(function() {
										var categoryID = subCategories[Object.keys(subCategories)[0]].fields.parent;

										$scope.categories[categoryID].fields.subCategories = subCategories;

										for(var subCategoryKey in subCategories) {
											inspectionFactory.getTasks(subCategoryKey, function(tasks) {
												if(!$.isEmptyObject(tasks)) {
													$scope.$apply(function() {
														var subCategoryID = tasks[Object.keys(tasks)[0]].fields.category;

														$scope.categories[categoryID].fields.subCategories[subCategoryID].fields.tasks = tasks;
													});
												}
											});
										}
									});
								}
							});
						}
					});
				}
			});

			$scope.expandCategory = function(categoryID) {
				$scope.expandedCategories[categoryID] = !$scope.expandedCategories[categoryID];
			};

			$scope.expandSubCategory = function(subCategoryID) {
				$scope.expandedSubCategories[subCategoryID] = !$scope.expandedSubCategories[subCategoryID];
			};

			$scope.addTask = function(taskID) {
				inspectionFactory.getCurrentInspection(function(inspection) {
					var fields = {};
					fields.inspectionarealineitem = $scope.$parent.pricingLineItem;
					fields.task = taskID;
					fields.inspection = inspection.fields.id;

					var quoteItem = new OneDBObject(fields, "quoteitem", inspectionFactory.getDatabase());
					quoteItem.save(function() {
						$scope.$apply(function() {
							var inspectionAreaLineItem = inspection.fields.inspectionAreas[$scope.$parent.pricingInspectionArea].fields.inspectionAreaLineItems[$scope.$parent.pricingLineItem];

							if(!inspectionAreaLineItem.fields.quoteItems) {
								inspectionAreaLineItem.fields.quoteItems = {};
							}

							inspectionAreaLineItem.fields.quoteItems[quoteItem.id] = quoteItem;
						});
					});
				});
			};
		}
	}
}]);
