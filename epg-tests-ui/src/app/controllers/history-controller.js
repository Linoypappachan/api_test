export default function HistoryController ($rootScope, $scope, $stateParams,
    $timeout, $interval, alertsService, testDetailsService) {
    $scope.moment = moment;
    $scope.alerts = [];
    $scope.selected_status = 'fail';
    $scope.lastrun = {};
    $scope.testCaseId = '';
    $scope.testCasesMap = {};

    $scope.loadTestCasesMap = function() {
        return new Promise(function(resolve, reject){
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

    $scope.loadAlerts = function (testCaseId) {
        $rootScope.$emit('test-history');
        $scope.testCaseId = testCaseId.replace('_now', '');
        alertsService.getCompletedByTestCase($scope.testCaseId, (err, data) => {
            if (err) {
                console.log(err);
            } else if (data && !data.error) {
                $scope.$apply(function() {
                    $scope.alerts = data;
                    $scope.alerts = $scope.alerts.filter((d) => {
                        return ((d.testId === $scope.testCaseId)
                        || (d.testId === $scope.testCaseId+'_now'));
                    });
                    $scope.alerts = $scope.alerts.map((d) => {
                        d.testCase = Object.assign({}, $scope.testCasesMap[d.testId]);
                        if (!d.testCase.name) {
                            let testId = `${d.testId}`;
                            d.testCase = Object.assign({}, $scope.testCasesMap[testId.replace('_now', '')]);
                            d.testCase.id += '_now';
                        }
                        d.resultLocation = encodeURIComponent(d.resultLocation);
                        return d;
                    });
                });
            }
        });
    };

    $scope.loadTestCasesMap()
    .then((data) => {
        $scope.loadAlerts($stateParams.testCaseId);
    })
    .catch((err) => {
        console.log(err);
    });
    
}
