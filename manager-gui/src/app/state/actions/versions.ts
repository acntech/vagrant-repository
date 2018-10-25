import axios from 'axios';

import {
    Version,
    GetVersionActionType,
    GetVersionErrorAction,
    GetVersionLoadingAction,
    GetVersionSuccessAction,
    FindVersionsActionType,
    FindVersionsErrorAction,
    FindVersionsLoadingAction,
    FindVersionsSuccessAction,
} from '../../models';

const getVersionLoading = (loading: boolean): GetVersionLoadingAction => ({ type: GetVersionActionType.LOADING, loading });
const getVersionSuccess = (payload: Version): GetVersionSuccessAction => ({ type: GetVersionActionType.SUCCESS, payload });
const getVersionError = (error: any): GetVersionErrorAction => ({ type: GetVersionActionType.ERROR, error });

const findVersionsLoading = (loading: boolean): FindVersionsLoadingAction => ({ type: FindVersionsActionType.LOADING, loading });
const findVersionsSuccess = (payload: Version[]): FindVersionsSuccessAction => ({ type: FindVersionsActionType.SUCCESS, payload });
const findVersionsError = (error: any): FindVersionsErrorAction => ({ type: FindVersionsActionType.ERROR, error });

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