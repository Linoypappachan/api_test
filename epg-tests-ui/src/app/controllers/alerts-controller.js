export default function AlertsController ($rootScope, $scope, $interval, alertsService,
    testDetailsService) {
    $scope.moment = moment;
    $scope.alerts = [];
    $scope.selected_status = 'FAIL';
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

    $scope.loadAlerts = function () {
        alertsService.getAlerts((err, data) => {
            if (err) {
                console.log(err);
            } else if (data && !data.error) {
                $scope.$apply(function() {
                    $scope.alerts = data;
                    $scope.alerts = $scope.alerts.map((d) => {
                        let testCase = $scope.testCasesMap[d.testId];
                        if (testCase) {
                            d.testCase = Object.assign({}, testCase);
                        } else {
                            testCase = $scope.testCasesMap[d.testId.replace('_now', '')];
                            if (testCase && testCase.id) {
                                d.testCase = Object.assign({}, testCase);
                                d.testCase.id += '_now';
                            }
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
        $scope.loadAlerts();
         $interval(() => {
            $scope.loadAlerts();
        }, 5000);
    })
    .catch((err) => {
        console.log(err);
    });

}