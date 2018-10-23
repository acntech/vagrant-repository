import { FindGroupsActionType, GetGroupActionType } from '../constants';
import { Error } from './globals';

export interface Group {
    id: number;
    name: string;
    description: string;
}

export interface GroupState {
    loading: boolean;
    groups: Group[];
    error?: any;
}

export const initialGroupState: GroupState = {
    loading: false,
    groups: []
};

export interface GetGroupLoadingAction {
    type: GetGroupActionType.LOADING,
    loading: boolean
}

export interface GetGroupSuccessAction {
    type: GetGroupActionType.SUCCESS,
    payload: Group
}

export interface GetGroupErrorAction {
    type: GetGroupActionType.ERROR,
    error: Error
}

export interface FindGroupsLoadingAction {
    type: FindGroupsActionType.LOADING,
    loading: boolean
}

export interface FindGroupsSuccessAction {
    type: FindGroupsActionType.SUCCESS,
    payload: Group[]
}

export interface FindGroupsErrorAction {
    type: FindGroupsActionType.ERROR,
    error: Error
}

export type GetGroupAction = GetGroupLoadingAction | GetGroupSuccessAction | GetGroupErrorAction;
export type FindGroupsAction = FindGroupsLoadingAction | FindGroupsSuccessAction | FindGroupsErrorAction;

export type GroupAction = GetGroupAction | FindGroupsAction;