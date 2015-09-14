/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.1.66-log : Database - condor_lbs
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `black_list` */

DROP TABLE IF EXISTS `black_list`;

CREATE TABLE `black_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pid` varchar(25) NOT NULL,
  `tid` varchar(25) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`),
  KEY `tid` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `bottle` */

DROP TABLE IF EXISTS `bottle`;

CREATE TABLE `bottle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `sender` varchar(25) NOT NULL COMMENT '发送者uid',
  `receiver` varchar(25) DEFAULT NULL COMMENT '接收者uid，可以为空',
  `text` varchar(100) DEFAULT NULL COMMENT '文字内容，可以为空',
  `vid` varchar(100) DEFAULT NULL COMMENT '语音id，可以为空',
  `sendTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发送时间',
  `replyText` varchar(100) DEFAULT NULL COMMENT '回复-文字内容或语音',
  `replyTime` timestamp NULL DEFAULT NULL COMMENT '回复-时间',
  `rewardId` int(11) NOT NULL DEFAULT '-1' COMMENT '回复奖励的id，可以为空',
  `senderRewarded` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发送者是否已领奖',
  `receiverRewarded` tinyint(1) NOT NULL DEFAULT '0' COMMENT '接收者是否已领奖',
  `senderReaded` tinyint(1) NOT NULL DEFAULT '1' COMMENT '发送者是否已读',
  `receiverReaded` tinyint(1) NOT NULL DEFAULT '0' COMMENT '接收者是否已读',
  `senderDeleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发送者是否已删除',
  `receiverDeleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '接收者是否已删除',
  PRIMARY KEY (`id`),
  KEY `receiver-sendTime` (`receiver`,`sendTime`),
  KEY `sender-sendTime` (`sender`,`sendTime`)
) ENGINE=InnoDB AUTO_INCREMENT=889 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `con_test` */

DROP TABLE IF EXISTS `con_test`;

CREATE TABLE `con_test` (
  `a` char(1) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `follow_list` */

DROP TABLE IF EXISTS `follow_list`;

CREATE TABLE `follow_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `follower` varchar(25) NOT NULL,
  `followed` varchar(25) NOT NULL,
  `followTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `followedClearMsg` tinyint(1) NOT NULL DEFAULT '0' COMMENT '被关注者是否清理掉该消息',
  `readMsg` tinyint(4) DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`),
  KEY `followed` (`followed`),
  KEY `follower` (`follower`)
) ENGINE=InnoDB AUTO_INCREMENT=399 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `game_chat` */

DROP TABLE IF EXISTS `game_chat`;

CREATE TABLE `game_chat` (
  `chatId` varchar(50) NOT NULL COMMENT '会话id',
  `chater1` int(11) NOT NULL COMMENT '私聊者1',
  `chater2` int(11) NOT NULL COMMENT '私聊者2',
  `chater1ServerId` int(11) NOT NULL COMMENT '服务器id1',
  `chater2ServerId` int(11) NOT NULL COMMENT '服务器id2',
  `lastMessage` varchar(100) NOT NULL COMMENT '最后一条聊天内容',
  `lastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一条聊天时间戳',
  `unReadNumChater1` int(11) NOT NULL DEFAULT '0' COMMENT 'chater1作为接收者未读数目',
  `unReadNumChater2` int(11) NOT NULL DEFAULT '0' COMMENT 'chater2作为接收者未读数目',
  `readedChater1` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'chater1已读的时间戳',
  `readedChater2` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'chater2已读的时间戳',
  `chater1State` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'chater1状态,0为正常状态,1为删除状态',
  `chater2State` tinyint(4) NOT NULL DEFAULT '0' COMMENT '同上',
  `delChater1` timestamp NULL DEFAULT NULL COMMENT 'chater1删除时间',
  `delChater2` timestamp NULL DEFAULT NULL COMMENT 'chater2删除时间',
  `chater1Id` varchar(25) NOT NULL COMMENT '仅仅用来索引',
  `chater2Id` varchar(25) NOT NULL COMMENT '仅仅用来索引',
  `lastReadedChater1` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastReadedChater2` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`chatId`),
  KEY `chater1Id` (`chater1Id`),
  KEY `chater2Id` (`chater2Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `game_chat_content` */

DROP TABLE IF EXISTS `game_chat_content`;

CREATE TABLE `game_chat_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NOT NULL COMMENT '发送者Id',
  `senderServerId` int(11) NOT NULL COMMENT '发送者服务器Id',
  `receiver` int(11) NOT NULL COMMENT '接收者Id',
  `receiverServerId` int(11) NOT NULL COMMENT '接收者服务器Id',
  `chatId` varchar(50) NOT NULL COMMENT '聊天id',
  `senderStatus` tinyint(6) NOT NULL COMMENT '发送者状态',
  `receiverStatus` tinyint(6) NOT NULL COMMENT '接收者状态',
  `content` varchar(100) DEFAULT NULL COMMENT '聊天内容',
  `sendTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `chatId` (`chatId`),
  CONSTRAINT `FK_game_chat_content` FOREIGN KEY (`chatId`) REFERENCES `game_chat` (`chatId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `player_info` */

DROP TABLE IF EXISTS `player_info`;

CREATE TABLE `player_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(25) NOT NULL COMMENT '服务器id和玩家id的组合',
  `playerId` int(11) NOT NULL COMMENT '玩家id',
  `serverId` int(11) NOT NULL COMMENT '服务器id',
  `name` varchar(11) NOT NULL COMMENT '玩家名',
  `serverName` varchar(20) NOT NULL COMMENT '服务器名',
  `jsonData` varchar(2000) NOT NULL COMMENT '其它玩家数据的json串',
  `updateTS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新的时间戳',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid` (`uid`),
  KEY `name` (`name`(3))
) ENGINE=InnoDB AUTO_INCREMENT=269514 DEFAULT CHARSET=utf8;

/*Table structure for table `server_url` */

DROP TABLE IF EXISTS `server_url`;

CREATE TABLE `server_url` (
  `serverId` int(11) NOT NULL COMMENT '服务器id',
  `url` varchar(100) NOT NULL COMMENT '游戏服地址',
  `name` varchar(20) NOT NULL COMMENT '服务器名',
  PRIMARY KEY (`serverId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/* Procedure structure for procedure `select_history_chat_content` */

/*!50003 DROP PROCEDURE IF EXISTS  `select_history_chat_content` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `select_history_chat_content`(cId varchar(30), pReadedTimestamp TIMESTAMP, pDelTimestamp TIMESTAMP)
BEGIN
	 SELECT COUNT(*) INTO @num FROM game_chat_content WHERE chatId = cId AND sendTime <= pReadedTimestamp AND sendTime > DATE_SUB(CURRENT_DATE(), INTERVAL 365 DAY) AND sendTime > pDelTimestamp;
	 
	 IF @num > 50 THEN 
		SET @st = @num - 50;
		SET @chat_id = cId;
		SET @login_tt = pReadedTimestamp;
		SET @del_tt = pDelTimestamp;
		SET @stmt = CONCAT('SELECT * FROM game_chat_content WHERE chatId = ? AND sendTime <= ? AND sendTime > DATE_SUB(CURRENT_DATE(), INTERVAL 365 DAY) AND sendTime > ? LIMIT ?, 50');
		PREPARE s1 FROM @stmt;
		EXECUTE s1 USING @chat_id, @login_tt, @del_tt, @st;
		DEALLOCATE PREPARE s1;
	 ELSE 
		SELECT * FROM game_chat_content WHERE chatId = cId AND sendTime <= pReadedTimestamp AND sendTime > DATE_SUB(CURRENT_DATE(), INTERVAL 365 DAY) AND sendTime > pDelTimestamp;
	 END IF;
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
