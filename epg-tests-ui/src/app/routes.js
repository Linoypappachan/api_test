export default function AppRouter ($locationProvider, $urlRouterProvider, $stateProvider) {

      let alertsPartial = '/web_testportal/partials/alerts.html',
          schedulePartial = '/web_testportal/partials/schedule.html',
          testsConfigurationPartial = '/web_testportal/partials/tests-configuration.html',
          testDetailsPartial = '/web_testportal/partials/test-details.html',
          testHistoryPartial = '/web_testportal/partials/test-history.html',
          adminPartial = '/web_testportal/partials/admin.html'
      ;


      $locationProvider.hashPrefix('');
      $urlRouterProvider.otherwise('/');
      $stateProvider
        .state('alerts', {
          name: 'alerts',
          url: '/',
          templateUrl: alertsPartial
        })
        .state('schedule', {
          name: 'schedule',
          url: '/schedule',
          templateUrl: schedulePartial
        })
        .state('tests-configuration',{
            name: 'tests-configuration',
            url: '/tests-configuration',
            templateUrl: testsConfigurationPartial
        })
        .state('test-details',{
            name: 'test-details',
            url: '/test-details/:location',
            templateUrl: testDetailsPartial
        })
        .state('test-history', {
            name: 'test-history',
            url: '/test-history/:testCaseId',
            templateUrl: testHistoryPartial
        })
        .state('admin', {
          name: 'admin',
          url: '/admin',
          templateUrl: adminPartial
        })
        ;
  };