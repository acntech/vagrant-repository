import axios from 'axios';

import {
    File,
    FindFilesActionType,
    FindFilesErrorAction,
    FindFilesLoadingAction,
    FindFilesSuccessAction
} from '../../models';

const findFilesLoading = (loading: boolean): FindFilesLoadingAction => ({ type: FindFilesActionType.LOADING, loading });
const findFilesSuccess = (payload: File[]): FindFilesSuccessAction => ({ type: FindFilesActionType.SUCCESS, payload });
const findFilesError = (error: any): FindFilesErrorAction => ({ type: FindFilesActionType.ERROR, error });

const providersRootPath = '/api/providers';

export function findFiles(providerId: number) {
    return (dispatch) => {
        dispatch(findFilesLoading(true));
        const url = `${providersRootPath}/${providerId}/files`;
        return axios.get(url)
            .then((response) => {
                return dispatch(findFilesSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findFilesError(error));
            });
    };
}
