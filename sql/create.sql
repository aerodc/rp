create table PlacementRed
(
ID int not null IDENTITY,
CampaignId int not null,
PlacementID int not null PRIMARY KEY,
PlacementName varchar(255) NOT NULL,
PlacementSize varchar(255) NOT NULL,
SiteName varchar(255) NOT NULL,
SiteId int,
DateBegin date,
DateEnd date,
CreateDate datetime DEFAULT(getdate()),
LastModDate datetime
);

create table RedTag
(
ID int not null IDENTITY PRIMARY KEY,
PlacementID int not null,
TagType varchar(255) NOT NULL,
TagValue varchar(2000),
Status varchar(255),
CreateDate datetime DEFAULT(getdate()),
LastModDate datetime,
TagDescription varchar(2000),
);

create table TMToken
(
ID int not null IDENTITY PRIMARY KEY,
CreateDate datetime DEFAULT(getdate()),
Token varchar(255) NOT NULL,
Expired int NOT NULL
);

create table CampaignDCM
(
IDDB int not null IDENTITY,
CamID int not null PRIMARY KEY,
CamName varchar(255) NOT NULL,
AdvertiserId int not null,
NetworkId int not null,
CreateDate datetime DEFAULT(getdate()),
LastModDate datetime,
BeginDate date,
EndDate date
);

create table LogDCM
(
ID int not null IDENTITY PRIMARY KEY,
CampaignId int not null,
Site varchar(255) NOT NULL,
PlacementID int not null,
PlacementName varchar(255) NOT NULL,
Status varchar(255),
LastModDate datetime DEFAULT(getdate()),
ModifyByName varchar(255),
--CreateDate datetime DEFAULT(getdate()),
tagType varchar(255),
tagValue varchar(2000)
);

alter table Network
add profileId int;

update Network set profileId=1096590   where resId=884;
update Network set profileId=1096597   where resId=1238;
update Network set profileId=1096577   where resId=1757;
update Network set profileId=1096569   where resId=2465;
update Network set profileId=1096589   where resId=3814;


update Network set profileId=1096591   where resId=4021;
update Network set profileId=1096592   where resId=4022;
update Network set profileId=1096567   where resId=5030;
update Network set profileId=1096594   where resId=5074;
update Network set profileId=1096599   where resId=5359;


update Network set profileId=1096595   where resId=5716;
update Network set profileId=1096598   where resId=6374;
update Network set profileId=1096583   where resId=6600;
update Network set profileId=1096586   where resId=6601;


update Network set profileId=1096587   where resId=6602;
update Network set profileId=1096588   where resId=6603;
update Network set profileId=1096572   where resId=8197;
update Network set profileId=1395101   where resId=99002;
update Network set profileId=1589855   where resId=1933;

-----V2---------
create table RPCamDesc
(
	ID int not null IDENTITY PRIMARY KEY,
	CamID int not null,
	AdServer varchar(255) NOT NULL,
	Desc1 varchar(2000),
	CreateDate datetime DEFAULT(getdate()),
	LastModDate datetime, 
);


