import axios from 'axios';

import {
    Group,
    FindGroupsActionType,
    FindGroupsErrorAction,
    FindGroupsLoadingAction,
    FindGroupsSuccessAction,
    GetGroupActionType,
    GetGroupErrorAction,
    GetGroupLoadingAction,
    GetGroupSuccessAction
} from '../../models';

const getGroupLoading = (loading: boolean): GetGroupLoadingAction => ({type: GetGroupActionType.LOADING, loading});
const getGroupSuccess = (payload: Group): GetGroupSuccessAction => ({type: GetGroupActionType.SUCCESS, payload});
const getGroupError = (error: any): GetGroupErrorAction => ({type: GetGroupActionType.ERROR, error});

const findGroupsLoading = (loading: boolean): FindGroupsLoadingAction => ({type: FindGroupsActionType.LOADING, loading});
const findGroupsSuccess = (payload: Group[]): FindGroupsSuccessAction => ({type: FindGroupsActionType.SUCCESS, payload});
const findGroupsError = (error: any): FindGroupsErrorAction => ({type: FindGroupsActionType.ERROR, error});

const rootPath = '/api/groups';

export function getGroup(id: number) {
    return (dispatch) => {
        dispatch(getGroupLoading(true));
        const url = `${rootPath}/${id}`;
        return axios.get(url)
            .then((response) => {
                return dispatch(getGroupSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(getGroupError(error));
            });
    };
}

export function findGroups(name?: string) {
    return (dispatch) => {
        dispatch(findGroupsLoading(true));
        const url = name ? `${rootPath}?name=${name}` : rootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findGroupsSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findGroupsError(error));
            });
    };
}