<#if tc_names?length gt 0>  ${tc_names} test cases have failed in the last run at ${time}
<#else> ${completed_jobs_count} test cases have been failed in the last run at ${time}. 
</#if>
http://intranet/web_testportal