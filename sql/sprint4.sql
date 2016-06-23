sp_rename LogDCM, RPLog;
sp_rename RedTag, RPRedTag;
sp_rename PlacementRed RPPlacementRed;
sp_rename CampaignDCM RPCampaign;

ALTER TABLE RPLog
ADD Adserver int;

ALTER TABLE RPRedTag
ADD Adserver int;

ALTER TABLE RPPlacementRed
ADD Adserver int;

ALTER TABLE RPCampaign
ADD Adserver int;


ALTER TABLE RPCamDesc
DROP COLUMN Adserver;

ALTER TABLE RPCamDesc
ADD Adserver int;

update RPCampaign
set Adserver=1;
update RPPlacementRed
set Adserver=1;
update RPRedTag
set Adserver=1;
update RPLog
set Adserver=1;
update RPCamDesc
set Adserver=1;

