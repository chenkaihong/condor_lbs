# -*- coding: utf-8 -*-

import os, sys, shutil, stat, time, platform
from tools import *


"""
    初始化 目录结构
    ./__root__
        |---- project
                |---- code
                |---- external_code
    ./target
        |---- *.sql
        |---- *.war
        |---- *.log
"""
fpath, fname = os.path.split(__file__)
CONF_ROOT = os.path.join(fpath, "conf")
ROOT = os.path.join(fpath, "__root__")
PROJECT = os.path.join(ROOT, "project")
PROJECT_CODE = os.path.join(PROJECT, "code")
PROJECT_EXTERNAL_CODE = os.path.join(PROJECT, "external_code")
TARGET = os.path.join(fpath, "target")

logger = Logger(TARGET, footprintsLog="package.log")

if os.path.exists(ROOT):
    try:
        shutil.rmtree(ROOT)
    except:
        pass
makedirs(PROJECT_CODE)
makedirs(PROJECT_EXTERNAL_CODE)
makedirs(TARGET)
logger.log ("init dirs ok", footprints=[], flush=True)


@safeCd
def mysqldump (db_config, save_path, save_name=None):
    sql = "mysqldump -R --events --triggers --no-data --add-drop-table --default-character-set=UTF8 -h%s -P%s -u%s -p%s %s" % (
        db_config["host"],
        db_config["port"],
        db_config["user"],
        db_config["password"],
        db_config["dbname"],
    )

    if save_name is None:
        save_name = db_config["dbname"] + ".sql"
    if not save_name.endswith(".sql"):
        save_name += ".sql"

    cmd = sql + " > " + os.path.join(save_path, save_name)
    records = executeCommond(cmd)
    logger.log ("backup db: name=" + db_config["dbname"], footprints=records, flush=True)

    return db_config["dbname"], save_name

@safeCd
def svn_checkout (url, external=[]):
    def _checkout (url, save_path):
        makedirs (save_path)
        os.chdir(save_path)

        cmd = "svn co " + url
        records = executeCommond(cmd)
        logger.log ("svn checkout: " + url, footprints=records, flush=True)

    # checkout main code
    _checkout (url, PROJECT_CODE)

    # checkout external code
    for e_dir, e_url in external:
        save_path = os.path.join(PROJECT_EXTERNAL_CODE, "_".join([item for item in e_dir.split("/")]))
        _checkout (e_url, save_path)

    # clean svn file
    time.sleep(3)
    for i in range(18):
        try:
            clean_svn_file (PROJECT)
        except:
            time.sleep(1)

    # use external
    project_root_dir_name = os.path.basename(url)
    for e_dir, e_url in external:
        save_path = os.path.join(PROJECT_EXTERNAL_CODE, "_".join([item for item in e_dir.split("/")]))
        base_name = os.path.basename(e_url)
        src = safepath( os.path.join(save_path, base_name) )
        dst = safepath( os.path.join(PROJECT_CODE, project_root_dir_name, e_dir) )        

        copytree(src, dst, override=True)
        logger.log ("use external(svn): \n\tsrc=%s \n\tdst=%s" % (src, dst), footprints=[], flush=True)

    return project_root_dir_name


@safeCd
def buildWar (fpath):
    def _getWarName ():
        f = open("pom.xml")
        data = f.read()
        f.close()

        pos_1 = data.find("<finalName>")
        if pos_1 < 0:
            raise Exception, "error pom.xml has not finalName"
        pos_2 = data.find("</finalName>")
        if pos_2 < 0:
            raise Exception, "error pom.xml has not finalName"
        finalName = data[pos_1 + len("<finalName>") : pos_2]

        return finalName + ".war"

    os.chdir(fpath)
    warName = _getWarName()
    warPath = os.path.join(fpath, "target", warName)
    warDst = os.path.join(TARGET, warName)
    
    records = executeCommond("mvn clean")
    logger.log ("mvn clean", footprints=records, flush=True)

    records = executeCommond("mvn install")
    logger.log ("mvn install", footprints=records, flush=True)

    shutil.move(warPath, warDst)


def createWar (configConfName, backupDB=True):
    if backupDB:
        # 备份数据库
        db_config = {
            "host": "db.bwzqgame.com",
            "port": "3306",
            "user": "root",
            "password": "shediao",
        }
        db_config["dbname"] = "game_lbs"
        mysqldump(db_config, TARGET)

    # 下载代码
    url = "svn://svn.bwzqgame.com/dev/server/condor_lbs/trunk/condor_lbs"
    external = []
    PROJECT_NAME = svn_checkout (url, external)

    # 更新配置
    configDst = "src/main/resources"
    if configConfName and configDst:
        configSrc = os.path.join(CONF_ROOT, configConfName)
        configDst = os.path.join(PROJECT_CODE, PROJECT_NAME, configDst)
        logger.log ("sync config: \n\tconfigSrc=%s \n\tconfigDst=%s" % (configSrc, configDst), footprints=[], flush=True)
        copytree(configSrc, configDst, override=True)
    
    # 编译
    buildPath = os.path.join(PROJECT_CODE, PROJECT_NAME)
    buildWar(buildPath)

    if platform.uname()[0].find("Windows") > -1:
        try:
            os.system('explorer /n, ' + TARGET)
        except:
            pass

    executeCommond ("pause")