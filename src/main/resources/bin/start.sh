#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
#配置文件目录
CONF_DIR=$DEPLOY_DIR/conf
#jar包目录
LIB_DIR=$DEPLOY_DIR/lib

echo "Current deploy dir is $DEPLOY_DIR"
SERVER_NAME=`sed '/project.name/!d;s/.*=//' conf/server.properties | tr -d '\r'`
SERVER_PORT=`sed '/project.port/!d;s/.*=//' conf/server.properties | tr -d '\r'`


#标准输出文件
STDOUT_FILE=$DEPLOY_DIR/stdout.log
#GC日志文件
GC_LOG_FILE=$DEPLOY_DIR/gc.log


#jar路径组装成classpath格式
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

#java启动参数
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
#JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
#JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=12235 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management
#.jmxremote.authenticate=false "
JAVA_MEM_OPTS=" -server -Xms5g -Xmx5g -Xmn1900m -Xss256k -XX:PermSize=128m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
JAVA_GC_OPTS=" -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintClassHistogram -XX:-TraceClassUnloading -verbose:gc -Xloggc:"$GC_LOG_FILE
#xmn recommended 3/8 of xmx
#jstat –gcutil
#jmap –heap


echo "$SERVER_NAME starting..."
nohup java $JAVA_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $JAVA_MEM_OPTS $JAVA_GC_OPTS -classpath $CONF_DIR:$LIB_JARS com.demai.App > $STDOUT_FILE 2>&1 &
PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "$SERVER_NAME started at PORT: $SERVER_PORT PID: $PIDS"
echo "Please check log files"





