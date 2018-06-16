export default function NavCtrl ($rootScope, $scope, $state) {
    $scope.tabs = ['alerts', 'schedule', 'tests-configuration',
        'test-details', 'admin', 'test-history'];
    $scope.selected_tab = $scope.tabs[0];
    $scope._result = {};
    $scope.show_details = false;
    $scope.show_history = false;

    $scope.hideDetails = function() {
        $scope.show_details = false;
    };

    $scope.hideHistory = function() {
        $scope.show_history = false;  
    };

    if (window.location && window.location.hash) {
        if (window.location.hash === '/') {
            $scope.selected_tab = $scope.tabs[0];
        } else if (window.location.hash === '#/schedule') {
            $scope.selected_tab = $scope.tabs[1];
        } else if (window.location.hash === '#/tests-configuration') {
            $scope.selected_tab = $scope.tabs[2];
        } else if (window.location.hash === '#/test-details') {
            $state.go('alerts');
        }else if (window.location.hash === '#/admin') {
            $scope.selected_tab = $scope.tabs[4];
        }
    }

    $rootScope.$on('schedule', (event, data) => {
        $scope.selected_tab = $scope.tabs[1];
        $state.go('schedule');
    });

    $rootScope.$on('alerts', (event, data) => {
        $scope.selected_tab = $scope.tabs[0];
        $state.go('alerts');
    });

    $rootScope.$on('test-details', (event, data) => {
        $scope.show_details = true;
        $scope.selected_tab = $scope.tabs[3];
    });

    $rootScope.$on('test-history', (event, data) => {
        $scope.show_history = true;
        $scope.selected_tab = $scope.tabs[5];
    });

}