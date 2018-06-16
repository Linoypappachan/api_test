export default function UIAlertsController ($rootScope, $scope, $timeout) {

    window.adjustAlertLayout = function() {
        let left = (document.body.clientWidth)/2 - 250;
        document.getElementById('epg-ui-alerts').style.left = left+'px';
    };
    window.addEventListener('resize', window.adjustAlertLayout);

    $scope.init = function() {
        window.adjustAlertLayout();
    };
    $scope.init();

    $scope.type = 'info';
    $scope.typeName = 'info';
    $scope.message = '';

    $rootScope.$on('ui-alert', (event, data) => {
        document.getElementById('epg-ui-alerts').style.display = 'block';
        $scope.type = data.type;
        if ($scope.type === 'danger') {
            $scope.typeName = 'Error';
        } else {
            $scope.typeName = (''+$scope.type[0]).toUpperCase() + $scope.type.substring(1);
        }
        $scope.message = data.message;
        $timeout(() => {
            document.getElementById('epg-ui-alerts').style.display = 'none';
        }, 6000);
    });
}
