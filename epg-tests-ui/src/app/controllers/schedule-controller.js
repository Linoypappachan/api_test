export default function SchedulerController(
  $rootScope,
  $scope,
  $timeout,
  $interval,
  scheduleService,
  alertsService,
  testDetailsService
) {
  $scope.moment = moment;
  $scope.scheduled_tests = [];
  $scope.selected_jobs = {};
  $scope.all_jobs_selected = false;
  $scope.atLeastOneSelected = false;
  $scope.lastrun = {};
  $scope.current_running = {};
  $scope.testCasesMap = {};

  $scope.loadTestCasesMap = function() {
    return new Promise(function(resolve, reject) {
      testDetailsService.getTestCasesMap((err, data) => {
        if (err) {
          reject(err);
        } else {
          $scope.testCasesMap = data;
          resolve($scope.testCasesMap);
        }
      });
    });
  };

  $scope.cron_map = [
    { id: "5mins", value: "- */5 * * * ?", display: "Every 5 minutes" },
    { id: "30mins", value: "- */30 * * * ?", display: "Every 30 minutes" },
    { id: "1hr", value: "- - */1 * * ?", display: "Every 1 hour" },
    { id: "2hr", value: "- - */2 * * ?", display: "Every 2 hours" },
    { id: "6hr", value: "- - */6 * * ?", display: "Every 6 hours" },
    { id: "12hr", value: "- - */12 * * ?", display: "Every 12 hours" },
    { id: "1d", value: "- - - */1 * ?", display: "Daily once" }
  ];

  $scope.getCopyOfCronMap = function() {
    let arr = [];
    for (let i = 0; i < $scope.cron_map.length; i += 1) {
      let _o = {};
      let o = Object.assign(_o, $scope.cron_map[i]);
      arr = arr.concat(o);
    }
    return arr;
  };

  $scope.getMappedCronSchedule = function(tc_schedule) {
    let cron_schedules = $scope.getCopyOfCronMap();
    cron_schedules = cron_schedules.map(ms => {
      let tc_schedule_arr = tc_schedule.split(" ").slice(0);
      let s_arr = ms.value.split(" ").slice(0);
      let idx = -1;
      for (let i = 0; i < s_arr.length; i += 1) {
        if (s_arr[i] !== "-") {
          idx = i;
          break;
        }
      }
      if (idx !== -1) {
        let is_same = true;
        for (let j = idx; j < s_arr.length; j += 1) {
          is_same = is_same && s_arr[j] === tc_schedule_arr[j];
        }
        if (is_same) {
          ms.value = tc_schedule;
        }
      }
      return ms;
    });
    return cron_schedules;
  };

  $scope.loadScheduledTests = function() {
    scheduleService.getScheduledTests((err, data) => {
      if (err) {
        console.log(err);
      } else if (data && !data.error) {
        $scope.$apply(function() {
          $scope.scheduled_tests = data;
          $scope.scheduled_tests = $scope.scheduled_tests.map(d => {
            d.cron_schedules = $scope.getMappedCronSchedule(
              d.testCase.schedule
            );
            return d;
          });
        });
      }
    });
  };

  $scope.loadScheduledTests();

  $scope.updateTestCases = function(testCases) {
    scheduleService.updateTestCases(testCases, (err, data) => {
      if (err) {
        console.log(err);
      } else {
        console.log(data);
        $scope.loadScheduledTests();
      }
    });
  };

  $scope.runNow = function(testCases) {
    scheduleService.runNow(testCases, (err, data) => {
      if (err) {
        console.log(err);
      } else if (data && !data.error) {
        console.log(data);
      }
    });
  };

  $scope.selectDeselectAll = function() {
    $scope.atLeastOneSelected = $scope.all_jobs_selected;
    for (let t of $scope.scheduled_tests) {
      $scope.selected_jobs[t.testCase.id] = $scope.all_jobs_selected;
    }
  };

  $scope.selectIndividualJob = function() {
    $scope.atLeastOneSelected = false;
    $scope.all_jobs_selected = true;
    for (let t of $scope.scheduled_tests) {
      $scope.all_jobs_selected =
        $scope.all_jobs_selected && $scope.selected_jobs[t.testCase.id];
      if (!$scope.atLeastOneSelected)
        $scope.atLeastOneSelected = $scope.selected_jobs[t.testCase.id];
    }
  };

  $scope.loadCompleted = function() {
    alertsService.getLastRun((err, data) => {
      if (err) {
        console.log(err);
      } else if (data && !data.error) {
        $scope.$apply(function() {
          $scope.lastrun = data;
        });
      }
    });
  };

  $scope.loadCompleted();

  $scope.activateSelected = function(activationFlag) {
    let testCases = [];
    for (let t of $scope.scheduled_tests) {
      if ($scope.selected_jobs[t.testCase.id]) {
        t.testCase.active = activationFlag ? "true" : "false";
        testCases = testCases.concat(t.testCase);
      }
    }
    if (activationFlag) {
      $rootScope.$emit("ui-alert", {
        type: "info",
        message: "Activated selected test cases"
      });
    } else {
      $rootScope.$emit("ui-alert", {
        type: "info",
        message: "Deactivated selected test cases"
      });
    }
    $scope.updateTestCases(testCases);
  };

  $scope.saveSchedule = function(testCase) {
    $scope.current_running[testCase.id] = true;
    $rootScope.$emit("ui-alert", {
      type: "info",
      message: `Successfully re-scheduled - ${testCase.name} job`
    });
    scheduleService.reScheduleTest(testCase, (err, data) => {
      if (err) {
        console.log(err);
      } else {
        console.log(data);
        $scope.loadScheduledTests();
      }
    });
  };

  $scope.loadCurrentlyRunningJobs = function() {
    scheduleService.getCurrentlyRunning((err, data) => {
      if (err) {
        console.log(err);
      } else if (data && !data.error) {
        $scope.$apply(function() {
          $scope.current_running = data;
        });
      }
    });
  };

  $scope.runNowTestCase = function(tc) {
    let testCase = Object.assign({}, tc);
    $scope.current_running[testCase.id] = true;
    $rootScope.$emit("ui-alert", {
      type: "info",
      message: `Running - ${testCase.name} job`
    });
    $timeout(() => {
      testCase.id = testCase.id + "_now";
      $scope.runNow(testCase);
    }, 100);
  };

  $interval(() => {
    $scope.loadCurrentlyRunningJobs();
    $scope.loadCompleted();
  }, 5000);
}
