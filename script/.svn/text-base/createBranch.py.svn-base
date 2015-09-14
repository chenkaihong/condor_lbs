# -*- coding: utf-8 -*-
import os, sys, shutil, stat, time, platform, subprocess
from tools import *
from xml.dom import minidom

logger = Logger()

fpath, fname = os.path.split(__file__)

def svnCreateBranch (trunkURL, branchURL, version):
    def _isExistBranch (branchURL):
        p = subprocess.Popen("svn info " + branchURL, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        status = p.wait()
        p.kill()
        return status == 0

    if _isExistBranch (branchURL):
        logger.log ("svnCreateBranch be exist. \n\tbranchURL=%s" % (branchURL,), footprints=[], flush=True)
        return False

    cmd = 'svn cp %s %s -m "%s"' % (trunkURL, branchURL, version)
    records = executeCommond(cmd)
    logger.log ("svnCreateBranch: \n\ttrunkURL=%s \n\tbranchURL=%s" % (trunkURL, branchURL), footprints=records, flush=True)

    return True

def branchFile (ROOT, version):
    def _getProjectName (fname):
        root = minidom.parse(fname).documentElement
        for node in root.childNodes:
            if node.nodeName == 'name':
                return node.childNodes[0].nodeValue

    # .project
    projectFilename = os.path.join(ROOT, ".project")
    projectName = _getProjectName (projectFilename)    
    fileFullReplace (projectFilename, projectName, projectName + "_" + version)
    logger.log ("replace project file: " + projectFilename, footprints=[], flush=True)

    # pom.xml
    pomFilename = os.path.join(ROOT, "pom.xml")
    fileFullReplace (pomFilename, "<version>0.0.1-SNAPSHOT</version>", "<version>%s-SNAPSHOT</version>" % version)
    logger.log ("replace project file: " + pomFilename, footprints=[], flush=True)

    # svn commit (.project & pom.xml)
    cmd = 'svn commit -m "%s" ".project" "pom.xml"' % version
    records = executeCommond(cmd)
    logger.log ("svn commit (.project & pom.xml)", footprints=records, flush=True)


def action (version, trunkURL):
    # init params
    arr = version.split(".")
    branchVersion = "%s.%s%s" % (arr[0], arr[1], arr[2])
    projectVersion = "%s.%s.%s" % (arr[0], arr[1], arr[2])

    pos = trunkURL.find("/trunk/")
    branchURL = '%s/branches/%s' % (trunkURL[:pos], branchVersion)

    ROOT = os.path.join(fpath, "__root__")
    if os.path.exists(ROOT):
        try:
            shutil.rmtree(ROOT)
        except:
            pass
    makedirs(ROOT)

    logger.log ("init params ok. version=" + version, footprints=[], flush=True)
    
    # create branch
    success = svnCreateBranch (trunkURL, branchURL, version)
    if not success:
        return

    # check out branch code
    os.chdir(ROOT)
    cmd = "svn co " + branchURL
    records = executeCommond(cmd)
    logger.log ("svn checkout: " + branchURL, footprints=records, flush=True)

    # replace project file
    PROJECT_ROOT = os.path.join(ROOT, branchVersion)
    os.chdir( PROJECT_ROOT )
    branchFile (PROJECT_ROOT, version)
    
    for i in range (10):
        if os.path.exists(ROOT):
            try:
                time.sleep(1)
                shutil.rmtree(ROOT)
            except:
                pass

if '__main__' == __name__:
    version = "2.6.0"
    trunkURL = "svn://svn.bwzqgame.com/dev/server/condor_lbs/trunk/condor_lbs"
    action (version, trunkURL)
    os.system("pause")