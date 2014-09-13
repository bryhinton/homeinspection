controlCenter.controller('AppCtrl',
	function AppCtrl ($scope, $location) {
		$scope.user = {};
		$scope.user.ID = localStorage.userID;
		$scope.user.name = localStorage.userFullName;
		$scope.user.companyID = localStorage.companyID;2
		$scope.user.companyName = localStorage.companyName;

		if($scope.user.ID == null) {
			$scope.path = "/login";
		}
		else {
			$scope.path = $location.path();
		}

		if($scope.user.ID != null && $scope.user.companyID != null) {
			$scope.inspections = [];
			$.ajax({
				type: "GET",
				url: "api/inspection/search?userid=" + $scope.user.ID + "&company=" + $scope.user.companyID + "&fields=technician",
				async: false,
				datatype: "json"
			}).done(function(msg) {
					$scope.inspections = msg;

					processInspectionData($scope.inspections);
				});
		}

		$scope.inspectionsTEST = [{
			id: '1',
			name: 'Brian Ford',
			address: '1234 Generic Street',
			city: 'Lehi',
			state: 'Utah',
			zip: '84043',
			email: 'brianford@gmail.com',
			phone: '123-456-7890',
			date: '7/3/13',
			tech: 'Rob Snow',
			standardTotal: '$1,456.23',
			memberTotal: '$1,045.20',
			savedTotal: '$411.03',
			contacted: false,
			failedLineItems: [{
				name: 'Faucet',
				parent: 'Sink',
				area: 'Master Bathroom',
				comment: 'It leaks'
			},
			{
				name: 'Dishwasher',
				parent: 'Kitchen Sink',
				area: 'Kitchen',
				comment: 'Heating element is broken'
			}],
			quoteItems: [{
				name: 'Minor T&S Commercial Faucet Repair 3 Handle',
				description: 'Includes New T&S Barrells.',
				standard: '$236.83',
				member: '$205.94',
				yoursaved: '$30.89'
			},
			{
				name: 'Really expensive dishwasher repair',
				description: 'Pretty pricey.',
				standard: '$1,219.40',
				member: '$839.26',
				yoursaved: '$380.14'
			}]
		},
		{
			id: '2',
			name: 'Serena Hinton',
			date: '7/2/13',
			tech: 'Brett Reeves',
			standardTotal: '$90.00',
			memberTotal: '$70.00',
			contacted: false
		},
		{
			id: '3',
			name: 'Sharon Hinton',
			date: '6/30/13',
			tech: 'Lawrence Snow',
			standardTotal: '$423.96',
			memberTotal: '$337.45',
			contacted: false
		},
		{
			id: '4',
			name: 'Chad Westover',
			date: '6/27/13',
			tech: 'Lawrence Snow',
			standardTotal: '$155.35',
			memberTotal: '$108.27',
			contacted: true
		}];

		$scope.timeCards = [{

		}];

		$scope.login = function() {
			$.ajax({
				type: "GET",
				url: "api/technician/search?username=" + $("#username")[0].value + "&password=" + $("#password")[0].value + "&fields=company",
				async: false,
				datatype: "json"
			}).done(function(msg) {
					localStorage.userID = msg[0].id;
					localStorage.userFullName = msg[0].FirstName + " " + msg[0].LastName;
					localStorage.companyID = msg[0].Company;
					localStorage.companyName = msg[0].company.name;

					window.location.reload();
				});
		};

		$scope.logout = function() {
			localStorage.removeItem("userID");
			localStorage.removeItem("userFullName");
			localStorage.removeItem("companyID");
			localStorage.removeItem("companyName");

			window.location.reload();
		}
	});

controlCenter.controller('InspectionsCtrl',
	function InspectionsCtrl($scope, $routeParams, $location) {
		if($scope.path != "/login") {
			$scope.path = "/inspections";
		}

		if($routeParams.id) {
			$scope.selectedID = $routeParams.id;
			$scope.inspection = $scope.inspections[$routeParams.id];

			$scope.loadInspection();
		}

		$scope.loadInspection = function() {
			if(!$scope.inspection.loaded) {
				$.ajax({
					type: "GET",
					url: "api/inspection/" + $scope.inspection.id + "?userid=1&fields=*",
					async: false,
					datatype: "json"
				}).done(function(msg) {
						processInspectionData(msg);
						$scope.inspections[$routeParams.id] = msg[0];
						$scope.inspection = $scope.inspections[$routeParams.id];
						$scope.inspection.loaded = true;
					});
			}
		};

		$scope.selectInspection = function(id) {
			$scope.selectedID = id;
			$scope.inspection = $scope.inspections[id];

			$scope.loadInspection();

			//$location.path("/inspections/" + id);
		};

		$scope.contact = function() {
			$.ajax({
				type: "POST",
				url: "api/inspection/" + $scope.inspection.id,
				async: false,
				datatype: "json",
				data: {
					"userid": 1,
					"Contacted": $scope.inspection.Contacted
				}
			}).done(function(msg) {
//					processInspectionData(msg);
//					$scope.inspections[$routeParams.id] = msg[0];
//					$scope.inspection = $scope.inspections[$routeParams.id];
//					$scope.inspection.loaded = true;
				});
		};

		$scope.sendInspectionEmail = function() {
			$.ajax({
				type: "GET",
				url: "api/inspection/" + $scope.inspection.id + "?userid=1&action=sendInspectionEmail",
				async: false,
				datatype: "json"
			}).done(function(msg) {
					$scope.message = msg;
					showMessage();
				});
		};

		$scope.deleteWarn = function() {
			if(confirm("Are you sure you want to delete this inspection?")) {
				$scope.deleteInspection();
			}
		};

		$scope.deleteInspection = function() {
			$.ajax({
				type: "DELETE",
				url: "api/inspection/" + $scope.inspection.id + "?userid=1",
				async: false,
				datatype: "json"
			}).done(function(msg) {
					$scope.inspections.splice($routeParams.id, 1);
					window.location="#/inspections";
				});
		};
	});

controlCenter.controller("SettingsCtrl",
	function InspectionsCtrl($scope, $routeParams, $location) {
		if($scope.path != "/login") {
			$scope.path = "/settings";
		}

		if($scope.currentSettings == null) {
			$scope.currentSettings = "companyInfo";
		}

		$.ajax({
			type: "GET",
			url: "api/company/" + $scope.user.companyID + "?userid=1",
			async: false,
			datatype: "json"
		}).done(function(msg) {
				$scope.company = msg[0];
			});

		$scope.submitCompanyInfo = function() {
			$.ajax({
				type: "POST",
				url: "api/company/" + $scope.user.companyID + "?userid=" + $scope.user.ID,
				datatype: "json",
				data: {
					"name": $scope.company.name,
					"email": $scope.company.email,
					"phone": $scope.company.phone
				}
			}).done(function(msg) {
				$scope.company = msg[0];
			});
		};
	}
);

controlCenter.directive('ngEnter', function () {
	return function (scope, element, attrs) {
		element.bind("keydown keypress", function (event) {
			if(event.which === 13) {
				scope.$apply(function (){
					scope.$eval(attrs.ngEnter);
				});

				event.preventDefault();
			}
		});
	};
});

function processInspectionData(inspections) {
	var i = 0;

	while(i < inspections.length) {
		inspections[i].Date = new Date(Date.parse(inspections[i].Date));
		i++;
	}
}

function showMessage() {
	$(".message").css("opacity", "1");

	setTimeout(function() {
		$(".message").animate({"opacity": "0"}, 300);
	}, 3000);
}