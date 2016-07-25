# dtm-subscriber
Directtomobile Subscriber Module

v1.2.41

- Search subscriber based on list , client , and phone number

- client subscriber view add dnc information

- Add new library :
  . beepcast_database-v1.2.02.jar
  . beepcast_properties-v2.0.01.jar
  . 

v1.2.40

- Make sure message email alert content can be composed outside

- Add new library :
  . 

v1.2.39

- Create module to store list numbers into the memory with hash map type

- Add new library :
  . 

v1.2.38

- Add class to subscribe with : method = insert/update , list fields ...

- Use jdk 1.7
  
- Add new library :

  . stax-api-1.0.1.jar
  . stax-1.2.0.jar
  . jettison-1.2.jar
  . xpp3_min-1.1.4c.jar
  . jdom-1.1.3.jar
  . dom4j-1.6.1.jar
  . xom-1.1.jar
  . cglib-nodep-2.2.jar
  . xmlpull-1.1.3.1.jar
  . xstream-1.4.8.jar

  . commons-logging-1.2.jar
  . commons-pool2-2.4.2.jar
  . commons-dbcp2-2.1.1.jar
  . mysql-connector-java-5.1.35-bin.jar
  . beepcast_database-v1.2.00.jar

v1.2.37

- Support list name with include and exclude keywords

- Migrate all the existing projects to use IDE MyEclipse Pro 2014
  . Build as java application ( not as web project )

- Add new library :
  . 

v1.2.36

- delete client subsriber based on id

- Add new library :
  . beepcast_dbmanager-v1.1.35.jar
  . 

v1.2.35

- Add feature to clean all the duplicated records in the 
  unsubs , invalid , and dnc table

- There is a file configuration changed in ./conf/subscriber.xml

      <synchSubscriberGroupReport enabled="true" limitRecords="2" 
        sleepPerRecordInSeconds="5" sleepPerBatchInSeconds="10" 
        sleepInitializedInSeconds="30" lastDays="365" /> 
      
      <cleanClientSubscriberUnsubsRecord enabled="true" 
        limitRecords="100" sleepInitializedInSeconds="30" /> 
      <cleanClientSubscriberInvalidRecord enabled="true" 
        limitRecords="100" sleepInitializedInSeconds="30" /> 
      <cleanClientSubscriberDncRecord enabled="true" 
        limitRecords="100" sleepInitializedInSeconds="30" /> 

- Add new library :
  . beepcast_dbmanager-v1.1.34.jar
  . beepcast_encrypt-v1.0.04.jar
  . beepcast_onm-v1.2.08.jar

v1.2.34

- Remove wild cards character for file name report

    // clean characters
    sgName = sgName.trim();
    sgName = sgName.replaceAll( "[^a-zA-Z0-9]" , "_" );

- Add new library :
  . 

v1.2.33

- Change in the contact list file name format

    Contact List:  list-name_of_list-all-YYYMMDD.HHMMSS-ZXCVB.zip
    Invalid Numbers (global): invalid-numbers-YYYMMDD.HHMMSS-ZXCVB.zip
    Unsubscried Numbers (global): unsubscribed-numbers-YYYMMDD.HHMMSS-ZXCVB.zip
    DNC Numbers (global): dnc-numbers-YYYMMDD.HHMMSS-ZXCVB.zip

- Clean out all the data from plain custom field 

- Found can't insert custom field with quote character inside 

    {HttpProcessor[8080][3]}ClientSubscriberService [Client-1] [List-136] [Subscriber-+6500000032] Successfully inserted client subscriber profile : custRefId =  , custRefCode =  , dateSend = null , description = 
    {HttpProcessor[8080][3]}DatabaseEngine [profiledb] Database execute query failed , com.mysql.jdbc.exceptions.MySQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'CO2_00000032','GjB6iLc4FGJCfkywXJfY'),'',AES_ENCRYPT('CO3_00000032','GjB6iLc4FGJ' at line 1
    {HttpProcessor[8080][3]}ClientSubscriberService [Client-1] [List-136] [Subscriber-+6500000032] Failed to insert client subscriber custom bean

- Add new library :
  . 

v1.2.32

- Found bug can't download contact list with un/subscribed and rsvp

    ClientSubscriberFileService [List-134] Generating file list of subscribed numbers with fields : [PhoneNumber, DateCreated, DateModified, SubscribedStatus, UnsubscribedDate, GlobalSubscribedStatus, GlobalUnsubscribedDate, UnsubscribedEventName, GlobalValidStatus, GlobalInvalidDate, GlobalInvalidEventName, NotDncRegisteredStatus, DncRegisteredDate, Description, FirstName, FamilyName, Email, ID, CustomerReferenceId, CustomerReferenceCode, DateSend, Custom1, Custom2, Custom3, Custom4, Custom5, Custom6, Custom7, Custom8, Custom9, Custom10, Custom11, Custom12, Custom13, Custom14, Custom15, Custom16, Custom17, Custom18, Custom19, Custom20]
    ClientSubscriberFileService [List-134] Preparing to create text file = C:\benny.iskandar\program\apache-tomcat-4.0.3\webapps\beepadmin\download\SG_100_A-subscribed-20140516111857.csv
    ClientSubscriberFileService [List-134] [profiledb] Database query failed , com.mysql.jdbc.exceptions.MySQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'AND ( ( cs.active = 1 ) AND ( cs.global_invalid = 0 ) AND ( cs.subscribed = 1 ) ' at line 1
    ClientSubscriberFileService [List-134] Successfully created file of client subscribers : name = C:\benny.iskandar\program\apache-tomcat-4.0.3\webapps\beepadmin\download\SG_100_A-subscribed-20140516111857.zip , total = 0 record(s) , take = 1470 ms

- Add feature to override the custom list during append the new contact list

- Add new library :
  . 

v1.2.31

- Encrypt all custom fields of contact list

- Execute sql below 

    ALTER TABLE `client_subscriber_custom` 
      ADD COLUMN `encrypt_custom_0`  VARBINARY(512) AFTER `custom_0` ,
      ADD COLUMN `encrypt_custom_1`  VARBINARY(512) AFTER `custom_1` ,
      ADD COLUMN `encrypt_custom_2`  VARBINARY(512) AFTER `custom_2` ,
      ADD COLUMN `encrypt_custom_3`  VARBINARY(512) AFTER `custom_3` ,
      ADD COLUMN `encrypt_custom_4`  VARBINARY(512) AFTER `custom_4` ,
      ADD COLUMN `encrypt_custom_5`  VARBINARY(512) AFTER `custom_5` ,
      ADD COLUMN `encrypt_custom_6`  VARBINARY(512) AFTER `custom_6` ,
      ADD COLUMN `encrypt_custom_7`  VARBINARY(512) AFTER `custom_7` ,
      ADD COLUMN `encrypt_custom_8`  VARBINARY(512) AFTER `custom_8` ,
      ADD COLUMN `encrypt_custom_9`  VARBINARY(512) AFTER `custom_9` ,
      ADD COLUMN `encrypt_custom_10` VARBINARY(512) AFTER `custom_10` ,
      ADD COLUMN `encrypt_custom_11` VARBINARY(512) AFTER `custom_11` ,
      ADD COLUMN `encrypt_custom_12` VARBINARY(512) AFTER `custom_12` ,
      ADD COLUMN `encrypt_custom_13` VARBINARY(512) AFTER `custom_13` ,
      ADD COLUMN `encrypt_custom_14` VARBINARY(512) AFTER `custom_14` ,
      ADD COLUMN `encrypt_custom_15` VARBINARY(512) AFTER `custom_15` ,
      ADD COLUMN `encrypt_custom_16` VARBINARY(512) AFTER `custom_16` ,
      ADD COLUMN `encrypt_custom_17` VARBINARY(512) AFTER `custom_17` ,
      ADD COLUMN `encrypt_custom_18` VARBINARY(512) AFTER `custom_18` ,
      ADD COLUMN `encrypt_custom_19` VARBINARY(512) AFTER `custom_19` ,
      ADD COLUMN `encrypt_custom_20` VARBINARY(512) AFTER `custom_20` ,
      ADD COLUMN `encrypt_custom_21` VARBINARY(512) AFTER `custom_21` ,
      ADD COLUMN `encrypt_custom_22` VARBINARY(512) AFTER `custom_22` ,
      ADD COLUMN `encrypt_custom_23` VARBINARY(512) AFTER `custom_23` ,
      ADD COLUMN `encrypt_custom_24` VARBINARY(512) AFTER `custom_24` ,
      ADD COLUMN `encrypt_custom_25` VARBINARY(512) AFTER `custom_25` ,
      ADD COLUMN `encrypt_custom_26` VARBINARY(512) AFTER `custom_26` ,
      ADD COLUMN `encrypt_custom_27` VARBINARY(512) AFTER `custom_27` ,
      ADD COLUMN `encrypt_custom_28` VARBINARY(512) AFTER `custom_28` ,
      ADD COLUMN `encrypt_custom_29` VARBINARY(512) AFTER `custom_29` ,
      ADD COLUMN `encrypt_custom_30` VARBINARY(512) AFTER `custom_30` ;

    UPDATE client_subscriber_custom SET 
       encrypt_custom_0 = AES_ENCRYPT(  custom_0 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_1 = AES_ENCRYPT(  custom_1 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_2 = AES_ENCRYPT(  custom_2 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_3 = AES_ENCRYPT(  custom_3 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_4 = AES_ENCRYPT(  custom_4 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_5 = AES_ENCRYPT(  custom_5 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_6 = AES_ENCRYPT(  custom_6 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_7 = AES_ENCRYPT(  custom_7 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_8 = AES_ENCRYPT(  custom_8 , 'GjB6iLc4FGJCfkywXJfY' ) , 
       encrypt_custom_9 = AES_ENCRYPT(  custom_9 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_10 = AES_ENCRYPT( custom_10 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_11 = AES_ENCRYPT( custom_11 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_12 = AES_ENCRYPT( custom_12 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_13 = AES_ENCRYPT( custom_13 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_14 = AES_ENCRYPT( custom_14 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_15 = AES_ENCRYPT( custom_15 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_16 = AES_ENCRYPT( custom_16 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_17 = AES_ENCRYPT( custom_17 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_18 = AES_ENCRYPT( custom_18 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_19 = AES_ENCRYPT( custom_19 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_20 = AES_ENCRYPT( custom_20 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_21 = AES_ENCRYPT( custom_21 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_22 = AES_ENCRYPT( custom_22 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_23 = AES_ENCRYPT( custom_23 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_24 = AES_ENCRYPT( custom_24 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_25 = AES_ENCRYPT( custom_25 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_26 = AES_ENCRYPT( custom_26 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_27 = AES_ENCRYPT( custom_27 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_28 = AES_ENCRYPT( custom_28 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_29 = AES_ENCRYPT( custom_29 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      encrypt_custom_30 = AES_ENCRYPT( custom_30 , 'GjB6iLc4FGJCfkywXJfY' ) , 
      date_updated = NOW() ;
      
- Add new library :
  . beepcast_encrypt-v1.0.03.jar

v1.2.30

- Add new "DateModified" field in the contact list download file

- Execute sql below

    ALTER TABLE `client_subscriber_custom` 
      ADD COLUMN `date_inserted` DATETIME AFTER `custom_30` ,
      ADD COLUMN `date_updated` DATETIME AFTER `date_inserted` ;

- Add new library :
  . 

v1.2.29

- Found bugs :

    WebSendMsgUtil Created mapCustomFields from httpRequest : strFieldNames = Mobile;Email;Custom1;Custom2;Custom10;Custom15;Custom20; , firstName = null , familyName = null , phoneNumber = 6598394294 , emailAddress = benny@beepcast.com , id = null , custRefId = null , custRefCode = null , custom1 = benny@beepcast.com , custom2 = Male , custom3 = null , custom4 = null , custom5 = null
    WebSendMsgUtil Created mapCustomFields ( size = 7 ) : {Custom10=Jalan Membina #03-139, Email=benny@beepcast.com, Mobile=6598394294, Custom1=benny@beepcast.com, Custom20=Singapore, Custom15=Singapore, Custom2=Male}
    MsgLinkApp Submitting message link : sessionKey = de28cb7377c520adeafb2b25a7e2389b , refPhoneNumber = 6598394294 , refEmailAddress = null , refEventId = 1 , xipmeCode = Q0cmw , visitId = QBmh4 , mapCustomFields = {Custom10=Jalan Membina #03-139, Email=benny@beepcast.com, Mobile=6598394294, Custom1=benny@beepcast.com, Custom20=Singapore, Custom15=Singapore, Custom2=Male} , userAgent = Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0
    SessionService Found message link id inside session text , with value = 18
    MessageLinkDAO Perform SELECT id , client_id , event_id , event_code , subscriber_group_id , auto_populate_field , session_key , url_link , hit_limit , captcha , description , display , active , date_expired FROM message_link WHERE ( id = 18 ) 
    SessionService Found session bean = MessageLinkBean ( id = 18 clientId = 1 eventId = 0 eventCode = null subscriberGroupId = 99 autoPopulateField = true sessionKey = de28cb7377c520adeafb2b25a7e2389b urlLink = http://apus:8883/LTu3 hitLimit = 0 captcha = false description = Test Incoming Basic Event 01 display = true active = true dateExpired = 2016-03-12 23:59:59  )
    MessageLinkTransactionService [MsgLnk-18] [User-+6598394294] Submitting message link : refPhoneNumber = +6598394294 , refEmailAddress =  , refEventId = 1 , xipmeCode = Q0cmw , visitId = QBmh4 , mapCustomFields = {Custom10=Jalan Membina #03-139, Email=benny@beepcast.com, Mobile=6598394294, Custom1=benny@beepcast.com, Custom20=Singapore, Custom15=Singapore, Custom2=Male} , userAgent = Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0
    MessageLinkTransactionService [MsgLnk-18] [User-+6598394294] Updating mobile user email = benny@beepcast.com -> benny@beepcast.com
    MessageLinkTransactionService [MsgLnk-18] [User-+6598394294] Successfully updated mobile user profile : phoneNumber = +6598394294
    MessageLinkTransactionService [MsgLnk-18] [User-+6598394294] Extracted array custom fields : length = 31 , values = [null, benny@beepcast.com, Male, null, null, null, null, null, null, null, Jalan Membina #03-139, null, null, null, null, Singapore, null, null, null, null, Singapore, null, null, null, null, null, null, null, null, null, null]
    ClientSubscriberService [Client-1] [List-99] [Subscriber-+6598394294] Successfully updated active subscribed status field in the client subscriber table 
    ClientSubscriberService [Client-1] [List-99] [Subscriber-+6598394294] Subscriber is cleaned from the global unsubs table
    ClientSubscriberService [Client-1] [List-99] [Subscriber-+6598394294] Successfully perform subscribed
    SubscriberApp Successfully performed subscribed with : id = 1234068 , clientId = 1 , subscriberGroupId = 99 , phoneNumber = +6598394294 , customBeans = ClientSubscriberCustomBean ( id = 1134035 clientSubscriberId = 1234068 custom0 = null custom1 = benny@beepcast.com custom2 = Male  )
    SubscriberApp Trying to update new costumFields = [null, benny@beepcast.com, Male, null, null, null, null, null, null, null, Jalan Membina #03-139, null, null, null, null, Singapore, null, null, null, null, Singapore, null, null, null, null, null, null, null, null, null, null]
    SubscriberApp Trying to update new custom bean = ClientSubscriberCustomBean ( id = 1134035 clientSubscriberId = 1234068 custom0 = null custom1 = benny@beepcast.com custom2 = Male  )
    BUG >> ClientSubscriberCustomDAO Perform UPDATE client_subscriber_custom SET custom_1 = 'benny@beepcast.com' , custom_2 = 'Male' , custom_3 = ' CCC' , custom_10 = 'Jalan Membina #03-139' WHERE ( id = 1134035 ) 
    SubscriberApp Successfully updated custom bean


- Add new library :
  . 

v1.2.28

- Execute sql below 
 
    ALTER TABLE `client_subscriber_custom` 
      ADD COLUMN `custom_16` VARCHAR(256) AFTER `custom_15` ,
      ADD COLUMN `custom_17` VARCHAR(256) AFTER `custom_16` ,
      ADD COLUMN `custom_18` VARCHAR(256) AFTER `custom_17` ,
      ADD COLUMN `custom_19` VARCHAR(256) AFTER `custom_18` ,
      ADD COLUMN `custom_20` VARCHAR(256) AFTER `custom_19` ,
      ADD COLUMN `custom_21` VARCHAR(256) AFTER `custom_20` ,
      ADD COLUMN `custom_22` VARCHAR(256) AFTER `custom_21` ,
      ADD COLUMN `custom_23` VARCHAR(256) AFTER `custom_22` ,
      ADD COLUMN `custom_24` VARCHAR(256) AFTER `custom_23` ,
      ADD COLUMN `custom_25` VARCHAR(256) AFTER `custom_24` ,
      ADD COLUMN `custom_26` VARCHAR(256) AFTER `custom_25` ,
      ADD COLUMN `custom_27` VARCHAR(256) AFTER `custom_26` ,
      ADD COLUMN `custom_28` VARCHAR(256) AFTER `custom_27` ,
      ADD COLUMN `custom_29` VARCHAR(256) AFTER `custom_28` ,
      ADD COLUMN `custom_30` VARCHAR(256) AFTER `custom_29` ;

- Add to support 30 custom fields in the contact list

- Add new library :
  . beepcast_onm-v1.2.07.jar
  . 

v1.2.27

- Found bugs , it doesn't insert when found blank string in the custom fields

  SubscriberApp Successfully performed subscribed with : id = 1234068 , clientId = 1 , subscriberGroupId = 99 , phoneNumber = +6598394294 
    , customBeans = ClientSubscriberCustomBean ( id = 1134035 clientSubscriberId = 1234068 custom0 =  custom1 = Custom1 custom2 = Custom2 custom3 =  CCC custom4 =  custom5 =  custom6 =  custom7 =  custom8 =  custom9 =  custom10 =  custom11 =  custom12 =  custom13 =  custom14 =  custom15 =   )
  SubscriberApp Trying to update new costumFields = [null, Custom1, Custom2,  BBB, , null, null, null, null, null, null, null, null, null, null]
  SubscriberApp Trying to update new custom bean = ClientSubscriberCustomBean ( id = 1134035 clientSubscriberId = 1234068 custom0 =  custom1 = Custom1 custom2 = Custom2 custom3 =  BBB custom4 =  custom5 =  custom6 =  custom7 =  custom8 =  custom9 =  custom10 =  custom11 =  custom12 =  custom13 =  custom14 =  custom15 =   )
  ClientSubscriberCustomDAO Perform UPDATE client_subscriber_custom SET custom_0 = '' , custom_1 = 'Custom1' , custom_2 = 'Custom2' , custom_3 = ' BBB' , custom_4 = '' , custom_5 = '' , custom_6 = '' , custom_7 = '' , custom_8 = '' , custom_9 = '' , custom_10 = '' , custom_11 = '' , custom_12 = '' , custom_13 = '' , custom_14 = '' , custom_15 = '' WHERE ( id = 1134035 ) 
  SubscriberApp Successfully updated custom bean

- Found bugs , it will insert client subscriber with the same phone number :

  {HttpProcessor[9080][7]}MessageLinkTransactionService Successfully updated mobile user profile
  {HttpProcessor[9080][7]}MessageLinkTransactionService [MsgLnk-6] [User-+6598394294] Trying to subscribe to list
  {HttpProcessor[9080][7]}MessageLinkTransactionService [MsgLnk-6] [User-+6598394294] Extracted array custom fields : length = 15 , values = [null, null, null, AAA, null, null, null, null, null, null, null, null, null, null, null]
  {HttpProcessor[9080][7]}ClientSubscriberService [Client-1] [List-38] [Subscriber-+6598394294] Found phoneNumber +6598394294 is not in the list , trying to insert the new one
  {HttpProcessor[9080][7]}ClientSubscriberService [Client-1] [List-38] [Subscriber-+6598394294] Successfully updated active subscribed status field in the client subscriber table

- When upload a contact list , also validate the dnc global list .
  and update into the contact list's global_dnc field

- Add feature DNC Contact List , with details list below :
  . Add function to upload and download the list
  . Encrypt and decrypt phone like global unsubs list
  . Have their own table ( dnc contact list )
  . The number of list segmented with the client profile

- Execute sql below :

  ALTER TABLE `client_subscriber_dnc`
    DROP INDEX `synch_no` ,
    ADD INDEX `client_id_synch_no`(`client_id`, `synch_no`) ;

  ALTER TABLE `client_subscriber_dnc` 
    ADD INDEX `synch_no`(`synch_no`) ;

  ALTER TABLE `client_subscriber_dnc` 
    ADD COLUMN `synch_no` INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER `encrypt_phone` ;

  ALTER TABLE `client_subscriber` 
    ADD COLUMN `global_dnc` BOOLEAN NOT NULL DEFAULT 0 AFTER `date_global_invalid` ,
    ADD COLUMN `date_global_dnc` DATETIME AFTER `global_dnc` ;

  ALTER TABLE `client_subscriber_dnc` 
    ADD COLUMN `type` VARCHAR(45) AFTER `encrypt_phone` ;

  CREATE TABLE  `client_subscriber_dnc` (
    `id` int(10) unsigned NOT NULL auto_increment,
    `client_id` int(10) unsigned NOT NULL default '0',
    `phone` varchar(256) collate utf8_unicode_ci NOT NULL,
    `encrypt_phone` varbinary(512) default NULL,
    `active` int(10) unsigned NOT NULL default '0',
    `description` varchar(256) collate utf8_unicode_ci default NULL,
    `date_inserted` datetime NOT NULL,
    `date_updated` datetime NOT NULL,
    PRIMARY KEY  (`id`),
    KEY `client_id_phone` (`client_id`,`phone`(255)),
    KEY `client_id_encrypt_phone` (`client_id`,`encrypt_phone`)
  ) ENGINE=InnoDB ;  
  
- Add new library :
  . 

v1.2.26

- Because the mobile_user encrypt all the following fields :
    encrypt_phone , encrypt_ic , encrypt_last_name , encrypt_name , encrypt_email 

  When download list , must include about fields ...
  
- Add new library :
  . beepcast_dbmanager-v1.1.31.jar
  . beepcast_onm-v1.2.06.jar
  . beepcast_encrypt-v1.0.02.jar
    
v1.2.25

- Add "DateCreated" field in the download contact list file

- Add new library :
  . 

v1.2.24

- Contact List dropdown menu with NONE in the top item , unlimit , and order by alphabetic sort

- Add new library :
  . 

v1.2.23

- Provide client subscriber and client costum subscriber function to update profile

- Add new library :
  . 

v1.2.22

- Fix bug to subscriber a list from landing page with custom fields

    26.07.2013 10.41.58:752 2568 DEBUG   {HttpProcessor[8080][7]}SubscriberApp Successfully performed subscribed with : id = 211101 , clientId = 1 , subscriberGroupId = 53 , phoneNumber = +6590517715 . 
    Trying to update costumFields = [null, Benny, Iskandar, Fariz, Iskandar, Olivia, Iskandar, Sriwaty, Iskandar, Rahadian, Iskandar, null, null, null, null]
    26.07.2013 10.41.58:753 2569 DEBUG   {HttpProcessor[8080][7]}SubscriberApp Trying to update new custom bean = ClientSubscriberCustomBean ( id = 211101 clientSubscriberId = 211101 
    custom0 =  custom1 = Benny custom2 = Iskandar custom3 = Fariz custom4 = Iskandar custom5 =   )
    26.07.2013 10.41.58:753 2570 WARNING {HttpProcessor[8080][7]}SubscriberApp Failed to update custom bean

- Add new library :
  . beepcast_dbmanager-v1.1.30.jar

v1.2.21

- Update unsubscribedEventName from subscribedAppId field when found it as empty

- Fix bug query error on global unsubs download file

    ClientSubscriberUnsubsFileService [profiledb] Database query failed , 
    com.mysql.jdbc.exceptions.MySQLSyntaxErrorException: Unknown column 'cs.encrypt_phone' in 'field list'

- Add new library :
  . beepcast_dbmanager-v1.1.29.jar
  . beepcast_onm-v1.2.02.jar

v1.2.20

- Found duplicated numbers when download the subscriber list , fixed by put "AND ( csu.active = 1 )" inside the below sql

    SELECT AES_DECRYPT(cs.encrypt_phone,'jkiZmu0s575xFbgGFkdQ') AS phone  , cs.subscribed , e1.event_name AS event1_name
      , cs.date_subscribed , cs.global_subscribed , cs.date_global_subscribed , cs.cust_ref_id , cs.cust_ref_code , cs.date_send , cs.description
      , cs.global_invalid , cs.date_global_invalid , e2.event_name AS event2_name , csc.custom_0 , csc.custom_1 , csc.custom_2 , csc.custom_3
      , csc.custom_4 , csc.custom_5 , csc.custom_6 , csc.custom_7 , csc.custom_8 , csc.custom_9 , csc.custom_10 , csc.custom_11 , csc.custom_12
      , csc.custom_13 , csc.custom_14 , csc.custom_15 , mu.name , mu.last_name , mu.email , mu.nationality , mu.ic , mu.gender
    FROM client_subscriber cs
      LEFT OUTER JOIN client_subscriber_unsubs csu ON ( csu.client_id = cs.client_id ) AND ( csu.encrypt_phone = cs.encrypt_phone ) AND ( csu.active = 1 )
      LEFT OUTER JOIN client_subscriber_custom csc ON cs.id = csc.client_subscriber_id
      LEFT OUTER JOIN event e1 ON e1.event_id = cs.subscribed_event_id
      LEFT OUTER JOIN event e2 ON e2.event_id = csu.from_event_id
      LEFT OUTER JOIN mobile_user mu ON ( mu.client_id = cs.client_id ) AND ( mu.encrypt_phone = cs.encrypt_phone )
    WHERE ( cs.subscriber_group_id = 398 )

- Add new library :
  . beepcast_database-v1.1.05.jar
  . beepcast_encrypt-v1.0.01.jar
  . beepcast_onm-v1.1.09.jar

v1.2.19

- Add custom fields until 15

    ALTER TABLE `client_subscriber_custom` 
      ADD COLUMN `custom_10` VARCHAR(256) AFTER `custom_9`  ,
      ADD COLUMN `custom_11` VARCHAR(256) AFTER `custom_10` ,
      ADD COLUMN `custom_12` VARCHAR(256) AFTER `custom_11` ,
      ADD COLUMN `custom_13` VARCHAR(256) AFTER `custom_12` ,
      ADD COLUMN `custom_14` VARCHAR(256) AFTER `custom_13` ,
      ADD COLUMN `custom_15` VARCHAR(256) AFTER `custom_14` ;

- Found bug can not display the event name from where the unsubs come from , only apply for global unsubs

- Found bug take along time to generate the contact list report , fixed inside join sql

    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "LEFT OUTER JOIN client_subscriber_custom csc ON cs.id = csc.client_subscriber_id ";
    sqlFrom += "LEFT OUTER JOIN event e ON e.event_id = cs.subscribed_event_id ";
    sqlFrom += "LEFT OUTER JOIN mobile_user mu ON ( mu.client_id = cs.client_id ) AND ( mu.encrypt_phone = cs.encrypt_phone ) ";

- Add new library :
  . 

v1.2.18

- Found bug

    java.lang.NullPointerException
    	at com.beepcast.database.DatabaseLibrary.simpleQuery(DatabaseLibrary.java:339)
    	at com.beepcast.subscriber.SubscriberGroupReportDAO.totalDuplicatedNumbers(SubscriberGroupReportDAO.java:1055)
    	at com.beepcast.subscriber.SubscriberGroupReportDAO.synchReport(SubscriberGroupReportDAO.java:285)
    	at com.beepcast.subscriber.SubscriberGroupReportService.synchReport(SubscriberGroupReportService.java:70)
    	at com.beepcast.subscriber.SubscriberManagement.doSynchSubscriberGroupReport(SubscriberManagement.java:184)
    	at com.beepcast.subscriber.SubscriberManagement$SubscriberManagementThread.run(SubscriberManagement.java:264)

- Put feature track able under unsubscribed method , means when there is unsubscribed request and found no match number inside the list , 
  it will still insert into the list with unsubscribed flag enable , so it still can be tracked .

- Add new library :
  . 

v1.2.17

- Add feature to update customer reference id and code , during the subscribed method

- Build report for total scheduled number from list

- Add new date send field inside the list

    ALTER TABLE `client_subscriber` 
      ADD COLUMN `date_send` DATETIME AFTER `date_rsvp` ;

- Add new library :
  . 

v1.2.16

- Provide xipme map param field related to subscriber table

- Add new library :
  . 

v1.2.15

- Provide two report country summary for duplicated or not duplicated numbers ( in progress )

- Additional function to calculate subscribers with/out duplicated numbers

- Validate before inserting client subscriber custom fields

- Support upload with duplicated numbers

- Put additional report total duplicated numbers 

    ALTER TABLE `subscriber_group_report` 
      ADD COLUMN `total_duplicated_number` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 AFTER `total_subscribed` ;

- Add new library :
  . 

v1.2.14

- Additional ClientSubscriberFileHeader : FirstName , FamilyName , ID , Gender , Nationality

- Add new library :
  .

v1.2.13

- clientSubscriberViewService.searchByPhoneNumber must put order by the news list id first

- Add new library :
  . 

v1.2.12

- Store to global unsubscribed related from event either

- Empty download unsubscribed in particular list :

    ClientSubscriberFileService [List-219] Generating file list of unsubscribed numbers with fields : [PhoneNumber, CustomerReferenceId, CustomerReferenceCode, SubscribedStatus, UnsubscribedEventName, UnsubscribedDate, GlobalSubscribedStatus, GlobalUnsubscribedDate, GlobalValidStatus, GlobalInvalidDate, Description]
    ClientSubscriberFileService [List-219] Preparing to create text file = C:/benny.iskandar/project/beepcast/subscriber2beepcast_v1.2.x/report/report.csv
    ClientSubscriberFileService [List-219] Perform SELECT AES_DECRYPT(cs.encrypt_phone,'jkiZmu0s575xFbgGFkdQ') AS phone  , cs.subscribed , e.event_name , cs.date_subscribed , cs.global_subscribed , cs.date_global_subscribed , cs.cust_ref_id , cs.cust_ref_code , cs.description , cs.global_invalid , cs.date_global_invalid , csc.custom_0 , csc.custom_1 , csc.custom_2 , csc.custom_3 , csc.custom_4 , csc.custom_5 , csc.custom_6 , csc.custom_7 , csc.custom_8 , csc.custom_9 , mu.name , mu.email , mu.nationality , mu.ic , mu.gender FROM client_subscriber cs LEFT OUTER JOIN client_subscriber_custom csc ON cs.id = csc.client_subscriber_id LEFT OUTER JOIN event e ON e.event_id = cs.subscribed_event_id LEFT OUTER JOIN mobile_user mu ON ( mu.client_id = cs.client_id ) AND ( mu.phone = AES_DECRYPT(cs.encrypt_phone,'jkiZmu0s575xFbgGFkdQ')  ) WHERE ( cs.subscriber_group_id = 219 )  AND ( ( active = 0 ) OR ( global_invalid = 1 ) OR ( subscribed = 0 ) OR ( global_subscribed = 0 )  ) 
    ClientSubscriberFileService [List-219] [profiledb] Database query failed , com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException: Column 'active' in where clause is ambiguous
    ClientSubscriberFileService [List-219] Successfully created file of client subscribers : name = C:\benny.iskandar\project\beepcast\subscriber2beepcast_v1.2.x\report\report.zip , total = 0 record(s) , take = 281 ms

- Add new library :
  . 

v1.2.11

- Put un/subscribed from event name information under the list name 

- Change the way to calculate total unsubscribed numbers , it will calculated based on total
  unsubscribed with related to the event .

- Restructure client subscriber to put subscribed_event_id , as reference 
  the subscribed status from which event_id 
  
  ALTER TABLE `client_subscriber` 
    ADD COLUMN `subscribed_event_id` INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER `subscribed` ;

- Add new library :
  . 

v1.2.10

- Put addition information when download the contact list : email , name , and ic

- Add api function to update custom fields under the subscribed function

- Add new library :
  . 

v1.2.09

- Add new function to list all group subscriber beans

- Add new library :
  . 

v1.2.08

- Stop to store all plain phone numbers into the list ,
  list affected table :
  . client_subscriber
  . client_subscriber_unsubs
  . client_subscriber_invalid

- Add new library :
  . 

v1.2.07

- Add feature to download contact list with additional fields from mobile user table

- Add new library :
  . beepcast_dbmanager-v1.1.26.jar

v1.2.06

- Fixed the bug to put character ' inside list name :

    {HttpProcessor[8080][6]}CreateSubscriberData Upload file : fieldName = uploadFile , 
      fileName = Big n Tasty SMS - Lunch_Tea_List-to upld. sv , contentType = application/vnd.ms-excel , sizeInBytes = 450253up 
      WHERE ( active = 1 ) AND ( client_id = 41 ) AND ( group_name = '110901.1100-New Big N' Tasty-Lunch and Tea'
    {HttpProcessor[8080][6]}SubscriberGroupDAO Perform SELECT id , client_id , group_name , active FROM subscriber_group 
      WHERE ( active = 1   AND ( client_id = 41 ) AND ( group_name = '110901.1100-New Big N' Tasty-Lunch and Tea' ) ORDER BY id DESC LIMIT 1
    {HttpProcessor[8080][6]}DatabaseLibrary [profiledb] Database query failed , com.mysql.jdbc.exceptions.MySQLSyntaxErrorException: 
      You hav  an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'Tasty-Lunch and Tea' ) 
      ORDER BY id DESC LIMIT 1'  t line 1est phone =  , message =  , senderID =  , messageType =  , 
      providerName = MODEM , command = get_send_queue , clientId =  , listId =  , subscribed =  , browserAddress =

- Add new library :
  . 

v1.2.05

- Add feature to select client subscriber custom from the id

- Add feature to support additional custom fields inside the list :
  . The client shall be able to upload list with costum fields
  . The client shall be able to download the custom fields
  . The client can apply the custom field inside the message
  
- Create client subscriber custom table

    CREATE TABLE `client_subscriber_custom` (
      `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
      `client_subscriber_id` INTEGER UNSIGNED NOT NULL DEFAULT 0,
      `custom_0` VARCHAR(256),
      `custom_1` VARCHAR(256),
      `custom_2` VARCHAR(256),
      `custom_3` VARCHAR(256),
      `custom_4` VARCHAR(256),
      `custom_5` VARCHAR(256),
      `custom_6` VARCHAR(256),
      `custom_7` VARCHAR(256),
      `custom_8` VARCHAR(256),
      `custom_9` VARCHAR(256),
      PRIMARY KEY (`id`),
      INDEX `client_subscriber_id`(`client_subscriber_id`)
    )
    ENGINE = InnoDB;
  
- Add new library :
  . 

v1.2.04

- Add feature to put the earliest date unsubscribed
  in the download global unsubscribed from the file

- Add new library :
  . beepcast_dbmanager-v1.1.22.jar
  . beepcast_idgen-v1.0.00.jar
  . beepcast_onm-v1.1.06.jar

v1.2.03

- Add feature to update client subscriber group : list name 

- Add new library :
  . beepcast_dbmanager-v1.1.21.jar

v1.2.02

- Encrypt all list's number

  . Change on file ClientSubscriberDAO.java
  . Change on file ClientSubscriberFileService.java
  . Change on file ClietnSubscriberFileRsvpService.java
  . Change on file SubscriberGroupReportDAO.java
  . Change on file ClientSubscriberViewDAO.java

- Add new field on client subscriber table , with this below sql :

    ALTER TABLE `client_subscriber`
      ADD COLUMN `encrypt_phone` VARBINARY(512) AFTER `phone` ;

    UPDATE `client_subscriber`
      SET encrypt_phone = AES_ENCRYPT( phone ,  'jkiZmu0s575xFbgGFkdQ' ) ;

    ALTER TABLE `client_subscriber` 
      ADD INDEX `client_id_encrypt_phone`(`client_id` , `encrypt_phone`) ;
      
    ALTER TABLE `client_subscriber` 
      ADD INDEX `client_id_subscriber_group_id_encrypt_phone`(`client_id` , `subscriber_group_id` , `encrypt_phone`) ;

    ALTER TABLE `client_subscriber` 
      DROP INDEX `client_id_subscriber_group_id_phone`, 
      DROP INDEX `client_id_phone` ;
                  
- Add new library :
  . 

v1.2.01

- Add api to add unsubs number with any reference event id

- Add valid number status and date on search subscriber model

- Make sure no update to subscribed field when there is found invalid global number , 
  in order to make no ambigoush .
  
  . fixed on file ClientSubscriberService.insertClientSubscriberBean

- Add new library :
  . 

v1.2.00

- Will encrypt all the global invalid number

- Change on global invalid service to support read / write encrypt phone number

- Update the encrypt password

    UPDATE `client_subscriber_invalid`
      SET encrypt_phone = AES_ENCRYPT( phone ,  'jkiZmu0s575xFbgGFkdQ' ) ;

- Add encrypt_phone on global unsubs table 

    ALTER TABLE `client_subscriber_invalid`
      ADD COLUMN `encrypt_phone` VARBINARY(512) AFTER `phone` ;

- Will encrypt all the global unsubs number

- Change on global unsubs service to support read / write encrypt phone number

- Update the encrypt password

    UPDATE `client_subscriber_unsubs`
      SET encrypt_phone = AES_ENCRYPT( phone ,  'jkiZmu0s575xFbgGFkdQ' ) ;

- Add encrypt_phone on global unsubs table 

    ALTER TABLE `client_subscriber_unsubs`
      ADD COLUMN `encrypt_phone` VARBINARY(512) AFTER `phone` ;
    
- Get the latest code version from v1.1.04

- Add new library :
  . bcprov-jdk14-145.jar
  . beepcast_encrypt-v1.0.00.jar
