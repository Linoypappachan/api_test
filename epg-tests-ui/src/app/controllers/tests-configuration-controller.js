export default function TestsConfigurationController ($rootScope, $scope, $timeout,
    scheduleService,
    testsConfigurationService) {
    $scope.moment = moment;
    $scope.download_href_prefix = rest_url_prefix;
    $scope.last_upload_timestamp = null;
    $scope.uploading = false;

    $scope.getFileUploadTime = function () {
        testsConfigurationService.getFileUploadTime({'file': 'urls.xml'},
        (err, data) => {
            if (err) {
                console.log(err);
            } else {
                if (data && !data.error) {
                    $scope.$apply(function() {
                        $scope.last_upload_timestamp = data;
                    });
                }
            }
        });
    };
    $scope.getFileUploadTime();

    $scope.uploadXML = function () {
        $scope.uploading = true;
        let id = 'xml-file-upload';
        let file = document.getElementById(id).files[0];
        var data = new FormData();
        data.append('file', file);
        data.append('fileName', 'urls.xml');
        testsConfigurationService.uploadXMLFile(data, (err, response) => {
            $scope.uploading = false;
            if (err) {
                $scope.$apply(function() {
                    $rootScope.$emit('ui-alert', {type:'danger', 
                        message: `XML Upload failed.`});
                });
                
            } else {
                $scope.$apply(function() {
                    $rootScope.$emit('ui-alert', {type:'success', 
                        message: `XML Upload successful.`});
                });
                $scope.getFileUploadTime();
            }
        });
    };
    
}