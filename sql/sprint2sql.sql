ALTER TABLE CampaignDCM
ADD RetrDate datetime;

alter table PlacementRed
add Status varchar(10);


update PlacementRed
set Status='VALID'
where Status is null;