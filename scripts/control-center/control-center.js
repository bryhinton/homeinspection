var controlCenter = angular.module('controlCenter', [])
	.config(function($routeProvider, $locationProvider) {
		$routeProvider.when('/index', {
			//templateUrl: 'partials/control-center/content.html'
			redirectTo: '/inspections'
		})
			.when('/inspections', {
				templateUrl: 'partials/control-center/main.html',
				controller: 'InspectionsCtrl'
			})
			.when('/inspections/:id', {
				templateUrl: 'partials/control-center/content.html',
				controller: 'InspectionsCtrl'
			})
			.when('/settings', {
				templateUrl: 'partials/control-center/content.html',
				controller: 'SettingsCtrl'
			})
			.when('/remove/:id', {
				templateUrl: 'partials/control-center/remove.html',
				controller: 'RemoveCtrl'
			})
			.otherwise({
				redirectTo: '/inspections'
			});
	});