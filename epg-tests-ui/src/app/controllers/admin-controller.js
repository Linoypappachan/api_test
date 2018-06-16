export default function AdminController ($rootScope, $scope, $timeout, propertiesService) {

    $scope.download_href_prefix = rest_url_prefix;
    $scope.all_properties = [];
    $scope.props_available = false;


    $scope['out_dir'] = '';

    $scope['to'] = ''; //key: email-to
    $scope['cc'] = ''; // key: email-cc
    $scope.emailMap = {'to': 'email-to', 'cc': 'email-cc'};
    $scope['email_body_template'] = '';
    $scope['email_subject_template'] = '';

    $scope['sms_template'] = '';
    $scope.sms_numbers = '';
    $scope.sms_max_size = 0;
    $scope.sms_enable = false;
    $scope.sms_job_cron = '';

    $scope.results_retention_cron = '';
    $scope.results_retention_duration = '';
    $scope.results_retention_start = '';

    $scope.getAllProperties = function() {
        return new Promise(function(resolve, reject){
            propertiesService.getAllProperties((err, data) => {
                if (err || (data && data.error)) {
                    reject(err);
                } else {
                    $scope.all_properties = data;
                    resolve();
                }
            });
        });
    };

    $scope.loadAllProperties = function () {
        $scope.getAllProperties().then(() => {
            $scope.all_properties.forEach((p) => {
                if (p['sms_max_size']) {
                    $scope.$apply(function() {
                        $scope.sms_max_size = p['sms_max_size'];
                    });
                }
                if (p['sms_numbers']) {
                    $scope.$apply(function() {
                        $scope.sms_numbers = p['sms_numbers'];
                    });
                }
                    
                if (p['sms_message']) {
                    $scope.$apply(function() {
                        $scope.sms_message = p['sms_message'];
                    });
                }
                    
                if (p['sms_enable']) {
                    $scope.$apply(function() {
                        $scope.sms_enable = (p['sms_enable'] === 'true') ? true: false;
                    });
                }
                    
                if (p['email-to']) {
                    $scope.$apply(function() {
                        $scope.to = p['email-to'];
                    });
                }
                    
                if (p['email-cc']) {
                    $scope.$apply(function() {
                        $scope.cc = p['email-cc'];
                    });
                }
                    
                if (p['out_dir']) {
                    $scope.$apply(function() {
                        $scope['out_dir'] = p['out_dir'];
                    });
                }

                if (p['sms_job_cron']) {
                    $scope.$apply(function() {
                        $scope.sms_job_cron = p['sms_job_cron'];
                    });
                }

                if (p['results_retention_start']) {
                    $scope.$apply(function() {
                        $scope.results_retention_start = p['results_retention_start'];
                    });
                }

                if (p['results_retention_duration']) {
                    $scope.$apply(function() {
                        $scope.results_retention_duration = p['results_retention_duration'];
                    });
                }

                if (p['results_retention_cron']) {
                    $scope.$apply(function() {
                        $scope.results_retention_cron = p['results_retention_cron'];
                    });
                }
                    
            });
        }).catch((err) => {
            console.log('Failed to fetch properties', err);
        });
    };

    $scope.loadAllProperties();

    $scope.getTemplate = function(templateName) {
        propertiesService.getTemplate({name:templateName}, (err, response) => {
            if (err) {
                console.log(err);
            } else {
                $scope.$apply(function() {
                    $scope[templateName] = response.message;
                });
            }
        });
    };

    $scope.getTemplate('email_body_template');
    $scope.getTemplate('email_subject_template');
    $scope.getTemplate('sms_template');

    $scope.saveTemplate = function(templateName) {
        var data = new FormData();
        data.append('data', $scope[templateName]);
        data.append('name', templateName)
        propertiesService.saveTemplate(data, 
            function(err, data) {
                if (err || (data && data.error)) {
                    console.log(err);
                } else {
                    propertiesService.getTemplate({name:templateName}, (err, response) => {
                        if (err) {
                            console.log(err);
                        } else {
                            $scope[templateName] = response.message;
                        }
                    });
                }
        });
    };

    $scope.uploadTemplate = function(elementId, templateName) {
        let file = document.querySelector('#'+elementId).files[0];
        var data = new FormData();
        data.append('file', file);
        data.append('name', templateName);
        propertiesService.uploadTemplate(data, (err, response) => {
            if (err) {
                console.log(err);
            } else {
                propertiesService.getTemplate({name:templateName}, (err, response) => {
                    if (err) {
                        console.log(err);
                    } else {
                        $scope[templateName] = response.message;
                    }
                });
            }
        });
    };

    $scope.setPropertiesFile = function () {
        let file = document.querySelector('#file-upload').files[0];
        var data = new FormData();
        data.append('file', file);
        propertiesService.setPropertiesFile(data, (err, response) => {
            if (err) {
                console.log(err);
            } else {
                $timeout(() => {
                    $scope.loadAllProperties();
                }, 10);
            }
        });
    };

    $scope.saveProperty = function (key, value) {
        propertiesService.saveProperty({'key': key, 'value': value}, function(err, data) {
            if (err || (data && data.error)) {
                $timeout(() => {
                    $scope.loadAllProperties();
                }, 10);
            }
        });
    };

    $scope.saveEmailProperties = function() {
        for (let key in $scope.emailMap) {
            let value = $scope.emailMap[key];
            propertiesService.saveProperty({'key': value, 
                'value': $scope[key]}, function(err, data) {
                if (err && (data && !data.error)) {
                    console.log(err);
                }
            });
        }
        $timeout(() => {
            $scope.loadAllProperties();
        }, 10);
    };

    $scope.saveSMSProperties = function () {
        $scope.saveProperty('sms_numbers', $scope.sms_numbers);
        $scope.saveProperty('sms_max_size', $scope.sms_max_size);
        $scope.saveProperty('sms_job_cron', $scope.sms_job_cron);
        $scope.saveProperty('sms_enable', ($scope.sms_enable)?'true': 'false');
        $timeout(() => {
            $scope.loadAllProperties();
        }, 10);
    };

    $scope.saveRetentionProperties = function() {
        $scope.saveProperty('results_retention_cron', $scope.results_retention_cron);
        $scope.saveProperty('results_retention_duration', $scope.results_retention_duration);
        $scope.saveProperty('results_retention_start', $scope.results_retention_start);
        $timeout(() => {
            $scope.loadAllProperties();
        }, 10);
    };

}