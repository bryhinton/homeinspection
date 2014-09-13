inspection.controller('InspectionCtrl',
	function InspectionCtrl ($scope, $location, inspectionFactory) {
		if(inspectionFactory.isReady()) {
			inspectionFactory.getCurrentInspection(function(inspection, apply) {
				if(apply) {
					$scope.$apply(function() {
						if(inspection == null) {
							$scope.path = "/customer";
							$location.path("/customer");
						}
						else {
							$scope.inspection = inspection;
							$scope.inspection.fields.date = moment($scope.inspection.fields.date).format("MMM D");

							$scope.path = $location.path();
						}
					})
				}
				else {
					$scope.inspection = inspection;
					$scope.inspection.fields.date = moment($scope.inspection.fields.date).format("MMM D");

					$scope.path = $location.path();
				}
			});
		}
		else {
			$(document).bind("dataReady", function() {
				$scope.$apply(function() {
					inspectionFactory.getCurrentInspection(function(inspection, apply) {
						if(apply) {
							$scope.$apply(function() {
								if(inspection == null) {
									$scope.path = "/customer";
									$location.path("/customer");
								}
								else {
									$scope.inspection = inspection;
									$scope.inspection.fields.date = moment($scope.inspection.fields.date).format("MMM D");

									$scope.path = $location.path();
								}
							});
						}
						else {
							if (inspection != null) {
								$scope.inspection = inspection;
								$scope.inspection.fields.date = moment($scope.inspection.fields.date).format("MMM D");
							}

							$scope.path = $location.path();
						}
					});
				});
			});
		}

		$scope.validateCustomerInfo = function() {
			$scope.inspection = new OneDBObject({}, "inspection", inspectionFactory.getDatabase());
			var lastNameInput = $("#lastname");
			$scope.inspection.fields.lastname = lastNameInput.val();
			if($scope.inspection.fields.lastname == "") {
				lastNameInput.css("backgroundColor", "#ff9999");
				lastNameInput.focus();
			}
			else {
				lastNameInput.css("backgroundColor", "none");

				$scope.inspection.fields.firstname = $("#firstname").val();
				$scope.inspection.fields.address = $("#address").val();
				$scope.inspection.fields.city = $("#city").val();
				$scope.inspection.fields.state = $("#state").val();
				$scope.inspection.fields.zip = $("#zip-code").val();
				$scope.inspection.fields.phone = $("#phone-number").val();
				$scope.inspection.fields.email = $("#email").val();
				$scope.inspection.fields.date = new Date();
				$scope.inspection.fields.finished = false;

				$scope.name = ($scope.inspection.fields.firstname != "" ? $scope.inspection.fields.firstname + " ": "") + $scope.inspection.fields.lastname;
				$scope.date = $scope.inspection.fields.date.toLocaleDateString();

				$scope.inspection.save(function(type, params) {
					$scope.$apply(function() {
						$scope.path = "/inspection";
						$location.path("/inspection");
					});
				});

			}
		};

		$scope.selectInspectionArea = function(id) {
			$scope.selectedID = id;
			$scope.selectedInspectionArea = $scope.inspection.fields.inspectionAreas[id];
			inspectionFactory.getSections(parseInt($scope.selectedInspectionArea.fields.area), function(objects) {
				$scope.$apply(function() {
					$scope.sections = objects;
				});
			});
		};

		$scope.getNewID = function() {
			return Math.ceil(Math.random() * 1000000);
		};

		$scope.deleteInspection = function() {
			if(confirm("Are you sure you want to delete this inspection?")) {
				inspectionFactory.deleteInspection($scope.inspection.id, function() {
					window.location.reload();
				});
			}
		}
	}
);