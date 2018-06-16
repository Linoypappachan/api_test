export default function TestDetailsController ($rootScope, $scope, $timeout, $stateParams,
     testDetailsService) {

    $scope.result = {};

    function updateTestDetails(result, o) {
        let tc = null;
        try {
            tc = JSON.parse(o['params']['param']['value']);
            result.name = tc.name;
            result.desc = tc.description;
            result.url = tc.url;
            result.params = tc.params;
            result.verb = tc.verb.toUpperCase();
        } catch(err) {
           console.log(err);
        }
    }

    function getResultDetailsObject(str) {
        let obj = JSON.parse(str);
        let result = {};
        let o1 = obj['testng-results']['suite'];
        result.startedAt = o1['started-at'];
        result.finishedAt = o1['finished-at'];
        result.duration = o1['duration-ms'];
        let o2 = o1['test']['class']['test-method'];
        if (Array.isArray(o2)) {
            o2 = o2.filter((_o) => {
                return (_o.name === 'test');
            })[0];
        }
        result.status = o2['status'];
        updateTestDetails(result, o2);
        if (result.status === 'FAIL') {
            result.exception = o2['exception']['full-stacktrace'];
            result.message = o2['exception']['message'];
        }
        return result;
    }

    $scope.getResultDetails = function (location) {
        $rootScope.$emit('test-details');
        testDetailsService.getDetails({location: location}, 
            function(err, data) {
                if (err) {
                    console.log(err);
                } else if(data && !data.error) {
                    $scope.$apply(function() {
                        $scope.result = getResultDetailsObject(data.message);
                    });
                }
            });
    };

    $scope.getResultDetails($stateParams.location);

}