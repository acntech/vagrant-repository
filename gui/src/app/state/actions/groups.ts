import axios from 'axios';

import {
    CreateGroup,
    UpdateGroup,
    Group,
    CreateGroupActionType,
    CreateGroupErrorAction,
    CreateGroupLoadingAction,
    CreateGroupSuccessAction,
    DeleteGroupActionType,
    DeleteGroupErrorAction,
    DeleteGroupLoadingAction,
    DeleteGroupSuccessAction,
    FindGroupsActionType,
    FindGroupsErrorAction,
    FindGroupsLoadingAction,
    FindGroupsSuccessAction,
    GetGroupActionType,
    GetGroupErrorAction,
    GetGroupLoadingAction,
    GetGroupSuccessAction,
    UpdateGroupActionType,
    UpdateGroupErrorAction,
    UpdateGroupLoadingAction,
    UpdateGroupSuccessAction
} from '../../models';
import { showError, showSuccess } from '../actions';

const createGroupLoading = (loading: boolean): CreateGroupLoadingAction => ({ type: CreateGroupActionType.LOADING, loading });
const createGroupSuccess = (payload: Group): CreateGroupSuccessAction => ({ type: CreateGroupActionType.SUCCESS, payload });
const createGroupError = (error: any): CreateGroupErrorAction => ({ type: CreateGroupActionType.ERROR, error });

const deleteGroupLoading = (loading: boolean): DeleteGroupLoadingAction => ({ type: DeleteGroupActionType.LOADING, loading });
const deleteGroupSuccess = (groupId: number): DeleteGroupSuccessAction => ({ type: DeleteGroupActionType.SUCCESS, groupId });
const deleteGroupError = (error: any): DeleteGroupErrorAction => ({ type: DeleteGroupActionType.ERROR, error });

const findGroupsLoading = (loading: boolean): FindGroupsLoadingAction => ({ type: FindGroupsActionType.LOADING, loading });
const findGroupsSuccess = (payload: Group[]): FindGroupsSuccessAction => ({ type: FindGroupsActionType.SUCCESS, payload });
const findGroupsError = (error: any): FindGroupsErrorAction => ({ type: FindGroupsActionType.ERROR, error });

const getGroupLoading = (loading: boolean): GetGroupLoadingAction => ({ type: GetGroupActionType.LOADING, loading });
const getGroupSuccess = (payload: Group): GetGroupSuccessAction => ({ type: GetGroupActionType.SUCCESS, payload });
const getGroupError = (error: any): GetGroupErrorAction => ({ type: GetGroupActionType.ERROR, error });

const updateGroupLoading = (loading: boolean): UpdateGroupLoadingAction => ({ type: UpdateGroupActionType.LOADING, loading });
const updateGroupSuccess = (payload: Group): UpdateGroupSuccessAction => ({ type: UpdateGroupActionType.SUCCESS, payload });
const updateGroupError = (error: any): UpdateGroupErrorAction => ({ type: UpdateGroupActionType.ERROR, error });

const groupsRootPath = '/api/groups';

export function createGroup(group: CreateGroup) {
    return (dispatch) => {
        dispatch(createGroupLoading(true));
        const url = `${groupsRootPath}`;
        return axios.post(url, group)
            .then((response) => {
                dispatch(showSuccess('Group created successfully'));
                return dispatch(createGroupSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                dispatch(showError('Error creating group', message));
                return dispatch(createGroupError(error));
            });
    };
}

export function findGroups(groupName?: string) {
    return (dispatch) => {
        dispatch(findGroupsLoading(true));
        const url = name ? `${groupsRootPath}?name=${groupName}` : groupsRootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findGroupsSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findGroupsError(error));
            });
    };
}

export function getGroup(groupId: number) {
    return (dispatch) => {
        dispatch(getGroupLoading(true));
        const url = `${groupsRootPath}/${groupId}`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getGroupSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getGroupError(error));
            });
    };
}

export function deleteGroup(groupId: number) {
    return (dispatch) => {
        dispatch(deleteGroupLoading(true));
        const url = `${groupsRootPath}/${groupId}`;
        return axios.delete(url)
            .then(() => {
                dispatch(showSuccess('Group deleted successfully'));
                return dispatch(deleteGroupSuccess(groupId));
            })
            .catch((error) => {
                const { message } = error.response.data;
                dispatch(showError('Error deleting group', message));
                return dispatch(deleteGroupError(error));
            });
    };
}

export function updateGroup(groupId: number, group: UpdateGroup) {
    return (dispatch) => {
        dispatch(updateGroupLoading(true));
        const url = `${groupsRootPath}/${groupId}`;
        return axios.put(url, group)
            .then((response) => {
                dispatch(showSuccess('Group updated successfully'));
                return dispatch(updateGroupSuccess(response.data));
            })
            .catch((error) => {
                const { message } = error.response.data;
                dispatch(showError('Error updating group', message));
                return dispatch(updateGroupError(error));
            });
    };
}
