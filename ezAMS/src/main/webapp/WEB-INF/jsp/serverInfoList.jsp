
<% String appPath = request.getContextPath(); %>
<% String serverUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/ezams/"; %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf"%>
<%@ page import="org.springframework.web.context.request.RequestAttributes"%>
<%@ page import="org.springframework.web.context.request.RequestContextHolder"%>


<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<title>ezAMS</title>
<script src="<%=appPath%>/js/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="<%=appPath %>/js/jquery.countdown360.js"></script>

<script src="<%=appPath%>/js/jquery-ui.js" type="text/javascript"></script>
<script src="<%=appPath%>/js/i18n/grid.locale-kr.js"type="text/javascript"></script>
<script src="<%=appPath%>/js/jquery.jqGrid.min.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/css/jquery-ui.css"></link>
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/css/ui.jqgrid.css"></link>

</head>
<script type="text/javascript">
   $(document).ready(function(){
    $("#tabs").tabs();
    $("#search").button();
    $("#logout").button();
    $("#startBatch").button();
    $("#selectTime").selectmenu ({width: "150px"});
    $("#ezAMS").css ({
      'height' : '50px',
      'width' : '100px'
     }); 
   $('input:text, input:password')
.button()
.css ({
            'font' : 'inherit',
            'text-align' : 'left',
            'outline' : 'none',
            'cursor' : 'text',
            'font-size' : '23px',
            'height' : '30px'
		});

/***** 캘린더 설정 *****/
$("#searchDate").datepicker ({
      inline: true,
      MaxDate: '0',
      dateFormat: 'yy/mm/dd',
      buttonImage: '<%=appPath%>/css/image/sort-desc.png',
      buttonImageOnly: true,
      dayName: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'],
      dayNamesMin: ['일','월','화','수','목','금','토'],
     monthNameShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
     monthName: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
     onSelect:function (selectedDate){}

    });

   fn_countdown(); 
   fn_dateInit();
   fn_search();
   
   setInterval(function(){
       fn_alarm()
       //fn_img();
       },400);

      setInterval(function(){
       fn_interval();
       },60000);

   });

function fn_alarm(){
    var rebootFlag = document.getElementsByName ('rebootFlag');
   for ( var i = 0; i < rebootFlag.length; i++){
     rebootFlag[i].style.color = (rebootFlag[i].style.color == "" ? "red" : "");
    }

var failoverFlag = document.getElementsByName ('failoverFlag');
   for ( var i = 0; i < failoverFlag.length; i++){
    failoverFlag[i].style.color = (failoverFlag[i].style.color == "" ? "red" : "");
    }

var diskAlarm = document.getElementsByName ('DiskAlarm');
   for ( var i = 0; i < diskAlarm.length; i++){
    diskAlarm[i].style.color = (diskAlarm[i].style.color == "" ? "red" : "");
    }

var ProcessUpCntFlag = document.getElementsByName ('ProcessUpCntFlag');
   for ( var i = 0; i < ProcessUpCntFlag.length; i++){
    ProcessUpCntFlag[i].style.color = (diskAlarm[i].style.color == "" ? "blue" : "");
    }

var ProcessDownCntFlag = document.getElementsByName ('ProcessDownCntFlag');
   for ( var i = 0; i < ProcessDownCntFlag.length; i++){
    ProcessDownCntFlag[i].style.color = (ProcessDownCntFlag[i].style.color == "" ? "red" : "");
    }

var ProcessAbnomalFlag = document.getElementsByName ('ProcessAbnomalFlag');
   for ( var i = 0; i < ProcessAbnomalFlag.length; i++){
    ProcessAbnomalFlag[i].style.color = (ProcessAbnomalFlag[i].style.color == "" ? "yellow" : "");
    }

var tab = document.getElementsByName ('tabCntAlarm');
   for ( var i = 0; i < tab.length; i++){
    tab[i].style.color = (tab[i].style.color == "red" ? "" : "red");
    }

var pingState = document.getElementsByName('pingFail');
   for ( var i = 0; i < pingState.length; i++){
    pingState[i].style.color = (pingState[i].style.color == "red" ? "" : "red");
    }
}

function fn_dateInit(){

    var selectTime = $('#selectTime');
    selectTime.empty();

   var today = new Date();
   var h = today.getHours();

   for (var i = 0; i < 24; i++){
     if(i == h){
       $("#selectTime").append("<option value = '"+i+"' selected = 'selected'>"+i+"시</option>");
 }else{
 $("#selectTime").append("<option value = '"+i+"'>"+i+"시</option>");
  }
}

selectTime.selectmenu('refresh');

    var strYYYY = new Date().getFullYear();
    var strMM = (new Date().getMonth()+1);
    var strDD = new Date().getDate ();

   if ((new Date().getMonth()+1) < 10) {
      strMM = "0"+strMM;
  }

if ((new Date().getDate) < 10) {
      strDD = "0"+strDD;
  }
   var strNowDateVal = strYYYY + "/" + strMM + "/" + strDD;
   $("#searchDate").val(strNowDateVal);

}

function fn_countdown(){
  
	$("#countdown").countdown360({
    	radius		:	30,	// 반지름
    	seconds		:	60,
    	fontColor	:	'#FFFFFF',
    	autostart	:	true,
    	onComplete	:	function(){}
    }).start()

}

    var alarmCnt = 0;

 function fn_search(){
	
  //### fn_search() start ##
   var paramAlarm = {
     searchDate    : $("#searchDate").val(),
     selectTime    : $("#selectTime").val(),
     searchColumn  : "",
     searchKeyword : ""
   };

   var initAlarm = {
     table : "#Alarm",
     tab : "#li-1",
     tabCnt : 0
 };

  
  $.jgrid.gridUnload(initAlarm.table); 
  jQuery(initAlarm.table).jqGrid({
   url:"common/selectServerInfo.do",
   mtype : "POST",
   datatype: "json",
   postData : paramAlarm,
   rowNum : 1000,
   colNames:['No','Alarm','Tool','HostName','Ip','Cpu (%)','Memory(%)','Process','SlowQuerBackUp(S-EQUS)','Disk','occuredTime','LastBootUpTime','Ping'],
  colModel:[
     {name: 'no', index: 'no', width:30, align:"center", sorttype:"int", sortable:true },
     {name:'alarm', index:'alarm', width:30, align:"center"},
     {name:'toolName', index:'toolName', width:60, align:"center" },
     {name:'hostName',  index:'alarm', width:90, align:"center", sortable:false},
     {name: 'ipInfo',index:'ipInfo', width:90, align:"center",sortable:false},
     {name: 'cpuInfo',index:'cpuInfo', width:55, align:"center",sorttype:"int"},
	{name: 'memoryInfo',index:'memoeryInfo', width:70, align:"center",sorttype:"int"},
   {name: 'runProcess',index:'runProcess', width:130, align:"left",sortable:false},
    {name: 'SlowQueryStat',index:'SlowQueryStat', width:10, align:"center",sortable:false},
	{name: 'diskInfo',index:'diskInfo', width:520, align:"center",sortable:false},
    {name: 'occuredTime',index:'occuredtime', width:100, align:"center",sortable:true},
    {name: 'lastBootUpTime',index:'lastBootUpTime', width:180, align:"center",sortable:true},
    {name: 'pingState',index:'pingState', width:30, align:"center",sortable:true}],
    sortname: 'no',
       sortorder: "asc",
       viewrecords: "true",
       loadonce:false,
     autowidth: false,
     width: $(window).width()-60,
    height: $(window).height()-135,
  ajaxGridOptions : {async:false},
   caption: "서버정보",
   loadtext: "조회 중입니다.",
   emptyrecords: "조회할 데이터가 없습니다.",
   onSelectRow: function(){
      return true;
 },
   afterInsertRow : function(rowid, rowdata, rowelem){
    if(rowdata.ipInfo == "nothing"){
      jQuery (initAlarm.table).delRowData(rowid);
    }else {
      switch(rowdata.alarm){
         case 'A':
           //jQuery (initAlarm.table).setCell(rowid, "alarm", "<ul id='icons'><li class='ui-state-error'><span class='ui-icon ui-icon-circle-check></span></li></ul>"); //특정 셀값을 변경하여줌
  break;

  case 'B':
    if(rowdata.ipInfo == "N/A" && rowdata.cpuInfo == "N/A" && rowdata.memoryInfo == "N/A" && rowdata.runProcessInfo == "N/A" && rowdata.diskInfo == "N/A" && rowdata.lastBootUpTimeInfo == "N/A"){
     // jQuery (initAlarm.table).setCell(rowid, "alarm", "<ul id='icons'><li class='ui-state-highlight'><span class='ui-icon ui-icon-circle-check></span></li></ul>"); //특정 셀값을 변경하여줌
        }else {
         jQuery(initAlarm.table).delRowData(rowid);
       }
      break;

     case 'N':
        jQuery(initAlarm.table).delRowData(rowid);
        break;
      }
    }
  },
  

});
//### end alarm ###
	
var param = {
     searchDate:$("#searchDate").val(),
    selectTime:$("#selectTime").val(),
    searchColumn : "toolName",
    searchKeyword : "PANTHEON"
   };

   var init = {
     table : "#PANTHEON",
     tab : "#li-2",
     tabCnt : 16
 };

 
   $.jgrid.gridUnload(init.table); 
  jQuery(init.table).jqGrid({
   url:"common/selectServerInfo.do",
   mtype : "POST",
   datatype: "json",
   postData : param,
   rowNum : 1000,
   colNames:['No','Alarm','Tool','HostName','Ip','Cpu (%)','Memory(%)','Process','Disk','LastBootUpTime','Ping'],
  colModel:[
     {name: 'no', index: 'no', width:30, align:"center", sorttype:"int"},
     {name:'alarm', index:'alarm', width:30, align:"center"},
     {name:'toolName', index:'toolName', width:60, align:"center" },
     {name:'hostName',  index:'alarm', width:90, align:"center", sortable:false},
     {name: 'ipInfo',index:'ipInfo', width:90, align:"center",sorttype:"int"},
     {name:'cpuInfo',index:'cpuInfo',width:90,align:"center",sorttype:"int"},
  	{name: 'memoryInfo',index:'memoeryInfo', width:70, align:"center",sorttype:"int"},
   	{name: 'runProcess',index:'runProcess', width:130, align:"left",sortable:false},
	{name: 'diskInfo',index:'diskInfo', width:520, align:"center",sortable:false},
    {name: 'lastBootUpTime',index:'lastBootUpTime', width:220, align:"center",sortable:true},
    {name: 'pingState',index:'pingState', width:30, align:"center",sortable:true}],
    sortname: 'no',
       sortorder: "desc",
       viewrecords: "true",
       loadonce: true,
     autowidth: false,
     width: $(window).width()-40,
    height: $(window).height()-135,
  ajaxGridOptions : {async:false},
   caption: "서버정보",
   loadtext: "조회 중입니다.",
   emptyrecords: "조회할 데이터가 없습니다.",
   onSelectRow: function(){
      return true;
 },
   afterInsertRow : function(rowid, rowdata, rowelem){
      switch(rowdata.alarm){
         case 'A':
         //  jQuery (init.table).setCell(rowid, "alarm", "<ul id='icons'><li class='ui-state-error'><span class='ui-icon ui-icon-circle-check></span></li></ul>"); //특정 셀값을 변경하여줌
  break;
  case 'B':
    //  jQuery (init.table).setCell(rowid, "alarm", "<ul id='icons'><li class='ui-state-highlight'><span class='ui-icon ui-icon-circle-check></span></li></ul>"); //특정 셀값을 변경하여줌
      break;
     case 'N':
        jQuery(init.table).setCell(rowid, "alarm"," ");
        break;
    }
  },

});

//## PANTHEON END ##
 	
 	fn_countdown();
 }
 
function fn_startBatch(){

   var result = confirm("배치를 실행하시겠습니까?");

   if(result){

      var param={};
     $.ajax({
       url:"common/startBatch.do",
       type:"POST",
       dataType:"html",
       data:param,
       success:function(data){
         var result = eval('('+data+')');
         alert(result.message);
         fn_search();
     },
     error:function(){ fn_countdown(); },
     complete:function(){ fn_countdown(); }
   });
   }
}


function fn_logOut(){
      document.location.href = '<%=serverUrl%>';
	}

	function fn_interval() {

		var intervalCheck = $("input:checkbox[id='interval']").is(":checked");

		var param = {};

		if (intervalCheck == true) {
			$.ajax({
				url : "common/selectInterval.do",
				type : "POST",
				dataType : "html",
				success : function(data) {
					var result = eval('(' + data + ')');
					if (result.rtn == true) {
						fn_dateInit();
						fn_search();
					} else {
						return;
					}
				},
				error : function() { fn_countdown(); 
				},
				complete : function() { fn_countdown(); 
				}
			});
		}
	}
	
	
</script>
<style>
body {
	font: 100% "Trebuchet MS", sans-serif;
	margin: 2px;
}

.demoHeaders {
	margin-top: 1em;
}

#dialog-link {
	padding: .4em 1em .4em 20px;
	text-decoration: none;
	position: realtive;
}

#diaglog-link {
	padding: .4em 1em .4em 20px;
	text-decoration: none;
	position: realtive;
}

#dialog-link span.ui-icon {
	margin: 0 5px 0 0;
	position: absolute;
	left: .2em;
	top: 50%;
	margin-top: -8px;
}

#icons {
	margin: 0;
	padding: 0;
}

#icons li {
	margin: 5px;
	position: realtive;
	padding: 1px 0;
	cursor: pointer;
	float: left;
	list-style: none;
}

#icons span.ui-icon {
	float: left;
	margin: 0 4px;
}

.fakewindowcontain .ui-widget-overlay {
	position: absolute;
}

.ui-jqgrid-btable .ui-state-highlight {
	background: yellow;
}
#countDown{
	
	width : 10px;
	height : 10px;
}
</style>
<body>
	<div id="alarmSound"></div>
	<table border='0' width="100%">
		<tr>
			<td width='10px'><img width='10px' height='10px' id='ezAMS' align="left" src="<%=appPath%>/css/image/ezams.png" /></td>
			<td align="left" width="200px"><input type="text" size="12" id="searchDate" name="searchDate" style='position: relative; z-index: 100000;'></td>
			<td align="left" width="130px" style="font-size: 21px"><select id="selectTime"></select></td>
			<td width="100px" align="right"><input type="button" size="12" id="search" value="Search" onclick="fn_search();"></td>
			<td width="20px">&nbsp;
			<td width="150px">&nbsp; <input id="interval" type="checkbox" value="Y" checked="checked" style="color: black">자동 조회
			</td>
			<td><div id="countdown"></div></td>
			<td width=100px align="right"><input type="button" id="startBatch" value="배치" onclick="fn_startBatch();"></td>
			<td width=100px align="right"><input type="button" size="12" id="logout" value="로그아웃" onclick="fn_logOut();"></td>
		</tr>
		
		<tr>
			<td colspan=6>&nbsp;</td>
		</tr>
	</table>

	<div id="tabs">
		<ul style="height: 40px">
			<li><a id="li-1" href="#tabs-1" style="font-size: 15px">Alarm</a></li>
			<li><a id="li-2" href="#tabs-2" style="font-size: 15px">PANTHEON</a></li>
		</ul>
		<div id="tabs-1">
			<table id="Alarm" ></table>
		</div>
		<div id="tabs-2">
			<table id="PANTHEON" ></table>
		</div>
	</div>
</body>
</html>