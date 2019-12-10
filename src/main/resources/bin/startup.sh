#!/bin/bash

CURRENT_PATH=$(cd `dirname $0`; pwd)
APP_PATH="/opt/file_manage"
APP_NAME="file-manage"

function get_jar_name() {
    local jar_name=$( ls ${APP_PATH}/*.jar | grep ${APP_NAME} )
    echo $( basename ${jar_name} )
}

function get_app_pid() {
    local jar_name=$(get_jar_name)
    pid=$(ps -ef | grep -w java | grep ${jar_name} | awk '{print $2}')
    echo "${pid}"
}

function start() {
    pid=$(get_app_pid)

    if [[ -n "${pid}" ]] ; then
        echo "${APP_NAME} is running, pid: ${pid}"
        return 0
    fi

    [[ ! -d "${APP_PATH}/logs" ]] && mkdir -p "${APP_PATH}/logs"

    local jar_name=$(get_jar_name)
    cd ${APP_PATH}
    nohup java -Dloader.home=${APP_PATH} -jar ${APP_PATH}/${jar_name} >> ${APP_PATH}/logs/catalina.out 2>&1 &

    pid=$(get_app_pid)

    echo "start ${APP_NAME} success, pid: ${pid}"
}

function stop() {
    pid=$(get_app_pid)

    if [[ -z "${pid}" ]] ; then
        echo "${APP_NAME} is not running, no need to stop."
    else
        echo "$APP_NAME is running, kill pid: ${pid}"
        kill -9 ${pid}
    fi
}

function restart() {
    echo "restart ${APP_NAME} ..."
    stop
    sleep 3
    start
    echo "restart ${APP_NAME} success."
}

function status() {
    pid=$(get_app_pid)
    if [[ -n ${pid} ]]; then
        echo "${APP_NAME} is running, pid:${pid}"
    else
        echo "${APP_NAME} is stopped"
    fi
}

function usage() {
    echo "sh $0 start|stop|restart|status"
}

function main() {
    action=$1
    case $1 in

        start)
            start
        ;;

        stop)
            stop
        ;;

        restart)
            restart
        ;;

        status)
            status
        ;;

        *)
            usage
        ;;
    esac
}

[[ $# -eq 0 ]] && usage && exit 1
action=$1
main $@
[[ $? -eq 0 ]] && echo "exec $action success." || echo "exec $action failed."
