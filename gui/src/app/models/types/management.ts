import {
    GetManagementInfoActionType,
    Error,
    Modified
} from '..';

export interface ManagementInfo {
    build: ManagementInfoBuild;
}

export interface ManagementInfoBuild {
    group: string;
    artifact: string;
    version: string;
    scm: ManagementInfoBuildScm;
}

export interface ManagementInfoBuildScm {
    revision: string;
    revisionUrl: string;
}

export interface ManagementState {
    loading: boolean;
    info?: ManagementInfo;
    error?: Error;
    modified?: Modified;
}

export interface GetManagementInfoLoadingAction {
    type: GetManagementInfoActionType.LOADING,
    loading: boolean
}

export interface GetManagementInfoSuccessAction {
    type: GetManagementInfoActionType.SUCCESS,
    payload: ManagementInfo
}

export interface GetManagementInfoErrorAction {
    type: GetManagementInfoActionType.ERROR,
    error: any
}

export type GetManagementInfoAction = GetManagementInfoLoadingAction | GetManagementInfoSuccessAction | GetManagementInfoErrorAction;

export type ManagementAction = GetManagementInfoAction;