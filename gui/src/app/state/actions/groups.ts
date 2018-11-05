import axios from 'axios';

import {
    CreateGroup,
    Group,
    CreateGroupActionType,
    CreateGroupErrorAction,
    CreateGroupLoadingAction,
    CreateGroupSuccessAction,
    FindGroupsActionType,
    FindGroupsErrorAction,
    FindGroupsLoadingAction,
    FindGroupsSuccessAction,
    GetGroupActionType,
    GetGroupErrorAction,
    GetGroupLoadingAction,
    GetGroupSuccessAction
} from '../../models';

const getGroupLoading = (loading: boolean): GetGroupLoadingAction => ({ type: GetGroupActionType.LOADING, loading });
const getGroupSuccess = (payload: Group): GetGroupSuccessAction => ({ type: GetGroupActionType.SUCCESS, payload });
const getGroupError = (error: any): GetGroupErrorAction => ({ type: GetGroupActionType.ERROR, error });

const findGroupsLoading = (loading: boolean): FindGroupsLoadingAction => ({ type: FindGroupsActionType.LOADING, loading });
const findGroupsSuccess = (payload: Group[]): FindGroupsSuccessAction => ({ type: FindGroupsActionType.SUCCESS, payload });
const findGroupsError = (error: any): FindGroupsErrorAction => ({ type: FindGroupsActionType.ERROR, error });

const createGroupLoading = (loading: boolean): CreateGroupLoadingAction => ({ type: CreateGroupActionType.LOADING, loading });
const createGroupSuccess = (payload: Group): CreateGroupSuccessAction => ({ type: CreateGroupActionType.SUCCESS, payload });
const createGroupError = (error: any): CreateGroupErrorAction => ({ type: CreateGroupActionType.ERROR, error });

const rootPath = '/api/groups';

export function getGroup(groupId: number) {
    return (dispatch) => {
        dispatch(getGroupLoading(true));
        const url = `${rootPath}/${groupId}`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getGroupSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getGroupError(error));
            });
    };
}

export function findGroups(groupName?: string) {
    return (dispatch) => {
        dispatch(findGroupsLoading(true));
        const url = name ? `${rootPath}?name=${groupName}` : rootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findGroupsSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findGroupsError(error));
            });
    };
}

export function createGroup(group: CreateGroup) {
    return (dispatch) => {
        dispatch(createGroupLoading(true));
        const url = `${rootPath}`;
        return axios.post(url, group)
            .then((response) => {
                return dispatch(createGroupSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(createGroupError(error));
            });
    };
}