import axios from 'axios';

import { globalConfig } from '../../core/config';
import {
    Version,
    ModifyVersion,
    CreateVersionActionType,
    CreateVersionErrorAction,
    CreateVersionLoadingAction,
    CreateVersionSuccessAction,
    DeleteVersionActionType,
    DeleteVersionErrorAction,
    DeleteVersionLoadingAction,
    DeleteVersionSuccessAction,
    GetVersionActionType,
    GetVersionErrorAction,
    GetVersionLoadingAction,
    GetVersionSuccessAction,
    FindVersionsActionType,
    FindVersionsErrorAction,
    FindVersionsLoadingAction,
    FindVersionsSuccessAction,
    UpdateVersionActionType,
    UpdateVersionErrorAction,
    UpdateVersionLoadingAction,
    UpdateVersionSuccessAction
} from '../../models';
import { showError, showSuccess } from '.';

const { timeout: successTimeout } = globalConfig.notifications.success;
const { timeout: errorTimeout } = globalConfig.notifications.error;

const getVersionLoading = (loading: boolean): GetVersionLoadingAction => ({ type: GetVersionActionType.LOADING, loading });
const getVersionSuccess = (payload: Version): GetVersionSuccessAction => ({ type: GetVersionActionType.SUCCESS, payload });
const getVersionError = (error: any): GetVersionErrorAction => ({ type: GetVersionActionType.ERROR, error });

const findVersionsLoading = (loading: boolean): FindVersionsLoadingAction => ({ type: FindVersionsActionType.LOADING, loading });
const findVersionsSuccess = (payload: Version[]): FindVersionsSuccessAction => ({ type: FindVersionsActionType.SUCCESS, payload });
const findVersionsError = (error: any): FindVersionsErrorAction => ({ type: FindVersionsActionType.ERROR, error });

const createVersionLoading = (loading: boolean): CreateVersionLoadingAction => ({ type: CreateVersionActionType.LOADING, loading });
const createVersionSuccess = (payload: Version): CreateVersionSuccessAction => ({ type: CreateVersionActionType.SUCCESS, payload });
const createVersionError = (error: any): CreateVersionErrorAction => ({ type: CreateVersionActionType.ERROR, error });

const deleteVersionLoading = (loading: boolean): DeleteVersionLoadingAction => ({ type: DeleteVersionActionType.LOADING, loading });
const deleteVersionSuccess = (versionId: number): DeleteVersionSuccessAction => ({ type: DeleteVersionActionType.SUCCESS, versionId });
const deleteVersionError = (error: any): DeleteVersionErrorAction => ({ type: DeleteVersionActionType.ERROR, error });

const updateVersionLoading = (loading: boolean): UpdateVersionLoadingAction => ({ type: UpdateVersionActionType.LOADING, loading });
const updateVersionSuccess = (payload: Version): UpdateVersionSuccessAction => ({ type: UpdateVersionActionType.SUCCESS, payload });
const updateVersionError = (error: any): UpdateVersionErrorAction => ({ type: UpdateVersionActionType.ERROR, error });

const versionsRootPath = '/api/versions';
const boxesRootPath = '/api/boxes';

export function getVersion(versionId: number) {
    return (dispatch) => {
        dispatch(getVersionLoading(true));
        const url = `${versionsRootPath}/${versionId}`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getVersionSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getVersionError(error));
            });
    };
}

export function findVersions(versionName?: string) {
    return (dispatch) => {
        dispatch(findVersionsLoading(true));
        const url = name ? `${versionsRootPath}?name=${versionName}` : versionsRootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findVersionsSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findVersionsError(error));
            });
    };
}

export function findBoxVersions(boxId: number) {
    return (dispatch) => {
        dispatch(findVersionsLoading(true));
        const url = `${boxesRootPath}/${boxId}/versions`;
        return axios.get(url)
            .then((response) => {
                return dispatch(findVersionsSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findVersionsError(error));
            });
    };
}

export function createBoxVersion(boxId: number, version: ModifyVersion) {
    return (dispatch) => {
        dispatch(createVersionLoading(true));
        const url = `${boxesRootPath}/${boxId}/versions`;
        return axios.post(url, version)
            .then((response) => {
                const notification = { title: 'Version created successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(createVersionSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error creating version', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(createVersionError(error));
            });
    };
}

export function deleteVersion(versionId: number) {
    return (dispatch) => {
        dispatch(deleteVersionLoading(true));
        const url = `${versionsRootPath}/${versionId}`;
        return axios.delete(url)
            .then(() => {
                const notification = { title: 'Version deleted successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(deleteVersionSuccess(versionId));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error deleting version', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(deleteVersionError(error));
            });
    };
}

export function updateVersion(versionId: number, version: ModifyVersion) {
    return (dispatch) => {
        dispatch(updateVersionLoading(true));
        const url = `${versionsRootPath}/${versionId}`;
        return axios.put(url, version)
            .then((response) => {
                const notification = { title: 'Version updated successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(updateVersionSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error updating version', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(updateVersionError(error));
            });
    };
}
