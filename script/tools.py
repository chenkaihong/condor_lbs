# -*- coding: utf-8 -*-

import shutil, os, stat, traceback


def executeCommond (cmd):
    return [line for line in os.popen(cmd)]

def makedirs(dirs):
    """
        递归生成目录, 无视原目录是否存在
    """
    try:
        os.makedirs(dirs)
    except:
        pass


def romveFile (fname):
    """
        删除文件
    """
    try:
        os.chmod(fname, stat.S_IWRITE)
    except:
        pass

    try:
        if os.path.exists(fname):
            os.remove(fname)
    except:
        traceback.print_stack()


def copytree(src, dst, override=True):
    """
        递归复制 src 下面的文件和目录到 dst 下面
            override: 是否覆盖已存在的文件
    """
    makedirs(dst)

    names = os.listdir(src)
    for name in names:
        srcname = os.path.join(src, name)
        dstname = os.path.join(dst, name)

        if os.path.isdir(srcname):
            copytree(srcname, dstname, override)
        else:
            if override and os.path.exists(dstname):
                romveFile(dstname)
            shutil.copy2(srcname, dstname)


def safepath (fpath):
    """
        统一文件路径的系统分隔符
    """
    fpath = fpath.replace("\\", "/")
    return os.sep.join([i for i in fpath.split("/")])


def clean_private_file (top):
    """
        清理掉以__(2个下划线)开头的目录和文件
    """
    for root, dirs, files in os.walk(top):
        for d in dirs:
            if d.startswith("__") > 0:
                shutil.rmtree( os.path.join(root, d) )
        for f in files:
            if f.startswith("__") > 0:
                romveFile( os.path.join(root, f) )

def clean_svn_file (top):
    """
        清理 top 目录下的 svn 文件
    """
    for root, dirs, files in os.walk(top):
        if root.find('.svn') > 0:
           shutil.rmtree(root)


def fileFullReplace(fname, src, tar):
    """
        全文替换
    """
    f = open(fname)
    data = f.read()
    f.close()

    data = data.replace(src, tar)

    f = open(fname, "w")
    f.write(data)
    f.close()

def safeCd (func):
    def _call_(*args, **kwargs):
        currentDir = os.getcwd()
        result = func(*args, **kwargs)
        os.chdir(currentDir)
        return result

    return _call_


class Logger:
    def __init__(self, logRoot=None, stepLog=None, footprintsLog=None):
        if logRoot is None:
            logRoot = "."
        self.logRoot = logRoot
        self.stepLog = stepLog                  # 每一步动作的文件名
        self.footprintsLog = footprintsLog      # 每一步动作详细的文件名
        self.steps = []
        self.footprints = []
        self.stepIndex = 0
        
        # 清除原来的log文件
        if self.stepLog is not None:
            romveFile(os.path.join(self.logRoot, self.stepLog))
        if self.footprintsLog is not None:
            romveFile(os.path.join(self.logRoot, self.footprintsLog))


    def log (self, step, footprints=[], flush=True):
        self.steps.append(step)

        if footprints is None:
            footprints = []
        elif type(footprints) is not list and type(footprints) is not tuple:
            footprints = [footprints]
        self.footprints.append(footprints)

        if flush:
            self._flush()

    def _flush(self):
        for i, step in enumerate(self.steps):
            self.stepIndex += 1
            msg = "############ [step %-2d] %-36s" % (self.stepIndex, step)
            
            if self.stepLog is not None:
                f = open(os.path.join(self.logRoot, self.stepLog), "a+")
                f.write (msg + "\n")
                f.close ()
            else:
                print msg

            if self.footprintsLog is not None:
                records = self.footprints[i]
                records = [line.strip() for line in records]
                records = [line for line in records if line]
                
                f = open(os.path.join(self.logRoot, self.footprintsLog), "a+")
                f.write ("##########################################################################################\n")
                f.write (msg + "\n\n")
                if len(records) > 0:
                    f.write ( "\n".join (records) )
                f.write ("\n\n\n")
                f.close ()

        self.steps = []
        self.footprints = []
        
    def close(self):
        pass

if "__main__" == __name__:
    print safepath("E:\\xserver\\condor_gameserver\\script\\__root__\\project\\code\\condor_gameserver\\src/main/resources/json")