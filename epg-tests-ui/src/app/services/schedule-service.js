import fetch from 'isomorphic-fetch';

function scheduleService () {
	return ({
        serializeObjectToQs,
        getScheduledTests,
        reScheduleAllTests,
        updateTestCases,
        getCurrentlyRunning,
        reScheduleTest,
        runNow
	});

    function serializeObjectToQs (obj) {
        let a = [];
        let qs = '';
        for(let p in obj) {
        if (obj.hasOwnProperty(p)) {
            a.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        }
        }
        qs = a.join("&");
        return qs;
    };

    function reScheduleAllTests (cb) {
        fetch(rest_url_prefix+'/reschedule_all_tests', {
			method: 'GET'
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function getScheduledTests (cb) {
        fetch(rest_url_prefix+'/get_scheduled', {
			method: 'GET'
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function updateTestCases (obj, cb) {
        fetch(rest_url_prefix+'/update_tests', {
			method: 'POST',
            body: JSON.stringify(obj),
            'headers': new Headers({
                'Content-Type': 'application/json',
            })
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function runNow (obj, cb) {
        fetch(rest_url_prefix+'/run_now', {
            method: 'POST',
            body: JSON.stringify(obj),
            'headers': new Headers({
                'Content-Type': 'application/json',
            })
        })
        .then((response) => {
            return response.json();
        })
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function getCurrentlyRunning (cb) {
        fetch(rest_url_prefix+'/currently_executingjobs', {
			method: 'GET'
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };

    function reScheduleTest (obj, cb) {
        fetch(rest_url_prefix+'/reschedule_test', {
			method: 'POST',
            body: JSON.stringify(obj),
            'headers': new Headers({
                'Content-Type': 'application/json',
            })
		})
        .then((response) => {
			return response.json();
		})
        .then((json) => {
            cb(null, json);
        })
        .catch((err) => {
            console.log(err);
            cb(err, null);			
        });
    };
   
}

export default scheduleService;