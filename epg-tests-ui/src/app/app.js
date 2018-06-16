import angular from 'angular';
import 'angular-ui-router';
import 'jquery';
import 'bootstrap';
import 'angular-ui-grid';

import 'angular-json-tree';

import 'bootstrap/dist/css/bootstrap.css';
import 'angular-ui-grid/ui-grid.css';

import './scss/app.scss';
import './scss/ui-alert.scss';
import './fonts/scheduler-fonts-styles.css';

import AppRouter from './routes';

import alertsService from './services/alerts-service';
import scheduleService from './services/schedule-service';
import testsConfigurationService from './services/tests-configuration-service';
import testDetailsService from './services/test-details-service';
import propertiesService from './services/properties-service';

import UIAlertsController from './controllers/ui-alerts-controller';
import NavCtrl from './controllers/nav-controller';
import AlertsController from './controllers/alerts-controller';
import SchedulerController from './controllers/schedule-controller';
import TestsConfigurationController from './controllers/tests-configuration-controller';
import TestDetailsController from './controllers/test-details-controller';
import AdminController from './controllers/admin-controller';
import HistoryController from './controllers/history-controller';


const MODULE_NAME = 'app';

angular.module(MODULE_NAME, ['ui.router', 'angular-json-tree'])
  .config(['$locationProvider','$urlRouterProvider', '$stateProvider', AppRouter])
  .service('scheduleService', scheduleService)
  .service('testsConfigurationService', testsConfigurationService)
  .service('alertsService', alertsService)
  .service('testDetailsService', testDetailsService)
  .service('propertiesService', propertiesService)
  .controller('UIAlertsController', ['$rootScope', '$scope', '$timeout', UIAlertsController])
  .controller('NavCtrl', ['$rootScope', '$scope', '$state', NavCtrl])
  .controller('AlertsController', ['$rootScope', '$scope', '$interval', 
    'alertsService', 'testDetailsService', AlertsController])
  .controller('SchedulerController', ['$rootScope', '$scope', '$timeout', '$interval',
    'scheduleService', 'alertsService','testDetailsService', SchedulerController])
  .controller('TestsConfigurationController', ['$rootScope', '$scope', '$timeout',
    'scheduleService', 'testsConfigurationService', TestsConfigurationController])
  .controller('TestDetailsController', ['$rootScope', '$scope', '$timeout', '$stateParams',
  'testDetailsService', TestDetailsController])
  .controller('HistoryController', ['$rootScope', '$scope', '$stateParams', '$timeout', 
    '$interval', 'alertsService', 'testDetailsService', HistoryController])
  .controller('AdminController', ['$rootScope', '$scope', '$timeout', 
    'propertiesService', AdminController])
  
;

export default MODULE_NAME;