import axios from 'axios';

import {
    Box,
    GetBoxActionType,
    GetBoxErrorAction,
    GetBoxLoadingAction,
    GetBoxSuccessAction,
    FindBoxesActionType,
    FindBoxesErrorAction,
    FindBoxesLoadingAction,
    FindBoxesSuccessAction,
} from '../../models';

const getBoxLoading = (loading: boolean): GetBoxLoadingAction => ({ type: GetBoxActionType.LOADING, loading });
const getBoxSuccess = (payload: Box): GetBoxSuccessAction => ({ type: GetBoxActionType.SUCCESS, payload });
const getBoxError = (error: any): GetBoxErrorAction => ({ type: GetBoxActionType.ERROR, error });

const findBoxesLoading = (loading: boolean): FindBoxesLoadingAction => ({ type: FindBoxesActionType.LOADING, loading });
const findBoxesSuccess = (payload: Box[]): FindBoxesSuccessAction => ({ type: FindBoxesActionType.SUCCESS, payload });
const findBoxesError = (error: any): FindBoxesErrorAction => ({ type: FindBoxesActionType.ERROR, error });

const boxesRootPath = '/api/boxes';
const groupsRootPath = '/api/groups';

export function getBox(boxId: number) {
    return (dispatch) => {
        dispatch(getBoxLoading(true));
        const url = `${boxesRootPath}/${boxId}`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getBoxSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getBoxError(error));
            });
    };
}

export function findBoxes(boxName?: string) {
    return (dispatch) => {
        dispatch(findBoxesLoading(true));
        const url = name ? `${boxesRootPath}?name=${boxName}` : boxesRootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findBoxesSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findBoxesError(error));
            });
    };
}

export function findGroupBoxes(groupId: number) {
    return (dispatch) => {
        dispatch(findBoxesLoading(true));
        const url = `${groupsRootPath}/${groupId}/boxes`;
        return axios.get(url)
            .then((response) => {
                return dispatch(findBoxesSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findBoxesError(error));
            });
    };
}