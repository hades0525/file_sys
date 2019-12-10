#!/usr/bin/env bash

CURRENT_PATH=$(cd `dirname $0`; pwd)

APP_PATH="/opt/file_manage"
APP_NAME="file-manage"
SHELL_NAME="${APP_PATH}/bin/startup.sh"

LOG_PATH="/var/log/file_manage"
LOG_FILE="${LOG_PATH}/${APP_NAME}-deploy.log"

function get_jar_name() {
    local jar_name=$( ls ${CURRENT_PATH}/*.jar | grep ${APP_NAME} )
    echo $( basename ${jar_name} )
}

function start() {
    log "start ${APP_NAME} ..."
    ${SHELL_NAME} start
    [[ $? -eq 0 ]] && log "start ${APP_NAME} success." || log "start ${APP_NAME} failed."
}

function stop() {
    log "stop ${APP_NAME} ..."
    ${SHELL_NAME} stop
    [[ $? -eq 0 ]] && log "stop ${APP_NAME} success." || log "stop ${APP_NAME} failed."
}

function prepare() {
    [[ ! -d ${LOG_PATH} ]] && mkdir -p ${LOG_PATH}
    [[ ! -d ${APP_PATH} ]] && mkdir -p ${APP_PATH}
    [[ ! -d "${APP_PATH}/logs" ]] && mkdir -p "${APP_PATH}/logs"
    [[ ! -d "${APP_PATH}/data" ]] && mkdir -p "${APP_PATH}/data"
}

function copy_res() {
    local jar_name=$(get_jar_name)
    cp -rf ${CURRENT_PATH}/bin ${APP_PATH}/
    cp -rf ${CURRENT_PATH}/lib ${APP_PATH}/
    [[ $1 == "deploy" ]] && cp -rf ${CURRENT_PATH}/config ${APP_PATH}/
    cp -f  ${CURRENT_PATH}/${jar_name} ${APP_PATH}/
}

function delete_res() {
    rm -f  ${APP_PATH}/*.jar
    rm -rf ${APP_PATH}/bin
    rm -rf ${APP_PATH}/lib
}

function config() {
    chmod 550 ${APP_PATH}/*.jar
    chmod 550 ${APP_PATH}/lib/*.jar
    chmod 550 ${APP_PATH}/bin/*.sh
    chmod 644 ${APP_PATH}/config/*.*

    chown -R root:root ${APP_PATH}
}

function deploy() {
    if [[ -d ${APP_PATH} ]] ; then
        log "deploy failed, ${APP_NAME} had deploy.";
        exit 1
    fi
    prepare
    copy_res "deploy"
    config
    start
}

function upgrade() {
    if [[ ! -d ${APP_PATH} ]] ; then
        log "upgrade failed, ${APP_NAME} not deploy.";
        exit 1
    fi
    stop
    prepare
    delete_res
    copy_res
    config
    start
}

function log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1">>${LOG_FILE} 2>&1
}

function usage() {
    echo "sh $0 deploy|upgrade"
}

function main() {

    case ${ACTION} in

        "deploy")
            deploy
        ;;

        "upgrade")
            upgrade
        ;;

        *)
            usage
        ;;
    esac
}

[[ $# -eq 0 ]] && usage && exit 1
ACTION=$1
main $@
[[ $? -eq 0 ]] && log "exec ${ACTION} success." || log "exec ${ACTION} failed."
