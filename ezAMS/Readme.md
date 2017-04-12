# ezAMS
Server Monitoring tools

## 개요
● ezAMS는 원격 호스트의 정보를 수집, 알람기준에 도달할 경우, 알람이 발생한 서버의 정보를 보여주는 Web Application 입니다.

![Summary1](https://github.com/rlgus357/ezAMS/blob/master/ezAMS/src/main/webapp/css/image/ezAMS%20architecture.PNG)
![Summary2](https://github.com/rlgus357/ezAMS/blob/master/ezAMS/src/main/webapp/css/image/ezAMS.PNG)

● 수집하는 정보
 - hostName
 - Cpu Usage
 - Memory Usage
 - Process Count
 - Disk Status
 - Last bootUp time
 - Ping Status
 - Event ( Process Error Report, Failover Check ) 

● Global Server Standard
~~~
<?xml version="1.0" encoding="UTF-8"?>
<configs>
	<server>
		<configPath>/config/remoteServer_P_info.xml</configPath>
		<fileDB>C:\Users\lkh\git\ezAMS\ezAMS\src\main\resources\fileDB\</fileDB>
		<remoteIp>12.54.13.159</remoteIp>
		<threadCnt>10</threadCnt>
		<threadTimeout>1000</threadTimeout> <!--단위는 초단위 -->
		<diskUseAmountOfSpot>95</diskUseAmountOfSpot>
		<diskUseAmountOfMarsprimeDB>95</diskUseAmountOfMarsprimeDB>
		<diskUseAmountOfMarsprimeAP>95</diskUseAmountOfMarsprimeAP>
		<diskUseAmountOfPantheon>60</diskUseAmountOfPantheon>
		<diskUseAmountOfSequs>95</diskUseAmountOfSequs>
		<taskSlowBackUpPath>C:\Windows\System32\task\</taskSlowBackUpPath>
		<slowBackUpPath>D:\ProgramData\MySQL Server 5.6\data\</slowBackUpPath>
		<processDownTimeout>60</processDownTimeout>
		<rebootTimeout>30</rebootTimeout>
		<failoverTimeout>60</failoverTimeout>
	</server>
</configs>
~~~
● Each Server Standard
~~~
<?xml version="1.0" encoding="UTF-8"?>
<RemoteServer>
	<server category="1">
		<toolName>PANTHEON</toolName>
		<groupName>LocalHost</groupName>
		<serverIp>LocalHost</serverIp>
		<serverId>administrator</serverId>
		<serverPassWd>01000000d08c9ddf0115d1118c7a00c04fc297eb01000000b30db682c4213742a048ef23abca3ba50000000002000000000010660000000100002000000060117868cfe2be916b540fc4bc503cd1fe7db0e251ef880818988baa0ca81fa9000000000e800000000200002000000085066e9a9113cc9b2bea904baa1256b9f94920eba28930fde06047b9a6ad87821000000002ebbcb6f86dc99a95ea573e6bec826e4000000005abe3acf721158b9617f6e99e3b3c38696dc40967bc658b065b881277d93ba92e666e76d8fdd8895c37e965601cf0f892ed78719206a52351caf1eca7818c1d</serverPassWd>
		<serverCheckInfo>Ip⊥HostName⊥Cpu⊥Memory⊥Disk</serverCheckInfo>
		<runProcess>KakaoTalk⊥whale</runProcess>
		<runProcessCnt>2⊥2</runProcessCnt>
		<failoverConfigFlag>N</failoverConfigFlag>
		<localDiskCnt>2</localDiskCnt>
		<networkDiskCnt>0</networkDiskCnt>
	</server>
</RemoteServer>

<RemoteServer>
	<server category="2">
		<toolName>PANTHEON</toolName>
		<groupName>RemoteHost</groupName>
		<serverIp>RemoteHost</serverIp>
		<serverId>administrator</serverId>
		<serverPassWd>01000000d08c9ddf0115d1118c7a00c04fc297eb01000000b30db682c4213742a048ef23abca3ba50000000002000000000010660000000100002000000060117868cfe2be916b540fc4bc503cd1fe7db0e251ef880818988baa0ca81fa9000000000e800000000200002000000085066e9a9113cc9b2bea904baa1256b9f94920eba28930fde06047b9a6ad87821000000002ebbcb6f86dc99a95ea573e6bec826e4000000005abe3acf721158b9617f6e99e3b3c38696dc40967bc658b065b881277d93ba92e666e76d8fdd8895c37e965601cf0f892ed78719206a52351caf1eca7818c1d</serverPassWd>
		<serverCheckInfo>Ip⊥HostName⊥Cpu⊥Memory⊥Disk</serverCheckInfo>
		<runProcess>RemoteServerProcess1⊥RemoteServerProcess2</runProcess>
		<runProcessCnt>1⊥2</runProcessCnt>
		<failoverConfigFlag>N</failoverConfigFlag>
		<localDiskCnt>2</localDiskCnt>
		<networkDiskCnt>0</networkDiskCnt>
	</server>
</RemoteServer>


~~~