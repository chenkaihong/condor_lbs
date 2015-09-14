@echo off
color 0C
echo 请确认是否已同步 SVN
echo 1) 未同步,请点右上角的X
echo 2) 已同步,请按任意键继续
pause>nul
color 0B

cd ..
call mvn deploy
pause