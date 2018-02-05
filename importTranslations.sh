#!/bin/sh

TOPDIR=`realpath .`
BASE=`pwd`
ZIP=$1

if [ -z $ZIP ]; then
    echo missing ZIP
    exit 1
fi

if [ ! -f $ZIP ]; then
    echo ZIP does not exists
    exit 1
fi

NAME=`basename $ZIP`
DIR=$(dirname $ZIP)/translations
if [ -e $DIR ]; then
    rm -r $DIR
fi
mkdir -p $DIR
cp $ZIP $DIR/
cd $DIR
unzip -q $NAME
rm $NAME
for i in `ls`; do
    mv $i values-$i
done

mv values-ast values-ast-rES
mv values-es-ES values-es
mv values-hy-AM values-hy-rAM
mv values-pt-BR values-pt-rBR
mv values-pt-PT values-pt-rPT
mv values-sv-SE values-sv
mv values-zh-CN values-zh-rCN
mv values-zh-TW values-zh-rTW

for i in `ls`; do
    cp -r $i $BASE/app/src/main/res/
done

cd ..
rm -r translations
cd $TOPDIR

if [ ! -f translation_verifyer ]; then
    clang++ -std=c++11 translation_verifyer.cpp -o translation_verifyer
fi

./translation_verifyer app/src/main/res
