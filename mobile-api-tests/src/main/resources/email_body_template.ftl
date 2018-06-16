<h4 style="font-family: Helvetica;">Dear All,</h4>

  <span style="font-family: Helvetica;">
    Following test case has <#if testCaseResult.status == "PASS"> <span style="font-weight:bold;color:#228B22;">passed</span> <#else> <span style="font-weight:bold;color:#FF4500;">failed</span> </#if> </span>
<p>&nbsp;&nbsp;</p>
  <table style="border: 1px solid #bbb;
    font-family: Helvetica;
    width: 70%;background-color:#1E90FF;">
    <tr>
      <td style="padding:4px;width:20%;border: 1px solid #bbb;
        color: #FFF;
        font-weight: bold;">Name</td>
      <td style="padding:4px;width:80%;border: 1px solid #bbb;
        background-color:#FFF;">${tc.name}</td>
    </tr>
    <tr>
      <td style="padding:4px;width:20%;border: 1px solid #bbb;
        color: #FFF;
        font-weight: bold;">Description</td>
        <td style="padding:4px;width:80%;border: 1px solid #bbb;
          background-color:#FFF;">${tc.description}</td>
    </tr>
    <#if testCaseResult.status == "FAIL">
      <tr>
        <td style="padding:4px;width:20%;border: 1px solid #bbb;
          color: #FFF;
          font-weight: bold;">Link</td>
        <td style="padding:4px;width:80%;border: 1px solid #bbb;
            background-color:#FFF;">
            <a href="http://intranet/web_testportal/#/test-details/${testCaseResult.resultLocation}">
              link to details
            </a>
        </td>
      </tr>
      <tr>
        <td style="padding:4px;width:20%;border: 1px solid #bbb;
          color: #FFF;
          font-weight: bold;">Message</td>
        <td style="padding:4px;width:80%;border: 1px solid #bbb;
          background-color:#FFF;">${failedDetails.failedMessage}</td>
      </td>
      <tr>
        <td style="padding:4px;width:20%;border: 1px solid #bbb;
          color: #FFF;
          font-weight: bold;">Exception</td>
        <td style="padding:4px;width:80%;border: 1px solid #bbb;
          background-color:#FFF;">${failedDetails.failedException}</td>
      </tr>
    </#if>
  </table>

  <br/>
  <span style="padding:4px;display:block;
    font-weight:bold;font-size:15px;">Regards,</span>
  <br/>
  <span style="padding:4px;display:block;
  font-size:13px;font-style:italic;">-</span>