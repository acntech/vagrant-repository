import {
    CreateGroupActionType,
    FindGroupsActionType,
    GetGroupActionType,
    UpdateGroupActionType
} from '../constants';
import { Error } from './globals';

export interface CreateGroup {
    name: string;
    description?: string;
}

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

export interface CreateGroupLoadingAction {
    type: CreateGroupActionType.LOADING,
    loading: boolean
}

export interface CreateGroupSuccessAction {
    type: CreateGroupActionType.SUCCESS,
    payload: Group
}

export interface CreateGroupErrorAction {
    type: CreateGroupActionType.ERROR,
    error: Error
}

export interface UpdateGroupLoadingAction {
    type: UpdateGroupActionType.LOADING,
    loading: boolean
}

export interface UpdateGroupSuccessAction {
    type: UpdateGroupActionType.SUCCESS,
    payload: Group
}

export interface UpdateGroupErrorAction {
    type: UpdateGroupActionType.ERROR,
    error: Error
}

export type GetGroupAction = GetGroupLoadingAction | GetGroupSuccessAction | GetGroupErrorAction;
export type FindGroupsAction = FindGroupsLoadingAction | FindGroupsSuccessAction | FindGroupsErrorAction;
export type CreateGroupAction = CreateGroupLoadingAction | CreateGroupSuccessAction | CreateGroupErrorAction;
export type UpdateGroupAction = UpdateGroupLoadingAction | UpdateGroupSuccessAction | UpdateGroupErrorAction;

export type GroupAction = GetGroupAction | FindGroupsAction | CreateGroupAction | UpdateGroupAction;