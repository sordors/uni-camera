#!/bin/sh
APP_NAME=__UNI__1AF6054
echo 'delete dir'
rm -rf ../app/src/main/assets/apps/$APP_NAME
echo 'copy dir'
cp -rf ./unpackage/resources/$APP_NAME ../app/src/main/assets/apps/$APP_NAME
