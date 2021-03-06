import axios from 'axios';

import { globalConfig } from '../../core/config';
import {
    Box,
    ModifyBox,
    CreateBoxActionType,
    CreateBoxErrorAction,
    CreateBoxLoadingAction,
    CreateBoxSuccessAction,
    DeleteBoxActionType,
    DeleteBoxErrorAction,
    DeleteBoxLoadingAction,
    DeleteBoxSuccessAction,
    GetBoxActionType,
    GetBoxErrorAction,
    GetBoxLoadingAction,
    GetBoxSuccessAction,
    FindBoxesActionType,
    FindBoxesErrorAction,
    FindBoxesLoadingAction,
    FindBoxesSuccessAction,
    UpdateBoxActionType,
    UpdateBoxErrorAction,
    UpdateBoxLoadingAction,
    UpdateBoxSuccessAction
} from '../../models';
import { showError, showSuccess } from '.';

const { timeout: successTimeout } = globalConfig.notifications.success;
const { timeout: errorTimeout } = globalConfig.notifications.error;

const getBoxLoading = (loading: boolean): GetBoxLoadingAction => ({ type: GetBoxActionType.LOADING, loading });
const getBoxSuccess = (payload: Box): GetBoxSuccessAction => ({ type: GetBoxActionType.SUCCESS, payload });
const getBoxError = (error: any): GetBoxErrorAction => ({ type: GetBoxActionType.ERROR, error });

const findBoxesLoading = (loading: boolean): FindBoxesLoadingAction => ({ type: FindBoxesActionType.LOADING, loading });
const findBoxesSuccess = (payload: Box[]): FindBoxesSuccessAction => ({ type: FindBoxesActionType.SUCCESS, payload });
const findBoxesError = (error: any): FindBoxesErrorAction => ({ type: FindBoxesActionType.ERROR, error });

const createBoxLoading = (loading: boolean): CreateBoxLoadingAction => ({ type: CreateBoxActionType.LOADING, loading });
const createBoxSuccess = (payload: Box): CreateBoxSuccessAction => ({ type: CreateBoxActionType.SUCCESS, payload });
const createBoxError = (error: any): CreateBoxErrorAction => ({ type: CreateBoxActionType.ERROR, error });

const deleteBoxLoading = (loading: boolean): DeleteBoxLoadingAction => ({ type: DeleteBoxActionType.LOADING, loading });
const deleteBoxSuccess = (boxId: number): DeleteBoxSuccessAction => ({ type: DeleteBoxActionType.SUCCESS, boxId });
const deleteBoxError = (error: any): DeleteBoxErrorAction => ({ type: DeleteBoxActionType.ERROR, error });

const updateBoxLoading = (loading: boolean): UpdateBoxLoadingAction => ({ type: UpdateBoxActionType.LOADING, loading });
const updateBoxSuccess = (payload: Box): UpdateBoxSuccessAction => ({ type: UpdateBoxActionType.SUCCESS, payload });
const updateBoxError = (error: any): UpdateBoxErrorAction => ({ type: UpdateBoxActionType.ERROR, error });

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

export function createGroupBox(groupId: number, box: ModifyBox) {
    return (dispatch) => {
        dispatch(createBoxLoading(true));
        const url = `${groupsRootPath}/${groupId}/boxes`;
        return axios.post(url, box)
            .then((response) => {
                const notification = { title: 'Box created successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(createBoxSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error creating box', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(createBoxError(error));
            });
    };
}

export function deleteBox(boxId: number) {
    return (dispatch) => {
        dispatch(deleteBoxLoading(true));
        const url = `${boxesRootPath}/${boxId}`;
        return axios.delete(url)
            .then(() => {
                const notification = { title: 'Box deleted successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(deleteBoxSuccess(boxId));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error deleting box', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(deleteBoxError(error));
            });
    };
}

export function updateBox(boxId: number, box: ModifyBox) {
    return (dispatch) => {
        dispatch(updateBoxLoading(true));
        const url = `${boxesRootPath}/${boxId}`;
        return axios.put(url, box)
            .then((response) => {
                const notification = { title: 'Box updated successfully', timeout: successTimeout };
                dispatch(showSuccess(notification));
                return dispatch(updateBoxSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                const notification = { title: 'Error updating box', content: message, timeout: errorTimeout };
                dispatch(showError(notification));
                return dispatch(updateBoxError(error));
            });
    };
}
