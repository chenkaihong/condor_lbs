# -*- coding: utf-8 -*-

import os, sys, shutil, stat, time, platform
from tools import *
from package import *


if '__main__' == __name__:
    createWar ("fengce", backupDB=False)