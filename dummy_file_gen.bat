@echo off

set filename=test.bin

set size=2147483648

fsutil file createnew %filename% %size%


if exist %filename% (
    echo File %filename% of size 2GB created successfully.
) else (
    echo Failed to create the file.
)

pause
